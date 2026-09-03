package com.app.api.unit.services;

import com.app.api.repositories.UserDeviceRepository;
import com.app.api.services.NotificationsService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserDeviceRepository userDeviceRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    private NotificationsService notificationsService;

    @BeforeEach
    void setUp() {
        notificationsService = new NotificationsService();

        ReflectionTestUtils.setField(
                notificationsService,
                "userDeviceRepository",
                userDeviceRepository
        );
    }
    @Test
    void sendNotification_sendsToAllRegisteredDevices() throws Exception {
        when(userDeviceRepository.findTokensByUserId(1))
                .thenReturn(List.of(
                        "token-one",
                        "token-two",
                        "token-three"
                ));

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenReturn("message-id");

            notificationsService.sendTaskCreatedNotification(
                    1,
                    50,
                    "New task"
            );

            verify(firebaseMessaging, times(3))
                    .send(any(Message.class));
        }
    }

    @Test
    void sendNotification_withNoRegisteredDevices_doesNotSend() throws Exception {
        when(userDeviceRepository.findTokensByUserId(1))
                .thenReturn(List.of());

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            notificationsService.sendTaskCreatedNotification(
                    1,
                    50,
                    "New task"
            );

            verifyNoInteractions(firebaseMessaging);
        }
    }

    @Test
    void sendNotification_unregisteredToken_deletesToken() throws Exception {
        String deadToken = "dead-token";

        when(userDeviceRepository.findTokensByUserId(1))
                .thenReturn(List.of(deadToken));

        FirebaseMessagingException exception =
                mock(FirebaseMessagingException.class);

        when(exception.getMessagingErrorCode())
                .thenReturn(MessagingErrorCode.UNREGISTERED);

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenThrow(exception);

            assertDoesNotThrow(() ->
                    notificationsService.sendTaskCreatedNotification(
                            1,
                            50,
                            "New task"
                    )
            );

            verify(userDeviceRepository).deleteToken(deadToken);
        }
    }

    @Test
    void sendNotification_invalidArgumentToken_deletesToken() throws Exception {
        String invalidToken = "invalid-token";

        when(userDeviceRepository.findTokensByUserId(1))
                .thenReturn(List.of(invalidToken));

        FirebaseMessagingException exception =
                mock(FirebaseMessagingException.class);

        when(exception.getMessagingErrorCode())
                .thenReturn(MessagingErrorCode.INVALID_ARGUMENT);

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenThrow(exception);

            assertDoesNotThrow(() ->
                    notificationsService.sendTaskCreatedNotification(
                            1,
                            50,
                            "New task"
                    )
            );

            verify(userDeviceRepository).deleteToken(invalidToken);
        }
    }

    @Test
    void sendNotification_otherFirebaseError_doesNotDeleteToken()
            throws Exception {

        String token = "temporary-failure-token";

        when(userDeviceRepository.findTokensByUserId(1))
                .thenReturn(List.of(token));

        FirebaseMessagingException exception =
                mock(FirebaseMessagingException.class);

        when(exception.getMessagingErrorCode())
                .thenReturn(MessagingErrorCode.INTERNAL);

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenThrow(exception);

            assertDoesNotThrow(() ->
                    notificationsService.sendTaskCreatedNotification(
                            1,
                            50,
                            "New task"
                    )
            );

            verify(userDeviceRepository, never())
                    .deleteToken(token);
        }
    }

    // -------------------------------------------------------------------------
    // Failure on one token must not prevent other tokens from being attempted
    // -------------------------------------------------------------------------

    @Test
    void sendNotification_failureOnOneToken_continuesToNextToken()
            throws Exception {

        String firstToken = "bad-token";
        String secondToken = "good-token";

        when(userDeviceRepository.findTokensByUserId(1))
                .thenReturn(List.of(firstToken, secondToken));

        FirebaseMessagingException exception =
                mock(FirebaseMessagingException.class);

        when(exception.getMessagingErrorCode())
                .thenReturn(MessagingErrorCode.UNREGISTERED);

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenThrow(exception)
                    .thenReturn("success");

            assertDoesNotThrow(() ->
                    notificationsService.sendTaskCreatedNotification(
                            1,
                            50,
                            "New task"
                    )
            );

            verify(firebaseMessaging, times(2))
                    .send(any(Message.class));

            verify(userDeviceRepository)
                    .deleteToken(firstToken);
        }
    }

    @Test
    void sendTestNotification_firebaseError_rethrowsException()
            throws Exception {

        FirebaseMessagingException exception =
                mock(FirebaseMessagingException.class);

        when(exception.getMessagingErrorCode())
                .thenReturn(MessagingErrorCode.INTERNAL);

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenThrow(exception);

            FirebaseMessagingException thrown =
                    assertThrows(
                            FirebaseMessagingException.class,
                            () -> notificationsService.sendTestNotification(
                                    "test-token",
                                    "Title",
                                    "Body",
                                    "TYPE",
                                    "1"
                            )
                    );

            assertSame(exception, thrown);
        }
    }

    @Test
    void sendTestNotification_unregisteredToken_rethrowsException()
            throws Exception {

        String token = "x".repeat(30);

        FirebaseMessagingException exception =
                mock(FirebaseMessagingException.class);

        when(exception.getMessagingErrorCode())
                .thenReturn(MessagingErrorCode.UNREGISTERED);

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenThrow(exception);

            assertThrows(
                    FirebaseMessagingException.class,
                    () -> notificationsService.sendTestNotification(
                            token,
                            "Title",
                            "Body",
                            "TYPE",
                            "1"
                    )
            );
        }
    }

    @Test
    void sendTestNotification_unexpectedException_wrapsInRuntimeException()
            throws Exception {

        RuntimeException originalException =
                new RuntimeException("Something went wrong");

        try (MockedStatic<FirebaseMessaging> firebaseMock =
                     mockStatic(FirebaseMessaging.class)) {

            firebaseMock.when(FirebaseMessaging::getInstance)
                    .thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenThrow(originalException);

            RuntimeException thrown =
                    assertThrows(
                            RuntimeException.class,
                            () -> notificationsService.sendTestNotification(
                                    "test-token",
                                    "Title",
                                    "Body",
                                    "TYPE",
                                    "1"
                            )
                    );

            assertEquals(
                    "Unexpected error sending test notification",
                    thrown.getMessage()
            );

            assertSame(
                    originalException,
                    thrown.getCause()
            );
        }
    }
}


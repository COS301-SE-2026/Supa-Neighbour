package com.app.api.unit.services;
import com.app.api.services.FirebaseAuthService;

import com.app.api.models.User;
import com.app.api.models.Settings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
 
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
 
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import com.app.api.repositories.UserRepository;
import com.app.api.repositories.SettingsRepository;
 
// NOTE: Adjust these two imports to match wherever your JPA entities actually
// live in the project (e.g. com.app.api.entities.User / .Settings). They are
// only referenced here as mock targets for their getUserid()/setLastSeen()
// methods, so the exact package doesn't affect the test logic below.


@ExtendWith(MockitoExtension.class)
public class FirebaseAuthServiceTest {
    @Mock
    private UserRepository userRepository; 

    @Mock
    private SettingsRepository settingsRepository;

    @Mock
    private FirebaseAuth firebaseAuth;

    @Mock
    private FirebaseToken firebaseToken;

    @Mock
    private User user;

    @Mock
    private Settings settings;

    private FirebaseAuthService service;

    @BeforeEach
    void setUp() {
        service = new FirebaseAuthService(userRepository, settingsRepository);
    }

    @Nested
    class VerifyIdTokenTests {
        @Test
        void verifyIdToken_validToken_returnsDecodedToken() throws FirebaseAuthException {
            String idToken = "valid-token";
 
            try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
                mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
                when(firebaseAuth.verifyIdToken(idToken, true)).thenReturn(firebaseToken);
 
                FirebaseToken result = service.verifyIdToken(idToken);
 
                assertEquals(firebaseToken, result);
                verify(firebaseAuth).verifyIdToken(idToken, true);
            }
        }

        @Test
        void propagatesFirebaseAuthException_whenTokenIsInvalid() throws FirebaseAuthException {
            String idToken = "invalid-token";
            FirebaseAuthException expected = mock(FirebaseAuthException.class);

            try(MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
                mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
                when(firebaseAuth.verifyIdToken(idToken,true)).thenThrow(expected);

                FirebaseAuthException thrown = assertThrows(FirebaseAuthException.class,()-> service.verifyIdToken(idToken));
                assertEquals(expected, thrown);
            }
        }

        @Test
        void alwaysChecksTokenRevocation() throws FirebaseAuthException {
            String idToken = "valid-token ";

            try(MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
                mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
                when(firebaseAuth.verifyIdToken(idToken,true)).thenReturn(firebaseToken);

                service.verifyIdToken(idToken);

                verify(firebaseAuth).verifyIdToken(eq(idToken), eq(true));
            }
        }
    }

    @Nested
    class GetUserIdFromTokenTests {

        private static final String ID_TOKEN = "token";
        private static final String FIREBASE_UID = "firebase-uid-123";
        private static final int EXPECTED_USER_ID = 42;

        @Test
        void returnsUserId_andUpdatesLastSeen_whenSettingsRowExists() throws FirebaseAuthException {
            try(MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
                mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
                when(firebaseAuth.verifyIdToken(ID_TOKEN, true)).thenReturn(firebaseToken);
                when(firebaseToken.getUid()).thenReturn(FIREBASE_UID);
                when(userRepository.findByFirebaseUid(FIREBASE_UID)).thenReturn(Optional.of(user));
                when(user.getUserid()).thenReturn(EXPECTED_USER_ID);
                when(settingsRepository.findById(EXPECTED_USER_ID)).thenReturn(Optional.of(settings));

                int userId = service.getUserIdFromToken(ID_TOKEN);

                assertEquals(EXPECTED_USER_ID, userId);
                verify(settings).setLastSeen(any(Instant.class));
                verify(settingsRepository).save(settings);
            }
        }

        @Test
        void returnsUserId_andSkipsSave_whenNoSettingsRowExists() throws FirebaseAuthException {
            try(MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
                mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
                when(firebaseAuth.verifyIdToken(ID_TOKEN, true)).thenReturn(firebaseToken);
                when(firebaseToken.getUid()).thenReturn(FIREBASE_UID);
                when(userRepository.findByFirebaseUid(FIREBASE_UID)).thenReturn(Optional.of(user));
                when(user.getUserid()).thenReturn(EXPECTED_USER_ID);
                when(settingsRepository.findById(EXPECTED_USER_ID)).thenReturn(Optional.empty());

                int userId = service.getUserIdFromToken(ID_TOKEN);

                assertEquals(EXPECTED_USER_ID, userId);
                verify(settingsRepository, never()).save(any(Settings.class));
            }
        }

        @Test
        void throwsRuntimeException_whenNoUserFoundForFirebaseUid() throws FirebaseAuthException {
            try(MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
                mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
                when(firebaseAuth.verifyIdToken(ID_TOKEN, true)).thenReturn(firebaseToken);
                when(firebaseToken.getUid()).thenReturn(FIREBASE_UID);
                when(userRepository.findByFirebaseUid(FIREBASE_UID)).thenReturn(Optional.empty());

                RuntimeException thrown = assertThrows(RuntimeException.class, () -> service.getUserIdFromToken(ID_TOKEN));
                assertTrue(thrown.getMessage().contains(FIREBASE_UID));
                verifyNoInteractions(settingsRepository);
            }
        }

        @Test
        void propogatesFirebaseAuthException_beforeTouchingRepositories() throws FirebaseAuthException {
            FirebaseAuthException expected = mock(FirebaseAuthException.class);
            
            try(MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
                mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
                when(firebaseAuth.verifyIdToken(ID_TOKEN, true)).thenThrow(expected);

                FirebaseAuthException thrown = assertThrows(FirebaseAuthException.class, () -> service.getUserIdFromToken(ID_TOKEN));
                assertEquals(expected, thrown);
                verifyNoInteractions(userRepository);
                verifyNoInteractions(settingsRepository);
            }
        } 
    }
    @Nested
    class RevokeUserSessionsTests {
    @Test
    void delegatesTofirebaseAuthRevokeSessions() throws FirebaseAuthException {
        String uid = "uid-1";

        try(MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            service.revokeUserSessions(uid);

            verify(firebaseAuth,times(1)).revokeRefreshTokens(uid);
            }   
        }

    @Test
    void propagatesFirebaseAuthException_whenRevokeFails() throws FirebaseAuthException {
        String uid = "uid-1";
        FirebaseAuthException expected = mock(FirebaseAuthException.class);

        try(MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
            doThrow(expected).when(firebaseAuth).revokeRefreshTokens(uid);

            FirebaseAuthException thrown = assertThrows(FirebaseAuthException.class, () -> service.revokeUserSessions(uid));
            assertEquals(expected, thrown);
            }
        }
    }  
}



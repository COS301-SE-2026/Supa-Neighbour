package com.app.api.unit.services;

import com.app.api.models.ModerationAction;
import com.app.api.models.Report;
import com.app.api.models.User;
import com.app.api.repositories.ModerationActionRepository;
import com.app.api.services.ModerationActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModerationActionServiceTest {
    @Mock
    private ModerationActionRepository moderationActionRepository;

    private ModerationActionService moderationActionService;

    private User user;

    @BeforeEach
    void setUp(){
        moderationActionService = new ModerationActionService(moderationActionRepository);
    }

    @Test
    void isBanned_whenBanExists_returnsTrue() {
        ModerationAction ban = mock(ModerationAction.class);
        when(moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user, "ban"))
        .thenReturn(List.of(ban));

        boolean result = moderationActionService.isBanned(user);

        assertTrue(result);
    }

    @Test
    void isBanned_whenNoActiveBan_returnsFalse(){
        when(moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user,"ban"))
        .thenReturn(List.of());

        boolean result = moderationActionService.isBanned(user);
        assertFalse(result);
    }

    @Test
    void isSuspended_whenNoSuspensions_returFalse() {
        when(moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user, "suspension"))
        .thenReturn(List.of());

        boolean result = moderationActionService.isSuspended(user);
        assertFalse(result);
    }

    @Test
    void isSuspended_whenActiveUnespiredSuspensionExists_returnsTrue() {
        ModerationAction suspension = mock(ModerationAction.class);
        when(suspension.getExpiredAt()).thenReturn(LocalDateTime.now().plusDays(1));

        when(moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user, "suspension"))
        .thenReturn(List.of(suspension));

        boolean result = moderationActionService.isSuspended(user);
        assertTrue(result);
    }

    @Test
    void isSuspended_whenSuspensionAlreadyExpired_returnFalse() {
        ModerationAction suspension = mock(ModerationAction.class);
        when(suspension.getExpiredAt()).thenReturn(LocalDateTime.now().minusDays(1));
        when(moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user, "suspension"))
        .thenReturn(List.of(suspension));

        boolean result = moderationActionService.isSuspended(user);
        assertFalse(result);
    }

    @Test
    void isSuspended_whenExpiredAtIsNull_returnFalse() {
        ModerationAction suspension = mock(ModerationAction.class);
        when(suspension.getExpiredAt()).thenReturn(null);

        when(moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user, "suspension"))
        .thenReturn(List.of(suspension));

        boolean result = moderationActionService.isSuspended(user);
        assertFalse(result);
    }

    @Test
    void isSuspended_whenOneExpiredAndOneActive_returnsTrue() {
        ModerationAction expired = mock(ModerationAction.class);
        when(expired.getExpiredAt()).thenReturn(LocalDateTime.now().minusHours(1));

        ModerationAction active = mock(ModerationAction.class);
        when(active.getExpiredAt()).thenReturn(LocalDateTime.now().plusHours(1));

        when(moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user, "suspension"))
        .thenReturn(List.of(expired,active));

        boolean result = moderationActionService.isSuspended(user);
        assertTrue(result);
    }

    @Test
    void issueModerationAction_whenActionTypeInvalid_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> moderationActionService.issueModerationAction(
                        user, "timeout", "reason", null, user, null));

        assertTrue(ex.getMessage().contains("timeout"));
        verify(moderationActionRepository, never()).save(any());
    }

    @Test
    void issueModerationAction_whenWarning_savesWithNullExpiry() {
        User issuedBy = mock(User.class);
        ModerationAction saved = mock(ModerationAction.class);
        ArgumentCaptor<ModerationAction> captor = ArgumentCaptor.forClass(ModerationAction.class);

        when(moderationActionRepository.save(any(ModerationAction.class))).thenReturn(saved);
        LocalDateTime before = LocalDateTime.now();
        ModerationAction result = moderationActionService.issueModerationAction(user, "warning", "spam", null, issuedBy, null);
        LocalDateTime after = LocalDateTime.now();

        assertEquals(saved,result);
        verify(moderationActionRepository).save(captor.capture());

        ModerationAction captured = captor.getValue();
        assertEquals(user, captured.getUser());
        assertEquals("warning", captured.getActionType());
        assertEquals("spam", captured.getReason());
        assertNull(captured.getReport());
        assertEquals(issuedBy, captured.getIssuedBy());
        assertNull(captured.getExpiredAt());
        assertNotNull(captured.getIssuedAt());
        assertFalse(captured.getIssuedAt().isBefore(before));
        assertFalse(captured.getIssuedAt().isAfter(after));
    }

    @Test
    void issueModerationAction_whenSuspension_savesWithReportAndExpiry() {
        User issuedBy = mock(User.class);
        Report report = mock(Report.class);
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        ModerationAction saved = mock(ModerationAction.class);
        ArgumentCaptor<ModerationAction> captor = ArgumentCaptor.forClass(ModerationAction.class);

        when(moderationActionRepository.save(any(ModerationAction.class))).thenReturn(saved);

        ModerationAction result = moderationActionService.issueModerationAction(user, "suspension", "repeated violations", report, issuedBy, expiresAt);

        assertEquals(saved,result);
        verify(moderationActionRepository).save(captor.capture());
        
        ModerationAction captured = captor.getValue();
        assertEquals(user, captured.getUser());
        assertEquals("suspension", captured.getActionType());
        assertEquals("repeated violations", captured.getReason());
        assertEquals(report, captured.getReport());
        assertEquals(issuedBy, captured.getIssuedBy());
        assertEquals(expiresAt, captured.getExpiredAt());
    }

    @Test
    void issueModerationAction_whenBan_savesWithNullExpiry() {
        User issuedBy =mock(User.class);
        ModerationAction saved = mock(ModerationAction.class);
        ArgumentCaptor<ModerationAction> captor = ArgumentCaptor.forClass(ModerationAction.class);

        when(moderationActionRepository.save(any(ModerationAction.class))).thenReturn(saved);

        ModerationAction result = moderationActionService.issueModerationAction(user, "ban", "srvere violation", null, issuedBy, null);

        assertEquals(saved, result);
        verify(moderationActionRepository).save(captor.capture());

        ModerationAction captured = captor.getValue();
        assertEquals("ban", captured.getActionType());
        assertNull(captured.getExpiredAt());
    }
}


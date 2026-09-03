package com.app.api.unit.services;


import com.app.api.dtos.PatchReportDTO;
import com.app.api.dtos.PatchReportResponseDTO;
import com.app.api.dtos.ReportRequestDTO;
import com.app.api.dtos.ReportResponseDTO;
import org.springframework.context.ApplicationEventPublisher;

import com.app.api.repositories.DependentRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.models.Comments;
import com.app.api.models.Posts;
import com.app.api.models.Report;
import com.app.api.models.Task;
import com.app.api.models.User;
import com.app.api.repositories.AdminRepository;
import com.app.api.repositories.CommentsRepository;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.ReportRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.ModerationActionService;
import com.app.api.services.ReportDetailService;
import com.app.api.services.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReportDetailService reportDetailService;
    @Mock
    private ModerationActionService moderationActionService;
    @Mock
    private PostsRepository postsRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AdminRepository adminRepository;

    private ReportService reportService;

    @Mock private ApplicationEventPublisher applicationEventPublisher;
    @Mock private DependentRepository dependentRepository;
    @Mock private HelperRepository helperRepository;
@BeforeEach
void setUp() {
    reportService = new ReportService(
            reportRepository,
            userRepository,
            reportDetailService,
            moderationActionService,
            postsRepository,
            commentsRepository,
            taskRepository,
            applicationEventPublisher,
            dependentRepository,
            helperRepository,
            adminRepository);
}

    @Test
    void getReportsForAdmin_invalidStatus_throws400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.getReportsForAdmin(1, "bogus", null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void getReportsForAdmin_invalidReportType_throws400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.getReportsForAdmin(1, null, "BOGUS"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void getReportsForAdmin_statusAndReportType_usesCombinedFinder() {
        Report r = new Report();
        r.setReportId(1);
        when(reportRepository.findByAdminIdAndStatusAndReportType(1, "submitted", "USER"))
                .thenReturn(List.of(r));

        List<ReportResponseDTO> result = reportService.getReportsForAdmin(1, "submitted", "USER");

        assertEquals(1, result.size());
        verify(reportRepository).findByAdminIdAndStatusAndReportType(1, "submitted", "USER");
    }

    @Test
    void getReportsForAdmin_onlyStatus_usesStatusFinder() {
        when(reportRepository.findByAdminIdAndStatus(1, "assigned"))
                .thenReturn(List.of());

        reportService.getReportsForAdmin(1, "assigned", null);

        verify(reportRepository).findByAdminIdAndStatus(1, "assigned");
    }

    @Test
    void getReportsForAdmin_onlyReportType_usesReportTypeFinder() {
        when(reportRepository.findByAdminIdAndReportType(1, "USER"))
                .thenReturn(List.of());

        reportService.getReportsForAdmin(1, null, "USER");

        verify(reportRepository)
                .findByAdminIdAndReportType(1, "USER");
    }

    @Test
    void submitReport_invalidReportType_throws400() {
        ReportRequestDTO dto = mock(ReportRequestDTO.class);
        when(dto.getReportType()).thenReturn("BOGUS");

        assertThrows(ResponseStatusException.class, () -> reportService.submitReport(1, dto));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void submitReport_userType_missingReportedUserId_throws400() {
        ReportRequestDTO dto = mock(ReportRequestDTO.class);
        when(dto.getReportType()).thenReturn("USER");
        when(dto.getReportedUserId()).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> reportService.submitReport(1, dto));

    }

    @Test
    void submitReport_userType_extraFieldsPresent_throws400() {
        ReportRequestDTO dto = mock(ReportRequestDTO.class);
        when(dto.getReportType()).thenReturn("USER");
        when(dto.getReportedUserId()).thenReturn(5);
        when(dto.getReportedPostId()).thenReturn(9);

        assertThrows(ResponseStatusException.class, () -> reportService.submitReport(1, dto));
    }


    @Test
    void submitReport_postType_missingReportedPostId_throws400() {
        ReportRequestDTO dto = mock(ReportRequestDTO.class);
        when(dto.getReportType()).thenReturn("POST");
        when(dto.getReportedPostId()).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> reportService.submitReport(1, dto));
    }

    @Test
    void submitReport_commentType_missingReportedCommentId_throws400() {
        ReportRequestDTO dto = mock(ReportRequestDTO.class);
        when(dto.getReportType()).thenReturn("COMMENT");

        assertThrows(ResponseStatusException.class, () -> reportService.submitReport(1, dto));
    }

    @Test
    void submitReport_taskDisputeType_missingTaskId_throws400() {
        ReportRequestDTO dto = mock(ReportRequestDTO.class);
        when(dto.getReportType()).thenReturn("TASK_DISPUTE");
        when(dto.getTaskId()).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> reportService.submitReport(1, dto));
    }

    @Test
    void submitReport_taskDisputeType_missingDisputeReason_throws400() {
        ReportRequestDTO dto = mock(ReportRequestDTO.class);
        when(dto.getReportType()).thenReturn("TASK_DISPUTE");
        when(dto.getTaskId()).thenReturn(3);
        when(dto.getDisputeReason()).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> reportService.submitReport(1, dto));
    }

    @Test
    void patchReport_reportIdNull_throws400() {
        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.patchReport(10, dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void patchReport_reportNotFound_throws400() {
        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(99);
        when(reportRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.patchReport(10, dto));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void patchReport_adminIdNull_throws403() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(null);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.patchReport(10, dto));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void patchReport_adminIdMismatch_throws403() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(999);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.patchReport(10, dto));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void patchReport_invalidStatus_throws400() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getStatus()).thenReturn("bogus");
        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.patchReport(10, dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void patchReport_validStatusUpdate_savesReport() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getStatus()).thenReturn("reviewed");
        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);

        PatchReportResponseDTO result = reportService.patchReport(10, dto);

        assertEquals("reviewed", existing.getStatus());
        assertEquals("reviewed", result.getStatus());
        verify(moderationActionService, never())
                .issueModerationAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void patchReport_violationTypeUpdate_setsViolationType() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getViolationType()).thenReturn("HARASSMENT");
        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);

        reportService.patchReport(10, dto);

        assertEquals("HARASSMENT", existing.getViolationType());
    }

    @Test
    void patchReport_invalidSeverity_throws400() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getSeverity()).thenReturn("EXTREME");
        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(ResponseStatusException.class, () -> reportService.patchReport(10, dto));
    }

    @Test
    void patchReport_validSeverityUpdate_setsSeverity() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getSeverity()).thenReturn("MODERATE");
        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);

        reportService.patchReport(10, dto);

        assertEquals("MODERATE", existing.getSeverity());
    }

    @Test
    void patchReport_actualActionNull_doesNotTriggerModerationAction() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getStatus()).thenReturn("reviewed");
        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);

        reportService.patchReport(10, dto);

        verify(moderationActionService, never())
                .issueModerationAction(any(), any(), any(), any(), any(), any());
        verifyNoInteractions(userRepository);
    }

    @Test
    void patchReport_actualActionWarning_resolvedAtNull_setsResolvedAtAndIssuesWarning() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);
        existing.setResolvedAt(null);
        existing.setViolationType("HARASSMENT");

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        User targetUser = mock(User.class);
        User adminUser = mock(User.class);

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(targetUser));
        when(userRepository.findById(10)).thenReturn(Optional.of(adminUser));

        reportService.patchReport(10, dto);

        assertNotNull(existing.getResolvedAt());
        assertEquals("warning", existing.getActualAction());

        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(moderationActionService).issueModerationAction(
                eq(targetUser), eq("warning"), reasonCaptor.capture(), eq(existing), eq(adminUser), isNull());
        assertEquals("HARASSMENT", reasonCaptor.getValue());
    }

    @Test
    void patchReport_actualActionBan_issuesBanWithNoExpiry() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("BAN");

        User targetUser = mock(User.class);
        User adminUser = mock(User.class);

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(targetUser));
        when(userRepository.findById(10)).thenReturn(Optional.of(adminUser));

        reportService.patchReport(10, dto);

        verify(moderationActionService).issueModerationAction(
                eq(targetUser), eq("ban"), anyString(), eq(existing), eq(adminUser), isNull());
    }

    @Test
    void patchReport_actualActionSuspend7d_issuesSuspensionWithExpiry() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("suspend_7d");

        User targetUser = mock(User.class);
        User adminUser = mock(User.class);

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(targetUser));
        when(userRepository.findById(10)).thenReturn(Optional.of(adminUser));

        LocalDateTime lowerBound = LocalDateTime.now().plusDays(7).minusSeconds(5);

        reportService.patchReport(10, dto);

        ArgumentCaptor<LocalDateTime> expiryCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(moderationActionService).issueModerationAction(
                eq(targetUser), eq("suspension"), anyString(), eq(existing), eq(adminUser), expiryCaptor.capture());

        assertNotNull(expiryCaptor.getValue());
        assertTrue(expiryCaptor.getValue().isAfter(lowerBound));
    }

    @Test
    void patchReport_actualActionUnrecognized_skipsModerationAction() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("NOT_A_REAL_ACTION");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);

        reportService.patchReport(10, dto);

        verify(moderationActionService, never())
                .issueModerationAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void patchReport_resolvedAtAlreadySet_doesNotOverwrite() {
        Timestamp originalResolvedAt = new Timestamp(1000L);
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);
        existing.setResolvedAt(originalResolvedAt);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(10)).thenReturn(Optional.of(mock(User.class)));

        reportService.patchReport(10, dto);

        assertEquals(originalResolvedAt, existing.getResolvedAt());
    }

    @Test
    void patchReport_targetResolution_postType_resolvesReportedPostAuthor() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("POST");
        existing.setReportedPostId(50);

        Posts post = mock(Posts.class);
        User postAuthor = mock(User.class);
        when(postAuthor.getUserid()).thenReturn(7);
        when(post.getUserid()).thenReturn(postAuthor);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(postsRepository.findById(50)).thenReturn(Optional.of(post));
        when(userRepository.findById(7)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(10)).thenReturn(Optional.of(mock(User.class)));

        reportService.patchReport(10, dto);

        verify(moderationActionService).issueModerationAction(
                any(), eq("warning"), anyString(), eq(existing), any(), isNull());
    }

    @Test
    void patchReport_targetResolution_postNotFound_skipsModerationAction() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("POST");
        existing.setReportedPostId(50);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(postsRepository.findById(50)).thenReturn(Optional.empty());

        reportService.patchReport(10, dto);

        verify(moderationActionService, never())
                .issueModerationAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void patchReport_targetResolution_commentType_resolvesCommentAuthor() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("COMMENT");
        existing.setReportedCommentId(60);

        Comments comment = mock(Comments.class);
        User commentAuthor = mock(User.class);
        when(commentAuthor.getUserid()).thenReturn(8);
        when(comment.getUserid()).thenReturn(commentAuthor);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(commentsRepository.findById(60)).thenReturn(Optional.of(comment));
        when(userRepository.findById(8)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(10)).thenReturn(Optional.of(mock(User.class)));

        reportService.patchReport(10, dto);

        verify(moderationActionService).issueModerationAction(
                any(), eq("warning"), anyString(), eq(existing), any(), isNull());
    }


    @Test
    void patchReport_targetResolution_unknownReportType_skipsModerationAction() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("SOMETHING_ELSE");

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);

        reportService.patchReport(10, dto);

        verify(moderationActionService, never())
                .issueModerationAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void patchReport_targetResolution_targetUserNotFound_skipsModerationAction() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.empty());

        reportService.patchReport(10, dto);

        verify(moderationActionService, never())
                .issueModerationAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void patchReport_targetResolution_adminUserNotFound_skipsModerationAction() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(10)).thenReturn(Optional.empty());

        reportService.patchReport(10, dto);

        verify(moderationActionService, never())
                .issueModerationAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void patchReport_buildReason_violationTypeAndAdminNotes_combinesBoth() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);
        existing.setViolationType("SPAM_SCAM");

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");
        when(dto.getAdminNotes()).thenReturn("Repeat offender");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(10)).thenReturn(Optional.of(mock(User.class)));

        reportService.patchReport(10, dto);

        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(moderationActionService).issueModerationAction(
                any(), anyString(), reasonCaptor.capture(), any(), any(), isNull());
        assertEquals("SPAM_SCAM: Repeat offender", reasonCaptor.getValue());
    }

    @Test
    void patchReport_buildReason_adminNotesOnly_usesAdminNotes() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");
        when(dto.getAdminNotes()).thenReturn("Notes only");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(10)).thenReturn(Optional.of(mock(User.class)));

        reportService.patchReport(10, dto);

        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(moderationActionService).issueModerationAction(
                any(), anyString(), reasonCaptor.capture(), any(), any(), isNull());
        assertEquals("Notes only", reasonCaptor.getValue());
    }

    @Test
    void patchReport_buildReason_reportReasonOnly_usesReportReason() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);
        existing.setReason("Original reason");

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(10)).thenReturn(Optional.of(mock(User.class)));

        reportService.patchReport(10, dto);

        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(moderationActionService).issueModerationAction(
                any(), anyString(), reasonCaptor.capture(), any(), any(), isNull());
        assertEquals("Original reason", reasonCaptor.getValue());
    }

    @Test
    void patchReport_buildReason_nothingSet_usesDefaultFallback() {
        Report existing = new Report();
        existing.setReportId(1);
        existing.setAdminId(10);
        existing.setReportType("USER");
        existing.setReportedUserId(5);

        PatchReportDTO dto = mock(PatchReportDTO.class);
        when(dto.getReportId()).thenReturn(1);
        when(dto.getActualAction()).thenReturn("warning");

        when(reportRepository.findById(1)).thenReturn(Optional.of(existing));
        when(reportRepository.save(existing)).thenReturn(existing);
        when(userRepository.findById(5)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(10)).thenReturn(Optional.of(mock(User.class)));

        reportService.patchReport(10, dto);

        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        verify(moderationActionService).issueModerationAction(
                any(), anyString(), reasonCaptor.capture(), any(), any(), isNull());
        assertEquals("Moderation action from report #1", reasonCaptor.getValue());
    }

    @Test
    void matchReportToAdmin_reportNotFound_throws404() {
        when(reportRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.matchReportToAdmin(1));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void matchReportToAdmin_reportNotSubmitted_throws409() {
        Report report = new Report();
        report.setReportId(1);
        report.setStatus("assigned");

        when(reportRepository.findById(1)).thenReturn(Optional.of(report));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.matchReportToAdmin(1));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void matchReportToAdmin_noAdminsAvailable_throws503() {
        Report report = new Report();
        report.setReportId(1);
        report.setStatus("submitted");

        when(reportRepository.findById(1)).thenReturn(Optional.of(report));
        when(adminRepository.findAdminWithLeastAssignedReports()).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.matchReportToAdmin(1));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }
}

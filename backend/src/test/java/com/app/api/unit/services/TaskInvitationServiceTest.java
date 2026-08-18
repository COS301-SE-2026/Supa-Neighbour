package com.app.api.unit.services;

import com.app.api.models.Helper;
import com.app.api.models.TaskInvitation;
import com.app.api.models.TaskInvoice;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.TaskInvitationRepository;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.services.TaskInvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class TaskInvitationServiceTest {

    @Mock
    private TaskInvitationRepository taskInvitationRepository;

    @Mock
    private TaskInvoiceRepository taskInvoiceRepository;

    @Mock
    private HelperRepository helperRepository;

    @InjectMocks
    private TaskInvitationService taskInvitationService;

    private Helper helper;
    private TaskInvoice taskInvoice;

    private static final int TASK_ID = 3;
    private static final int HELPER_ID = 7;

    @BeforeEach
    void setUp() {
        helper = new Helper();
        helper.setHelperid(HELPER_ID);

        taskInvoice = mock(TaskInvoice.class);
    }

    private Helper buildHelper(int id) {
        Helper h = new Helper();
        h.setHelperid(id);
        return h;
    }

    private TaskInvitation buildInvitation(int helperId, String status) {
        return TaskInvitation.builder()
                .taskId(taskInvoice)
                .helperId(buildHelper(helperId))
                .status(status)
                .build();
    }


    @Test
    void getTaskInvitations_ReturnsAllInvitations() {
        List<TaskInvitation> invitations = Arrays.asList(mock(TaskInvitation.class));
        when(taskInvitationRepository.findAll()).thenReturn(invitations);

        List<TaskInvitation> result = taskInvitationService.getTaskInvitations();

        assertEquals(1, result.size());
        verify(taskInvitationRepository, times(1)).findAll();
    }


    @Test
    void getInvitationById_WhenFound_ReturnsInvitation() {
        TaskInvitation invitation = mock(TaskInvitation.class);
        when(taskInvitationRepository.findById(1)).thenReturn(Optional.of(invitation));

        TaskInvitation result = taskInvitationService.getInvitationById(1);

        assertSame(invitation, result);
    }

    @Test
    void getInvitationById_WhenNotFound_ReturnsNull() {
        when(taskInvitationRepository.findById(999)).thenReturn(Optional.empty());

        TaskInvitation result = taskInvitationService.getInvitationById(999);

        assertNull(result);
    }


    @Test
    void saveTaskInvitation_WhenNull_ReturnsNull() {
        TaskInvitation result = taskInvitationService.saveTaskInvitation(null);

        assertNull(result);
        verify(taskInvitationRepository, never()).save(any());
    }

    @Test
    void saveTaskInvitation_WhenValid_SavesAndReturns() {
        TaskInvitation invitation = mock(TaskInvitation.class);
        when(taskInvitationRepository.save(invitation)).thenReturn(invitation);

        TaskInvitation result = taskInvitationService.saveTaskInvitation(invitation);

        assertSame(invitation, result);
        verify(taskInvitationRepository, times(1)).save(invitation);
    }


    @Test
    void updateTaskInvitation_WhenNotExists_ReturnsNull() {
        when(taskInvitationRepository.findById(999)).thenReturn(Optional.empty());

        TaskInvitation result = taskInvitationService.updateTaskInvitation(999, mock(TaskInvitation.class));

        assertNull(result);
        verify(taskInvitationRepository, never()).save(any());
    }

    @Test
    void updateTaskInvitation_WhenExists_CopiesFieldsOntoExistingAndSaves() {
        TaskInvitation existing = mock(TaskInvitation.class);
        TaskInvitation updated = mock(TaskInvitation.class);

        java.util.Date invitedAt = new java.util.Date();
        when(updated.getInvitedAt()).thenReturn(invitedAt);
        when(updated.getHelperId()).thenReturn(helper);
        when(updated.getStatus()).thenReturn("Accepted");
        when(updated.getTaskId()).thenReturn(taskInvoice);

        when(taskInvitationRepository.findById(1)).thenReturn(Optional.of(existing));
        when(taskInvitationRepository.save(existing)).thenReturn(existing);

        TaskInvitation result = taskInvitationService.updateTaskInvitation(1, updated);

        assertSame(existing, result);
        verify(existing).setInvitedAt(invitedAt);
        verify(existing).setHelperId(helper);
        verify(existing).setStatus("Accepted");
        verify(existing).setTaskId(taskInvoice);
        verify(taskInvitationRepository, times(1)).save(existing);
        verify(taskInvitationRepository, never()).save(updated);
    }


    @Test
    void deleteTaskInvitation_CallsRepositoryDeleteById() {
        doNothing().when(taskInvitationRepository).deleteById(1);

        taskInvitationService.deleteTaskInvitation(1);

        verify(taskInvitationRepository, times(1)).deleteById(1);
    }


    @Test
    void inviteHelper_WhenAlreadyInvited_ReturnsNullAndDoesNothingElse() {
        TaskInvitation existingInvited = buildInvitation(HELPER_ID, "Invited");
        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.of(existingInvited));

        TaskInvitation result = taskInvitationService.inviteHelper(TASK_ID, HELPER_ID, taskInvoice, helper);

        assertNull(result);
        verify(taskInvitationRepository, never()).save(any());
        verify(taskInvoiceRepository, never()).save(any());
        verify(taskInvitationRepository, never()).findByTaskId_Taskid(anyInt());
    }

    @Test
    void inviteHelper_WhenNoExistingInvitation_CreatesNewInvitedInvitation() {
        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.empty());
        when(taskInvitationRepository.findByTaskId_Taskid(TASK_ID)).thenReturn(List.of());
        when(taskInvitationRepository.save(any(TaskInvitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskInvitation result = taskInvitationService.inviteHelper(TASK_ID, HELPER_ID, taskInvoice, helper);

        assertNotNull(result);
        assertEquals("Invited", result.getStatus());
        assertNotNull(result.getInvitedAt());
        assertEquals(helper, result.getHelperId());
        assertEquals(taskInvoice, result.getTaskId());

        verify(taskInvoice).setStatus("assigned");
        verify(taskInvoice).setHelperid(helper);
        verify(taskInvoiceRepository, times(1)).save(taskInvoice);
    }

    @Test
    void inviteHelper_WhenExistingInvitationNotInvited_UpdatesSameInstanceToInvited() {
        TaskInvitation existing = buildInvitation(HELPER_ID, "Rejected");
        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.of(existing));
        when(taskInvitationRepository.findByTaskId_Taskid(TASK_ID)).thenReturn(List.of());
        when(taskInvitationRepository.save(any(TaskInvitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskInvitation result = taskInvitationService.inviteHelper(TASK_ID, HELPER_ID, taskInvoice, helper);

        assertSame(existing, result);
        assertEquals("Invited", existing.getStatus());
        assertNotNull(existing.getInvitedAt());
        verify(taskInvitationRepository, times(1)).save(existing);
    }

    @Test
    void inviteHelper_RejectsOtherPendingInvitationsForDifferentHelpersOnly() {
        TaskInvitation sameHelper = buildInvitation(HELPER_ID, "Declined");
        TaskInvitation alreadyInvited = buildInvitation(8, "Invited");
        TaskInvitation alreadyRejected = buildInvitation(9, "Rejected");
        TaskInvitation candidate = buildInvitation(10, "Declined");

        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.empty());
        when(taskInvitationRepository.findByTaskId_Taskid(TASK_ID))
                .thenReturn(new ArrayList<>(List.of(sameHelper, alreadyInvited, alreadyRejected, candidate)));
        when(taskInvitationRepository.save(any(TaskInvitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        taskInvitationService.inviteHelper(TASK_ID, HELPER_ID, taskInvoice, helper);

        assertEquals("Declined", sameHelper.getStatus());
        assertEquals("Invited", alreadyInvited.getStatus());
        assertEquals("Rejected", alreadyRejected.getStatus());
        assertEquals("Rejected", candidate.getStatus());

        verify(taskInvitationRepository, never()).save(sameHelper);
        verify(taskInvitationRepository, never()).save(alreadyInvited);
        verify(taskInvitationRepository, never()).save(alreadyRejected);
        verify(taskInvitationRepository, times(1)).save(candidate);
    }


    @Test
    void declineInvitation_WhenExistingIsInvited_ThrowsUnprocessable() {
        TaskInvitation existing = buildInvitation(HELPER_ID, "Invited");
        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> taskInvitationService.declineInvitation(TASK_ID, HELPER_ID, taskInvoice, helper));
        assertEquals("UNPROCESSABLE", ex.getMessage());
        verify(taskInvitationRepository, never()).save(any());
    }

    @Test
    void declineInvitation_WhenExistingIsOtherStatus_ThrowsConflict() {
        TaskInvitation existing = buildInvitation(HELPER_ID, "Accepted");
        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> taskInvitationService.declineInvitation(TASK_ID, HELPER_ID, taskInvoice, helper));
        assertEquals("CONFLICT", ex.getMessage());
        verify(taskInvitationRepository, never()).save(any());
    }

    @Test
    void declineInvitation_WhenNoExistingInvitation_CreatesDeclinedInvitation() {
        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.empty());
        when(taskInvitationRepository.save(any(TaskInvitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskInvitation result = taskInvitationService.declineInvitation(TASK_ID, HELPER_ID, taskInvoice, helper);

        assertNotNull(result);
        assertEquals("Declined", result.getStatus());
        assertNull(result.getInvitedAt());
        assertEquals(helper, result.getHelperId());
        assertEquals(taskInvoice, result.getTaskId());
        verify(taskInvitationRepository, times(1)).save(any(TaskInvitation.class));
    }


    @Test
    void acceptInvitation_WhenNoExistingInvitation_ThrowsNotFound() {
        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> taskInvitationService.acceptInvitation(TASK_ID, HELPER_ID, taskInvoice, helper));
        assertEquals("NOT_FOUND", ex.getMessage());
    }

    @Test
    void acceptInvitation_WhenExistingHasNonNullStatus_ThrowsConflict() {
        TaskInvitation existing = buildInvitation(HELPER_ID, "Invited");
        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> taskInvitationService.acceptInvitation(TASK_ID, HELPER_ID, taskInvoice, helper));
        assertEquals("CONFLICT", ex.getMessage());
        verify(taskInvitationRepository, never()).save(any());
    }

    @Test
    void acceptInvitation_WhenExistingStatusIsNull_AcceptsUpdatesInvoiceAndRejectsOthers() {
        TaskInvitation existing = buildInvitation(HELPER_ID, null);
        TaskInvitation otherPending = buildInvitation(8, "Declined");
        TaskInvitation otherAlreadyRejected = buildInvitation(9, "Rejected");

        when(taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(TASK_ID, HELPER_ID))
                .thenReturn(Optional.of(existing));
        when(taskInvitationRepository.save(any(TaskInvitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(taskInvitationRepository.findByTaskId_Taskid(TASK_ID))
                .thenReturn(new ArrayList<>(List.of(otherPending, otherAlreadyRejected)));

        TaskInvitation result = taskInvitationService.acceptInvitation(TASK_ID, HELPER_ID, taskInvoice, helper);

        assertSame(existing, result);
        assertEquals("Accepted", existing.getStatus());

        verify(taskInvoice).setStatus("assigned");
        verify(taskInvoice).setHelperid(helper);
        verify(taskInvoiceRepository, times(1)).save(taskInvoice);

        assertEquals("Rejected", otherPending.getStatus());
        assertEquals("Rejected", otherAlreadyRejected.getStatus());
        verify(taskInvitationRepository, times(1)).save(otherPending);
        verify(taskInvitationRepository, never()).save(otherAlreadyRejected);
    }


    @Test
    void getAllTasksBasedOnUserId_WhenNotHelper_ReturnsEmptyList() {
        when(helperRepository.findByUserid_Userid(42)).thenReturn(Optional.empty());

        List<TaskInvoice> result = taskInvitationService.getAllTasksBasedOnUserId(42);

        assertTrue(result.isEmpty());
        verify(taskInvitationRepository, never()).findByHelperId_HelperidAndStatus(anyInt(), any());
    }

    @Test
    void getAllTasksBasedOnUserId_WhenHelper_ReturnsMatchingInvoices() {
        when(helperRepository.findByUserid_Userid(42)).thenReturn(Optional.of(helper));

        TaskInvoice invoiceOne = mock(TaskInvoice.class);
        when(invoiceOne.getTaskid()).thenReturn(101);
        TaskInvitation pendingOne = TaskInvitation.builder().taskId(invoiceOne).helperId(helper).status(null).build();

        when(taskInvitationRepository.findByHelperId_HelperidAndStatus(HELPER_ID, null))
                .thenReturn(List.of(pendingOne));

        List<TaskInvoice> expected = List.of(invoiceOne);
        when(taskInvoiceRepository.findAllById(List.of(101))).thenReturn(expected);

        List<TaskInvoice> result = taskInvitationService.getAllTasksBasedOnUserId(42);

        assertEquals(expected, result);
    }

    @Test
    void getAllTasksBasedOnUserId_WhenPendingInvitationHasNullTaskInvoice_SkipsIt() {
        when(helperRepository.findByUserid_Userid(42)).thenReturn(Optional.of(helper));

        TaskInvitation pendingWithNoInvoice = TaskInvitation.builder().taskId(null).helperId(helper).status(null).build();
        when(taskInvitationRepository.findByHelperId_HelperidAndStatus(HELPER_ID, null))
                .thenReturn(List.of(pendingWithNoInvoice));
        when(taskInvoiceRepository.findAllById(List.of())).thenReturn(List.of());

        List<TaskInvoice> result = taskInvitationService.getAllTasksBasedOnUserId(42);

        assertTrue(result.isEmpty());
        verify(taskInvoiceRepository, times(1)).findAllById(List.of());
    }
}
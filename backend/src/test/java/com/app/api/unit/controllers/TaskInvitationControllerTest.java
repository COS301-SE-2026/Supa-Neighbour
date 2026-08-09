package com.app.api.unit.controllers;

import com.app.api.controllers.TaskInvitationController;
import com.app.api.dtos.MatchedHelperDTO;
import com.app.api.models.Helper;
import com.app.api.models.TaskInvitation;
import com.app.api.models.TaskInvoice;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.MatchingService;
import com.app.api.services.TaskInvitationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class TaskInvitationControllerTest {

    @Mock
    private TaskInvitationService taskInvitationService;

    @Mock
    private TaskInvoiceRepository taskInvoiceRepository;

    @Mock
    private HelperRepository helperRepository;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @Mock
    private MatchingService matchingService;

    @InjectMocks
    private TaskInvitationController taskInvitationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String BEARER_TOKEN = "Bearer valid-token";
    private static final String RAW_TOKEN = "valid-token";
    private static final int CALLER_ID = 42;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskInvitationController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    private Helper buildHelper(int helperId) {
        Helper helper = new Helper();
        helper.setHelperid(helperId);
        return helper;
    }

    private TaskInvoice buildOpenTaskInvoiceOwnedBy(int ownerId) {
        TaskInvoice taskInvoice = mock(TaskInvoice.class, RETURNS_DEEP_STUBS);
        when(taskInvoice.getStatus()).thenReturn("open");
        when(taskInvoice.getDependentid().getUserId().getUserid()).thenReturn(ownerId);
        return taskInvoice;
    }

    // ---------- GET /api/task-invitations ----------

    @Test
    void getAllTaskInvitations_WhenAuthorized_ReturnsList() throws Exception {

        List<TaskInvoice> tasks = Arrays.asList(mock(TaskInvoice.class));
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(taskInvitationService.getAllTasksBasedOnUserId(CALLER_ID)).thenReturn(tasks);

        mockMvc.perform(get("/api/task-invitations")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(taskInvitationService, times(1)).getAllTasksBasedOnUserId(CALLER_ID);
    }

    @Test
    void getAllTaskInvitations_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(get("/api/task-invitations")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isUnauthorized());

        verify(taskInvitationService, never()).getAllTasksBasedOnUserId(anyInt());
    }

    // ---------- GET /api/task-invitations/{id} ----------

    @Test
    void getTaskInvitationById_WhenFound_ReturnsInvitation() throws Exception {

        TaskInvitation invitation = mock(TaskInvitation.class);
        when(taskInvitationService.getInvitationById(1)).thenReturn(invitation);

        mockMvc.perform(get("/api/task-invitations/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(taskInvitationService, times(1)).getInvitationById(1);
    }

    @Test
    void getTaskInvitationById_WhenNotFound_ReturnsNotFound() throws Exception {

        when(taskInvitationService.getInvitationById(999)).thenReturn(null);

        mockMvc.perform(get("/api/task-invitations/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskInvitationService, times(1)).getInvitationById(999);
    }

    // ---------- POST /api/task-invitations ----------

    @Test
    void createTaskInvitation_WhenValid_ReturnsCreated() throws Exception {

        TaskInvitation created = mock(TaskInvitation.class);
        when(taskInvitationService.saveTaskInvitation(any(TaskInvitation.class))).thenReturn(created);

        mockMvc.perform(post("/api/task-invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isCreated());

        verify(taskInvitationService, times(1)).saveTaskInvitation(any(TaskInvitation.class));
    }

    @Test
    void createTaskInvitation_WhenServiceReturnsNull_ReturnsBadRequest() throws Exception {

        when(taskInvitationService.saveTaskInvitation(any(TaskInvitation.class))).thenReturn(null);

        mockMvc.perform(post("/api/task-invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- PUT /api/task-invitations/{id} ----------

    @Test
    void updateTaskInvitation_WhenExists_ReturnsUpdated() throws Exception {

        TaskInvitation updated = mock(TaskInvitation.class);
        when(taskInvitationService.updateTaskInvitation(eq(1), any(TaskInvitation.class))).thenReturn(updated);

        mockMvc.perform(put("/api/task-invitations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        verify(taskInvitationService, times(1)).updateTaskInvitation(eq(1), any(TaskInvitation.class));
    }

    @Test
    void updateTaskInvitation_WhenNotExists_ReturnsNotFound() throws Exception {

        when(taskInvitationService.updateTaskInvitation(eq(999), any(TaskInvitation.class))).thenReturn(null);

        mockMvc.perform(put("/api/task-invitations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTaskInvitation_WhenBodyIsNull_ReturnsBadRequest() throws Exception {

        mockMvc.perform(put("/api/task-invitations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());

        verify(taskInvitationService, never()).updateTaskInvitation(anyInt(), any(TaskInvitation.class));
    }

    // ---------- DELETE /api/task-invitations/{id} ----------

    @Test
    void deleteTaskInvitation_WhenExists_ReturnsNoContent() throws Exception {

        TaskInvitation existing = mock(TaskInvitation.class);
        when(taskInvitationService.getInvitationById(1)).thenReturn(existing);
        doNothing().when(taskInvitationService).deleteTaskInvitation(1);

        mockMvc.perform(delete("/api/task-invitations/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(taskInvitationService, times(1)).deleteTaskInvitation(1);
    }

    @Test
    void deleteTaskInvitation_WhenNotExists_ReturnsNotFound() throws Exception {

        when(taskInvitationService.getInvitationById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/task-invitations/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskInvitationService, never()).deleteTaskInvitation(anyInt());
    }

    // ---------- POST /api/task-invitations/{taskId}/invite ----------

    @Test
    void inviteHelper_WhenSuccessful_ReturnsCreated() throws Exception {

        Map<String, Integer> body = new HashMap<>();
        body.put("helperId", 7);

        TaskInvoice taskInvoice = buildOpenTaskInvoiceOwnedBy(CALLER_ID);
        Helper helper = buildHelper(7);
        TaskInvitation invitation = mock(TaskInvitation.class);

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(helperRepository.findById(7)).thenReturn(Optional.of(helper));
        when(taskInvitationService.inviteHelper(3, 7, taskInvoice, helper)).thenReturn(invitation);

        mockMvc.perform(post("/api/task-invitations/3/invite")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Invitation sent"))
                .andExpect(jsonPath("$.taskId").value(3))
                .andExpect(jsonPath("$.helperId").value(7))
                .andExpect(jsonPath("$.status").value("Invited"));

        verify(taskInvitationService, times(1)).inviteHelper(3, 7, taskInvoice, helper);
    }

    @Test
    void inviteHelper_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        Map<String, Integer> body = new HashMap<>();
        body.put("helperId", 7);

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(post("/api/task-invitations/3/invite")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));

        verify(taskInvoiceRepository, never()).findById(anyInt());
    }

    @Test
    void inviteHelper_WhenHelperIdMissing_ReturnsBadRequest() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);

        mockMvc.perform(post("/api/task-invitations/3/invite")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("helperId is required"));

        verify(taskInvoiceRepository, never()).findById(anyInt());
    }

    @Test
    void inviteHelper_WhenTaskNotFound_ReturnsNotFound() throws Exception {

        Map<String, Integer> body = new HashMap<>();
        body.put("helperId", 7);

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/task-invitations/3/invite")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task not found"));
    }

    @Test
    void inviteHelper_WhenTaskNotOpen_ReturnsConflict() throws Exception {

        Map<String, Integer> body = new HashMap<>();
        body.put("helperId", 7);

        TaskInvoice taskInvoice = mock(TaskInvoice.class, RETURNS_DEEP_STUBS);
        when(taskInvoice.getStatus()).thenReturn("closed");

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));

        mockMvc.perform(post("/api/task-invitations/3/invite")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Task is not open for invitations"));
    }

    @Test
    void inviteHelper_WhenCallerDoesNotOwnTask_ReturnsForbidden() throws Exception {

        Map<String, Integer> body = new HashMap<>();
        body.put("helperId", 7);

        TaskInvoice taskInvoice = buildOpenTaskInvoiceOwnedBy(999);

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));

        mockMvc.perform(post("/api/task-invitations/3/invite")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("You are not authorised to invite helpers to this task"));
    }

    @Test
    void inviteHelper_WhenHelperNotFound_ReturnsNotFound() throws Exception {

        Map<String, Integer> body = new HashMap<>();
        body.put("helperId", 7);

        TaskInvoice taskInvoice = buildOpenTaskInvoiceOwnedBy(CALLER_ID);

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(helperRepository.findById(7)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/task-invitations/3/invite")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Helper not found"));
    }

    @Test
    void inviteHelper_WhenAlreadyInvited_ReturnsConflict() throws Exception {

        Map<String, Integer> body = new HashMap<>();
        body.put("helperId", 7);

        TaskInvoice taskInvoice = buildOpenTaskInvoiceOwnedBy(CALLER_ID);
        Helper helper = buildHelper(7);

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(helperRepository.findById(7)).thenReturn(Optional.of(helper));
        when(taskInvitationService.inviteHelper(3, 7, taskInvoice, helper)).thenReturn(null);

        mockMvc.perform(post("/api/task-invitations/3/invite")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Helper already invited"));
    }

    // ---------- POST /api/task-invitations/{taskId}/accept ----------

    @Test
    void acceptTask_WhenSuccessful_ReturnsCreated() throws Exception {

        Helper helper = buildHelper(7);
        TaskInvoice taskInvoice = mock(TaskInvoice.class);
        when(taskInvoice.getStatus()).thenReturn("open");

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(taskInvitationService.acceptInvitation(3, 7, taskInvoice, helper)).thenReturn(mock(TaskInvitation.class));

        mockMvc.perform(post("/api/task-invitations/3/accept")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Task accepted successfully."))
                .andExpect(jsonPath("$.taskId").value(3))
                .andExpect(jsonPath("$.status").value("Accepted"));

        verify(taskInvitationService, times(1)).acceptInvitation(3, 7, taskInvoice, helper);
    }

    @Test
    void acceptTask_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(post("/api/task-invitations/3/accept")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));

        verify(helperRepository, never()).findByUserid_Userid(anyInt());
    }

    @Test
    void acceptTask_WhenCallerNotHelper_ReturnsForbidden() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/task-invitations/3/accept")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("User is not a helper"));
    }

    @Test
    void acceptTask_WhenTaskNotFound_ReturnsNotFound() throws Exception {

        Helper helper = buildHelper(7);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/task-invitations/3/accept")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task not found"));
    }

    @Test
    void acceptTask_WhenTaskNotOpen_ReturnsUnprocessableEntity() throws Exception {

        Helper helper = buildHelper(7);
        TaskInvoice taskInvoice = mock(TaskInvoice.class);
        when(taskInvoice.getStatus()).thenReturn("closed");

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));

        mockMvc.perform(post("/api/task-invitations/3/accept")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Task is not available for acceptance"));
    }

    @Test
    void acceptTask_WhenAlreadyAccepted_ReturnsConflict() throws Exception {

        Helper helper = buildHelper(7);
        TaskInvoice taskInvoice = mock(TaskInvoice.class);
        when(taskInvoice.getStatus()).thenReturn("open");

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(taskInvitationService.acceptInvitation(3, 7, taskInvoice, helper))
                .thenThrow(new IllegalStateException("CONFLICT"));

        mockMvc.perform(post("/api/task-invitations/3/accept")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("This task has already been accepted"));
    }

    @Test
    void acceptTask_WhenServiceRejectsForOtherReason_ReturnsUnprocessableEntity() throws Exception {

        Helper helper = buildHelper(7);
        TaskInvoice taskInvoice = mock(TaskInvoice.class);
        when(taskInvoice.getStatus()).thenReturn("open");

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(taskInvitationService.acceptInvitation(3, 7, taskInvoice, helper))
                .thenThrow(new IllegalStateException("OTHER"));

        mockMvc.perform(post("/api/task-invitations/3/accept")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Task is not available for acceptance"));
    }

    // ---------- POST /api/task-invitations/{taskId}/decline ----------

    @Test
    void declineTask_WhenSuccessful_ReturnsOk() throws Exception {

        Helper helper = buildHelper(7);
        TaskInvoice taskInvoice = mock(TaskInvoice.class);
        when(taskInvoice.getStatus()).thenReturn("open");
        TaskInvitation declined = mock(TaskInvitation.class);

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(taskInvitationService.declineInvitation(3, 7, taskInvoice, helper)).thenReturn(declined);

        mockMvc.perform(post("/api/task-invitations/3/decline")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task declined."))
                .andExpect(jsonPath("$.taskId").value(3))
                .andExpect(jsonPath("$.status").value("Declined"));

        verify(taskInvitationService, times(1)).declineInvitation(3, 7, taskInvoice, helper);
    }

    @Test
    void declineTask_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(post("/api/task-invitations/3/decline")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void declineTask_WhenCallerNotHelper_ReturnsForbidden() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/task-invitations/3/decline")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("User is not a helper"));
    }

    @Test
    void declineTask_WhenTaskNotFound_ReturnsNotFound() throws Exception {

        Helper helper = buildHelper(7);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/task-invitations/3/decline")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task not found"));
    }

    @Test
    void declineTask_WhenTaskNotOpen_ReturnsUnprocessableEntity() throws Exception {

        Helper helper = buildHelper(7);
        TaskInvoice taskInvoice = mock(TaskInvoice.class);
        when(taskInvoice.getStatus()).thenReturn("closed");

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));

        mockMvc.perform(post("/api/task-invitations/3/decline")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Task is not available for declining"));
    }

    @Test
    void declineTask_WhenCannotBeDeclined_ReturnsConflict() throws Exception {

        Helper helper = buildHelper(7);
        TaskInvoice taskInvoice = mock(TaskInvoice.class);
        when(taskInvoice.getStatus()).thenReturn("open");

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(taskInvitationService.declineInvitation(3, 7, taskInvoice, helper))
                .thenThrow(new IllegalStateException("CONFLICT"));

        mockMvc.perform(post("/api/task-invitations/3/decline")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Task cannot be declined in its current state"));
    }

    @Test
    void declineTask_WhenServiceRejectsForOtherReason_ReturnsUnprocessableEntity() throws Exception {

        Helper helper = buildHelper(7);
        TaskInvoice taskInvoice = mock(TaskInvoice.class);
        when(taskInvoice.getStatus()).thenReturn("open");

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(CALLER_ID);
        when(helperRepository.findByUserid_Userid(CALLER_ID)).thenReturn(Optional.of(helper));
        when(taskInvoiceRepository.findById(3)).thenReturn(Optional.of(taskInvoice));
        when(taskInvitationService.declineInvitation(3, 7, taskInvoice, helper))
                .thenThrow(new IllegalStateException("OTHER"));

        mockMvc.perform(post("/api/task-invitations/3/decline")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Task is not available for declining"));
    }

    // ---------- POST /api/task-invitations/{taskId}/match ----------

    @Test
    void matchHelpers_WhenTaskFound_ReturnsMatches() throws Exception {

        List<MatchedHelperDTO> matched = Arrays.asList(mock(MatchedHelperDTO.class), mock(MatchedHelperDTO.class));
        when(matchingService.matchHelpersForTask(3)).thenReturn(matched);

        mockMvc.perform(post("/api/task-invitations/3/match")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(3))
                .andExpect(jsonPath("$.matchedCount").value(2));

        verify(matchingService, times(1)).matchHelpersForTask(3);
    }

    @Test
    void matchHelpers_WhenTaskNotFound_ReturnsNotFound() throws Exception {

        when(matchingService.matchHelpersForTask(999)).thenReturn(null);

        mockMvc.perform(post("/api/task-invitations/999/match")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Task not found"));
    }
}
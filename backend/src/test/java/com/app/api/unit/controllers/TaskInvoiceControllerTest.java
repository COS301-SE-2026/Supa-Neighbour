package com.app.api.unit.controllers;

import com.app.api.controllers.TaskInvoiceController;
import com.app.api.models.*;
import com.app.api.services.TaskInvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskInvoiceControllerTest {

    @Mock
    private TaskInvoiceService taskInvoiceService;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @InjectMocks
    private TaskInvoiceController taskInvoiceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TaskInvoice taskInvoice;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskInvoiceController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();

        Helper helper = new Helper();
        helper.setHelperid(101);
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(3);
        
        Location location = new Location();
        location.setLocationid(10);
        
        TaskType taskType = new TaskType();
        taskType.setTasktypeid(1);
        
        Admin admin = new Admin();
        admin.setAdminid(2);
        
        Badges badge = new Badges();
        badge.setBadgeid(7);
        
        Compatibility compatibility = new Compatibility();
        compatibility.setCompadibilityid(5);

        taskInvoice = TaskInvoice.builder()
                .taskid(1)
                .helperid(helper)
                .dependentid(dependent)
                .locationid(location)
                .tasktypeid(taskType)
                .signedadminid(admin)
                .helperbadgeid(badge)
                .compatibilityid(compatibility)
                .isImmediate(true)
                .needsspecialist(false)
                .startdate(LocalDate.parse("2026-07-01"))
                .enddate(LocalDate.parse("2026-07-03"))
                .dependentRatingreview("Excellent helper, very professional")
                .helperRatingreview("Great family, easy to work with")
                .adminReview("Task completed successfully")
                .build();
    }

    
    @Test
    void getAllTaskInvoices_ListOfTaskInvoices() throws Exception {
        // Given
        List<TaskInvoice> invoices = Arrays.asList(taskInvoice);
        when(taskInvoiceService.getAllTaskInvoices()).thenReturn(invoices);

        mockMvc.perform(get("/api/taskinvoices")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].taskid").value(1))
                .andExpect(jsonPath("$[0].isImmediate").value(true))
                .andExpect(jsonPath("$[0].needsspecialist").value(false));

        verify(taskInvoiceService, times(1)).getAllTaskInvoices();
    }

    @Test
    void getAllTaskInvoices_WhenEmpty_EmptyList() throws Exception {
    
        when(taskInvoiceService.getAllTaskInvoices()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/taskinvoices")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(taskInvoiceService, times(1)).getAllTaskInvoices();
    }

    
    @Test
    void getTaskInvoiceById_ReturnTaskInvoice() throws Exception {
        // Given
        when(taskInvoiceService.getTaskInvoiceById(1)).thenReturn(taskInvoice);

        mockMvc.perform(get("/api/taskinvoices/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskid").value(1))
                .andExpect(jsonPath("$.isImmediate").value(true))
                .andExpect(jsonPath("$.dependentRatingreview").value("Excellent helper, very professional"));

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(1);
    }

    @Test
    void getTaskInvoiceById_WhenNotExisting_NotFound() throws Exception {
        // Given
        when(taskInvoiceService.getTaskInvoiceById(999)).thenReturn(null);

        mockMvc.perform(get("/api/taskinvoices/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(999);
    }

    @Test
    void getTaskInvoiceById_WithInvalidId() throws Exception {
        // Given
        when(taskInvoiceService.getTaskInvoiceById(-1)).thenReturn(null);

        mockMvc.perform(get("/api/taskinvoices/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(-1);
    }
    
    @Test
    void createTaskInvoice_CreatedTaskInvoice() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(anyString()))
        .thenReturn(1);

        when(taskInvoiceService.saveTaskInvoice(anyInt(), any(TaskInvoice.class)))
        .thenReturn(taskInvoice);

        mockMvc.perform(post("/api/taskinvoices")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskInvoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskid").value(1))
                .andExpect(jsonPath("$.isImmediate").value(true))
                .andExpect(jsonPath("$.needsspecialist").value(false));

        verify(taskInvoiceService, times(1))
        .saveTaskInvoice(anyInt(), any(TaskInvoice.class));
    }

    @Test
    void createTaskInvoice_WithAllFields() throws Exception {
        // Given
        Helper helper = new Helper();
        helper.setHelperid(101);
        
        Dependent dependent = new Dependent();
        dependent.setDependentId(3);
        
        Location location = new Location();
        location.setLocationid(10);
        
        TaskType taskType = new TaskType();
        taskType.setTasktypeid(1);
        
        Admin admin = new Admin();
        admin.setAdminid(2);
        
        Badges badge = new Badges();
        badge.setBadgeid(7);
        
        Compatibility compatibility = new Compatibility();
        compatibility.setCompadibilityid(5);

        TaskInvoice newInvoice = TaskInvoice.builder()
                .helperid(helper)
                .dependentid(dependent)
                .locationid(location)
                .tasktypeid(taskType)
                .signedadminid(admin)
                .helperbadgeid(badge)
                .compatibilityid(compatibility)
                .isImmediate(true)
                .needsspecialist(false)
                .startdate(LocalDate.parse("2026-07-01"))
                .enddate(LocalDate.parse("2026-07-03"))
                .dependentRatingreview("Test review")
                .helperRatingreview("Test helper review")
                .adminReview("Test admin review")
                .build();

        TaskInvoice savedInvoice = TaskInvoice.builder()
                .taskid(1)
                .helperid(helper)
                .dependentid(dependent)
                .locationid(location)
                .tasktypeid(taskType)
                .signedadminid(admin)
                .helperbadgeid(badge)
                .compatibilityid(compatibility)
                .isImmediate(true)
                .needsspecialist(false)
                .startdate(LocalDate.parse("2026-07-01"))
                .enddate(LocalDate.parse("2026-07-03"))
                .dependentRatingreview("Test review")
                .helperRatingreview("Test helper review")
                .adminReview("Test admin review")
                .build();

        when(firebaseAuthService.getUserIdFromToken(anyString()))
        .thenReturn(1);

        when(taskInvoiceService.saveTaskInvoice(anyInt(), any(TaskInvoice.class)))
        .thenReturn(taskInvoice);

        mockMvc.perform(post("/api/taskinvoices")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newInvoice)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskid").value(1))
                .andExpect(jsonPath("$.isImmediate").value(true));

        verify(taskInvoiceService, times(1)) .saveTaskInvoice(anyInt(), any(TaskInvoice.class));
    }

    @Test
    void createTaskInvoice_WithNullObject() throws Exception {        
        mockMvc.perform(post("/api/taskinvoices")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());

        verify(taskInvoiceService, never()).updateTaskInvoice(anyInt(), any(TaskInvoice.class));
    }
    
    @Test
    void updateTaskInvoice_WhenExists() throws Exception {
        
        Helper updatedHelper = new Helper();
        updatedHelper.setHelperid(202);

        TaskInvoice updatedInvoice = TaskInvoice.builder()
                .taskid(1)
                .helperid(updatedHelper)
                .isImmediate(false)
                .adminReview("Approved by admin")
                .build();

        when(taskInvoiceService.getTaskInvoiceById(1)).thenReturn(taskInvoice);
        when(taskInvoiceService.updateTaskInvoice(eq(1), any(TaskInvoice.class))).thenReturn(updatedInvoice);

        mockMvc.perform(put("/api/taskinvoices/1")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInvoice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskid").value(1))
                .andExpect(jsonPath("$.isImmediate").value(false))
                .andExpect(jsonPath("$.adminReview").value("Approved by admin"));

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(1);
        verify(taskInvoiceService, times(1)).updateTaskInvoice(eq(1), any(TaskInvoice.class));
    }

    @Test
    void updateTaskInvoice_WhenNotExists() throws Exception {
        // Given
        when(taskInvoiceService.getTaskInvoiceById(999)).thenReturn(null);

        mockMvc.perform(put("/api/taskinvoices/999")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskInvoice)))
                .andExpect(status().isNotFound());

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(999);
        verify(taskInvoiceService, never()).updateTaskInvoice(anyInt(), any(TaskInvoice.class));
    }

    @Test
    void updateTaskInvoice_WithPartialData() throws Exception {
        // Given
        TaskInvoice partialUpdate = TaskInvoice.builder()
                .adminReview("Rejected - needs more information")
                .dependentRatingreview("Good but could be better")
                .build();

        Helper existingHelper = new Helper();
        existingHelper.setHelperid(101);

        TaskInvoice updatedInvoice = TaskInvoice.builder()
                .taskid(1)
                .helperid(existingHelper)
                .isImmediate(true)
                .adminReview("Rejected - needs more information")
                .dependentRatingreview("Good but could be better")
                .build();

        when(taskInvoiceService.getTaskInvoiceById(1)).thenReturn(taskInvoice);
        when(taskInvoiceService.updateTaskInvoice(eq(1), any(TaskInvoice.class))).thenReturn(updatedInvoice);

        mockMvc.perform(put("/api/taskinvoices/1")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adminReview").value("Rejected - needs more information"))
                .andExpect(jsonPath("$.dependentRatingreview").value("Good but could be better"));

        verify(taskInvoiceService, times(1)).updateTaskInvoice(eq(1), any(TaskInvoice.class));
    }
    
    @Test
    void deleteTaskInvoice_WhenExisting() throws Exception {
        // Given
        when(taskInvoiceService.getTaskInvoiceById(1)).thenReturn(taskInvoice);
        doNothing().when(taskInvoiceService).deleteTaskInvoice(1);

        mockMvc.perform(delete("/api/taskinvoices/1"))
                .andExpect(status().isNoContent());

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(1);
        verify(taskInvoiceService, times(1)).deleteTaskInvoice(1);
    }

    @Test
    void deleteTaskInvoice_WhenNotExisting() throws Exception {
        // Given
        when(taskInvoiceService.getTaskInvoiceById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/taskinvoices/999"))
                .andExpect(status().isNotFound());

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(999);
        verify(taskInvoiceService, never()).deleteTaskInvoice(anyInt());
    }

    @Test
    void deleteTaskInvoice_MultipleTimesRequests() throws Exception {
        // Given
        when(taskInvoiceService.getTaskInvoiceById(1))
                .thenReturn(taskInvoice)
                .thenReturn(null);

        mockMvc.perform(delete("/api/taskinvoices/1"))
                .andExpect(status().isNoContent());

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(1);
        verify(taskInvoiceService, times(1)).deleteTaskInvoice(1);

        mockMvc.perform(delete("/api/taskinvoices/1"))
                .andExpect(status().isNotFound());

        verify(taskInvoiceService, times(2)).getTaskInvoiceById(1);
        verify(taskInvoiceService, times(1)).deleteTaskInvoice(1);
    }

    
    @Test
    void getAllTaskInvoices_WhenServiceThrowsException() throws Exception {
        // Given
        when(taskInvoiceService.getAllTaskInvoices()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/taskinvoices")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(taskInvoiceService, times(1)).getAllTaskInvoices();
    }

    @Test
    void getTaskInvoiceById_WithLargeId() throws Exception {
        // Given
        int largeId = Integer.MAX_VALUE;
        when(taskInvoiceService.getTaskInvoiceById(largeId)).thenReturn(null);

        mockMvc.perform(get("/api/taskinvoices/" + largeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskInvoiceService, times(1)).getTaskInvoiceById(largeId);
    }

    @Test
    void updateTaskInvoice_WithDateFields() throws Exception {
        // Given
        LocalDate newStartDate = LocalDate.parse("2026-08-01");
        LocalDate newEndDate = LocalDate.parse("2026-08-05");

        System.out.println(newStartDate.toString());
        System.out.println(newEndDate.toString());
        
        TaskInvoice dateUpdate = TaskInvoice.builder()
                .startdate(newStartDate)
                .enddate(newEndDate)
                .build();

        System.out.println(newStartDate);
        System.out.println(newEndDate);
        TaskInvoice updatedInvoice = TaskInvoice.builder()
                .taskid(1)
                .startdate(newStartDate)
                .enddate(newEndDate)
                .build();
        System.out.println(updatedInvoice.getStartdate());
        System.out.println(updatedInvoice.getEnddate());
        System.out.println(objectMapper.writeValueAsString(updatedInvoice));

        when(taskInvoiceService.getTaskInvoiceById(1)).thenReturn(taskInvoice);
        when(taskInvoiceService.updateTaskInvoice(eq(1), any(TaskInvoice.class))).thenReturn(updatedInvoice);

        mockMvc.perform(put("/api/taskinvoices/1")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dateUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startdate").value("2026-08-01"))
                .andExpect(jsonPath("$.enddate").value("2026-08-05"));

        verify(taskInvoiceService, times(1)).updateTaskInvoice(eq(1), any(TaskInvoice.class));
    }

    @Test
    void createTaskInvoice_WhenServiceReturnsNull_BadRequest() throws Exception{
        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);

        when(taskInvoiceService.saveTaskInvoice(
                anyInt(), any(TaskInvoice.class)
        )).thenReturn(null);

        mockMvc.perform(post("/api/taskinvoices")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskInvoice)))
                .andExpect(status().isBadRequest());
        verify(firebaseAuthService, times(1)).getUserIdFromToken(anyString());

        verify(taskInvoiceService, times(1)).saveTaskInvoice(anyInt(), any(TaskInvoice.class));
    }

    @Test
    void createTaskInvoice_WithInvalidFirebaseToken_Unauthorized() throws Exception{
        FirebaseAuthException firebaseAuthException = mock(FirebaseAuthException.class);

        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(firebaseAuthException);

        mockMvc.perform(post("/api/taskinvoices")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(taskInvoice)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid or expired Firebase token"));

        verify(firebaseAuthService, times(1)).getUserIdFromToken(anyString());

        verify(taskInvoiceService, never()).saveTaskInvoice(anyInt(), any(TaskInvoice.class));
    }
}

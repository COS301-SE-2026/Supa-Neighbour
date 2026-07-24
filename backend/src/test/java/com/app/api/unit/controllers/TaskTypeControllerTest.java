package com.app.api.unit.controllers;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.app.api.controllers.TaskTypeController;
import com.app.api.models.Badges;
import com.app.api.models.TaskType;
import com.app.api.services.TaskTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TaskTypeControllerTest {

    @Mock
    private TaskTypeService taskTypeService;

    @InjectMocks
    private TaskTypeController taskTypeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TaskType taskType;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskTypeController).build();
        objectMapper = new ObjectMapper();

        Badges badge = new Badges();
        badge.setBadgeid(1);
        badge.setBadgeName("Gold Helper");
        badge.setXpReward(100);

        taskType = TaskType.builder()
                .tasktypeid(1)
                .badgeid(badge)
                .description("Cleaning Service")
                .needsSpecialist(false)
                .xpWorth(50)
                .build();
    }

    @Test
    void getAllTaskTypes_ShouldReturnListOfTaskTypes() throws Exception {
        List<TaskType> taskTypes = Arrays.asList(taskType);
        when(taskTypeService.getAllTaskTypes()).thenReturn(taskTypes);

        mockMvc.perform(get("/api/tasktypes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tasktypeid").value(1))
                .andExpect(jsonPath("$[0].description").value("Cleaning Service"))
                .andExpect(jsonPath("$[0].needsSpecialist").value(false))
                .andExpect(jsonPath("$[0].xpWorth").value(50));

        verify(taskTypeService, times(1)).getAllTaskTypes();
    }

    @Test
    void getAllTaskTypes_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(taskTypeService.getAllTaskTypes()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tasktypes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(taskTypeService, times(1)).getAllTaskTypes();
    }

    @Test
    void getTaskTypeById_WhenExists_ShouldReturnTaskType() throws Exception {
        when(taskTypeService.getTaskTypeById(1)).thenReturn(taskType);

        mockMvc.perform(get("/api/tasktypes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasktypeid").value(1))
                .andExpect(jsonPath("$.description").value("Cleaning Service"))
                .andExpect(jsonPath("$.needsSpecialist").value(false))
                .andExpect(jsonPath("$.xpWorth").value(50));

        verify(taskTypeService, times(1)).getTaskTypeById(1);
    }

    @Test
    void getTaskTypeById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(taskTypeService.getTaskTypeById(999)).thenReturn(null);

        mockMvc.perform(get("/api/tasktypes/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskTypeService, times(1)).getTaskTypeById(999);
    }

    @Test
    void getTaskTypeById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(taskTypeService.getTaskTypeById(-1)).thenReturn(null);

        mockMvc.perform(get("/api/tasktypes/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskTypeService, times(1)).getTaskTypeById(-1);
    }

    @Test
    void createTaskType_ShouldReturnCreatedTaskType() throws Exception {
        when(taskTypeService.saveTaskType(any(TaskType.class))).thenReturn(taskType);

        mockMvc.perform(post("/api/tasktypes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tasktypeid").value(1))
                .andExpect(jsonPath("$.description").value("Cleaning Service"))
                .andExpect(jsonPath("$.needsSpecialist").value(false))
                .andExpect(jsonPath("$.xpWorth").value(50));

        verify(taskTypeService, times(1)).saveTaskType(any(TaskType.class));
    }

    @Test
    void createTaskType_WithAllFields_ShouldPersistCorrectly() throws Exception {
        Badges badge = new Badges();
        badge.setBadgeid(2);
        badge.setBadgeName("Silver Helper");
        badge.setXpReward(75);

        TaskType newTaskType = TaskType.builder()
                .badgeid(badge)
                .description("Gardening Service")
                .needsSpecialist(true)
                .xpWorth(75)
                .build();

        TaskType savedTaskType = TaskType.builder()
                .tasktypeid(2)
                .badgeid(badge)
                .description("Gardening Service")
                .needsSpecialist(true)
                .xpWorth(75)
                .build();

        when(taskTypeService.saveTaskType(any(TaskType.class))).thenReturn(savedTaskType);

        mockMvc.perform(post("/api/tasktypes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTaskType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tasktypeid").value(2))
                .andExpect(jsonPath("$.description").value("Gardening Service"))
                .andExpect(jsonPath("$.needsSpecialist").value(true))
                .andExpect(jsonPath("$.xpWorth").value(75));

        verify(taskTypeService, times(1)).saveTaskType(any(TaskType.class));
    }

    @Test
    void createTaskType_WithNullObject_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/tasktypes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest());

        verify(taskTypeService, never()).saveTaskType(any(TaskType.class));
    }

    @Test
    void updateTaskType_WhenExists_ShouldReturnUpdatedTaskType() throws Exception {
        Badges updatedBadge = new Badges();
        updatedBadge.setBadgeid(3);
        updatedBadge.setBadgeName("Platinum Helper");
        updatedBadge.setXpReward(200);

        TaskType updatedTaskType = TaskType.builder()
                .tasktypeid(1)
                .badgeid(updatedBadge)
                .description("Premium Cleaning Service")
                .needsSpecialist(true)
                .xpWorth(100)
                .build();

        when(taskTypeService.getTaskTypeById(1)).thenReturn(taskType);
        when(taskTypeService.updateTaskType(eq(1), any(TaskType.class))).thenReturn(updatedTaskType);

        mockMvc.perform(put("/api/tasktypes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTaskType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasktypeid").value(1))
                .andExpect(jsonPath("$.description").value("Premium Cleaning Service"))
                .andExpect(jsonPath("$.needsSpecialist").value(true))
                .andExpect(jsonPath("$.xpWorth").value(100));

        verify(taskTypeService, times(1)).getTaskTypeById(1);
        verify(taskTypeService, times(1)).updateTaskType(eq(1), any(TaskType.class));
    }

    @Test
    void updateTaskType_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(taskTypeService.getTaskTypeById(999)).thenReturn(null);

        mockMvc.perform(put("/api/tasktypes/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskType)))
                .andExpect(status().isNotFound());

        verify(taskTypeService, times(1)).getTaskTypeById(999);
        verify(taskTypeService, never()).updateTaskType(anyInt(), any(TaskType.class));
    }

    @Test
    void updateTaskType_WithPartialData_ShouldUpdateOnlyProvidedFields() throws Exception {
        TaskType partialUpdate = TaskType.builder()
                .description("Updated Description")
                .xpWorth(150)
                .build();

        Badges existingBadge = new Badges();
        existingBadge.setBadgeid(1);

        TaskType updatedTaskType = TaskType.builder()
                .tasktypeid(1)
                .badgeid(existingBadge)
                .description("Updated Description")
                .needsSpecialist(false)
                .xpWorth(150)
                .build();

        when(taskTypeService.getTaskTypeById(1)).thenReturn(taskType);
        when(taskTypeService.updateTaskType(eq(1), any(TaskType.class))).thenReturn(updatedTaskType);

        mockMvc.perform(put("/api/tasktypes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.xpWorth").value(150));

        verify(taskTypeService, times(1)).updateTaskType(eq(1), any(TaskType.class));
    }

    @Test
    void deleteTaskType_WhenExists_ShouldReturnNoContent() throws Exception {
        when(taskTypeService.getTaskTypeById(1)).thenReturn(taskType);
        doNothing().when(taskTypeService).deleteTaskType(1);

        mockMvc.perform(delete("/api/tasktypes/1"))
                .andExpect(status().isNoContent());

        verify(taskTypeService, times(1)).getTaskTypeById(1);
        verify(taskTypeService, times(1)).deleteTaskType(1);
    }

    @Test
    void deleteTaskType_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(taskTypeService.getTaskTypeById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/tasktypes/999"))
                .andExpect(status().isNotFound());

        verify(taskTypeService, times(1)).getTaskTypeById(999);
        verify(taskTypeService, never()).deleteTaskType(anyInt());
    }

    @Test
    void deleteTaskType_MultipleTimes_ShouldHandleIdempotently() throws Exception {
        when(taskTypeService.getTaskTypeById(1))
                .thenReturn(taskType)
                .thenReturn(null);

        mockMvc.perform(delete("/api/tasktypes/1"))
                .andExpect(status().isNoContent());

        verify(taskTypeService, times(1)).getTaskTypeById(1);
        verify(taskTypeService, times(1)).deleteTaskType(1);

        mockMvc.perform(delete("/api/tasktypes/1"))
                .andExpect(status().isNotFound());

        verify(taskTypeService, times(2)).getTaskTypeById(1);
        verify(taskTypeService, times(1)).deleteTaskType(1);
    }

    @Test
    void getTaskTypeById_WithLargeId_ShouldHandleCorrectly() throws Exception {
        int largeId = Integer.MAX_VALUE;
        when(taskTypeService.getTaskTypeById(largeId)).thenReturn(null);

        mockMvc.perform(get("/api/tasktypes/" + largeId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(taskTypeService, times(1)).getTaskTypeById(largeId);
    }

    @Test
    void createTaskType_WithSpecialistRequired_ShouldPersistCorrectly() throws Exception {
        Badges specialistBadge = new Badges();
        specialistBadge.setBadgeid(5);
        specialistBadge.setBadgeName("Specialist");
        specialistBadge.setXpReward(150);

        TaskType specialistTask = TaskType.builder()
                .badgeid(specialistBadge)
                .description("Electrical Work")
                .needsSpecialist(true)
                .xpWorth(200)
                .build();

        TaskType savedTask = TaskType.builder()
                .tasktypeid(3)
                .badgeid(specialistBadge)
                .description("Electrical Work")
                .needsSpecialist(true)
                .xpWorth(200)
                .build();

        when(taskTypeService.saveTaskType(any(TaskType.class))).thenReturn(savedTask);

        mockMvc.perform(post("/api/tasktypes")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialistTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tasktypeid").value(3))
                .andExpect(jsonPath("$.needsSpecialist").value(true))
                .andExpect(jsonPath("$.xpWorth").value(200));

        verify(taskTypeService, times(1)).saveTaskType(any(TaskType.class));
    }
}
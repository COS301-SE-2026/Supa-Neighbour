package com.app.api.unit.controllers;

import com.app.api.controllers.DependentController;
import com.app.api.models.Dependent;
import com.app.api.models.TaskType;
import com.app.api.models.User;
import com.app.api.services.DependentService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DependentControllerTest {

    @Mock
    private DependentService dependentService;

    @InjectMocks
    private DependentController dependentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Dependent dependent;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dependentController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();

        User user = new User();

        TaskType taskType = new TaskType();
        taskType.setTasktypeid(1);

        dependent = new Dependent();
        dependent.setDependentId(1);
        dependent.setUserId(user);
        dependent.setTaskTypeId(taskType);
    }

    @Test
    void getAllDependents_ListOfDependents() throws Exception {

        List<Dependent> dependents = Arrays.asList(dependent);
        when(dependentService.getAllDependents()).thenReturn(dependents);

        mockMvc.perform(get("/api/dependents")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dependentId").value(1));

        verify(dependentService, times(1)).getAllDependents();
    }

    @Test
    void getAllDependents_WhenEmpty_EmptyList() throws Exception {

        when(dependentService.getAllDependents()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/dependents")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(dependentService, times(1)).getAllDependents();
    }

    @Test
    void getDependentById_ReturnDependent() throws Exception {

        when(dependentService.getDependentById(1)).thenReturn(dependent);

        mockMvc.perform(get("/api/dependents/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dependentId").value(1));

        verify(dependentService, times(1)).getDependentById(1);
    }

    @Test
    void getDependentById_WhenNotExisting_NotFound() throws Exception {

        when(dependentService.getDependentById(999)).thenReturn(null);

        mockMvc.perform(get("/api/dependents/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(dependentService, times(1)).getDependentById(999);
    }

    @Test
    void createDependent_CreatedDependent() throws Exception {

        when(dependentService.saveDependent(any(Dependent.class))).thenReturn(dependent);

        mockMvc.perform(post("/api/dependents")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dependent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dependentId").value(1));

        verify(dependentService, times(1)).saveDependent(any(Dependent.class));
    }

    @Test
    void updateDependent_WhenExists() throws Exception {

        TaskType newTaskType = new TaskType();
        newTaskType.setTasktypeid(2);

        Dependent updatedDependent = new Dependent();
        updatedDependent.setDependentId(1);
        updatedDependent.setTaskTypeId(newTaskType);

        when(dependentService.getDependentById(1)).thenReturn(dependent);
        when(dependentService.updateDependent(eq(1), any(Dependent.class))).thenReturn(updatedDependent);

        mockMvc.perform(put("/api/dependents/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDependent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dependentId").value(1))
                .andExpect(jsonPath("$.taskTypeId.tasktypeid").value(2));

        verify(dependentService, times(1)).getDependentById(1);
        verify(dependentService, times(1)).updateDependent(eq(1), any(Dependent.class));
    }

    @Test
    void updateDependent_WhenNotExists() throws Exception {

        when(dependentService.getDependentById(999)).thenReturn(null);

        mockMvc.perform(put("/api/dependents/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dependent)))
                .andExpect(status().isNotFound());

        verify(dependentService, times(1)).getDependentById(999);
        verify(dependentService, never()).updateDependent(anyInt(), any(Dependent.class));
    }

    @Test
    void deleteDependent_WhenExisting() throws Exception {

        when(dependentService.getDependentById(1)).thenReturn(dependent);
        doNothing().when(dependentService).deleteDependent(1);

        mockMvc.perform(delete("/api/dependents/1"))
                .andExpect(status().isNoContent());

        verify(dependentService, times(1)).getDependentById(1);
        verify(dependentService, times(1)).deleteDependent(1);
    }

    @Test
    void deleteDependent_WhenNotExisting() throws Exception {

        when(dependentService.getDependentById(999)).thenReturn(null);

        mockMvc.perform(delete("/api/dependents/999"))
                .andExpect(status().isNotFound());

        verify(dependentService, times(1)).getDependentById(999);
        verify(dependentService, never()).deleteDependent(anyInt());
    }
}
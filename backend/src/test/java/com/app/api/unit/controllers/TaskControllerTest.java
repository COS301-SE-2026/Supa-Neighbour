package com.app.api.unit.controllers;

import com.app.api.controllers.TaskController; 
import com.app.api.dtos.TaskDetailDTO;
import com.app.api.models.Task; 
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.TaskService; 
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
import com.fasterxml.jackson.databind.ObjectMapper; 
import java.util.List;

import static org.mockito.ArgumentMatchers.any; 
import static org.mockito.Mockito.never; 
import static org.mockito.Mockito.verify; 
import static org.mockito.Mockito.when; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; 
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    
    @Mock
    private TaskService taskService;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @InjectMocks
    private TaskController taskController;

    private Task task;
    private TaskDetailDTO taskDetail;

    private MockMvc mockMvc; 
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
        task = new Task();
        task.setTaskId(1);
        task.setHelperId(10);
        task.setDependentId(20);
        task.setLocationId(30);
        task.setTaskTypeId(40);
        task.setStatus("open");

        taskDetail = new TaskDetailDTO();
        taskDetail.setTaskId(1);
        taskDetail.setHelperId(10);
        taskDetail.setDependentId(20);
        taskDetail.setStatus("open");
    }

    @Test
     void getTaskById_whenTaskExists_returns200() throws Exception{
        when(taskService.getTaskDetailById(1)).thenReturn(taskDetail);

        mockMvc.perform(get("/tasks/1")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.taskId").value(1))
        .andExpect(jsonPath("$.helperId").value(10))
        .andExpect(jsonPath("$.dependentId").value(20))
        .andExpect(jsonPath("$.status").value("open"));

        verify(taskService).getTaskDetailById(1);
    }

    @Test
    void getTaskById_whenTaskDoesNoTexist_returns404() throws Exception{
        when(taskService.getTaskDetailById(999)).thenReturn(null);
        
        mockMvc.perform(get("/tasks/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        verify(taskService).getTaskDetailById(999);
    }

    @Test
    void getAllTasks_withValidToken_ShouldReturn200() throws Exception{
        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(5);

        when(taskService.getAllTaskDetailsByUserId(5)).thenReturn(List.of(taskDetail));
        mockMvc.perform(get("/tasks")
        .header("Authorization", "Bearer valid-token")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].taskId").value(1))
        .andExpect(jsonPath("$[0].helperId").value(10))
        .andExpect(jsonPath("$[0].dependentId").value(20));

        verify(firebaseAuthService).getUserIdFromToken("valid-token");

        verify(taskService).getAllTaskDetailsByUserId(5);
    }

    @Test
    void getAllTasks_WIthInvalidToken_ShouldReturn401() throws Exception{
        FirebaseAuthException firebaseAuthException = org.mockito.Mockito.mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken("invalid-token")).thenThrow(firebaseAuthException);


        mockMvc.perform(get("/tasks")
        .header("Authorization", "Bearer invalid-token")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("Invalid or expired Firebase token"));

        verify(firebaseAuthService).getUserIdFromToken("invalid-token");
        verify(taskService, never()).getAllTaskDetailsByUserId(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void deleteTask_WhenTaksDoesnNotExist_ShouldReturn404() throws Exception{
        when(taskService.deleteTask(999)).thenReturn(false);

        mockMvc.perform(delete("/tasks/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        verify(taskService).deleteTask(999);
    }

    @Test
    void updateTask_WhenTaskExists_ShouldReturn200() throws Exception{

        Task updated = new Task();
        updated.setStatus("completed");
        updated.setHelperId(15);

        Task updatedTask = new Task();
        updatedTask.setTaskId(1);
        updatedTask.setHelperId(15);
        updatedTask.setStatus("completed");

        when(taskService.updateTask(org.mockito.ArgumentMatchers.eq(1), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/1")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.taskId").value(1))
        .andExpect(jsonPath("$.status").value("completed"))
        .andExpect(jsonPath("$.helperId").value(15));

        verify(taskService).updateTask(org.mockito.ArgumentMatchers.eq(1), any(Task.class));
    }

    @Test
    void updateTask_WhenTaskDoesNotExist_SHouldReturn404() throws Exception{
        Task updated = new Task();
        updated.setStatus("completed");

        
        when(taskService.updateTask(org.mockito.ArgumentMatchers.eq(999), any(Task.class))).thenReturn(null);

        mockMvc.perform(put("/tasks/999")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isNotFound());


        verify(taskService).updateTask(org.mockito.ArgumentMatchers.eq(999), any(Task.class));
    }

    @Test
    void getTaskByUserId_WhenTaskExists_ShouldReturn200() throws Exception{
        
        when(taskService.getTaskDetailsByUserId(5)).thenReturn(List.of(taskDetail));

        mockMvc.perform(get("/users/5/tasks")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].taskId").value(1))
        .andExpect(jsonPath("$[0].status").value("open"));

        verify(taskService).getTaskDetailsByUserId(5);
    }

    @Test
    void getTasksByUserId_WhenNoTasksExist_ShouldReturn404() throws Exception{
        when(taskService.getTaskDetailsByUserId(999)).thenReturn(null);

        mockMvc.perform(get("/users/999/tasks")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        verify(taskService).getTaskDetailsByUserId(999);
    }

    @Test
    void createTask_ShouldReturn201() throws Exception{
        Task createdTask = new Task();
        createdTask.setTaskId(100);
        createdTask.setStatus("open");
        createdTask.setHelperId(10);
        createdTask.setDependentId(20);

        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/tasks/create")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(task)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.taskId").value(100))
        .andExpect(jsonPath("$.status").value("open"))
        .andExpect(jsonPath("$.helperId").value(10))
        .andExpect(jsonPath("$.dependentId").value(20));

        verify(taskService).createTask(any(Task.class));
    }
}

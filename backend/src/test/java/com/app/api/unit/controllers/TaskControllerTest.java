package com.app.api.unit.controllers;

import com.app.api.controllers.TaskController;
import com.app.api.models.Task;
import com.app.api.services.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(TaskController.class)
public class TaskControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser
    void getTaskById_success() throws Exception
    {
        Task task = new Task();
        task.setTaskId(1001);
        when(taskService.getTaskById(1001)).thenReturn(task);

        ResultActions result = mockMvc.perform(get("/tasks/1001"));

        result.andExpect(status().isOk()).andExpect(jsonPath("$.taskId").value(1001));
    }


    @Test
    @WithMockUser
    void getTaskById_fail() throws Exception
    {
        when(taskService.getTaskById(99)).thenReturn(null);

        ResultActions result = mockMvc.perform(get("/tasks/99"));

        result.andExpect(status().isNotFound());
    }




    @Test
    @WithMockUser
    void getAllTasks_success() throws Exception
    {
        Task task = new Task();
        task.setTaskId(1001);
        when(taskService.getAllTasks()).thenReturn(List.of(task));

        ResultActions result = mockMvc.perform(get("/tasks"));

        result.andExpect(status().isOk());
    }




    @Test
    @WithMockUser
    void createTask_success() throws Exception
    {
        Task task = new Task();
        task.setTaskId(2001);
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        ResultActions result = mockMvc.perform(post("/tasks/create").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        result.andExpect(status().isCreated());
    }




    @Test
    @WithMockUser
    void updateTask_success() throws Exception 
    {
        Task task = new Task();
        task.setTaskId(1001);
        task.setAdminReview("Updated");
        when(taskService.updateTask(eq(1001), any(Task.class))).thenReturn(task);

        ResultActions result = mockMvc.perform(put("/tasks/1001").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)));

        result.andExpect(status().isOk()).andExpect(jsonPath("$.taskId").value(1001));
    }


    @Test
    @WithMockUser
    void updateTask_fail() throws Exception
    {
        when(taskService.updateTask(eq(99), any(Task.class))).thenReturn(null);

        ResultActions result = mockMvc.perform(put("/tasks/99").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Task())));

        result.andExpect(status().isNotFound());
    }




    @Test
    @WithMockUser
    void deleteTask_success() throws Exception
    {
        when(taskService.deleteTask(1001)).thenReturn(true);

        ResultActions result = mockMvc.perform(delete("/tasks/1001").with(csrf()));

        result.andExpect(status().isOk());
    }


    @Test
    @WithMockUser
    void deleteTask_fail() throws Exception
    {
        when(taskService.deleteTask(99)).thenReturn(false);

        ResultActions result = mockMvc.perform(delete("/tasks/99").with(csrf()));

        result.andExpect(status().isNotFound());
    }




    @Test
    @WithMockUser
    void getTasksByUser_success() throws Exception
    {
        Task task = new Task();
        task.setTaskId(1001);
        when(taskService.getTasksByUserId(103)).thenReturn(List.of(task));

        ResultActions result = mockMvc.perform(get("/users/103/tasks"));

        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getTasksByUser_fail() throws Exception
    {
        when(taskService.getTasksByUserId(99)).thenReturn(null);

        ResultActions result = mockMvc.perform(get("/users/99/tasks"));

        result.andExpect(status().isNotFound());
    }

}

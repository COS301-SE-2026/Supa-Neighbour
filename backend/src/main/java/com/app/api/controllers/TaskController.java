package com.app.api.controllers;

import com.app.api.models.Task;
import com.app.api.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController
{
    // 1. dependencies

    private final TaskService taskService;

    // 2. constructor

    @Autowired
    public TaskController(TaskService taskService)
    {
        this.taskService = taskService;
    }

    // 3.

    @Operation(summary = "Get a task by ID")
    @ApiResponse(responseCode = "200" , description = " Task found")
    @ApiResponse(responseCode = "404" , description = " Task not found")
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable int taskId)
    {
        Task task = taskService.getTaskById(taskId);

        if(task == null)
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }


    @Operation(summary = "Geta all tasks")
    @ApiResponse(responseCode = "200" , description = "Tasks retrieved")
    @ApiResponse(responseCode = "404" , description = "Unauthorised")
    @GetMapping("/tasks")
    public ResponseEntity<Iterable<Task>> getAllTasks()
    {
        return ResponseEntity.ok(taskService.getAllTasks());
    }


    @Operation(summary = "Delete a task by ID")
    @ApiResponse(responseCode = "200" , description = "Task deleted")
    @ApiResponse(responseCode = "404" , description = "Task not found")
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable int taskId)
    {
        boolean deleted = taskService.deleteTask(taskId);

        if(!deleted)
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Task deleted");
    }

}
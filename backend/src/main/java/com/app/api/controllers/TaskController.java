package com.app.api.controllers;

import com.app.api.models.Task;
import com.app.api.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for task-related endpoints.
 */
@RestController
public class TaskController {

    /** The task service. */
    private final TaskService taskService;

    /**
     * Constructs a TaskController with the given TaskService.
     * @param taskService the task service
     */
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get a task by its ID.
     * @param taskId the ID of the task
     * @return the task if found, 404 otherwise
     */
    @Operation(summary = "Get a task by ID")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable int taskId) {
        Task task = taskService.getTaskById(taskId);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    /**
     * Get all tasks.
     * @return all tasks
     */
    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved")
    @ApiResponse(responseCode = "404", description = "Unauthorised")
    @GetMapping("/tasks")
    public ResponseEntity<Iterable<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    /**
     * Delete a task by its ID.
     * @param taskId the ID of the task to delete
     * @return 200 if deleted, 404 if not found
     */
    @Operation(summary = "Delete a task by ID")
    @ApiResponse(responseCode = "200", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable int taskId) {
        boolean deleted = taskService.deleteTask(taskId);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Task deleted");
    }

    /**
     * Update a task by its ID.
     * @param taskId the ID of the task to update
     * @param updates the task object containing updated values
     * @return the updated task, or 404 if not found
     */
    @Operation(summary = "Update task by ID")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable int taskId, @RequestBody Task updates) {
        Task updatedTask = taskService.updateTask(taskId, updates);

        if (updatedTask == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Get all tasks for a specific user.
     * @param userId the ID of the user
     * @return tasks linked to the user's dependent profile, or 404 if not found
     */
    @Operation(summary = "Get all tasks for a specific user")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved")
    @ApiResponse(responseCode = "404", description = "No dependent profile found for user")
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<Iterable<Task>> getTasksByUserId(@PathVariable int userId) {
        Iterable<Task> tasks = taskService.getTasksByUserId(userId);

        if (tasks == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(tasks);
    }

    /**
     * Create a new task.
     * @param task the task to create
     * @return the created task with HTTP 201
     */
    @Operation(summary = "Create a new task")
    @ApiResponse(responseCode = "201", description = "Task created successfully")
    @ApiResponse(responseCode = "500", description = "Server error, task not created")
    @PostMapping("/tasks/create")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task newTask = taskService.createTask(task);
        return ResponseEntity.status(201).body(newTask);
    }
    
}

package com.app.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.TaskDetailDTO;
import com.app.api.models.Task;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.TaskService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for task-related endpoints.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Tasks", description = "Operations for managing tasks")
public class TaskController {

    /** The task service. */
    private final TaskService taskService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a TaskController with the given TaskService.
     * @param taskService the task service
     */
    public TaskController(TaskService taskService, FirebaseAuthService firebaseAuthService) {
        this.taskService = taskService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Get a task by its ID, including resolved requester and helper names.
     * @param taskId the ID of the task
     * @return the task if found, 404 otherwise
     */
    @GetMapping("/tasks/{taskId}")
    @Operation(
        summary = "Get a task by ID",
        description = "Retrieves a task by its ID, including resolved requester and helper names",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found"),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<TaskDetailDTO> getTaskById(
        @Parameter(description = "ID of the task to retrieve", example = "1")
        @PathVariable int taskId
    ) {
        TaskDetailDTO task = taskService.getTaskDetailById(taskId);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    /**
     * Get all tasks, including resolved requester and helper names.
     * @return all tasks
     */
    @GetMapping("/tasks")
    @Operation(
        summary = "Get all tasks for authenticated user",
        description = "Retrieves all tasks for the authenticated user, including resolved requester and helper names",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> getAllTasks(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            return ResponseEntity.ok(taskService.getAllTaskDetailsByUserId(userId));
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Delete a task by its ID.
     * @param taskId the ID of the task to delete
     * @return 200 if deleted, 404 if not found
     */
    @DeleteMapping("/tasks/{taskId}")
    @Operation(
        summary = "Delete a task by ID",
        description = "Deletes a task by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    public ResponseEntity<String> deleteTask(
        @Parameter(description = "ID of the task to delete", example = "1")
        @PathVariable int taskId
    ) {
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
    @PutMapping("/tasks/{taskId}")
    @Operation(
        summary = "Update task by ID",
        description = "Updates an existing task by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid task data", content = @Content)
    })
    public ResponseEntity<Task> updateTask(
        @Parameter(description = "ID of the task to update", example = "1")
        @PathVariable int taskId,
        @RequestBody Task updates
    ) {
        Task updatedTask = taskService.updateTask(taskId, updates);

        if (updatedTask == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Get all tasks for a specific user, including resolved requester and helper names.
     * @param userId the ID of the user
     * @return tasks linked to the user's dependent profile, or 404 if not found
     */
    @GetMapping("/users/{userId}/tasks")
    @Operation(
        summary = "Get all tasks for a specific user",
        description = "Retrieves all tasks for a specific user, including resolved requester and helper names",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No dependent profile found for user", content = @Content)
    })
    public ResponseEntity<List<TaskDetailDTO>> getTasksByUserId(
        @Parameter(description = "ID of the user to retrieve tasks for", example = "1")
        @PathVariable int userId,
        @Parameter(description = "Optional helper ID filter", example = "1")
        Integer helperId
    ) {
        List<TaskDetailDTO> tasks = taskService.getTaskDetailsByUserId(userId);

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
    @PostMapping("/tasks/create")
    @Operation(
        summary = "Create a new task",
        description = "Creates a new task",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task data", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server error, task not created", content = @Content)
    })
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task newTask = taskService.createTask(task);
        return ResponseEntity.status(201).body(newTask);
    }
}

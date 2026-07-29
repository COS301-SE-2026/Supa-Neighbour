package com.app.api.controllers;

import com.app.api.dtos.TaskDetailDTO;
import com.app.api.models.Task;
import com.app.api.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
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
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
   
    }

    /**
     * Get a task by its ID, including resolved requester and helper names.
     * @param taskId the ID of the task
     * @return the task if found, 404 otherwise
     */
    @Operation(summary = "Get a task by ID")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskDetailDTO> getTaskById(@PathVariable int taskId) {
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
    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved")
    @ApiResponse(responseCode = "404", description = "Unauthorised")
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDetailDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTaskDetails());
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
     * Get all tasks for a specific user, including resolved requester and helper names.
     * @param userId the ID of the user
     * @return tasks linked to the user's dependent profile, or 404 if not found
     */
    @Operation(summary = "Get all tasks for a specific user")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved")
    @ApiResponse(responseCode = "404", description = "No dependent profile found for user")
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<TaskDetailDTO>> getTasksByUserId(@PathVariable int userId, Integer helperId) {
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
    @Operation(summary = "Create a new task")
    @ApiResponse(responseCode = "201", description = "Task created successfully")
    @ApiResponse(responseCode = "500", description = "Server error, task not created")
    @PostMapping("/tasks/create")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task newTask = taskService.createTask(task);
        return ResponseEntity.status(201).body(newTask);
    }
}

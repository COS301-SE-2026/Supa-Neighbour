package com.app.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.TaskType;
import com.app.api.services.TaskTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for task type.
 */
@RestController
@RequestMapping("/api/tasktypes")
@Tag(name = "Task Types", description = "Operations for managing task types")
public class TaskTypeController {

    private final TaskTypeService taskTypeService;

    /**
     * TaskType Constructor
     * @param taskTypeService taskTypeService
     */
    public TaskTypeController(TaskTypeService taskTypeService) {
        this.taskTypeService = taskTypeService;
    }

    // GET /api/tasktypes
    /**
     * Retrieves all task type.
     *
     * @return a list of all task type
     */
    @GetMapping
    @Operation(summary = "Get all task types", description = "Retrieves a list of all task types")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved task types")
    public ResponseEntity<List<TaskType>> getAllTaskTypes() {
        return ResponseEntity.ok(taskTypeService.getAllTaskTypes());
    }

    // GET /api/tasktypes/1
    /**
     * Retrieves a task type by its ID.
     *
     * @param id the task type ID
     * @return the task type if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get task type by ID", description = "Retrieves a single task type by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task type found"),
        @ApiResponse(responseCode = "404", description = "Task type not found", content = @Content)
    })
    public ResponseEntity<TaskType> getTaskTypeById(
        @Parameter(description = "ID of the task type to retrieve", example = "1")
        @PathVariable int id
    ) {
        TaskType taskType = taskTypeService.getTaskTypeById(id);
        if (taskType == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskType);
    }

    // POST /api/tasktypes
    /**
     * Creates a new task type.
     *
     * @param taskType the task type to create
     * @return the created task type with HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new task type", description = "Creates a new task type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task type created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task type data", content = @Content)
    })
    public ResponseEntity<TaskType> createTaskType(@RequestBody TaskType taskType) {
        TaskType saved = taskTypeService.saveTaskType(taskType);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/tasktypes/1
    /**
     * Updates an existing task type.
     *
     * @param id the ID of the task type to update
     * @param taskType the updated task type data
     * @return the updated task type if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a task type", description = "Updates an existing task type by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task type updated successfully"),
        @ApiResponse(responseCode = "404", description = "Task type not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid task type data", content = @Content)
    })
    public ResponseEntity<TaskType> updateTaskType(
        @Parameter(description = "ID of the task type to update", example = "1")
        @PathVariable int id,
        @RequestBody TaskType taskType
    ) {
        TaskType existing = taskTypeService.getTaskTypeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        TaskType updated = taskTypeService.updateTaskType(id, taskType);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/tasktypes/1
    /**
     * Deletes a task type by its ID.
     *
     * @param id the ID of the task type to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task type", description = "Deletes a task type by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task type deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task type not found", content = @Content)
    })
    public ResponseEntity<Void> deleteTaskType(
        @Parameter(description = "ID of the task type to delete", example = "1")
        @PathVariable int id
    ) {
        TaskType existing = taskTypeService.getTaskTypeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        taskTypeService.deleteTaskType(id);
        return ResponseEntity.noContent().build();
    }
}

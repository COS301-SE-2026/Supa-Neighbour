package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * REST controller for task type.
 */
@RestController
@RequestMapping("/api/tasktypes")
public class TaskTypeController {

    @Autowired
    private TaskTypeService taskTypeService;

    /**
     * Retrieves all task type.
     *
     * @return a list of all task type
     */ 
    @GetMapping
    public ResponseEntity<List<TaskType>> getAllTaskTypes() {
        return ResponseEntity.ok(taskTypeService.getAllTaskTypes());
    }

     /**
     * Retrieves a task type by its ID.
     *
     * @param id the task type ID
     * @return the task type if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskType> getTaskTypeById(@PathVariable int id) {
        TaskType taskType = taskTypeService.getTaskTypeById(id);
        if (taskType == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskType);
    }

    /**
     * Creates a new task type.
     *
     * @param taskType the task type to create
     * @return the created task type with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<TaskType> createTaskType(@RequestBody TaskType taskType) {
        TaskType saved = taskTypeService.saveTaskType(taskType);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing task type.
     *
     * @param id the ID of the task type to update
     * @param taskType the updated task type data
     * @return the updated task type if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskType> updateTaskType(@PathVariable int id, @RequestBody TaskType taskType) {
        TaskType existing = taskTypeService.getTaskTypeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        TaskType updated = taskTypeService.updateTaskType(id, taskType);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a task type by its ID.
     *
     * @param id the ID of the task type to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskType(@PathVariable int id) {
        TaskType existing = taskTypeService.getTaskTypeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        taskTypeService.deleteTaskType(id);
        return ResponseEntity.noContent().build();
    }
}

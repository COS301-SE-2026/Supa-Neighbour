package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.TaskType;
import com.app.api.services.TaskTypeService;

@RestController
@RequestMapping("/api/tasktypes")
public class TaskTypeController {

    @Autowired
    private TaskTypeService taskTypeService;

    // GET /api/tasktypes
    @GetMapping
    public ResponseEntity<List<TaskType>> getAllTaskTypes() {
        return ResponseEntity.ok(taskTypeService.getAllTaskTypes());
    }

    // GET /api/tasktypes/1
    @GetMapping("/{id}")
    public ResponseEntity<TaskType> getTaskTypeById(@PathVariable int id) {
        TaskType taskType = taskTypeService.getTaskTypeById(id);
        if (taskType == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskType);
    }

    // POST /api/tasktypes
    @PostMapping
    public ResponseEntity<TaskType> createTaskType(@RequestBody TaskType taskType) {
        TaskType saved = taskTypeService.saveTaskType(taskType);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/tasktypes/1
    @PutMapping("/{id}")
    public ResponseEntity<TaskType> updateTaskType(@PathVariable int id, @RequestBody TaskType taskType) {
        TaskType existing = taskTypeService.getTaskTypeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        TaskType updated = taskTypeService.updateTaskType(id, taskType);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/tasktypes/1
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
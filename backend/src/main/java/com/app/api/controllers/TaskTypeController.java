package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.TaskType;
import com.app.api.services.TaskTypeService;

/**
 * Task type controller.
 */
@RestController
@RequestMapping("api/task-types")
public class TaskTypeController {

    @Autowired
    private TaskTypeService taskTypeService;

    /**
     * Get all task types.
     * @return task types
     */
    @GetMapping
    public Iterable<TaskType> getAllTaskTypes() {
        return taskTypeService.getAllTaskTypes();
    }

    /**
     * Get task type by id.
     * @param id task type id
     * @return task type
     */
    @GetMapping("api/task-types/{id}")
    public TaskType getTaskTypeById(@PathVariable int id) {
        return taskTypeService.getTaskTypeById(id);
    }

    /**
     * Create task type.
     * @param taskType task type
     * @return saved task type
     */
    @PostMapping
    public TaskType createTaskType(@RequestBody TaskType taskType) {
        return taskTypeService.saveTaskType(taskType);
    }
}

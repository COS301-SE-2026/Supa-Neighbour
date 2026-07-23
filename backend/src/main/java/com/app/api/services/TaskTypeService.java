package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.TaskType;
import com.app.api.repositories.TaskTypeRepository;

/**
 * Service layer for managing task type operations.
 * Provides CRUD functionality for TaskType entities.
 */
@Service
public class TaskTypeService {

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    // Get all
    /**
     * Retrieves all task types from the repository.
     *
     * @return a list of all task types
     */
    public List<TaskType> getAllTaskTypes() {
        return taskTypeRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a task type by its identifier.
     *
     * @param id the task type identifier
     * @return the task type if found, or null if no task type exists with the given id
     */
    public TaskType getTaskTypeById(int id) {
        return taskTypeRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new task type to the repository.
     *
     * @param taskType the task type to save
     * @return the saved task type, or null if the provided task type is null
     */
    public TaskType saveTaskType(TaskType taskType) {
        if(taskType == null) {
            return null;
        }
        if (taskType.getXpWorth() == null) {
            taskType.setXpWorth(0);
        }
        return taskTypeRepository.save(taskType);
    }

    // Update
    /**
     * Updates an existing task type with the provided details.
     *
     * @param id      the identifier of the task type to update
     * @param updated the task type object containing the updated fields
     * @return the updated task type, or null if no task type exists with the given id
     */
    public TaskType updateTaskType(int id, TaskType updated) {
        if (updated == null) {
            return null;
        }
        TaskType existing = taskTypeRepository.findById(id).orElse(null);
        
        if (existing == null) {
            return null;
        }
        
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        if (updated.getBadgeid() != null) {
            existing.setBadgeid(updated.getBadgeid());
        }
        if (updated.getXpWorth() != null) {
            existing.setXpWorth(updated.getXpWorth());
        }
        
        if (updated.isNeedsSpecialist() != null) {
            existing.setNeedsSpecialist(updated.isNeedsSpecialist());
        }

        return taskTypeRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a task type by its identifier.
     *
     * @param id the identifier of the task type to delete
     */
    public void deleteTaskType(int id) {
        taskTypeRepository.deleteById(id);
    }
}

package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.TaskType;
import com.app.api.repositories.TaskTypeRepository;

@Service
public class TaskTypeService {

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    // Get all
    public List<TaskType> getAllTaskTypes() {
        return taskTypeRepository.findAll();
    }

    // Get by id
    public TaskType getTaskTypeById(int id) {
        return taskTypeRepository.findById(id).orElse(null);
    }

    // Create
    public TaskType saveTaskType(TaskType taskType) {
        return taskTypeRepository.save(taskType);
    }

    // Update
    public TaskType updateTaskType(int id, TaskType updated) {
        TaskType existing = taskTypeRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setDescription(updated.getDescription());
        existing.setNeedsSpecialist(updated.isNeedsSpecialist());

        return taskTypeRepository.save(existing);
    }

    // Delete
    public void deleteTaskType(int id) {
        taskTypeRepository.deleteById(id);
    }
}
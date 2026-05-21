package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.TaskType;
import com.app.api.repositories.TaskTypeRepository;

/**
 * Task type service.
 */
@Service
public class TaskTypeService {

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    /**
     * Get all task types.
     * @return list of task types
     */
    public Iterable<TaskType> getAllTaskTypes() {
        return taskTypeRepository.findAll();
    }

    /**
     * Get task type by id.
     * @param id task type id
     * @return task type
     */
    public TaskType getTaskTypeById(int id) {
        return taskTypeRepository.findById(id).orElse(null);
    }

    /**
     * Save task type.
     * @param taskType task type
     * @return saved task type
     */
    public TaskType saveTaskType(TaskType taskType) {
        return taskTypeRepository.save(taskType);
    }
}

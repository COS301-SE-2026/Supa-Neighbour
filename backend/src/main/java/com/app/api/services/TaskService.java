package com.app.api.services;

import com.app.api.models.Task;
import com.app.api.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService
{
    // 1. dependencies
    private final TaskRepository taskRepo;

    // 2. constructor
    @Autowired
    public TaskService(TaskRepository taskRepo)
    {
        this.taskRepo = taskRepo;
    }

    // 3.
     /**
     * Get a task by its ID.
     * @param taskId the ID of the task
     * @return the task if found, else null
     */
    public Task getTaskById(int taskId)
    {
        return taskRepo.findById(taskId).orElse(null);
    }


    /**
     * Get all tasks
     * @return all tasks
     */
    public Iterable<Task> getAllTasks()
    {
        return taskRepo.findAll();
    }

}
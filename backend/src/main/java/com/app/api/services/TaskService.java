package com.app.api.services;

import com.app.api.models.Analytics;
import com.app.api.models.Task;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService
{
    // 1. dependencies
    private final TaskRepository taskRepo;
    private final AnalyticsRepository analyticsRepo;

    // 2. constructor
    @Autowired
    public TaskService(TaskRepository taskRepo,AnalyticsRepository analyticsRepo)
    {
        this.taskRepo = taskRepo;
        this.analyticsRepo = analyticsRepo;
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

    /**
     * Delete a task by its ID
     * Deletes then linked analytics records first to fall in line with the foreignKey constraints
     * @param taskId the ID of the task to be deleted
     * @return true if deleted, else false if not found
     */
    public boolean deleteTask(int taskId)
    {
        if(!taskRepo.existsById(taskId))
        {
            return false;
        }

        Iterable<Analytics> linkedAnalytics = analyticsRepo.findByTaskId(taskId);
        analyticsRepo.deleteAll(linkedAnalytics);


        taskRepo.deleteById(taskId);
        return true;
    }

}
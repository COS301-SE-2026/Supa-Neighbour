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


    /**
     * Update an existing task by ID.
     * @param taskId of the task to update
     * @param updates a task containing new values
     * @return the uploaded task else null if not found
     */
    public Task updateTask(int taskId, Task updates)
    {

        Task targetTask = taskRepo.findById(taskId).orElse(null);
        if(targetTask == null){ return null;}

        // update helper
        if (updates.getHelperId() != null)
        {
            targetTask.setHelperId(updates.getHelperId());
        }

        // update dependent
        if(updates.getDependentId() != null)
        {
            targetTask.setDependentId(updates.getDependentId());
        }

        // update :
        // task type
        if(updates.getTaskTypeId() != null)
        {
            targetTask.setTaskTypeId(updates.getTaskTypeId());
        }

        // location
        if(updates.getLocationId() != null)
        {
            targetTask.setLocationId(updates.getLocationId());
        }

        // start date
        if(updates.getStartDate() != null)
        {
            targetTask.setStartDate(updates.getStartDate());
        }

        // end dae
        if(updates.getEndDate() != null)
        {
            targetTask.setEndDate(updates.getEndDate());
        }

        //admin review ----- need to think about this only admin can really chnage this.
        if(updates.getAdminReview() != null)
        {
            targetTask.setAdminReview(updates.getAdminReview());
        }
        
        return taskRepo.save(targetTask);
    }
}

package com.app.api.services;

import com.app.api.models.Analytics;
import com.app.api.models.Task;
import com.app.api.models.Dependent;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.repositories.DependentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer for task-related business logic.
 */
@Service
public class TaskService {

    /** The task repository. */
    private final TaskRepository taskRepo;

    /** The analytics repository. */
    private final AnalyticsRepository analyticsRepo;

    /** The dependent repository. */
    private final DependentRepository dependentRepo;

    /**
     * Constructs a TaskService with the required repositories.
     * @param taskRepo the task repository
     * @param analyticsRepo the analytics repository
     * @param dependentRepo the dependent repository
     */
    @Autowired
    public TaskService(TaskRepository taskRepo, AnalyticsRepository analyticsRepo,
            DependentRepository dependentRepo) {
        this.taskRepo = taskRepo;
        this.analyticsRepo = analyticsRepo;
        this.dependentRepo = dependentRepo;
    }

    /**
     * Get a task by its ID.
     * @param taskId the ID of the task
     * @return the task if found, else null
     */
    public Task getTaskById(int taskId) {
        return taskRepo.findById(taskId).orElse(null);
    }

    /**
     * Get all tasks.
     * @return all tasks
     */
    public Iterable<Task> getAllTasks() {
        return taskRepo.findAll();
    }

    /**
     * Delete a task by its ID.
     * Deletes linked analytics records first to satisfy foreign key constraints.
     * @param taskId the ID of the task to be deleted
     * @return true if deleted, false if not found
     */
    public boolean deleteTask(int taskId) {
        if (!taskRepo.existsById(taskId)) {
            return false;
        }

        Iterable<Analytics> linkedAnalytics = analyticsRepo.findByTaskId(taskId);
        analyticsRepo.deleteAll(linkedAnalytics);

        taskRepo.deleteById(taskId);
        return true;
    }

    /**
     * Update an existing task by ID.
     * @param taskId the ID of the task to update
     * @param updates a task containing new values
     * @return the updated task, or null if not found
     */
    public Task updateTask(int taskId, Task updates) {
        Task targetTask = taskRepo.findById(taskId).orElse(null);
        if (targetTask == null) {
            return null;
        }

        if (updates.getHelperId() != null) {
            targetTask.setHelperId(updates.getHelperId());
        }

        if (updates.getDependentId() != null) {
            targetTask.setDependentId(updates.getDependentId());
        }

        if (updates.getTaskTypeId() != null) {
            targetTask.setTaskTypeId(updates.getTaskTypeId());
        }

        if (updates.getLocationId() != null) {
            targetTask.setLocationId(updates.getLocationId());
        }

        if (updates.getStartDate() != null) {
            targetTask.setStartDate(updates.getStartDate());
        }

        if (updates.getEndDate() != null) {
            targetTask.setEndDate(updates.getEndDate());
        }

        if (updates.getAdminReview() != null) { // change later allow on admin t edit reviews
            targetTask.setAdminReview(updates.getAdminReview());
        }

        return taskRepo.save(targetTask);
    }

    /**
     * Get tasks for a user using their dependent profile.
     * @param userId the user ID to look up
     * @return tasks linked to the user's dependent ID, or null if profile not found
     */
    public Iterable<Task> getTasksByUserId(int userId) {
        Dependent dependent = dependentRepo.findByUserId(userId);
        if (dependent == null) {
            return null;
        }

        return taskRepo.findByDependentId(dependent.getDependentId());
    }

    /**
     * Create a new task.
     * @param task the task to create
     * @return the saved task
     */
    public Task createTask(Task task) {
        return taskRepo.save(task);
    }
}

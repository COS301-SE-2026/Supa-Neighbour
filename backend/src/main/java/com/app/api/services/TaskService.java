package com.app.api.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.api.models.Analytics;
import com.app.api.models.Chat;
import com.app.api.models.Dependent;
import com.app.api.models.Task;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.repositories.ChatRepository;
import com.app.api.repositories.DependentRepository;
import com.app.api.repositories.MessageRepository;
import com.app.api.repositories.TaskRepository;

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

    /** The chat repository. */
    private final ChatRepository chatRepo;

    /** The message repository. */
    private final MessageRepository messageRepo;

    /**
     * Constructs a TaskService with the required repositories.
     * @param taskRepo the task repository
     * @param analyticsRepo the analytics repository
     * @param dependentRepo the dependent repository
     * @param chatRepo the chat repository
     * @param messageRepo the message repository
     */

    public TaskService(TaskRepository taskRepo, AnalyticsRepository analyticsRepo,
            DependentRepository dependentRepo, ChatRepository chatRepo,
            MessageRepository messageRepo) {
        this.taskRepo = taskRepo;
        this.analyticsRepo = analyticsRepo;
        this.dependentRepo = dependentRepo;
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
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
    public List<Task> getAllTasks() {
        return taskRepo.findAll();
    }

    /**
     * Delete a task by its ID.
     * Deletes linked messages, chats, and analytics first to satisfy foreign key constraints.
     * @param taskId the ID of the task to be deleted
     * @return true if deleted, false if not found
     */
    @Transactional
    public boolean deleteTask(int taskId) {
        if (!taskRepo.existsById(taskId)) {
            return false;
        }

        // Delete messages, then chats (message_table -> chat_table -> task_invoice_table)
        List<Chat> linkedChats = chatRepo.findByTask_Taskid(taskId);
        for (Chat chat : linkedChats) {
            messageRepo.deleteByChatId(chat.getChatId());
        }
        chatRepo.deleteAll(linkedChats);

        // Delete linked analytics (analytics -> task_invoice_table)
        Iterable<Analytics> linkedAnalytics = analyticsRepo.findByTaskid_Taskid(taskId);
        analyticsRepo.deleteAll(linkedAnalytics);

        // Finally delete the task itself
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
    public List<Task> getTasksByUserId(int userId) {
        Dependent dependent = dependentRepo.findByUserId_Userid(userId);
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

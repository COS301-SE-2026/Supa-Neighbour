package com.app.api.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.api.dtos.TaskDetailDTO;
import com.app.api.models.Analytics;
import com.app.api.models.Chat;
import com.app.api.models.Dependent;
import com.app.api.models.Helper;
import com.app.api.models.Task;
import com.app.api.models.User;
import com.app.api.repositories.AnalyticsRepository;
import com.app.api.repositories.ChatRepository;
import com.app.api.repositories.DependentRepository;
import com.app.api.repositories.HelperRepository;
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

    /** The helper repository. */
    private final HelperRepository helperRepo;

    /**
     * Constructs a TaskService with the required repositories.
     * @param taskRepo the task repository
     * @param analyticsRepo the analytics repository
     * @param dependentRepo the dependent repository
     * @param chatRepo the chat repository
     * @param messageRepo the message repository
     * @param helperRepo the helper repository
     */
    public TaskService(TaskRepository taskRepo, AnalyticsRepository analyticsRepo,
            DependentRepository dependentRepo, ChatRepository chatRepo,
            MessageRepository messageRepo, HelperRepository helperRepo) {
        this.taskRepo = taskRepo;
        this.analyticsRepo = analyticsRepo;
        this.dependentRepo = dependentRepo;
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
        this.helperRepo = helperRepo;
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

        if (updates.getStatus() != null) {
            targetTask.setStatus(updates.getStatus());
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

    /**
     * Combines a user's first and last name into a single display name.
     * @param user the user to build a display name for
     * @return the combined name, or null if the user has no usable name
     */
    private String fullName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        String combined = (first + " " + last).trim();
        return combined.isEmpty() ? null : combined;
    }

    /**
     * Converts a Task into a TaskDetailDTO, resolving the requester and
     * helper display names via their Dependent/Helper -> User relations.
     * @param task the task to convert
     * @return the enriched task detail
     */
    private TaskDetailDTO toDetailDTO(Task task) {
        TaskDetailDTO dto = new TaskDetailDTO();
        dto.setTaskId(task.getTaskId());
        dto.setHelperId(task.getHelperId());
        dto.setDependentId(task.getDependentId());
        dto.setImmediate(task.isImmediate());
        dto.setLocationId(task.getLocationId());
        dto.setTaskTypeId(task.getTaskTypeId());
        dto.setNeedsSpecialist(task.isNeedsSpecialist());
        dto.setSignedAdminId(task.getSignedAdminId());
        dto.setStartDate(task.getStartDate());
        dto.setEndDate(task.getEndDate());
        dto.setHelperBadgeId(task.getHelperBadgeId());
        dto.setDependentRatingId(task.getDependentRatingId());
        dto.setHelperRatingId(task.getHelperRatingId());
        dto.setAdminReview(task.getAdminReview());
        dto.setCompatibilityId(task.getCompatibilityId());
        dto.setStatus(task.getStatus());

        if (task.getDependentId() != null) {
            Dependent dependent = dependentRepo.findById(task.getDependentId()).orElse(null);
            if (dependent != null && dependent.getUserId() != null) {
                dto.setRequesterName(fullName(dependent.getUserId()));
            }
        }

        if (task.getHelperId() != null) {
            Helper helper = helperRepo.findById(task.getHelperId()).orElse(null);
            if (helper != null && helper.getUserid() != null) {
                dto.setHelperName(fullName(helper.getUserid()));
            }
        }

        return dto;
    }

    /**
     * Get a task by its ID, with requester and helper names resolved.
     * @param taskId the ID of the task
     * @return the task detail if found, else null
     */
    public TaskDetailDTO getTaskDetailById(int taskId) {
        Task task = taskRepo.findById(taskId).orElse(null);
        if (task == null) {
            return null;
        }
        return toDetailDTO(task);
    }

    /**
     * Get all tasks, with requester and helper names resolved.
     * @return all task details
     */
    public List<TaskDetailDTO> getAllTaskDetails() {
        List<TaskDetailDTO> details = new ArrayList<>();
        for (Task task : taskRepo.findAll()) {
            details.add(toDetailDTO(task));
        }
        return details;
    }

    /**
     * Get tasks for a user using their dependent profile, with names resolved.
     * @param userId the user ID to look up
     * @return task details linked to the user's dependent ID, or null if profile not found
     */
    public List<TaskDetailDTO> getTaskDetailsByUserId(int userId) {
        Dependent dependent = dependentRepo.findByUserId_Userid(userId);
        if (dependent == null) {
            return null;
        }

        List<Task> tasks = taskRepo.findByDependentId(dependent.getDependentId());
        List<TaskDetailDTO> details = new ArrayList<>();
        for (Task task : tasks) {
            details.add(toDetailDTO(task));
        }
        return details;
    }
}

package com.app.api.services;

import java.util.List;


import org.springframework.stereotype.Service;
import com.app.api.models.TaskInvitation;
import com.app.api.repositories.TaskInvitationRepository;

/**
 * Service layer for managing analytics operations.
 * Provides CRUD functionality for Analytics entities.
 */
@Service
public class TaskInvitationService {

    
    private final TaskInvitationRepository taskInvitationRepository;

    TaskInvitationService(TaskInvitationRepository taskInvitationRepository) {
        this.taskInvitationRepository = taskInvitationRepository;
    }

    // Get all
    /**
     * Retrieves all analytics records from the repository.
     *
     * @return a list of all analytics records
     */
    public List<TaskInvitation> getrInvitations() {
        return taskInvitationRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves an analytics record by its identifier.
     *
     * @param id the analytics identifier
     * @return the analytics record if found, or null if no record exists with the given id
     */
    public TaskInvitation getInvitationById(int id) {
        return taskInvitationRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new analytics record to the repository.
     *
     * @param invitation the analytics record to save
     * @return the saved analytics record, or null if the provided analytics is null
     */
    public TaskInvitation saveAnalytics(TaskInvitation invitation) {
        if(invitation == null) {
            return null;
        }
        return taskInvitationRepository.save(invitation);
    }

    // Update
    /**
     * Updates an existing analytics record with the provided details.
     *
     * @param id      the identifier of the analytics record to update
     * @param updated the analytics object containing the updated fields
     * @return the updated analytics record, or null if no record exists with the given id
     */
    public TaskInvitation updateTaskInvitation(int id, TaskInvitation updated) {
        TaskInvitation existing = taskInvitationRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setInvitedAt(updated.getInvitedAt());
        existing.setHelperId(updated.getHelperId());
        existing.setStatus(updated.getStatus());
        existing.setTaskId(updated.getTaskId());

        return taskInvitationRepository.save(existing);
    }

    // Delete
    /**
     * Deletes an analytics record by its identifier.
     *
     * @param id the identifier of the analytics record to delete
     */
    public void deleteAnalytics(int id) {
        taskInvitationRepository.deleteById(id);
    }
}

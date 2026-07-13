package com.app.api.services;

import java.util.List;


import org.springframework.stereotype.Service;
import com.app.api.models.TaskInvitation;
import com.app.api.models.TaskInvoice;
import com.app.api.repositories.TaskInvitationRepository;

import jakarta.transaction.Transactional;
import java.util.Optional;
import com.app.api.models.Helper;

/**
 * Service layer for managing task invitation operations.
 * Provides CRUD functionality for TaskInvitation entities.
 */
@Service
public class TaskInvitationService {

    
    private final TaskInvitationRepository taskInvitationRepository;

    TaskInvitationService(TaskInvitationRepository taskInvitationRepository) {
        this.taskInvitationRepository = taskInvitationRepository;
    }

    // Get all
    /**
     * Retrieves all task invitation records from the repository.
     *
     * @return a list of all task invitation records
     */
    public List<TaskInvitation> getTaskInvitations() {
        return taskInvitationRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a task invitation record by its identifier.
     *
     * @param id the task invitation identifier
     * @return the task invitation record if found, or null if no record exists with the given id
     */
    public TaskInvitation getInvitationById(int id) {
        return taskInvitationRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new task invitation record to the repository.
     *
     * @param invitation the task invitation record to save
     * @return the saved task invitation record, or null if the provided task invitation is null
     */
    public TaskInvitation saveTaskInvitation(TaskInvitation invitation) {
        if(invitation == null) {
            return null;
        }
        return taskInvitationRepository.save(invitation);
    }

    // Update
    /**
     * Updates an existing task invitation record with the provided details.
     *
     * @param id      the identifier of the task invitation record to update
     * @param updated the task invitation object containing the updated fields
     * @return the updated task invitation record, or null if no record exists with the given id
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
     * Deletes a task invitation record by its identifier.
     *
     * @param id the identifier of the task invitation record to delete
     */
    public void deleteTaskInvitation(int id) {
        taskInvitationRepository.deleteById(id);
    }

    @Transactional
    public TaskInvitation acceptInvitation(int taskId, int helperId, TaskInvoice taskInvoice, Helper helper){
        Optional<TaskInvitation> existing = taskInvitationRepository.findByTaskId_TaskidAndHelperId_Helperid(taskId, helperId);

        if(existing.isPresent()){
            String status = existing.get().getStatus();
            if("Invited".equals(status)){
                throw new IllegalStateException("UNPROCESSABLE");
            }
            throw new IllegalStateException("CONFLICT");
        }

        TaskInvitation invitation = TaskInvitation.builder().taskId(taskInvoice).helperId(helper).status("Accepted").invitedAt(null).build();

        return taskInvitationRepository.save(invitation);
    }
}

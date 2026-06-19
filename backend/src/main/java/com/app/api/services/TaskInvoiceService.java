package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.TaskInvoice;
import com.app.api.repositories.TaskInvoiceRepository;

/**
 * Service layer for managing task invoice operations.
 * Provides CRUD functionality for TaskInvoice entities.
 */
@Service
public class TaskInvoiceService {

    @Autowired
    private TaskInvoiceRepository taskInvoiceRepository;

    /**
     * Service layer for managing task invoice operations.
     * Provides CRUD functionality for TaskInvoice entities.
     */
    public List<TaskInvoice> getAllTaskInvoices() {
        return taskInvoiceRepository.findAll();
    }

    /**
     * Retrieves a task invoice by its identifier.
     *
     * @param id the task invoice identifier
     * @return the task invoice if found, or null if no task invoice exists with the given id
     */
    public TaskInvoice getTaskInvoiceById(int id) {
        return taskInvoiceRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new task invoice to the repository.
     *
     * @param taskInvoice the task invoice to save
     * @return the saved task invoice, or null if the provided task invoice is null
     */
    public TaskInvoice saveTaskInvoice(TaskInvoice taskInvoice) {
        if(taskInvoice == null) {
            return null;
        }
        return taskInvoiceRepository.save(taskInvoice);
    }

    /**
     * Updates an existing task invoice with the provided details.
     *
     * @param id      the identifier of the task invoice to update
     * @param updated the task invoice object containing the updated fields
     * @return the updated task invoice, or null if no task invoice exists with the given id
     */
    public TaskInvoice updateTaskInvoice(int id, TaskInvoice updated) {
        TaskInvoice existing = taskInvoiceRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setHelperid(updated.getHelperid());
        existing.setTasktypeid(updated.getTasktypeid());
        existing.setAdminreview(updated.getAdminReview());
        existing.setCompatibilityid(updated.getCompatibilityid());
        existing.setSignedadminid(updated.getSignedadminid());
        existing.setDependentid(updated.getDependentid());
        existing.setImmediate(updated.isImmediate());
        existing.setLocationid(updated.getLocationid());
        existing.setNeedsspecialist(updated.isNeedsspecialist());
        existing.setDependentratingreview(updated.getDependentratingreview());
        existing.setHelperRatingreview(updated.getHelperRatingreview());
        existing.setHelperbadgeid(updated.getHelperbadgeid());
        existing.setStartdate(updated.getStartdate());
        existing.setEnddate(updated.getEnddate());

        return taskInvoiceRepository.save(existing);
    }

    /**
     * Deletes a task invoice by its identifier.
     *
     * @param id the identifier of the task invoice to delete
     */
    public void deleteTaskInvoice(int id) {
        taskInvoiceRepository.deleteById(id);
    }
}

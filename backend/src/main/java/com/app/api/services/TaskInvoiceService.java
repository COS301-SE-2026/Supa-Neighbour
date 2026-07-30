package com.app.api.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.app.api.models.Dependent;

import com.app.api.models.TaskInvoice;
import com.app.api.repositories.DependentRepository;
import com.app.api.repositories.TaskInvoiceRepository;

/**
 * Service layer for managing task invoice operations.
 * Provides CRUD functionality for TaskInvoice entities.
 */
@Service
public class TaskInvoiceService {

    private final TaskInvoiceRepository taskInvoiceRepository;
    private final DependentRepository dependentRepository;

    /**
     * Constructs the service with its required repository dependency.
     *
     * @param taskInvoiceRepository repository providing analytics data for taskInvoice
     */
    public TaskInvoiceService(TaskInvoiceRepository taskInvoiceRepository, DependentRepository dependentRepository) {
        this.taskInvoiceRepository = taskInvoiceRepository;
        this.dependentRepository = dependentRepository;
    }

    // Get all
    /**
     * Service layer for managing task invoice operations.
     * Provides CRUD functionality for TaskInvoice entities.
     */
    public List<TaskInvoice> getAllTaskInvoices() {
        return taskInvoiceRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a task invoice by its identifier.
     *
     * @param id the task invoice identifier
     * @return the task invoice if found, or null if no task invoice exists with the given id
     */
    public TaskInvoice getTaskInvoiceById(int id) {
        return taskInvoiceRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new task invoice to the repository.
     *
     * @param taskInvoice the task invoice to save
     * @return the saved task invoice, or null if the provided task invoice is null
     */
    public TaskInvoice saveTaskInvoice(int userId, TaskInvoice taskInvoice) {
        if(taskInvoice == null) {
            return null;
        }

        Dependent dependent = dependentRepository.findByUserId_Userid(userId);

        if(dependent == null){
            return null;
        }

        taskInvoice.setDependentid(dependent);
        return taskInvoiceRepository.save(taskInvoice);
    }

    // Update
    /**
     * Updates an existing task invoice with the provided details.
     *
     * @param id      the identifier of the task invoice to update
     * @param updated the task invoice object containing the updated fields
     * @return the updated task invoice, or null if no task invoice exists with the given id
     */
    public TaskInvoice updateTaskInvoice(int id, TaskInvoice updated) {
        TaskInvoice existing = taskInvoiceRepository.findById(id).orElse(null);

        if (existing == null || updated == null) {
            return null;
        }

        if (updated.getHelperid() != null) {
            existing.setHelperid(updated.getHelperid());
        }
        if (updated.getTasktypeid() != null) {
            existing.setTasktypeid(updated.getTasktypeid());
        }
        if (updated.getAdminReview() != null) {
            existing.setAdminreview(updated.getAdminReview());
        }
        if (updated.getCompatibilityid() != null) {
            existing.setCompatibilityid(updated.getCompatibilityid());
        }
        if (updated.getSignedadminid() != null) {
            existing.setSignedadminid(updated.getSignedadminid());
        }
        if (updated.getDependentid() != null) {
            existing.setDependentid(updated.getDependentid());
        }
        existing.setImmediate(updated.getImmediate());
        if (updated.getLocationid() != null) {
            existing.setLocationid(updated.getLocationid());
        }
        existing.setNeedsspecialist(updated.isNeedsspecialist());
        if (updated.getDependentRatingreview() != null) {
            existing.setDependentRatingreview(updated.getDependentRatingreview());
        }
        if (updated.getHelperRatingreview() != null) {
            existing.setHelperRatingreview(updated.getHelperRatingreview());
        }
        if (updated.getHelperbadgeid() != null) {
            existing.setHelperbadgeid(updated.getHelperbadgeid());
        }
        if (updated.getStartdate() != null) {
            existing.setStartdate(updated.getStartdate());
        }
        if (updated.getEnddate() != null) {
            existing.setEnddate(updated.getEnddate());
        }

        return taskInvoiceRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a task invoice by its identifier.
     *
     * @param id the identifier of the task invoice to delete
     */
    public void deleteTaskInvoice(int id) {
        taskInvoiceRepository.deleteById(id);
    }
}

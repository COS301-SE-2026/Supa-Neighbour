package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.TaskInvoice;
import com.app.api.repositories.TaskInvoiceRepository;

@Service
public class TaskInvoiceService {

    @Autowired
    private TaskInvoiceRepository taskInvoiceRepository;

    // Get all
    public List<TaskInvoice> getAllTaskInvoices() {
        return taskInvoiceRepository.findAll();
    }

    // Get by id
    public TaskInvoice getTaskInvoiceById(int id) {
        return taskInvoiceRepository.findById(id).orElse(null);
    }

    // Create
    public TaskInvoice saveTaskInvoice(TaskInvoice taskInvoice) {
        return taskInvoiceRepository.save(taskInvoice);
    }

    // Update
    public TaskInvoice updateTaskInvoice(int id, TaskInvoice updated) {
        TaskInvoice existing = taskInvoiceRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setHelperid(updated.getHelperid());
        existing.setTasktypeid(updated.getTasktypeid());
        existing.setAdminreview(updated.getAdminReview());
        existing.setCompatibilityid(updated.getCompatibilityid());
        existing.setSignedadminid(updated.getSignedadminid());
        existing.setDependentid(updated.getDependentid());
        existing.setImmediate(updated.isImmediate());
        existing.setLocationid(updated.getLocationid());
        existing.setNeedsspecialist(updated.isNeedsspecialist());
        existing.setDependentratingreview(updated.getDependentRatingreview());
        existing.setHelperRatingreview(updated.getHelperRatingreview());
        existing.setHelperbadgeid(updated.getHelperbadgeid());
        existing.setStartdate(updated.getStartdate());
        existing.setEnddate(updated.getEnddate());

        return taskInvoiceRepository.save(existing);
    }

    // Delete
    public void deleteTaskInvoice(int id) {
        taskInvoiceRepository.deleteById(id);
    }
}
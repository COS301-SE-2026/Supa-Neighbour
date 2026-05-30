package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.TaskInvoice;
import com.app.api.repositories.TaskInvoiceRepository;

/**
 * Task invoice service.
 */
@Service
public class TaskInvoiceService {

    @Autowired
    private TaskInvoiceRepository taskInvoiceRepository;

    /**
     * Get all task invoices.
     * @return list of task invoices
     */
    public List<TaskInvoice> getAllTaskInvoices() {
        return taskInvoiceRepository.findAll();
    }

    /**
     * Get task invoice by id.
     * @param id task invoice id
     * @return task invoice
     */
    public TaskInvoice getTaskInvoiceById(int id) {
        return taskInvoiceRepository.findById(id).orElse(null);
    }

    /**
     * Save task invoice.
     * @param taskInvoice task invoice
     * @return saved task invoice
     */
    public TaskInvoice saveTaskInvoice(TaskInvoice taskInvoice) {
        return taskInvoiceRepository.save(taskInvoice);
    }
}

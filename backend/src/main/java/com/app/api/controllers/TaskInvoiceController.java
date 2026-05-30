package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.TaskInvoice;
import com.app.api.services.TaskInvoiceService;

/**
 * Task invoice controller.
 */
@RestController
@RequestMapping("api/task-invoices")
public class TaskInvoiceController {

    @Autowired
    private TaskInvoiceService taskInvoiceService;

    /**
     * Get all task invoices.
     * @return task invoices
     */
    @GetMapping
    public List<TaskInvoice> getAllTaskInvoices() {
        return taskInvoiceService.getAllTaskInvoices();
    }

    /**
     * Get task invoice by id.
     * @param id task invoice id
     * @return task invoice
     */
    @GetMapping("api/task-invoices/{id}")
    public TaskInvoice getTaskInvoiceById(@PathVariable int id) {
        return taskInvoiceService.getTaskInvoiceById(id);
    }

    /**
     * Create task invoice.
     * @param taskInvoice task invoice
     * @return saved task invoice
     */
    @PostMapping
    public TaskInvoice createTaskInvoice(@RequestBody TaskInvoice taskInvoice) {
        return taskInvoiceService.saveTaskInvoice(taskInvoice);
    }
}

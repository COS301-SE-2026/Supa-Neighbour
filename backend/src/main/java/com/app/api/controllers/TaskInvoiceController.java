package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.TaskInvoice;
import com.app.api.services.TaskInvoiceService;

/**
 * REST controller for task invoice.
 */
@RestController
@RequestMapping("/api/taskinvoices")
public class TaskInvoiceController {

    @Autowired
    private TaskInvoiceService taskInvoiceService;

    /**
     * Retrieves all task invoice.
     *
     * @return a list of all task invoice
     */ 
    @GetMapping
    public ResponseEntity<List<TaskInvoice>> getAllTaskInvoices() {
        return ResponseEntity.ok(taskInvoiceService.getAllTaskInvoices());
    }

     /**
     * Retrieves a task invoice by its ID.
     *
     * @param id the task invoice ID
     * @return the task invoice if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskInvoice> getTaskInvoiceById(@PathVariable int id) {
        TaskInvoice taskInvoice = taskInvoiceService.getTaskInvoiceById(id);
        if (taskInvoice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskInvoice);
    }

    /**
     * Creates a new task invoice.
     *
     * @param location the task invoice to create
     * @return the created task invoice with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<TaskInvoice> createTaskInvoice(@RequestBody TaskInvoice taskInvoice) {
        TaskInvoice saved = taskInvoiceService.saveTaskInvoice(taskInvoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing task invoice.
     *
     * @param id the ID of the task invoice to update
     * @param likes the updated task invoice data
     * @return the updated task invoice if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskInvoice> updateTaskInvoice(@PathVariable int id, @RequestBody TaskInvoice taskInvoice) {
        TaskInvoice existing = taskInvoiceService.getTaskInvoiceById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        TaskInvoice updated = taskInvoiceService.updateTaskInvoice(id, taskInvoice);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a task invoice by its ID.
     *
     * @param id the ID of the task invoice to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskInvoice(@PathVariable int id) {
        TaskInvoice existing = taskInvoiceService.getTaskInvoiceById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        taskInvoiceService.deleteTaskInvoice(id);
        return ResponseEntity.noContent().build();
    }
}

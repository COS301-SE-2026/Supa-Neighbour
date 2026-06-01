package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.TaskInvoice;
import com.app.api.services.TaskInvoiceService;

@RestController
@RequestMapping("/api/taskinvoices")
public class TaskInvoiceController {

    @Autowired
    private TaskInvoiceService taskInvoiceService;

    // GET /api/taskinvoices    
    @GetMapping
    public ResponseEntity<List<TaskInvoice>> getAllTaskInvoices() {
        return ResponseEntity.ok(taskInvoiceService.getAllTaskInvoices());
    }

    // GET /api/taskinvoices/1
    @GetMapping("/{id}")
    public ResponseEntity<TaskInvoice> getTaskInvoiceById(@PathVariable int id) {
        TaskInvoice taskInvoice = taskInvoiceService.getTaskInvoiceById(id);
        if (taskInvoice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskInvoice);
    }

    // POST /api/taskinvoices
    @PostMapping
    public ResponseEntity<TaskInvoice> createTaskInvoice(@RequestBody TaskInvoice taskInvoice) {
        TaskInvoice saved = taskInvoiceService.saveTaskInvoice(taskInvoice);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/taskinvoices/1
    @PutMapping("/{id}")
    public ResponseEntity<TaskInvoice> updateTaskInvoice(@PathVariable int id, @RequestBody TaskInvoice taskInvoice) {
        TaskInvoice existing = taskInvoiceService.getTaskInvoiceById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        TaskInvoice updated = taskInvoiceService.updateTaskInvoice(id, taskInvoice);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/taskinvoices/1
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
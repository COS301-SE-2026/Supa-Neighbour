package com.app.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.TaskInvoice;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.TaskInvoiceService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for task invoice.
 */
@RestController
@RequestMapping("/api/taskinvoices")
@Tag(name = "Task Invoices", description = "Operations for managing task invoices")
public class TaskInvoiceController {

    private final TaskInvoiceService taskInvoiceService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param taskInvoiceService service providing analytics data for taskInvoice
     */
    public TaskInvoiceController(TaskInvoiceService taskInvoiceService, FirebaseAuthService firebaseAuthService) {
        this.taskInvoiceService = taskInvoiceService;
        this.firebaseAuthService = firebaseAuthService;
    }

    // GET /api/taskinvoices    
    /**
     * Retrieves all task invoice.
     *
     * @return a list of all task invoice
     */
    @GetMapping
    @Operation(
        summary = "Get all task invoices",
        description = "Retrieves a list of all task invoices",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved task invoices")
    public ResponseEntity<List<TaskInvoice>> getAllTaskInvoices() {
        return ResponseEntity.ok(taskInvoiceService.getAllTaskInvoices());
    }

    // GET /api/taskinvoices/1
    /**
     * Retrieves a task invoice by its ID.
     *
     * @param id the task invoice ID
     * @return the task invoice if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get task invoice by ID",
        description = "Retrieves a single task invoice by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task invoice found"),
        @ApiResponse(responseCode = "404", description = "Task invoice not found", content = @Content)
    })
    public ResponseEntity<TaskInvoice> getTaskInvoiceById(
        @Parameter(description = "ID of the task invoice to retrieve", example = "1")
        @PathVariable int id
    ) {
        TaskInvoice taskInvoice = taskInvoiceService.getTaskInvoiceById(id);
        if (taskInvoice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskInvoice);
    }

    // POST /api/taskinvoices
    /**
     * Creates a new task invoice.
     *
     * @param location the task invoice to create
     * @return the created task invoice with HTTP 201 status
     */
    @PostMapping
    @Operation(
        summary = "Create a new task invoice",
        description = "Creates a new task invoice for the authenticated user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task invoice created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task invoice data", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> createTaskInvoice(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @RequestBody(required = false) TaskInvoice taskInvoice
    ) {
        if(taskInvoice == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            TaskInvoice saved = taskInvoiceService.saveTaskInvoice(userId, taskInvoice);
            if (saved == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Adding this to handle the exception handling within tests
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }

    // PUT /api/taskinvoices/1
    /**
     * Updates an existing task invoice.
     *
     * @param id the ID of the task invoice to update
     * @param likes the updated task invoice data
     * @return the updated task invoice if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update a task invoice",
        description = "Updates an existing task invoice by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task invoice updated successfully"),
        @ApiResponse(responseCode = "404", description = "Task invoice not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid task invoice data", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> updateTaskInvoice(
        @Parameter(description = "ID of the task invoice to update", example = "1")
        @PathVariable int id,
        @RequestBody TaskInvoice taskInvoice,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ) {
        try{
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);

            TaskInvoice existing = taskInvoiceService.getTaskInvoiceById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }
            TaskInvoice updated = taskInvoiceService.updateTaskInvoice(id, taskInvoice);
            return ResponseEntity.ok(updated);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    // DELETE /api/taskinvoices/1
    /**
     * Deletes a task invoice by its ID.
     *
     * @param id the ID of the task invoice to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a task invoice",
        description = "Deletes a task invoice by its ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task invoice deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task invoice not found", content = @Content)
    })
    public ResponseEntity<Void> deleteTaskInvoice(
        @Parameter(description = "ID of the task invoice to delete", example = "1")
        @PathVariable int id
    ) {
        TaskInvoice existing = taskInvoiceService.getTaskInvoiceById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        taskInvoiceService.deleteTaskInvoice(id);
        return ResponseEntity.noContent().build();
    }
}

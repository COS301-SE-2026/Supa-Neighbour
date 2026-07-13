package com.app.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.TaskInvitation;
import com.app.api.models.TaskInvoice;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.services.TaskInvitationService;
import com.google.firebase.auth.FirebaseAuthException;
import com.app.api.services.FirebaseAuthService;
import com.app.api.models.Helper;
import com.app.api.repositories.HelperRepository;

import java.util.Map;
/**
 * TaskInvitation controller.
 * REST controller for TaskInvitation.
 */
@RestController
@RequestMapping("/api/task-invitations")
public class TaskInvitationController {

    private final TaskInvitationService taskInvitationService;
    private final TaskInvoiceRepository taskInvoiceRepository;
    private final FirebaseAuthService firebaseAuthService;
    private final HelperRepository helperRepository;


    public TaskInvitationController(TaskInvitationService taskInvitationService, TaskInvoiceRepository taskInvoiceRepository, FirebaseAuthService firebaseAuthService, HelperRepository helperRepository) {
        this.taskInvitationService = taskInvitationService;
        this.firebaseAuthService = firebaseAuthService;
        this.taskInvoiceRepository = taskInvoiceRepository;
        this.helperRepository = helperRepository;
    }
    // GET /api/task-invitations
    /**
     * Retrieves all task invitations.
     *     * @return a list of all task invitations
     */
    @GetMapping
    public ResponseEntity<List<TaskInvitation>> getAllTaskInvitations() {
        return ResponseEntity.ok(taskInvitationService.getTaskInvitations());
    }

    // GET /api/task-invitations/1
    /**
     * Retrieves a task invitation by its ID.
     *     * @param id the task invitation ID
     * @return the task invitation if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskInvitation> getTaskInvitationById(@PathVariable int id) {
        TaskInvitation invitation = taskInvitationService.getInvitationById(id);
        if (invitation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invitation);
    }

    // POST /api/task-invitations
    /**
     * Creates a new task invitation.
     *     * @param invitation the task invitation to create
     * @return the created task invitation with a 201 Created status, or 400 Bad Request if the invitation is null
     */
    @PostMapping
    public ResponseEntity<TaskInvitation> createTaskInvitation(@RequestBody TaskInvitation invitation) {
        TaskInvitation created = taskInvitationService.saveTaskInvitation(invitation);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/task-invitations/1
    /**
     * Updates an existing task invitation.
     *     * @param id         the task invitation ID
     * @param invitation the task invitation with updated details
     * @return the updated task invitation if successful, 404 Not Found if the invitation doesn't exist, or 400 Bad Request if the provided invitation is null
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskInvitation> updateTaskInvitation(@PathVariable int id, @RequestBody TaskInvitation invitation) {
        if (invitation == null) {
            return ResponseEntity.badRequest().build();
        }
        TaskInvitation updated = taskInvitationService.updateTaskInvitation(id, invitation);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/task-invitations/1
    /**
     * Deletes a task invitation by its ID.
     *     * @param id the task invitation ID
     * @return 204 No Content if successful, or 404 Not Found if the invitation doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskInvitation(@PathVariable int id) {
        TaskInvitation existing = taskInvitationService.getInvitationById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        taskInvitationService.deleteTaskInvitation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/accept")
    public ResponseEntity<?> acceptTask(
        @PathVariable int taskId,
        @RequestHeader("Authorization") String authHeader
    ){
        int callerId;
        try{
            String token = authHeader.replace("Bearer ", "");
            callerId = firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Helper helper = helperRepository.findByUserid_Userid(callerId).orElse(null);
        if(helper == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "User is not a helper"));
        }

        TaskInvoice taskInvoice = taskInvoiceRepository.findById(taskId).orElse(null);
        if(taskInvoice == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
        }

        if(!"open".equals(taskInvoice.getStatus())){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "Task is not available for acceptance"));
        }

        try{
            taskInvitationService.acceptInvitation(taskId, helper.getHelperid(), taskInvoice, helper);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "Task accepted successfully.",
            "taskId", taskId,
            "status", "Accepted"
        ));
        }catch(IllegalStateException e){
            if("CONFLICT".equals(e.getMessage())){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "This task has already been accepted"));
            }

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "Task is not available for acceptance"));
        }
    }
}

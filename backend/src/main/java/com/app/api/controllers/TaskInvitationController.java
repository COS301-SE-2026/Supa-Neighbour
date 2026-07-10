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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.TaskInvitation;
import com.app.api.services.TaskInvitationService;
/**
 * TaskInvitation controller.
 * REST controller for TaskInvitation.
 */
@RestController
@RequestMapping("/api/task-invitations")
public class TaskInvitationController {

    private final TaskInvitationService taskInvitationService;

    public TaskInvitationController(TaskInvitationService taskInvitationService) {
        this.taskInvitationService = taskInvitationService;
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
}

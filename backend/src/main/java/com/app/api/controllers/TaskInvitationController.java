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
import com.app.api.models.Helper;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.TaskInvitationService;
import com.google.firebase.auth.FirebaseAuthException;
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
    private final HelperRepository helperRepository;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs the controller with its required collaborators.
     *
     * @param taskInvitationService service handling invite/accept/decline business logic
     * @param helperRepository      repository for looking up {@link Helper} records
     * @param firebaseAuthService   service for validating Firebase tokens and resolving user IDs
     * @param taskInvoiceRepository repository for looking up {@link TaskInvoice} records
     */
    public TaskInvitationController(TaskInvitationService taskInvitationService, HelperRepository helperRepository, FirebaseAuthService firebaseAuthService, TaskInvoiceRepository taskInvoiceRepository) {
        this.taskInvitationService = taskInvitationService;
        this.taskInvoiceRepository = taskInvoiceRepository;
        this.helperRepository = helperRepository;
        this.firebaseAuthService = firebaseAuthService;
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

    /**
     * Invites a helper to a specific task.
     * <p>
     * Only the requester (dependent) who owns the task may send invitations,
     * and the task must currently be in the "open" status. Creates a new
     * {@link TaskInvitation} record with status "Invited".
     *
     * @param taskId     the ID of the task to invite a helper to
     * @param body       request body containing the {@code helperId} of the helper being invited
     * @param authHeader the Firebase Authorization header ("Bearer &lt;token&gt;")
     * @return 201 Created with invitation details on success;
     *         400 if {@code helperId} is missing;
     *         401 if the token is invalid or missing;
     *         403 if the caller does not own the task;
     *         404 if the task or helper does not exist;
     *         409 if the task is not open or the helper has already been invited
     */
    @PostMapping("/{taskId}/invite")
    public ResponseEntity<?> inviteHelper(
        @PathVariable int taskId,
        @RequestBody Map<String, Integer> body,
        @RequestHeader("Authorization") String authHeader
    ){
        int callerId;
        try{
            String token = authHeader.replace("Bearer ", "");
            callerId = firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Integer helperId = body.get("helperId");
        if(helperId == null){
            return ResponseEntity.badRequest().body(Map.of("error", "helperId is required"));
        }

        TaskInvoice taskInvoice = taskInvoiceRepository.findById(taskId).orElse(null);
        if(taskInvoice == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
        }

        if (!"open".equals(taskInvoice.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Task is not open for invitations"));
        }

        if (taskInvoice.getDependentid().getUserId().getUserid() != callerId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorised to invite helpers to this task"));
        }

        Helper helper = helperRepository.findById(helperId).orElse(null);
        if(helper == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
        }

        TaskInvitation invitation = taskInvitationService.inviteHelper(taskId, helperId, taskInvoice, helper);

        if(invitation ==  null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Helper already invited"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "Invitation sent",
            "taskId", taskId,
            "helperId", helperId,
            "status", "Invited"
    ));
    }

    /**
     * Accepts a task invitation on behalf of the calling helper.
     * <p>
     * The caller must be a registered helper, and the target task must be
     * in "open" status. On success, the underlying invitation/task record
     * transitions to "Accepted".
     *
     * @param taskId     the ID of the task being accepted
     * @param authHeader the Firebase Authorization header ("Bearer &lt;token&gt;")
     * @return 201 Created with acceptance details on success;
     *         401 if the token is invalid or missing;
     *         403 if the caller is not a registered helper;
     *         404 if the task does not exist;
     *         409 if the task has already been accepted;
     *         422 if the task is not otherwise available for acceptance
     */
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


    /**
     * Declines a task invitation on behalf of the calling helper.
     * <p>
     * The caller must be a registered helper, and the target task must be
     * in "open" status. On success, the underlying invitation/task record
     * transitions to "Declined".
     *
     * @param taskId     the ID of the task being declined
     * @param authHeader the Firebase Authorization header ("Bearer &lt;token&gt;")
     * @return 200 OK with decline details on success;
     *         401 if the token is invalid or missing;
     *         403 if the caller is not a registered helper;
     *         404 if the task does not exist;
     *         409 if the task cannot be declined in its current state;
     *         422 if the task is not otherwise available for declining
     */
    @PostMapping("/{taskId}/decline")
    public ResponseEntity<?> declineTask(
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "User is not a helper"));
        }

        TaskInvoice taskInvoice = taskInvoiceRepository.findById(taskId).orElse(null);
        if(taskInvoice == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
        }

        if (!"open".equals(taskInvoice.getStatus())) {
            System.out.println(taskInvoice.getStatus());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("error", "Task is not available for declining"));
        }


        try{
            TaskInvitation updated = taskInvitationService.declineInvitation(taskId, helper.getHelperid(), taskInvoice, helper);
            return ResponseEntity.ok(
                Map.of(
                "message", "Task declined.",
                "taskId", taskId,
                "status", "Declined"
            ));

        }catch(IllegalStateException e){
            if("CONFLICT".equals(e.getMessage())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Task cannot be declined in its current state"));
            }

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "Task is not available for declining"));
        }

    }
}

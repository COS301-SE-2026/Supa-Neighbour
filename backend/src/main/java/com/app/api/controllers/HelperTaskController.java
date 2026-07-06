package com.app.api.controllers;

import com.app.api.dtos.HelperTaskResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.HelperTasksService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/helpers")
public class HelperTaskController {
    private final HelperTasksService helperTasksService;
    private final FirebaseAuthService firebaseAuthService;


    public HelperTaskController(HelperTasksService helperTasksService, FirebaseAuthService firebaseAuthService){
        this.helperTasksService = helperTasksService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * GET /api/helpers/me/tasks
     *
     * Returns the full task history for the authenticated helper across
     * both task_invitation_table (Invited/Declined) and
     * task_invoice_table (assigned/in_progress/pending_approval/completed/cancelled).
     */

    @GetMapping("/me/tasks")
    public ResponseEntity<?> getMyTasks(
        @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false)           String statusFilter,
            @RequestParam(defaultValue = "20")        int    limit,
            @RequestParam(defaultValue = "0")         int    offset
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            HelperTaskResponse response = helperTasksService.getTasks(userId, statusFilter, limit, offset);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

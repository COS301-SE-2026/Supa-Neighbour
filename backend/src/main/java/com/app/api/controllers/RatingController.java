package com.app.api.controllers;

import com.app.api.dtos.RatingRequest;
import com.app.api.dtos.RatingResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.RatingService;
import com.google.firebase.auth.FirebaseAuthException;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/tasks")
public class RatingController {

    private final RatingService ratingService;
    private final FirebaseAuthService firebaseAuthService;

    public RatingController(RatingService ratingService, FirebaseAuthService firebaseAuthService){
        this.ratingService = ratingService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * POST /api/tasks/{taskId}/rate
     *
     * Called by the requester (dependent) after a task is completed.
     * Writes the rating to task_invoice_table and recalculates
     * the helper's average_rating in helper_analytics_table.
     */
    @PostMapping("/{taskId}/rate")
    public ResponseEntity<?> rateTask(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable int taskId,
        @Valid @RequestBody RatingRequest request
    ){
        
        try{
            String token = authHeader.replace("Bearer ", "");
            int callerId = firebaseAuthService.getUserIdFromToken(token);
            RatingResponse response = ratingService.submitRating(taskId, callerId, request);
            return ResponseEntity.ok(response);
        }catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

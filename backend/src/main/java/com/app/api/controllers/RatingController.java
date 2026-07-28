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


/**
 * REST controller that provides endpoints for submitting ratings
 * for completed tasks.
 */
@RestController
@RequestMapping("/api/tasks")
public class RatingController {

    private final RatingService ratingService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a {@code RatingController} with the required services.
     *
     * @param ratingService service responsible for processing task ratings
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the associated user ID
     */
    public RatingController(RatingService ratingService, FirebaseAuthService firebaseAuthService){
        this.ratingService = ratingService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Submits a rating for a completed task.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the rating
     * is recorded. The submitted rating is stored, and the helper's
     * average rating is recalculated.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @param taskId the identifier of the task being rated
     * @param request the rating details submitted by the requester
     * @return a {@link ResponseEntity} containing the rating result if the
     *         request is successful, or a 401 Unauthorized response if
     *         the Firebase token is invalid or expired
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

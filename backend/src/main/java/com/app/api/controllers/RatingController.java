package com.app.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.RatingRequest;
import com.app.api.dtos.RatingResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.RatingService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller that provides endpoints for submitting ratings
 * for completed tasks.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Ratings", description = "Endpoints for submitting task ratings")
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
    @Operation(
        summary = "Rate a completed task",
        description = "Submits a rating for a completed task. The helper's average rating is recalculated after submission.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rating submitted successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid rating data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Rating must be between 1 and 5\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired Firebase token",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "Invalid or expired Firebase token"
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to rate this task",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"You are not authorized to rate this task\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found or not completed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Task not found or not completed\"}"
                )
            )
        )
    })
    public ResponseEntity<?> rateTask(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @Parameter(description = "ID of the task being rated", example = "1")
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

package com.app.api.controllers;

import com.app.api.dtos.AchievementResponse;
import com.app.api.services.AchievementService;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that provides endpoints for retrieving the
 * authenticated user's achievements.
 */
@RestController
@RequestMapping("/api/users/me")
public class AchievementsController {
    
    private final AchievementService achievementService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs an {@code AchievementsController} with the required services.
     *
     * @param achievementService service responsible for retrieving user achievements
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the associated user ID
     */
    public AchievementsController(AchievementService achievementService, FirebaseAuthService firebaseAuthService){
        this.achievementService = achievementService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Retrieves the achievements of the authenticated user.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header, validated, and used to determine
     * the user's ID before fetching their achievements.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @return a {@link ResponseEntity} containing the user's achievements if the
     *         token is valid, or a 401 Unauthorized response if the token is
     *         invalid or expired
     */
    @GetMapping("/achievements")
    @Operation(
        summary = "Get authenticated user's achievements",
        description = "Retrieves all achievements earned by the currently authenticated user. " +
                      "Requires a valid Firebase authentication token.",
        security = @SecurityRequirement(name = "BearerAuth"),
        tags = {"Achievements"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user achievements",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AchievementResponse.class),
                examples = @ExampleObject(
                    name = "Achievements Response",
                    value = """
                        {
                            "achievements": [
                                {
                                    "id": 1,
                                    "name": "First Milestone",
                                    "description": "Completed your first task",
                                    "earnedDate": "2026-08-28T10:30:00Z",
                                }
                            ],
                            "total": 1
                        }
                        """
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
        )
    })
    public ResponseEntity<?> getAchievements(
        @RequestHeader("Authorization") String authHeader
    ){
      
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            AchievementResponse response = achievementService.getAchievements(userId);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

package com.app.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.UpdateProfileRequest;
import com.app.api.dtos.UpdateProfileResponse;
import com.app.api.dtos.UserProfileResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.UserProfileService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that provides endpoints for retrieving and
 * updating the authenticated user's profile.
 */
@RestController
@RequestMapping("/api/users/me")
@Tag(name = "User Profile", description = "Endpoints for retrieving and updating the authenticated user's profile")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a {@code UserProfileController} with the required services.
     *
     * @param userProfileService service responsible for retrieving and updating
     *                           user profile information
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the associated user ID
     */
    public UserProfileController(UserProfileService userProfileService, FirebaseAuthService firebaseAuthService){
        this.userProfileService = userProfileService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Retrieves the profile of the authenticated user.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the user's
     * profile is returned. Helper-specific fields are {@code null}
     * for users who are not registered as helpers.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @return a {@link ResponseEntity} containing the authenticated user's
     *         profile if the token is valid, or a 401 Unauthorized response
     *         if the token is invalid or expired
     */
    @GetMapping("profile")
    @Operation(
        summary = "Get authenticated user's profile",
        description = "Retrieves the profile of the authenticated user. Helper-specific fields are null for non-helpers.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
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
    public ResponseEntity<?> getMyProfile(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            UserProfileResponse response = userProfileService.getProfile(userId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Updates the profile of the authenticated user.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the supplied
     * profile information is applied.</p>
     *
     * @param request the profile fields to update
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @return a {@link ResponseEntity} containing the updated profile
     *         information if the request is successful, or a 401
     *         Unauthorized response if the token is invalid or expired
     */
    @PatchMapping("profile")
    @Operation(
        summary = "Update authenticated user's profile",
        description = "Updates the profile of the authenticated user with the provided fields",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
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
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Invalid profile data provided\"}"
                )
            )
        )
    })
    public ResponseEntity<?> updateProfile(
        @RequestBody UpdateProfileRequest request,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            UpdateProfileResponse response = userProfileService.updateProfile(userId, request);
            System.out.println("Leaving controller successfully");
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

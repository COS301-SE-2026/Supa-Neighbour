package com.app.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.HelperProfileResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.HelperProfileService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that provides endpoints for retrieving
 * public helper profile information.
 */
@RestController
@RequestMapping("/api/helpers")
@Tag(name = "Helper Profiles", description = "Endpoints for retrieving public helper profile information")
public class HelperProfileController {
    private final HelperProfileService helperProfileService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a {@code HelperProfileController} with the required services.
     *
     * @param helperProfileService service responsible for retrieving helper profiles
     * @param firebaseAuthService service used to authenticate Firebase tokens
     */
    public HelperProfileController(HelperProfileService helperProfileService, FirebaseAuthService firebaseAuthService){
        this.helperProfileService = helperProfileService;
        this.firebaseAuthService = firebaseAuthService;
    }

    
    /**
     * Retrieves the public profile of a helper.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the helper's
     * public profile is returned. Sensitive information such as
     * addresses and contact details is excluded from the response.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @param helperId the identifier of the helper whose profile is requested
     * @return a {@link ResponseEntity} containing the helper's public profile
     *         if the request is successful, or a 401 Unauthorized response
     *         if the Firebase token is invalid or expired
     */
    @GetMapping("{helperId}/profile")
    @Operation(
        summary = "Get helper profile by helper ID",
        description = "Retrieves the public profile of a helper using their helper ID. " +
                      "Sensitive information such as addresses and contact details is excluded.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Helper profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Helper not found", content = @Content)
    })
    public ResponseEntity<?> getHelperProfile(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @Parameter(description = "ID of the helper", example = "1")
        @PathVariable int helperId
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.verifyIdToken(token);
            HelperProfileResponse response = helperProfileService.getProfile(helperId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase Token");
        }
    }

    /**
     * Retrieves the public profile of a helper by their user ID.
     *
     * <p>Use this endpoint when you have a user ID rather than a helper ID.
     * The backend resolves the user ID to the correct helper record.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @param userId the user identifier of the helper
     * @return a {@link ResponseEntity} containing the helper's public profile
     *         if the request is successful, or a 401/404 otherwise
     */
    @GetMapping("by-user/{userId}/profile")
    @Operation(
        summary = "Get helper profile by user ID",
        description = "Retrieves the public profile of a helper using their user ID. " +
                      "Use this endpoint when you have a user ID rather than a helper ID.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Helper profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "Helper not found", content = @Content)
    })
    public ResponseEntity<?> getHelperProfileByUserId(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @Parameter(description = "User ID of the helper", example = "1")
        @PathVariable int userId
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.verifyIdToken(token);
            HelperProfileResponse response = helperProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase Token");
        }
    }
}

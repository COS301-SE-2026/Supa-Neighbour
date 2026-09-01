package com.app.api.controllers;

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
import org.springframework.web.server.ResponseStatusException;

import com.app.api.dtos.ModeResponse;
import com.app.api.dtos.ShowStatusRequest;
import com.app.api.dtos.ShowStatusResponse;
import com.app.api.dtos.UpdateSettingsDTO;
import com.app.api.dtos.UserSettingsDTO;
import com.app.api.dtos.UserStatusResponse;
import com.app.api.models.User;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.SettingsServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller responsible for managing user application settings.
 * <p>
 * Provides endpoints for retrieving and updating a user's visibility
 * status and application mode. All endpoints require a valid Firebase
 * authentication token supplied in the Authorization header.
 * </p>
 */
@RestController
@RequestMapping("api/settings")
@Tag(name = "Settings", description = "Endpoints for managing user application settings")
public class SettingsController {

    private final FirebaseAuthService firebaseAuthService;
    private final SettingsServices settingsServices;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;

    /**
     * Constructs a new {@code SettingsController}.
     *
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the authenticated user's ID
     * @param settingsServices    service responsible for retrieving and
     *                            updating user settings
     */
    public SettingsController(FirebaseAuthService firebaseAuthService, SettingsServices settingsServices,
            UserRepository userRepository, SettingsRepository settingsRepository) {
        this.firebaseAuthService = firebaseAuthService;
        this.settingsServices = settingsServices;
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
    }

    /**
     * Retrieves the authenticated user's visibility status.
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @return HTTP 200 containing the user's current visibility status,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @GetMapping("/users/show-status")
    @Operation(
        summary = "Get user visibility status",
        description = "Retrieves the authenticated user's visibility status",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visibility status retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> getStatus(
            @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            UserStatusResponse response = settingsServices.getUserStatus(userId);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Updates the authenticated user's visibility status.
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @param request    request containing the new visibility status
     * @return HTTP 200 containing the updated visibility status,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @PostMapping("/users/show-status")
    @Operation(
        summary = "Update user visibility status",
        description = "Updates the authenticated user's visibility status",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visibility status updated successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    public ResponseEntity<?> updateStatus(
            @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ShowStatusRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ShowStatusResponse response = settingsServices.updateShowStatus(userId, request.getshowStatus());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Retrieves the authenticated user's current application mode.
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @return HTTP 200 containing the user's current application mode,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @GetMapping("/users/mode")
    @Operation(
        summary = "Get user application mode",
        description = "Retrieves the authenticated user's current application mode (LIGHT or DARK)",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application mode retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> getMode(
            @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ModeResponse response = settingsServices.getUserMode(userId);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Updates the authenticated user's application mode.
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @param request    request containing the new application mode
     * @return HTTP 200 containing the updated application mode,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @PostMapping("/users/mode")
    @Operation(
        summary = "Update user application mode",
        description = "Updates the authenticated user's application mode (LIGHT or DARK)",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application mode updated successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid mode value", content = @Content)
    })
    public ResponseEntity<?> setMode(
            @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ModeResponse request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ModeResponse response = settingsServices.setUserMode(userId, request.getMode());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Retrieves the online status information for a specified user.
     * <p>
     * A valid Firebase authentication token is required to access this
     * endpoint. The authenticated user may retrieve the visibility and
     * online status information of another user, subject to that user's
     * visibility settings.
     * </p>
     *
     * @param authHeader the Firebase Bearer token contained in the
     *                   Authorization header
     * @param userId     the unique identifier of the user whose status
     *                   is being requested
     * @return HTTP 200 containing the user's status information,
     *         or HTTP 401 if the Firebase token is invalid or expired
     */
    @GetMapping("/users/{userId}/status")
    @Operation(
        summary = "Get user status by ID",
        description = "Retrieves the online status information for a specified user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User status retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<?> getStatusForUser(
            @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "ID of the user to retrieve status for", example = "1")
            @PathVariable int userId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            UserStatusResponse response = settingsServices.getUserStatus(userId);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Retrieves settings and profile information for the specified user.
     *
     * @param userId     the identifier of the user
     * @param authHeader the Firebase Bearer token
     * @return the user's settings information, or 401 if unauthenticated
     */
    @GetMapping("/users/information/{userId}")
    @Operation(
        summary = "Get user settings information",
        description = "Retrieves settings and profile information for the specified user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User settings retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<?> getUserInfo(
            @Parameter(description = "ID of the user to retrieve settings for", example = "1")
            @PathVariable int userId,
            @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            UserSettingsDTO response = settingsServices.getUserInfo(userId);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid Firebase Token");
        }
    }

    /**
     * Updates settings for the specified user.
     *
     * @param userId     the identifier of the user
     * @param dto        the settings fields to update
     * @param authHeader the Firebase Bearer token
     * @return the updated settings information, or 401 if unauthenticated
     */
    @PutMapping("/{userId}")
    @Operation(
        summary = "Update user settings",
        description = "Updates settings for the specified user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User settings updated successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid settings data", content = @Content)
    })
    public ResponseEntity<?> updateSettings(
            @Parameter(description = "ID of the user to update settings for", example = "1")
            @PathVariable int userId,
            @RequestBody UpdateSettingsDTO dto,
            @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            UserSettingsDTO response = settingsServices.updateSettings(userId, dto);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid Firebase Token");
        }
    }

    /**
     * Deletes the authenticated user's account.
     *
     * @param authHeader the Firebase Bearer token
     * @return 204 No Content on success
     */
    @DeleteMapping("/me/user")
    @Operation(
        summary = "Delete user account",
        description = "Permanently deletes the authenticated user's account and all associated data",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Account deleted successfully", content = @Content),
        @ApiResponse(responseCode = "401", description = "Invalid Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            if (user.getFirebaseUid() != null) {
                FirebaseAuth.getInstance().deleteUser(user.getFirebaseUid());
            }
            settingsRepository.deleteById(userId);
            userRepository.delete(user);
            return ResponseEntity.noContent().build();
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Firebase Token");
        }
    }
}

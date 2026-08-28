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

import com.app.api.dtos.DeviceTokenRequestDTO;
import com.app.api.models.User;
import com.app.api.repositories.UserDeviceRepository;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.UserService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that provides endpoints for managing users.
 * <p>
 * Supports operations to create, retrieve, update, and delete users.
 * All endpoints are accessible under the {@code /api/users} path.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing user accounts")
public class UserController {

    /**
     * Service used to perform user-related business logic.
     */
    private final UserService userService;
    private final UserDeviceRepository userDeviceRepository;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * User Constructor
     * @param userService service for the user controller
     */
    public UserController(UserService userService, UserDeviceRepository userDeviceRepository, FirebaseAuthService firebaseAuthService) {
        this.userService = userService;
        this.firebaseAuthService = firebaseAuthService;
        this.userDeviceRepository = userDeviceRepository;
    }

    /**
     * Retrieves all users.
     *
     * @return a {@code ResponseEntity} containing a list of all users and
     *         an HTTP 200 (OK) status.
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the ID of the user to retrieve.
     * @return a {@code ResponseEntity} containing the user and HTTP 200 (OK)
     *         if found, or HTTP 404 (Not Found) if the user does not exist.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a single user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<User> getUserById(
        @Parameter(description = "ID of the user to retrieve", example = "1")
        @PathVariable int id
    ) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Creates a new user.
     *
     * @param user the user object containing the details of the user to create.
     * @return a {@code ResponseEntity} containing the newly created user and
     *         an HTTP 201 (Created) status.
     */
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content)
    })
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update.
     * @param user the updated user information.
     * @return a {@code ResponseEntity} containing the updated user and
     *         an HTTP 200 (OK) status if the user exists, or
     *         HTTP 404 (Not Found) if no user with the specified ID exists.
     */
    @PutMapping
    @Operation(
        summary = "Update authenticated user",
        description = "Updates the authenticated user's information",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content)
    })
    public ResponseEntity<User> updateUser(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @RequestBody User user
    ) {
        int userId;
        try{
            String token = authHeader.replace("Bearer ", "");
            userId = firebaseAuthService.getUserIdFromToken(token);
            User updated = userService.updateUser(userId, user);
            if(updated == null){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the ID of the user to delete.
     * @return a {@code ResponseEntity} with HTTP 204 (No Content) if the user
     *         was successfully deleted, or HTTP 404 (Not Found) if no user
     *         with the specified ID exists.
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a user",
        description = "Deletes a user by their ID",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "ID of the user to delete", example = "1")
        @PathVariable int id
    ) {
        User existing = userService.getUserById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Registers or updates a device's Firebase Cloud Messaging (FCM) token for the authenticated user.
     * 
     * <p>This endpoint allows a client application to register its device token with the server,
     * enabling push notifications to be sent to that specific device. If the user already has
     * a registered token for this device, it will be updated; otherwise, a new token record
     * will be created.
     * 
     * <p>Once registered, the token can be used by the notification service to deliver
     * push notifications to the user's device. This is essential for features such as:
     * <ul>
     *   <li>Task assignment notifications (helper matching)</li>
     *   <li>Task start notifications to requesters</li>
     *   <li>Post creation and comment notifications</li>
     * </ul>
     * 
     * <p><strong>Important:</strong> The client should call this endpoint whenever:
     * <ul>
     *   <li>The app is first installed</li>
     *   <li>The user logs in</li>
     *   <li>The FCM token is refreshed by Firebase</li>
     *   <li>The app is relaunched</li>
     * </ul>
     * 
     * @param authHeader The Authorization header containing the Bearer JWT token.
     *                   Expected format: "Bearer {token}". This token is validated
     *                   and used to authenticate the user.
     * @param request The request body containing the device's FCM token information.
     *                Must include a valid FCM token string.
     * @return A {@code ResponseEntity} with:
     *         <ul>
     *           <li>{@code 200 OK} if the token was successfully registered or updated</li>
     *           <li>{@code 401 Unauthorized} if the authentication token is invalid,
     *               expired, or cannot be verified by Firebase Authentication</li>
     *           <li>{@code 400 Bad Request} if the request body is invalid or the FCM token
     *               is missing/empty (if validation is implemented)</li>
     *         </ul>
     **/
    @PostMapping("/me/device-token")
    @Operation(
        summary = "Register device token for push notifications",
        description = "Registers or updates a device's Firebase Cloud Messaging (FCM) token for the authenticated user",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device token registered successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    public ResponseEntity<?> registerDeviceToken(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @RequestBody DeviceTokenRequestDTO request
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            userDeviceRepository.upsertToken(userId, request.getFcmToken());
            return ResponseEntity.ok().build();
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).build();
        }      
    }
}
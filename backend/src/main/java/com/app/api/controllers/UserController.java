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

import com.app.api.models.User;
import com.app.api.services.UserService;
import com.app.api.repositories.UserDeviceRepository;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.app.api.dtos.DeviceTokenRequestDTO;

/**
 * REST controller that provides endpoints for managing users.
 * <p>
 * Supports operations to create, retrieve, update, and delete users.
 * All endpoints are accessible under the {@code /api/users} path.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

     /**
     * Service used to perform user-related business logic.
     */
    private final UserService userService;
    private final UserDeviceRepository userDeviceRepository;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * User Contructor
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
    public ResponseEntity<User> getUserById(@PathVariable int id) {
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
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
        User existing = userService.getUserById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setFirebaseUid(user.getFirebaseUid());
        existing.setEmailVerified(user.isEmailVerified());
        existing.setPhoneVerified(user.isPhoneVerified());
        existing.setUsername(user.getUsername());
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setEmail(user.getEmail());
        existing.setPhoneNumber(user.getPhoneNumber());
        existing.setDateOfBirth(user.getDateOfBirth());
        existing.setGender(user.getGender());
        existing.setIsAdmin(user.getIsAdmin());
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
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
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
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
     * <p>The registration process involves:
     * <ol>
     *   <li>Validating the user's authentication via JWT token from the Authorization header</li>
     *   <li>Extracting the user ID from the authenticated token using Firebase Authentication</li>
     *   <li>Persisting or updating the FCM token in the database for the identified user</li>
     * </ol>
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
    public ResponseEntity<?> registerDeviceToken(
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

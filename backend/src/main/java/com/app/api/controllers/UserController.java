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

    @PostMapping("/me/device-token")
    public ResponseEntity<?> registerDeviceToken(
        @RequestHeader("Authorization") String authHeader,
        @RequestHeader DeviceTokenRequestDTO request
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

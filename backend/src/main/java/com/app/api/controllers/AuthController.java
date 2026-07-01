package com.app.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.app.api.repositories.UserRepository;
import com.app.api.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.core.Authentication;
import com.app.api.security.AuthenticatedUser;

/**
 * REST controller responsible for user authentication and account management.
 * <p>
 * This controller provides endpoints for:
 * <ul>
 *     <li>Registering a new user using a Firebase ID token.</li>
 *     <li>Logging in an existing user.</li>
 *     <li>Retrieving the authenticated user's profile.</li>
 * </ul>
 * Authentication is performed using Firebase Authentication, while user
 * information is persisted in the application's database.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    /**
     * Service responsible for verifying Firebase ID tokens.
     */
    private final FirebaseAuthService firebaseAuthService;
    /**
     * Repository used to manage application users.
     */
    private final UserRepository userRepository;

    /**
     * Creates a new authentication controller.
     *
     * @param firebaseAuthService the Firebase authentication service
     * @param userRepository the repository used to manage users
     */
    public AuthController(FirebaseAuthService firebaseAuthService, UserRepository userRepository) {
        this.firebaseAuthService = firebaseAuthService;
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user using a Firebase ID token.
     * <p>
     * The provided Firebase token is verified, and if no existing user is found
     * with the corresponding Firebase UID, a new user is created and saved.
     * </p>
     *
     * @param idToken the Authorization header containing the Firebase Bearer token
     * @return a {@link ResponseEntity} containing the newly created user, or
     *         an error response if the user already exists
     * @throws FirebaseAuthException if the Firebase token is invalid or cannot be verified
     */
@PostMapping("/register")
public ResponseEntity<?> registerUser(@RequestHeader("Authorization") String idToken) throws FirebaseAuthException {
        String token = idToken.replace("Bearer ", "");
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(token);

        if(userRepository.findByFirebaseUid(decodedToken.getUid()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User newUser = new User();
        newUser.setFirebaseUid(decodedToken.getUid());
        newUser.setEmail(decodedToken.getEmail());
        userRepository.save(newUser);

        return ResponseEntity.ok(newUser);
    }

    /**
     * Authenticates an existing user.
     * <p>
     * The Firebase ID token is verified and the corresponding application user
     * is retrieved from the database.
     * </p>
     *
     * @param idToken the Authorization header containing the Firebase Bearer token
     * @return a {@link ResponseEntity} containing the authenticated user
     * @throws FirebaseAuthException if the Firebase token cannot be verified
     * @throws RuntimeException if no user exists for the authenticated Firebase account
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String idToken) throws FirebaseAuthException {
        String token = idToken.replace("Bearer ", "");
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(token);

        User user =userRepository.findByFirebaseUid(decodedToken.getUid()).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }
    
    /**
     * Returns the profile of the currently authenticated user.
     *
     * @param authentication the current Spring Security authentication object
     * @return the authenticated {@link User}
     */
    @GetMapping("/profile")
    public User getProfile(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        return authenticatedUser.getUser();
    } 
}

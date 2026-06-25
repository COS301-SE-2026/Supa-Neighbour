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

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final FirebaseAuthService firebaseAuthService;
    private final UserRepository userRepository;

    public AuthController(FirebaseAuthService firebaseAuthService, UserRepository userRepository) {
        this.firebaseAuthService = firebaseAuthService;
        this.userRepository = userRepository;
    }

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String idToken) throws FirebaseAuthException {
        String token = idToken.replace("Bearer ", "");
        FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(token);

        User user =userRepository.findByFirebaseUid(decodedToken.getUid()).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/profile")
    public User getProfile(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        return authenticatedUser.getUser();
    }
    
    
}

package com.app.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.app.api.services.FirebaseAuthService;
import com.app.api.repositories.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.firebase.auth.FirebaseAuthException;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final FirebaseAuthService firebaseAuthService;
    private final UserRepository userRepository;

    public AuthController(FirebaseAuthService firebaseAuthService, UserRepository userRepository) {
        this.firebaseAuthService = firebaseAuthService;
        this.userRepository = userRepository;
    }
    
}

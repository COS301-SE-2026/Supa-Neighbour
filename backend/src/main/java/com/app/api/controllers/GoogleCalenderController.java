package com.app.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.app.api.services.GoogleCalenderTokenService;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

@RestController 
@RequestMapping("/api/users/me/google-calender")
public class GoogleCalenderController {
    private final GoogleCalenderTokenService tokenService;
    private final FirebaseAuthService firebaseAuthService;

    public GoogleCalenderController(
            GoogleCalenderTokenService tokenService, 
            FirebaseAuthService firebaseAuthService
    ){
        this.tokenService = tokenService;
        this.firebaseAuthService = firebaseAuthService;
    }

    @PostMapping("/connect")
    public ResponseEntity<String> connect(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody ConnectRequest body
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            tokenService.exchangeAndStore(userId, body.authCode());
            return ResponseEntity.ok("connected");
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }catch (Exception e) {
        return ResponseEntity.status(500).body("Failed to connect Google Calendar");
        }
    }
    public record ConnectRequest(String authCode) {}
}

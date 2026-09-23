package com.app.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.services.FirebaseAuthService;
import com.app.api.services.GoogleCalenderTokenService;
import com.google.firebase.auth.FirebaseAuthException;

@RestController 
@RequestMapping("/api/users/me/google-calender")
public class GoogleCalenderController {
    private final GoogleCalenderTokenService tokenService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a new {@code GoogleCalenderController}.
     *
     * @param tokenService service responsible for exchanging and storing
     *                     Google Calendar authorization credentials
     * @param firebaseAuthService service responsible for validating Firebase
     *                            authentication tokens and retrieving user IDs
     */
    public GoogleCalenderController(
            GoogleCalenderTokenService tokenService, 
            FirebaseAuthService firebaseAuthService
    ){
        this.tokenService = tokenService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Connects the authenticated user's Google Calendar account.
     *
     * <p>Validates the Firebase authentication token, retrieves the user's ID,
     * and exchanges the provided Google authorization code for calendar
     * credentials.</p>
     *
     * @param authHeader the Firebase Bearer authentication token
     * @param body the request containing the Google Calendar authorization code
     * @return a successful response if the account was connected, or an error
     *         response if authentication or connection fails
     */
    @PostMapping("/connect")
    public ResponseEntity<String> connect(
        @RequestHeader("Authorization") String authHeader, 
        @RequestBody ConnectRequest body
    ){
        try{
            if (body == null || body.authCode() == null || body.authCode().isBlank()) {
                return ResponseEntity.badRequest().body("Google authorization code is required");
            }

            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            tokenService.exchangeAndStore(userId, body.authCode());
            return ResponseEntity.ok("connected");
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            String message = e.getMessage() == null ? "Unknown OAuth error" : e.getMessage();
            return ResponseEntity.status(500).body("Failed to connect Google Calendar: " + message);
        }
    }
    /**
     * Request body containing the Google Calendar authorization code.
     *
     * @param authCode the authorization code provided by Google
     */
    public record ConnectRequest(String authCode) {}
}

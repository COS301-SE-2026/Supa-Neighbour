package com.app.api.controllers;

import com.app.api.dtos.UserProfileResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.UserProfileService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {
    private final UserProfileService userProfileService;
    private final FirebaseAuthService firebaseAuthService;

    private UserProfileController(UserProfileService userProfileService, FirebaseAuthService firebaseAuthService){
        this.userProfileService = userProfileService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * GET /api/users/me/profile
     *
     * Returns the authenticated user's own profile.
     * Helper-specific fields (trustScore, currentXp, skills, recentTasks)
     * are null for non-helper users.
     */
    @GetMapping("profile")
    public ResponseEntity<?> getMyProfile(
        @RequestHeader("Authorization") String authHeader
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            UserProfileResponse response = userProfileService.getProfile(userId);
            return ResponseEntity.ok(response);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

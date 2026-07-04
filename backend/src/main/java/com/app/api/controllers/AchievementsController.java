package com.app.api.controllers;

import com.app.api.dtos.AchievementResponse;
import com.app.api.services.AchievementService;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

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


@RestController
@RequestMapping("/api/users/me")
public class AchievementsController {
    
    private final AchievementService achievementService;
    private final FirebaseAuthService firebaseAuthService;

    public AchievementsController(AchievementService achievementService, FirebaseAuthService firebaseAuthService){
        this.achievementService = achievementService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * GET /api/users/me/achievements
     *
     * Returns all achievements for the authenticated user split into
     * earned (awarded_on IS NOT NULL) and unearned (awarded_on IS NULL).
     */
    @GetMapping("/achievements")
    public ResponseEntity<?> getAchievements(
        @RequestHeader("Authorization") String authHeader
    ){
      
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            AchievementResponse response = achievementService.getAchievements(userId);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

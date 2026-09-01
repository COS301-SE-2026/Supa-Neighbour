package com.app.api.controllers;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.dtos.NotificationDTO;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.NotificationsService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Parameter;


@RestController
@RequestMapping("/api")
public class NotificationController {
    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @GetMapping("/notifications")
    public ResponseEntity<?>  getMyNotifications(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ){
        int userId;
        try{
            String token = authHeader.replace("Bearer ", "");
            userId = firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }

        List<NotificationDTO> notifications = notificationsService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<?> markNotificationRead(
        @Parameter(description = "ID of the notification", example = "1")
        @PathVariable int notificationId,
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ) {
        int userId;
        try{
            String token = authHeader.replace("Bearer ", "");
            userId = firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }

        try {
            notificationsService.markAsRead(notificationId, userId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

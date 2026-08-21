package com.app.api.controllers;

import com.app.api.dtos.TestNotificationRequest;
import com.app.api.services.NotificationsService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestNotificationController {

    private static final Logger logger = LoggerFactory.getLogger(TestNotificationController.class);

    @Autowired
    private NotificationsService notificationsService;

    /**
     * Test endpoint for sending push notifications from the Flutter app.
     * This is a development-only endpoint for testing FCM notifications.
     * 
     * POST /api/test/notification
     * 
     * @param request The test notification request containing FCM token and message details
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/notification")
    public ResponseEntity<?> sendTestNotification(@RequestBody TestNotificationRequest request) {
        try {
            logger.info(" Test notification request received");
            
            // Validate request
            if (request.getFcmToken() == null || request.getFcmToken().isEmpty()) {
                logger.warn(" Missing fcmToken in request");
                return ResponseEntity.badRequest().body("Missing fcmToken");
            }
            
            // Log token partially for debugging (don't log full token)
            String tokenPreview = request.getFcmToken().length() > 20 
                ? request.getFcmToken().substring(0, 20) + "..." 
                : request.getFcmToken();
            logger.info("FCM Token: {}", tokenPreview);
            logger.info("Title: {}", request.getTitle());
            logger.info("Body: {}", request.getBody());
            logger.info("Type: {}", request.getType());
            logger.info("EntityId: {}", request.getEntityId());
            
            // Build the message using the existing send method
            // Since send() is private, we need to use the public methods or create a new one
            // We'll add a new method to NotificationsService
            
            notificationsService.sendTestNotification(
                request.getFcmToken(),
                request.getTitle() != null ? request.getTitle() : " Test Notification",
                request.getBody() != null ? request.getBody() : "Supa Neighbour is working!",
                request.getType() != null ? request.getType() : "TASK_CREATED",
                request.getEntityId() != null ? request.getEntityId() : "123"
            );
            
            logger.info(" Test notification sent successfully");
            return ResponseEntity.ok().body("Notification sent successfully");
            
        } catch (FirebaseMessagingException e) {
            logger.error(" Firebase error sending test notification: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Firebase error: " + e.getMessage());
        } catch (Exception e) {
            logger.error(" Error sending test notification: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Failed to send notification: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint for testing the test controller is working.
     * GET /api/test/ping
     * 
     * @return "pong" if the controller is working
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
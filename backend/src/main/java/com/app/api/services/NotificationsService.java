package com.app.api.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Sends push notifications via Firebase Cloud Messaging (FCM) to a user's
 * registered devices, and cleans up tokens FCM reports as dead.
 */
@Service
public class NotificationsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationsService.class);

    private final FirebaseMessaging firebaseMessaging;

    /**
     * Default constructor that initializes the FirebaseMessaging instance.
     */
    public NotificationsService() {
        this.firebaseMessaging = FirebaseMessaging.getInstance();
    }

    /**
     * Sends a test notification to a specific FCM token.
     * This method is used for development and testing purposes.
     *
     * @param fcmToken the recipient's FCM token
     * @param title    the notification title
     * @param body     the notification body
     * @param type     the notification type (e.g., TASK_CREATED)
     * @param entityId the related entity ID
     * @throws FirebaseMessagingException if the message fails to send
     */
    public void sendTestNotification(String fcmToken, String title, String body, String type, String entityId)
            throws FirebaseMessagingException {
        LOGGER.info("Preparing to send test notification to token: {}", maskToken(fcmToken));

        // Build the notification
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // Build the message with custom data
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putData("type", type)
                .putData("entityId", entityId)
                .putData("source", "test_endpoint")
                .build();

        // Send the message
        try {
            String response = firebaseMessaging.send(message);
            LOGGER.info("Test notification sent successfully. Message ID: {}", response);
        } catch (FirebaseMessagingException e) {
            LOGGER.error("Failed to send test notification: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while sending test notification: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error sending test notification", e);
        }
    }

    /**
     * Masks an FCM token for logging purposes.
     *
     * @param token the full token
     * @return a masked version (first 8 characters + "...")
     */
    private String maskToken(String token) {
        if (token == null || token.length() <= 8) {
            return "***";
        }
        return token.substring(0, 8) + "...";
    }
}

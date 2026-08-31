package com.app.api.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.repositories.UserDeviceRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;

/**
 * Sends push notifications via Firebase Cloud Messaging (FCM) to a user's
 * registered devices, and cleans up tokens FCM reports as dead.
 */
@Service
public class NotificationsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationsService.class);

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    /**
     * Notifies a requester that someone wants to start their task.
     *
     * @param requesterUserId the user_id of the task requester
     * @param taskId          the task the helper is starting
     * @param helperName      display name of the helper
     */
    public void sendTaskStartNotification(int requesterUserId, int taskId, String helperName) {
        send(requesterUserId,
                "Your task is starting",
                helperName + " wants to start their task",
                "TASK_START",
                String.valueOf(taskId));
    }

    /**
     * Notifies relevant users that a new task was created nearby.
     *
     * @param recipientUserId the user_id to notify
     * @param taskId          the newly created task
     * @param taskTitle       the task's title
     */
    public void sendTaskCreatedNotification(int recipientUserId, int taskId, String taskTitle) {
        send(recipientUserId,
                "New task nearby!",
                taskTitle,
                "TASK_CREATED",
                String.valueOf(taskId));
    }

    /**
     * Notifies relevant users that a new community post was created.
     *
     * @param recipientUserId the user_id to notify
     * @param postId          the newly created post
     * @param postTitle       the post's title
     */
    public void sendPostCreatedNotification(int recipientUserId, int postId, String postTitle) {
        send(recipientUserId,
                "New community post!",
                postTitle,
                "POST_CREATED",
                String.valueOf(postId));
    }

    /**
     * Notifies a post's author that someone commented on their post.
     *
     * @param postAuthorUserId the user_id of the post's author
     * @param postId           the post that was commented on
     * @param commenterName    display name of the commenter
     */
    public void sendPostCommentNotifications(int postAuthorUserId, int postId, String commenterName) {
        send(postAuthorUserId,
                "New comment under your post!",
                commenterName + " commented on your post",
                "POST_COMMENT",
                String.valueOf(postId));
    }

    /**
     * Send a test notification directly to a specific device token.
     * Used for development testing from the Flutter app.
     *
     * @param fcmToken the device FCM token
     * @param title    the notification title
     * @param body     the notification body
     * @param type     the notification type
     * @param entityId the ID of the related entity
     * @throws FirebaseMessagingException if sending fails
     */
    public void sendTestNotification(String fcmToken, String title, String body,
                                     String type, String entityId) throws FirebaseMessagingException {

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("type", type)
                .putData("entityId", entityId)
                .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            LOGGER.info("✅ Test notification sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            LOGGER.error("❌ FCM send failed: {}", e.getMessage(), e);

            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED ||
                    e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                String tokenPreview = fcmToken.length() > 20
                        ? fcmToken.substring(0, 20) + "..."
                        : fcmToken;
                LOGGER.warn("⚠️ Invalid FCM token: {}", tokenPreview);
            }
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while sending test notification: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error sending test notification", e);
        }
    }

    /**
     * Notifies a user they've received a moderation warning.
     *
     * @param userId   the user being warned
     * @param reportId the report that triggered this warning (empty string if none)
     * @param reason   short explanation shown in the notification body
     */
    public void sendWarningNotification(int userId, String reportId, String reason) {
        send(userId,
                "You've received a warning",
                reason,
                "ACCOUNT_WARNING",
                reportId);
    }

    /**
     * Notifies a user their account has been suspended.
     *
     * @param userId   the user being suspended
     * @param reportId the report that triggered this suspension
     * @param reason   short explanation
     */
    public void sendSuspensionNotification(int userId, String reportId, String reason) {
        send(userId,
                "Your account has been suspended",
                reason,
                "ACCOUNT_SUSPENDED",
                reportId);
    }

    /**
     * Notifies a user their account has been banned.
     *
     * @param userId   the user being banned
     * @param reportId the report that triggered this ban
     * @param reason   short explanation
     */
    public void sendBanNotification(int userId, String reportId, String reason) {
        send(userId,
                "Your account has been banned",
                reason,
                "ACCOUNT_BANNED",
                reportId);
    }

    /**
     * Core send method: fans out one FCM message per registered device for the user.
     * Dead tokens are deleted so they stop being retried.
     * Failures are logged and never thrown – a push failure must not break the calling flow.
     *
     * @param userId   the user to notify
     * @param title    notification title
     * @param body     notification body
     * @param type     caller-defined type string for deep-link routing
     * @param entityId the ID of the relevant entity as a string
     */
    private void send(int userId, String title, String body, String type, String entityId) {
        List<String> tokens = userDeviceRepository.findTokensByUserId(userId);

        for (String token : tokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("type", type)
                    .putData("entityId", entityId)
                    .build();

            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED ||
                        e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                    userDeviceRepository.deleteToken(token);
                }
                LOGGER.error("FCM send failed for user {}: {}", userId, e.getMessage(), e);
            }
        }
    }
}
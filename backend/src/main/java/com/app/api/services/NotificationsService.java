package com.app.api.services;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.dtos.NotificationDTO;
import com.app.api.models.Notifications;
import com.app.api.repositories.NotificationRepository;
import com.app.api.repositories.UserDeviceRepository;
import com.app.api.repositories.UserRepository;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
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

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPersistenceService notifPersistance;

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private UserRepository userRepository;

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
     * Uses HIGH priority on Android so the notification is delivered immediately
     * even under Doze mode. Dead tokens are deleted so they stop being retried.
     *
     * <p>Cleanup failures (e.g. deleting a dead token from the DB) are caught
     * and logged so they never abort the send loop — every other token for the
     * user still gets its chance to receive the notification.
     *
     * @param userId   the user to notify
     * @param title    notification title
     * @param body     notification body
     * @param type     caller-defined type string for deep-link routing
     * @param entityId the ID of the relevant entity as a string
     */
    private void send(int userId, String title, String body, String type, String entityId) {
        notifPersistance.saveNotification(userId, title, body, type, entityId);

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
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setChannelId("supa_neighbour_channel")
                                    .setSound("default")
                                    .setDefaultVibrateTimings(true)
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                    .setSound("default")
                                    .setContentAvailable(true)
                                    .build())
                            .build())
                    .build();

            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                MessagingErrorCode code = e.getMessagingErrorCode();
                if (code == MessagingErrorCode.UNREGISTERED ||
                        code == MessagingErrorCode.INVALID_ARGUMENT) {
                    try {
                        userDeviceRepository.deleteToken(token);
                    } catch (Exception cleanupEx) {
                        // Never let cleanup failure stop us from trying the next token
                        LOGGER.warn("Failed to remove dead token: {}", cleanupEx.getMessage());
                    }
                }
                // SonarQube-safe: only the error code is logged, never the token or user id
                LOGGER.warn("FCM delivery failed: code={}", code);
            }
        }
    }

    /**
     * Fetches all notifications for a user, most recent first.
     *
     * @param userId the user_id to fetch notifications for
     * @return the user's notifications as DTOs
     */
    public List<NotificationDTO> getNotificationsForUser(int userId) {
        return notificationRepository.findByUser_UseridOrderByCreatedatDesc(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marks a single notification as read.
     *
     * @param notificationId the notification to mark as read
     * @param userId the user requesting the change
     */
    public void markAsRead(int notificationId, int userId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        if (notification.getUser() == null ||
                notification.getUser().getUserid() != userId) {
            throw new IllegalArgumentException(
                    "Notification does not belong to user: " + userId);
        }
        notification.setIsread(true);
        notificationRepository.save(notification);
    }

    private NotificationDTO toDTO(Notifications n) {
        return new NotificationDTO(
                n.getNotificationid(),
                n.getNotificationtype(),
                n.getEntityid(),
                n.getNotificationtitle(),
                n.getNotificationbody(),
                n.isIsread(),
                n.getCreatedat().format(TIMESTAMP_FORMAT));
    }
}

package com.app.api.services;
import java.util.List;

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
    @Autowired
    private UserDeviceRepository userDeviceRepository;

    /**
     * Notifies a requester that someone wants to start their task
     * (i.e. a helper has accepted/been auto-assigned to the task).
     *
     * @param requesterUserId the user_id of the task requester
     * @param taskId the task the helper is starting
     * @param helperName display name of the helper, for the notification body
     */
    public void sendTaskStartNotification(int requesterUserId, int taskId, String helperName){
        send(
            requesterUserId, 
            "Your task is starting",
            helperName + " wants to start their task",
            "TASK_START",
            String.valueOf(taskId)
        );
    }

    /**
     * Notifies relevant users that a new task was created nearby.
     * (Exact audience — e.g. helpers in the neighbourhood — is resolved by the caller;
     * this just fires one notification per resolved user.)
     *
     * @param recipientUserId the user_id to notify
     * @param taskId the newly created task
     * @param taskTitle the task's title, for the notification body
     */
    public void sendTaskCreatedNotification(int recipientUserId, int taskId, String taskTitle){
        send( 
            recipientUserId, 
            "New task nearby!",
            taskTitle,
            "TASK_CREATED",
            String.valueOf(taskId)
        );
    }


    /**
     * Notifies relevant users that a new community post was created.
     *
     * @param recipientUserId the user_id to notify
     * @param postId the newly created post
     * @param postTitle the post's title, for the notification body
     */
    public void sendPostCreatedNotification(int recipientUserId, int postId, String postTitle){
        send(
            recipientUserId, 
            "New community post!",
            postTitle,
            "POST_CREATED",
            String.valueOf(postId)
        );
    }

    /**
     * Notifies a post's author that someone commented on their post.
     *
     * @param postAuthorUserId the user_id of the post's author
     * @param postId the post that was commented on
     * @param commenterName display name of the commenter, for the notification body
     */
    public void sendPostCommentNotifications(int postAuthorUserId, int postId, String commenterName){
        send(
            postAuthorUserId, 
            "New comment under your post!", 
            commenterName + " commented on your post",
            "POST_COMMENT",
            String.valueOf(postId)
        );
    }
    /**
     * Send a test notification directly to a specific device token.
     * This is used for development testing from the Flutter app.
     * 
     * @param fcmToken The device FCM token to send to
     * @param title The notification title
     * @param body The notification body
     * @param type The notification type (TASK_CREATED, POST_CREATED, etc.)
     * @param entityId The ID of the related entity
     * @throws FirebaseMessagingException if sending fails
     */
    public void sendTestNotification(String fcmToken, String title, String body, 
                                     String type, String entityId) throws FirebaseMessagingException {
        
        // Build the FCM message
        Message message = Message.builder()
            .setToken(fcmToken)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .putData("type", type)
            .putData("entityId", entityId)
            .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
            .build();

        try {
            // Send the message
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Test notification sent successfully: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("❌ FCM send failed: " + e.getMessage());
            
            // If the token is invalid, log it
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED || 
                e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                String tokenPreview = fcmToken.length() > 20 
                    ? fcmToken.substring(0, 20) + "..." 
                    : fcmToken;
                System.err.println("⚠️ Invalid FCM token: " + tokenPreview);
            }
            throw e;
        }
    }

    /**
     * Notifies a user they've received a moderation warning.
     *
     * @param userId the user_id being warned
     * @param reportId the report that triggered this warning, if any (empty string if none)
     * @param reason short explanation shown in the notification body
     */
    public void sendWarningNotification(int userId, String reportId, String reason){
        send(
            userId, 
            "You've recieved a warning",
            reason,
            "ACCOUNT_WARNING",
            reportId
        );
    }

    /**
     * Notifies a user their account has been suspended.
     *
     * @param userId the user_id being suspended
     * @param reportId the report that triggered this suspension, if any
     * @param reason short explanation shown in the notification body
     */
    public void sendSuspensionNotification(int userId, String reportId, String reason){
        send(
            userId, 
            "Your account has been suspended",
            reason,
            "ACCOUNT_SUSPENDED",
            reportId
        );
    }

    /**
     * Notifies a user their account has been banned.
     *
     * @param userId the user_id being banned
     * @param reportId the report that triggered this ban, if any
     * @param reason short explanation shown in the notification body
     */
    public void sendBanNotification(int userId, String reportId, String reason){
        send(
            userId, 
            "Your account has been banned",
            reason,
            "ACCOUNT_BANNED",
            reportId
        );
    }



    
    /**
     * Core send: fans out one FCM message per registered device for the user.
     * Dead tokens (UNREGISTERED / INVALID_ARGUMENT) are deleted so they stop
     * being retried on future sends. Failures here are logged, never thrown —
     * a push failure must never break the calling business flow (task creation,
     * invite, comment, etc. must still succeed even if notification delivery fails).
     *
     * @param userId the user_id to notify
     * @param title notification title
     * @param body notification body
     * @param type a caller-defined type string, used by Flutter for deep-link routing
     * @param entityId the id of the relevant entity (taskId, postId, etc.), as a string
     */
    private void send(int userId, String title, String body, String type, String entityId){
        List<String> tokens = userDeviceRepository.findTokensByUserId(userId);

        for(String token: tokens){
            Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build())
            .putData("type", type)
            .putData("entityId", entityId)
            .build();

            try{
            FirebaseMessaging.getInstance().send(message);
            }catch(FirebaseMessagingException e){
                if(e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED || e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT){
                    userDeviceRepository.deleteToken(token);
                }

                System.err.println("FCM send failed for user " + userId + ": " + e.getMessage());
            }
        }
    }

}

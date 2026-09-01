package com.app.api.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.app.api.events.HelperMatchedEvent;
import com.app.api.events.PostCommentEvent;
import com.app.api.events.PostCreatedEvent;
import com.app.api.events.TaskStartedEvent;
import com.app.api.events.UserWarnedEvent;
import com.app.api.services.NotificationsService;

/**
 * Event listener component responsible for handling task-related domain events
 * and triggering appropriate notifications to users.
 * 
 * <p>This listener listens for various task and post lifecycle events and
 * delegates notification sending to the {@link NotificationsService}.
 * All event handling methods are transactional and execute after the
 * transaction commits to ensure data consistency.
 * 
 * @author Your Name
 * @version 1.0
 * @see NotificationsService
 * @see HelperMatchedEvent
 * @see TaskStartedEvent
 * @see PostCreatedEvent
 * @see PostCommentEvent
 */
@Component
public class TaskNotificationListener {

    @Autowired
    private NotificationsService notificationsService;

    /**
     * Handles the {@link HelperMatchedEvent} to send a notification to the helper
     * informing them that they have been matched to a task.
     * 
     * <p>This method is triggered after the transaction commits to ensure that
     * the task assignment data is fully persisted before sending the notification.
     * 
     * @param event The HelperMatchedEvent containing task and helper information
     * @see NotificationsService#sendTaskCreatedNotification(Integer, Long, String)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onHelperMatched(HelperMatchedEvent event){
        notificationsService.sendTaskCreatedNotification(
            event.getHelperUserId(), 
            event.getTaskId(),
            event.getTaskTitle());
    }

    /**
     * Handles the {@link TaskStartedEvent} to send a notification to the task requester
     * informing them that the task has been started by the assigned helper.
     * 
     * <p>This method is triggered after the transaction commits to ensure that
     * the task status update is fully persisted before sending the notification.
     * 
     * @param event The TaskStartedEvent containing requester, task, and helper details
     * @see NotificationsService#sendTaskStartNotification(Integer, Long, String)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTaskStarted(TaskStartedEvent event){
        notificationsService.sendTaskStartNotification(
            event.getRequesterUserId(),
            event.getTaskId(), 
            event.getHelperName());
    }

    /**
     * Handles the {@link PostCreatedEvent} to send notifications to all recipients
     * who are subscribed or should be notified about the new post creation.
     * 
     * <p>This method iterates through all recipient user IDs and sends an individual
     * notification to each one. It is triggered after the transaction commits to
     * ensure that the post data is fully persisted.
     * 
     * @param event The PostCreatedtEvent containing the post details and list of recipient users
     * @see NotificationsService#sendPostCreatedNotification(Integer, Object, Object)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPostCreated(PostCreatedEvent event){
        for(Integer recipientUserId : event.getRecipientUserIds()){
            notificationsService.sendPostCreatedNotification(
                recipientUserId,
                 event.getPostById(), 
                event.getPostPreview()
            );
        }
    }

     /**
     * Handles the {@link PostCommentEvent} to send a notification to the post author
     * informing them that a new comment has been added to their post.
     * 
     * <p>This method is triggered after the transaction commits to ensure that
     * the comment data is fully persisted before sending the notification.
     * 
     * @param event The PostCommentEvent containing the post author, post ID, and commenter details
     * @see NotificationsService#sendPostCommentNotifications(Integer, Object, String)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPostCommented(PostCommentEvent event){
        notificationsService.sendPostCommentNotifications(
            event.getPostAuthorUserId(),
            event.getPostId(), 
            event.getCommentorName());
    }   

    /**
     * Handles the {@link UserWarnedEvent} after the current transaction has successfully committed.
     * Sends a warning notification to the user who received a warning.
     *
     * @param event the user warned event containing the user ID, report ID, and reason for the warning
     * @see TransactionPhase#AFTER_COMMIT
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserWarned(UserWarnedEvent event){
        notificationsService.sendWarningNotification(event.getUserId(), event.getReportId(), event.getReason());
    }

    /**
     * Handles the {@link UserWarnedEvent} after the current transaction has successfully committed.
     * Sends a suspension notification to the user who has been suspended.
     *
     * @param event the user warned event containing the user ID, report ID, and reason for the suspension
     * @see TransactionPhase#AFTER_COMMIT
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserSuspended(UserWarnedEvent event){
        notificationsService.sendSuspensionNotification(event.getUserId(), event.getReportId(), event.getReason());
    }

    /**
     * Handles the {@link UserWarnedEvent} after the current transaction has successfully committed.
     * Sends a ban notification to the user who has been permanently banned.
     *
     * @param event the user warned event containing the user ID, report ID, and reason for the ban
     * @see TransactionPhase#AFTER_COMMIT
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserBanned(UserWarnedEvent event){
        notificationsService.sendBanNotification(event.getUserId(), event.getReportId(), event.getReason());
    }

}

package com.app.api.services;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.api.models.Notifications;
import com.app.api.repositories.NotificationRepository;
import com.app.api.repositories.UserRepository;



@Service
public class NotificationPersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationPersistenceService.class);

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Saves a new notification for a user.
     * Runs in a new transaction to ensure it persists independently of the calling transaction.
     *
     * @param userId The ID of the user receiving the notification
     * @param title The notification title
     * @param body The notification body content
     * @param type The notification type (e.g., "COMMENT", "LIKE", "SYSTEM")
     * @param entityId The ID of the associated entity (post, comment, etc.)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNotification(int userId, String title, String body, String type, String entityId) {
        Notifications notification = Notifications.builder()
                .user(userRepository.getReferenceById(userId))
                .notificationtype(type)
                .entityid(entityId)
                .notificationtitle(title)
                .notificationbody(body)
                .isread(false)
                .createdat(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
        LOGGER.info("✅ Notification persisted: userId={}, type={}, entityId={}", userId, type, entityId);
    }
}

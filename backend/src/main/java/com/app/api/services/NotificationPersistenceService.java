package com.app.api.services;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.api.models.Notifications;
import com.app.api.repositories.NotificationRepository;
import com.app.api.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class NotificationPersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationPersistenceService.class);

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

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

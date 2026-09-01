package com.app.api.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Notifications;

public interface NotificationRepository extends JpaRepository<Notifications, Integer>{
    
    /**
     * Finds all notifications for a specific user, ordered newest first.
     *
     * @param userid The user's unique identifier
     * @return List of notifications for the user, or empty list if none found
     */
    List<Notifications> findByUser_UseridOrderByCreatedatDesc(int userid);
}

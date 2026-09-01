package com.app.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.ModerationAction;
import com.app.api.models.User;

public interface ModerationActionRepository extends JpaRepository<ModerationAction, Integer> {
    /**
     * Finds all active (non-lifted) moderation actions for a specific user.
     *
     * @param user the user whose active moderation actions to retrieve
     * @return list of active moderation actions for the given user
     */
    List<ModerationAction> findByUserAndLiftedAtIsNull(User user);

    /**
     * Finds all active (non-lifted) moderation actions of a specific type for a user.
     *
     * @param user the user whose active moderation actions to retrieve
     * @param actionType the type of moderation action (e.g., "ban", "suspension")
     * @return list of active moderation actions matching the user and action type
     */
    List<ModerationAction> findByUserAndActionTypeAndLiftedAtIsNull(
        User user, String actionType
    );
}

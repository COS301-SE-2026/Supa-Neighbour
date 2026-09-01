package com.app.api.services;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.api.models.ModerationAction;
import com.app.api.models.User;
import com.app.api.repositories.ModerationActionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModerationActionService {
    private final ModerationActionRepository moderationActionRepository;

    /**
     * Checks if a user is currently banned.
     * <p>
     * A user is considered banned if there exists at least one active ban record
     * (action type "ban") that has not been lifted. This method does not consider
     * expiration dates for bans, as bans are typically permanent until lifted
     * by an administrator.
     * </p>
     * 
     * @param user the user to check for active bans
     * @return {@code true} if the user has at least one active ban that hasn't been lifted,
     *         {@code false} otherwise
     * @throws IllegalArgumentException if the user parameter is null
     */
    public boolean isBanned(User user){
        List<ModerationAction> bans = moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user, "ban");

        return !bans.isEmpty();
    }

    /**
     * Checks if a user is currently suspended.
     * <p>
     * A user is considered suspended if there exists at least one suspension record
     * (action type "suspension") that:
     * <ul>
     *   <li>Has not been lifted (liftedAt is null)</li>
     *   <li>Has not expired yet (expiredAt is after the current time)</li>
     * </ul>
     * </p>
     * <p>
     * This method evaluates suspensions based on their expiration dates, meaning
     * temporary suspensions will automatically become inactive after their
     * expiration time has passed.
     * </p>
     * 
     * @param user the user to check for active suspensions
     * @return {@code true} if the user has at least one active, unexpired suspension,
     *         {@code false} otherwise
     * @throws IllegalArgumentException if the user parameter is null
     */
    public boolean isSuspended(User user){
        List<ModerationAction> suspensions = moderationActionRepository.findByUserAndActionTypeAndLiftedAtIsNull(user, "suspension");

        LocalDateTime now = LocalDateTime.now();

        return suspensions.stream().anyMatch(
            action -> 
                action.getExpiredAt() != null &&
                action.getExpiredAt().isAfter(now)
        );
    }
}

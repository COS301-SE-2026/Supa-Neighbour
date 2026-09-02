package com.app.api.services;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.app.api.models.ModerationAction;
import com.app.api.models.Report;
import com.app.api.models.User;
import com.app.api.repositories.ModerationActionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModerationActionService {

    private static final Set<String> VALID_ACTION_TYPES =
            Set.of("warning", "suspension", "ban");

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

    /**
     * Records a moderation action in the {@code moderation_action} table.
     *
     * <p>The {@code actionType} must be one of {@code "warning"}, {@code "suspension"},
     * or {@code "ban"} — values enforced by the DB CHECK constraint.
     * For suspensions, pass a non-null {@code expiresAt} to set the expiry window;
     * warnings and bans should pass {@code null}.</p>
     *
     * @param targetUser the user being moderated
     * @param actionType DB-level action type: {@code "warning"}, {@code "suspension"}, or {@code "ban"}
     * @param reason     human-readable reason stored in the record
     * @param report     the report that triggered this action (may be null)
     * @param issuedBy   the admin user issuing the action
     * @param expiresAt  expiry timestamp for suspensions; null for warnings and bans
     * @return the saved {@link ModerationAction}
     * @throws IllegalArgumentException if {@code actionType} is not a recognised value
     */
    public ModerationAction issueModerationAction(
            User targetUser,
            String actionType,
            String reason,
            Report report,
            User issuedBy,
            LocalDateTime expiresAt) {

        if (!VALID_ACTION_TYPES.contains(actionType)) {
            throw new IllegalArgumentException(
                    "actionType must be one of: warning, suspension, ban — got: " + actionType);
        }

        ModerationAction action = ModerationAction.builder()
                .user(targetUser)
                .actionType(actionType)
                .reason(reason)
                .report(report)
                .issuedBy(issuedBy)
                .issuedAt(LocalDateTime.now())
                .expiredAt(expiresAt)
                .build();

        return moderationActionRepository.save(action);
    }
}

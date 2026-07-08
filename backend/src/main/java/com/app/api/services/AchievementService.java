package com.app.api.services;

import com.app.api.dtos.AchievementDTO;
import com.app.api.dtos.AchievementResponse;
import com.app.api.repositories.AchievementRepository;
import org.springframework.stereotype.Service;
 
import java.util.List;
/**
 * Service responsible for retrieving a user's earned and
 * unearned achievements.
 */
@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;

    /**
     * Constructs an {@code AchievementService} with the required repository.
     *
     * @param achievementRepository repository used to retrieve achievement
     *                              information for users
     */
    public AchievementService(AchievementRepository achievementRepository){
        this.achievementRepository = achievementRepository;
    }

    /**
     * Retrieves the achievements associated with a user.
     *
     * <p>The returned response includes both achievements that the user
     * has already earned and achievements that are still in progress.
     * Unearned achievements include the user's current progress toward
     * completion.</p>
     *
     * @param userId the identifier of the user whose achievements are requested
     * @return an {@link AchievementResponse} containing the user's earned
     *         and unearned achievements
     */
    public AchievementResponse getAchievements(int userId){
        List<AchievementDTO> earned = achievementRepository.findEarned(userId)
                .stream()
                .map(row -> new AchievementDTO(
                        ((Number) row[0]).intValue(),  // badge_id
                        (String)  row[1],              // badge_name
                        (String)  row[2],              // badge_description
                        row[3] != null ? row[3].toString() : null  // awarded_on as string
                ))
                .toList();

        List<AchievementDTO> unearned = achievementRepository.findUnearned(userId).stream()
        .map(
            row -> new AchievementDTO(
                ((Number)  row[0]).intValue(),
                (String) row[1],
                (String) row[2],
                ((Number) row[3]).intValue(),
                ((Number) row[4]).intValue()
            )).toList();

        return new AchievementResponse(earned, unearned);
    }
}

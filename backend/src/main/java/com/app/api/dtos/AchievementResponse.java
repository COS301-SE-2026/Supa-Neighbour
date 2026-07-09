package com.app.api.dtos;
import java.util.List;

/**
 * Data Transfer Object representing a user's achievements.
 *
 * <p>The response contains two collections: achievements that have
 * already been earned and achievements that are still in progress.</p>
 */
public class AchievementResponse {
    private List<AchievementDTO> earned;
    private List<AchievementDTO> unearned;

    /**
     * Creates an achievement response.
     *
     * @param earned the list of achievements earned by the user
     * @param unearned the list of achievements the user has not yet earned
     */
    public AchievementResponse(List<AchievementDTO> earned, List<AchievementDTO> unearned){
        this.earned = earned;
        this.unearned = unearned;
    }

    /**
     * @return the earned achievements.
     */
    public List<AchievementDTO> getEarned(){
        return earned;
    }

    /**
     * @return the unearned achievements
     */
    public List<AchievementDTO> getUnearned(){
        return unearned;
    }
}

package com.app.api.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing a user's profile.
 *
 * <p>The response contains general profile information for all users.
 * For users registered as helpers, helper-specific information such as
 * experience points, completed tasks, skills, and recent tasks is also
 * included.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    
    private int userId;
    private String displayName;
    private String neighbourhood;
    private String level;
    private Integer currentXp;
    private List<String> skills;
    private List<AchievementDTO> achievements;
    private int completedTasks;
    private List<RecentTaskDTO>  recentTasks;
    private Double trustScore;

    /**
     * Creates a user profile response.
     *
     * @param userId the unique identifier of the user
     * @param displayName the user's display name
     * @param neighbourhood the user's neighbourhood
     * @param level the helper's neighbourhood ranking level
     * @param currentXp the helper's current experience points
     * @param skills the helper's skills
     * @param completedTasks the number of completed tasks
     * @param recentTasks the helper's recently completed tasks
     * @param achievements the user's earned achievements
     */
    public UserProfileResponse(int userId, String displayName, String neighbourhood, String level,Integer currentXp,List<String> skills,int completedTasks,List<RecentTaskDTO>  recentTasks,List<AchievementDTO> achievements,Double trustScore){
        this.userId = userId;
        this.displayName = displayName;
        this.neighbourhood = neighbourhood;
        this.level = level;
        this.currentXp = currentXp;
        this.trustScore = trustScore;
        this.skills = skills;
        this.achievements = achievements;
        this.completedTasks = completedTasks;
        this.recentTasks = recentTasks;
    }

    /**
     * Returns the user's unique identifier.
     *
     * @return the user identifier
     */
    public int getUserId(){
        return userId;
    }

    /**
     * Returns the user's display name.
     *
     * @return the display name
     */
    public String getDisplayName(){
        return displayName;
    }

    /**
     * Returns the user's neighbourhood.
     *
     * @return the neighbourhood name
     */
    public String getNeighbourhood(){
        return neighbourhood;
    }

    /**
     * Returns the helper's neighbourhood ranking level.
     *
     * @return the helper's level, or {@code null} if the user is not a helper
     */
    public String getLevel(){
        return level;
    }

    /**
     * Returns the helper's current experience points.
     *
     * @return the current experience points
     */
    public Integer getCurrentXp(){
        return currentXp;
    }

    /**
     * Returns the helper's trust score.
     *
     * @return the trust score
     */
    public Double getTrusctScore(){
        return trustScore;
    }

    /**
     * Returns the helper's skills.
     *
     * @return the list of skills, or {@code null} if the user is not a helper
     */
    public List<String> getSkills(){
        return skills;
    }

    /**
     * Returns the user's earned achievements.
     *
     * @return the list of achievements
     */
    public List<AchievementDTO> getAchievements(){
        return achievements;
    }

     /**
     * Returns the number of completed tasks.
     *
     * @return the completed task count
     */
    public int getCompletedTasks(){
        return completedTasks;
    }

    /**
     * Returns the helper's recently completed tasks.
     *
     * @return the list of recently completed tasks
     */
    public List<RecentTaskDTO> getRecentTasks(){
        return recentTasks;
    }

}

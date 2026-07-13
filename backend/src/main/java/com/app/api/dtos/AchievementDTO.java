package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing an achievement.
 *
 * <p>An achievement may either be an earned achievement, identified by
 * its award date, or an unearned achievement, identified by the user's
 * current progress toward completing it.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AchievementDTO {
    private int badgeId;
    private String name;
    private String description;
    private String awardedOn;

    private String progress;

    /**
     * Creates an earned achievement.
     *
     * @param badgeId the unique identifier of the achievement badge
     * @param name the name of the achievement
     * @param description a description of the achievement
     * @param awardedOn the date on which the achievement was awarded
     */
    public AchievementDTO(int badgeId, String name, String description, String awardedOn){
        this.badgeId = badgeId;
        this.name = name;
        this.description = description;
        this.awardedOn = awardedOn;
    }

    /**
     * Creates an unearned achievement with progress information.
     *
     * @param badgeId the unique identifier of the achievement badge
     * @param name the name of the achievement
     * @param description a description of the achievement
     * @param progressCurrent the user's current progress
     * @param progressTarget the progress required to earn the achievement
     */

    public AchievementDTO(int badgeId, String name, String description, int progressCurrent, int progressTarget){
        this.badgeId = badgeId;
        this.name = name;
        this.description = description;
        this.progress = progressCurrent + "/" + progressTarget;
    }

    /**
     * @return the badgeId
     */
    public int getBadgeId(){
        return badgeId;
    }

    /**
     * @return the achievement name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the achievement description
     */
    public String getDescription(){
        return description;
    }

    /**
     * @return the achievement award Dat
     */
    public String getAwardedOn(){
        return awardedOn;
    }

    /**
     * @return the achievement Progress.
     */
    public String getProgress(){
        return progress;
    }
}

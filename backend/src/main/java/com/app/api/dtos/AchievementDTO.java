package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class AchievementDTO {
    private int badgeId;
    private String name;
    private String description;
    private String awardedOn;

    private String progress;

    public AchievementDTO(int badgeId, String name, String description, String awardedOn){
        this.badgeId = badgeId;
        this.name = name;
        this.description = description;
        this.awardedOn = awardedOn;
    }

    public AchievementDTO(int badgeId, String name, String description, int progressCurrent, int progressTarget){
        this.badgeId = badgeId;
        this.name = name;
        this.description = description;
        this.progress = progressCurrent + "/" + progressTarget;
    }

    public int getBadgeId(){
        return badgeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getAwardedOn(){
        return awardedOn;
    }

    public String getProgress(){
        return progress;
    }
}

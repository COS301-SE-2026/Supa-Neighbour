package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.app.api.dtos.AchievementDTO;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    
    private int userId;
    private String displayName;
    private String neighbourhood;
    private String level;
    private int currentXp;
    private double trustScore;
    private List<String> skills;
    private List<AchievementDTO> achievements;
    private int completedTasks;
    private List<RecentTaskDTO>  recentTasks;

    public UserProfileResponse(int userId, String displayName, String neighbourhood, String level,int currentXp,List<String> skills,int completedTasks,List<RecentTaskDTO>  recentTasks,List<AchievementDTO> achievements){
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

    public int getUserId(){
        return userId;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getNeighbourhood(){
        return neighbourhood;
    }

    public String getLevel(){
        return level;
    }

    public Integer getCurrentXp(){
        return currentXp;
    }

    public Double getTrusctScore(){
        return trustScore;
    }

    public List<String> getSkills(){
        return skills;
    }

    public List<AchievementDTO> getAchievements(){
        return achievements;
    }

    public int getCompletedTasks(){
        return completedTasks;
    }

    public List<RecentTaskDTO> getRecentTasks(){
        return recentTasks;
    }

}

package com.app.api.dtos;
import java.util.List;

public class AchievementResponse {
    private List<AchievementDTO> earned;
    private List<AchievementDTO> unearned;

    public AchievementResponse(List<AchievementDTO> earned, List<AchievementDTO> unearned){
        this.earned = earned;
        this.unearned = unearned;
    }

    public List<AchievementDTO> getEarned(){
        return earned;
    }

    public List<AchievementDTO> getUnearned(){
        return unearned;
    }
}

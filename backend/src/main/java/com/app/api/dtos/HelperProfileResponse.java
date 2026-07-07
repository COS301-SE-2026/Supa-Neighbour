package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
 
import java.util.List;
 
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HelperProfileResponse {
    private int helperId;
    private String displayName;
    private String level;
    private double trustScore;
    private int completedTasks;
    private int neighboursHelped;
    private List<String> skills;
    private List<ReviewDTO> reviews;

    public HelperProfileResponse(int helperId, String displayName, String level,double trustScore, int completedTasks,int neighboursHelped, List<String> skills,List<ReviewDTO> reviews){
        this.helperId = helperId;
        this.displayName = displayName;
        this.level = level;
        this.trustScore = trustScore;
        this.completedTasks = completedTasks;
        this.neighboursHelped = neighboursHelped;
        this.skills = skills;
        this.reviews = reviews;
    }

    public int getHelperId(){
        return helperId;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getLevel(){
        return level;
    }

    public double getTrustScore(){
        return trustScore;
    }

    public int getCompletedTasks(){
        return completedTasks;
    }

    public int getNeighboursHelped(){
        return neighboursHelped;
    }

    public List<String> getSkills(){
        return skills;
    }

    public List<ReviewDTO> getReviews(){
        return reviews;
    }
}

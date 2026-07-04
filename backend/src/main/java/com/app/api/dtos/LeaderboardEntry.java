package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaderboardEntry {
    private int rank;
    private int userId;
    private String displayName;
    private String level;
    private double score;

    public LeaderboardEntry(int rank, int userId, String displayName, double score){
        this.rank = rank;
        this.userId = userId;
        this.displayName = displayName;
        this.score = score;
        this.level = switch(rank){
            case 1 -> "Gold";
            case 2 -> "Silver";
            case 3 -> "Bronze";
            default -> null;
        };
    }

    public int getRank() {
        return rank;
    }

    public int getUserId() {
        return userId;
    }
    
    public String getDisplayName(){
        return displayName;
    }

    public String getLevel(){
        return level;
    }

    public double getScore(){
        return score;
    }
}

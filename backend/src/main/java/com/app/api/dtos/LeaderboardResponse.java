package com.app.api.dtos;
import java.util.List;

public class LeaderboardResponse {
    private String neighbourhood;
    private String rankBy;
    private List<LeaderboardEntry> leaderboard;
    private LeaderboardEntry currentUser;

    public LeaderboardResponse(String neighbourhood, String rankBy, List<LeaderboardEntry> leaderboard, LeaderboardEntry currentUser){
        this.neighbourhood = neighbourhood;
        this.rankBy = rankBy;
        this.leaderboard = leaderboard;
        this.currentUser = currentUser;
    }

    public String getNeighbourhood(){
        return neighbourhood;
    }

    public String getRankBy(){
        return rankBy;
    }

    public List<LeaderboardEntry> getLeaderboard(){
        return leaderboard;
    }

    public LeaderboardEntry getCurrentUser(){
        return currentUser;
    }

}

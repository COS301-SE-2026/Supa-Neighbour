package com.app.api.dtos;
import java.util.List;


/**
 * Data Transfer Object representing a leaderboard response.
 *
 * <p>This response contains the neighbourhood for which the leaderboard
 * was generated, the ranking criterion, the ranked leaderboard entries,
 * and the authenticated user's leaderboard entry.</p>
 */
public class LeaderboardResponse {
    private String neighbourhood;
    private String rankBy;
    private List<LeaderboardEntry> leaderboard;
    private LeaderboardEntry currentUser;

    /**
     * Creates a leaderboard response.
     *
     * @param neighbourhood the name of the neighbourhood
     * @param rankBy the ranking criterion used to order the leaderboard
     * @param leaderboard the ranked leaderboard entries
     * @param currentUser the authenticated user's leaderboard entry
     */
    public LeaderboardResponse(String neighbourhood, String rankBy, List<LeaderboardEntry> leaderboard, LeaderboardEntry currentUser){
        this.neighbourhood = neighbourhood;
        this.rankBy = rankBy;
        this.leaderboard = leaderboard;
        this.currentUser = currentUser;
    }

    /**
     * Returns the name of the neighbourhood.
     *
     * @return the neighbourhood name
     */
    public String getNeighbourhood(){
        return neighbourhood;
    }

    /**
     * Returns the ranking criterion.
     *
     * @return the ranking criterion
     */
    public String getRankBy(){
        return rankBy;
    }

    /**
     * Returns the leaderboard entries.
     *
     * @return the list of leaderboard entries
     */
    public List<LeaderboardEntry> getLeaderboard(){
        return leaderboard;
    }

    /**
     * Returns the authenticated user's leaderboard entry.
     *
     * @return the current user's leaderboard entry, or {@code null}
     *         if the user does not appear on the leaderboard
     */
    public LeaderboardEntry getCurrentUser(){
        return currentUser;
    }
}

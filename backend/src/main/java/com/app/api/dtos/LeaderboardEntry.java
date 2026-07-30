package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing a single entry in the leaderboard.
 *
 * <p>Each entry contains the helper's ranking, display information,
 * neighbourhood level, and ranking score.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaderboardEntry {
    private int rank;
    private int userId;
    private String displayName;
    private String level;
    private double score;
    private Integer helperId;

    /**
     * Creates a leaderboard entry.
     *
     * @param rank the helper's rank on the leaderboard
     * @param userId the unique identifier of the user
     * @param displayName the user's display name
     * @param score the score used to determine the ranking
     * @param helperId the unique identifier of the helper, or {@code null} if not applicable
     */
    public LeaderboardEntry(int rank, int userId, String displayName, double score, Integer helperId) {
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
        this.helperId = helperId;
    }

    /**
     * Returns the user's leaderboard rank.
     *
     * @return the leaderboard rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns the user's unique identifier.
     *
     * @return the user identifier
     */
    public int getUserId() {
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
     * Returns the user's leaderboard level.
     *
     * @return the level, or {@code null} if no level applies
     */
    public String getLevel(){
        return level;
    }
    /**
     * Returns the user's leaderboard score.
     *
     * @return the ranking score
     */
    public double getScore(){
        return score;
    }
    /**
     * returns helperID
     * @return
     */
    public Integer getHelperId() {
        return helperId;
    }
}

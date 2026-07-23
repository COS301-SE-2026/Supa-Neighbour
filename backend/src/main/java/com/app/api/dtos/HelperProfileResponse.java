package com.app.api.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * Data Transfer Object representing the public profile of a helper.
 *
 * <p>This response includes the helper's profile information,
 * trust score, completed task statistics, skills, and reviews.</p>
 */
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


    /**
     * Creates a helper profile response.
     *
     * @param helperId the unique identifier of the helper
     * @param displayName the helper's display name
     * @param level the helper's ranking level within the neighbourhood
     * @param trustScore the helper's average trust score
     * @param completedTasks the number of completed tasks
     * @param neighboursHelped the number of unique neighbours helped
     * @param skills the helper's list of skills
     * @param reviews the helper's recent reviews
     */
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

    /**
     * Returns the helper's unique identifier.
     *
     * @return the helper identifier
     */
    public int getHelperId(){
        return helperId;
    }

    /**
     * Returns the helper's display name.
     *
     * @return the display name
     */
    public String getDisplayName(){
        return displayName;
    }

    /**
     * Returns the helper's neighbourhood ranking level.
     *
     * @return the helper's level
     */
    public String getLevel(){
        return level;
    }

    /**
     * Returns the helper's trust score.
     *
     * @return the average trust score
     */
    public double getTrustScore(){
        return trustScore;
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
     * Returns the number of unique neighbours helped.
     *
     * @return the number of neighbours helped
     */
    public int getNeighboursHelped(){
        return neighboursHelped;
    }

    /**
     * Returns the helper's skills.
     *
     * @return the list of skills
     */
    public List<String> getSkills(){
        return skills;
    }

    /**
     * Returns the helper's reviews.
     *
     * @return the list of reviews
     */
    public List<ReviewDTO> getReviews(){
        return reviews;
    }
}

package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a user's rating and experience level.
 * Tracks user reviews, XP progression, and grouping based on achievement levels.
 */
@Data
@Builder
@Entity
@Table(name = "rating_table")
public class Ratings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private int ratingid;
    @Column(name = "rating_review")
    private String ratingReview;
    @Column(name = "total_xp_level")
    private int totalXpLevel;
    @Column(name = "current_group")
    private String currentGroup;

    /**
     * Constructs a Posts with all the fields specified
     *
     * @param ratingid            the rating identifier
     * @param ratinReview         the reason for the rating
     * @param totalXpLevel        the XP level of the rating
     * @param currentGroup        current group of the rating
     */
    public Ratings(int ratingid, String ratingReview, int totalXpLevel, String currentGroup) {
        this.ratingid = ratingid;
        this.ratingReview = ratingReview;
        this.totalXpLevel = totalXpLevel;
        this.currentGroup = currentGroup;
    }

    /**
     * Default constructor.
     */
    public Ratings(){

    }

    /**
     * Gets the rating identifier.
     *
     * @return the rating identifier
     */
    public int getRatingid() {
        return ratingid;
    }

    /**
     * Sets the rating identifier.
     *
     * @param ratingid the rating identifier
     */
    public void setRatingid(int ratingid) {
        this.ratingid = ratingid;
    }

    /**
     * Gets the rating review.
     *
     * @return the rating review
     */
    public String getRatingReview() {
        return ratingReview;
    }


    /**
     * Sets the rating review
     *
     * @param ratingReview the rating review
     */
    public void setRatingReview(String ratingReview) {
        this.ratingReview = ratingReview;
    }

    /**
     * Gets the totalXpLevel.
     *
     * @return the total XP level
     */
    public int getTotalXpLevel() {
        return totalXpLevel;
    }

    /**
     * Sets the total XP level
     *
     * @param totalXpLevel the total XP Level
     */
    public void setTotalXpLevel(int totalXpLevel) {
        this.totalXpLevel = totalXpLevel;
    }

    /**
     * Gets the current group of review
     *
     * @return the current group
     */
    public String getCurrentGroup() {
        return currentGroup;
    }

    /**
     * Sets the current group
     *
     * @param currentGroup the current group
     */
    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }
    
}

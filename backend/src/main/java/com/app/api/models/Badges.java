package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a badge that can be earned by users.
 * Badges recognize user achievements and specialist status.
 */
@Data
@Builder
@Entity
@Table(name = "badge_table")
public class Badges {
    
    /*Unique Idetifier of badge */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private int badgeid;

    /*Display name of the badge */
    @Column(name = "badge_name")
    private String badgeName;

    /** Description of what the badge represents. */
    @Column(name = "badge_description")
    private String badge_description;

    /** Indicates whether this badge denotes specialist status. */
    @Column(name = "is_specialist")
    private Boolean isSpecialist;

    /** The XP reward granted when this badge is earned. */
    @Column(name = "current_xp")
    private int xpReward;

    /** The rating associated with this badge. */
    @ManyToOne
    @JoinColumn(name = "rating_id")
    private Ratings ratingid;

    /**
     * Default no-args constructor required by JPA.
     */
    public Badges() {
    }

    /**
     * Constructs a new Badges instance with all fields.
     *
     * @param badgeid           the unique badge ID
     * @param badgeName         the name of the badge
     * @param badge_description the description of the badge
     * @param isSpecialist      whether the badge denotes specialist status
     * @param xpReward          the XP reward for earning the badge
     * @param ratingid          the associated rating
     */
    public Badges(int badgeid,String badgeName, String badge_description,Boolean isSpecialist, int xpReward, Ratings ratingid) {
        this.badgeid=badgeid;
        this.badgeName = badgeName;
        this.badge_description = badge_description;
        this.xpReward = xpReward;
        this.ratingid = ratingid;
        this.isSpecialist = isSpecialist;
    }


    /**
     * Returns the badge ID.
     *
     * @return the badge ID
     */
    public int getBadgeid() {
        return badgeid;
    }

    /**
     * Sets the badge ID.
     *
     * @param badgeid the badge ID to set
     */
    public void setBadgeid(int badgeid) {
        this.badgeid = badgeid;
    }

     /**
     * Returns the badge name.
     *
     * @return the badge name
     */
    public String getBadgeName() {
        return badgeName;
    }

    /**
     * Sets the badge name.
     *
     * @param badgeName the badge name to set
     */
    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }


    /**
     * Returns the badge description.
     *
     * @return the badge description
     */
    public String getBadgeDescription() {
        return badge_description;
    }

    /**
     * Sets the badge description.
     *
     * @param badge_description the badge description to set
     */
    public void setBadgeDescription(String badge_description) {
        this.badge_description = badge_description;
    }

    /**
     * Returns the XP reward for this badge.
     *
     * @return the XP reward
     */
    public int getXpReward() {
        return xpReward;
    }

    
    /**
     * Sets the XP reward for this badge.
     *
     * @param xpReward the XP reward to set
     */
    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    /**
     * Returns the associated rating.
     *
     * @return the rating
     */
    public Ratings getRatingid() {
        return ratingid;
    }

    /**
     * Sets the associated rating.
     *
     * @param ratingid the rating to set
     */
    public void setRatingid(Ratings ratingid) {
        this.ratingid = ratingid;
    }

    /**
     * Returns whether this badge denotes specialist status.
     *
     * @return true if the badge is a specialist badge, false otherwise
     */
    public Boolean getIsSpecialist() {
        return isSpecialist;
    }

    /**
     * Sets whether this badge denotes specialist status.
     *
     * @param isSpecialist true if the badge is a specialist badge, false otherwise
     */
    public void setIsSpecialist(Boolean isSpecialist) {
        this.isSpecialist = isSpecialist;
    }
}

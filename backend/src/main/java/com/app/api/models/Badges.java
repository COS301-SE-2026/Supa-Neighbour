package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "badgestable")
public class Badges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badgeid")
    private int badgeId;
    @Column(name = "badgename")
    private String badgeName;

    @Column(name = "isspecialist")
    private String description;

    @Column(name = "currentxp")
    private int xpReward;

    @Column(name = "ratingid")
    private String ratingId;

    public Badges() {
    }

    public Badges(String badgeName, String description, int xpReward, String ratingId) {
        this.badgeName = badgeName;
        this.description = description;
        this.xpReward = xpReward;
        this.ratingId = ratingId;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }
    
}

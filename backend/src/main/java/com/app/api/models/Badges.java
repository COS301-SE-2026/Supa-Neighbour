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

@Data
@Builder
@Entity
@Table(name = "badge_table")
public class Badges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private int badgeid;

    @Column(name = "badge_name")
    private String badgeName;

    @Column(name = "is_specialist")
    private String description;

    @Column(name = "current_xp")
    private int xpReward;

    @ManyToOne
    @JoinColumn(name = "rating_id")
    private Ratings ratingid;

    public Badges() {
    }

    public Badges(int badgeid,String badgeName, String description, int xpReward, Ratings ratingid) {
        this.badgeid=badgeid;
        this.badgeName = badgeName;
        this.description = description;
        this.xpReward = xpReward;
        this.ratingid = ratingid;
    }

    public int getBadgeid() {
        return badgeid;
    }

    public void setBadgeid(int badgeid) {
        this.badgeid = badgeid;
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

    public Ratings getRatingid() {
        return ratingid;
    }

    public void setRatingid(Ratings ratingid) {
        this.ratingid = ratingid;
    }
    
}

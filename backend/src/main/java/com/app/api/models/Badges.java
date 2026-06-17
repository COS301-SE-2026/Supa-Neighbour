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

    @Column(name = "badge_description")
    private String badge_description;

    @Column(name = "is_specialist")
    private Boolean isSpecialist;


    @Column(name = "current_xp")
    private int xpReward;

    @ManyToOne
    @JoinColumn(name = "rating_id")
    private Ratings ratingid;

    public Badges() {
    }

    public Badges(int badgeid,String badgeName, String badge_description,Boolean isSpecialist, int xpReward, Ratings ratingid) {
        this.badgeid=badgeid;
        this.badgeName = badgeName;
        this.badge_description = badge_description;
        this.xpReward = xpReward;
        this.ratingid = ratingid;
        this.isSpecialist = isSpecialist;
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

    public String getBadge_description() {
        return badge_description;
    }

    public void setBadge_description(String badge_description) {
        this.badge_description = badge_description;
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
    
    public Boolean getIsSpecialist() {
        return isSpecialist;
    }

    public void setIsSpecialist(Boolean isSpecialist) {
        this.isSpecialist = isSpecialist;
    }
}

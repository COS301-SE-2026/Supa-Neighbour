package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

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

    public Ratings(int ratingid, String ratingReview, int totalXpLevel, String currentGroup) {
        this.ratingid = ratingid;
        this.ratingReview = ratingReview;
        this.totalXpLevel = totalXpLevel;
        this.currentGroup = currentGroup;
    }

    public int getRatingid() {
        return ratingid;
    }

    public void setRatingid(int ratingid) {
        this.ratingid = ratingid;
    }

    public String getRatingReview() {
        return ratingReview;
    }

    public void setRatingReview(String ratingReview) {
        this.ratingReview = ratingReview;
    }

    public int getTotalXpLevel() {
        return totalXpLevel;
    }

    public void setTotalXpLevel(int totalXpLevel) {
        this.totalXpLevel = totalXpLevel;
    }

    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }
    
}

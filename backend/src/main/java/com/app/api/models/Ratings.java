package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ratingstable")
public class Ratings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ratingid")
    private int ratingId;
    @Column(name = "ratingreview")
    private String ratingReview;
    @Column(name = "totalxplevel")
    private int totalXpLevel;
    @Column(name = "currentgroup")
    private String currentGroup;

    public Ratings(int ratingId, String ratingReview, int totalXpLevel, String currentGroup) {
        this.ratingId = ratingId;
        this.ratingReview = ratingReview;
        this.totalXpLevel = totalXpLevel;
        this.currentGroup = currentGroup;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
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

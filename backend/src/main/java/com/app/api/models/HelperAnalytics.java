package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "helpertable")
public class HelperAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "helperid")
    private int helperId;

    @Column(name = "tasktypeid")
    private int taskTypeId;

    @Column(name = "locationid")
    private int locationId;

    @Column(name = "compatibilityid")
    private int compatibilityId;

    @Column(name = "locationid")
    private int locationid;

    @Column(name = "averagerating")
    private float averageRating;

    @Column(name = "averagegivingrating")
    private float averageGivingRating;

    public HelperAnalytics() {
        // Default constructor
    }

    public HelperAnalytics(int helperId, int taskTypeId, int locationId, int compatibilityId, int locationid, float averageRating, float averageGivingRating) {
        this.helperId = helperId;
        this.taskTypeId = taskTypeId;
        this.locationId = locationId;
        this.compatibilityId = compatibilityId;
        this.locationid = locationid;
        this.averageRating = averageRating;
        this.averageGivingRating = averageGivingRating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHelperId() {
        return helperId;
    }

    public void setHelperId(int helperId) {
        this.helperId = helperId;
    }

    public int getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(int taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getCompatibilityId() {
        return compatibilityId;
    }

    public void setCompatibilityId(int compatibilityId) {
        this.compatibilityId = compatibilityId;
    }

    public int getLocationid() {
        return locationid;
    }

    public void setLocationid(int locationid) {
        this.locationid = locationid;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public float getAverageGivingRating() {
        return averageGivingRating;
    }

    public void setAverageGivingRating(float averageGivingRating) {
        this.averageGivingRating = averageGivingRating;
    }
}
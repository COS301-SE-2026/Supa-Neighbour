package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "helper_analytics_table")
public class HelperAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "helper_analytics_id_seq")
    private int helperAnalyticsid;

    @OneToOne
    @JoinColumn(name = "helper_id")
    private Helper helperid;

    @OneToMany
    @JoinColumn(name = "task_type_id")
    private TaskType taskTypeid;

    @OneToMany
    @JoinColumn(name = "location_id")
    private Location locationid;

    @OneToMany
    @JoinColumn(name = "compatibility_id")
    private Compatibility compatibilityid;

    @Column(name = "average_rating")
    private float averageRating;

    @Column(name = "average_giving_rating")
    private float averageGivingRating;

    public HelperAnalytics() {
        // Default constructor
    }

    public HelperAnalytics(int helperAnalyticsid,Helper helperid, TaskType taskTypeid, Location locationid, Compatibility compatibilityid, float averageRating, float averageGivingRating) {
        this.helperid=helperid;
        this.helperAnalyticsid = helperAnalyticsid;
        this.taskTypeid = taskTypeid;
        this.locationid = locationid;
        this.compatibilityid = compatibilityid;
        this.averageRating = averageRating;
        this.averageGivingRating = averageGivingRating;
    }

    public int getHelperAnalyticsid() {
        return helperAnalyticsid;
    }

    public void setid(int helperAnalyticsid) {
        this.helperAnalyticsid = helperAnalyticsid;
    }

    public Helper getHelperid() {
        return helperid;
    }

    public void setHelperid(Helper helperid) {
        this.helperid = helperid;
    }

    public TaskType getTaskTypeid() {
        return taskTypeid;
    }

    public void setTaskTypeId(TaskType taskTypeid) {
        this.taskTypeid = taskTypeid;
    }

    public Location getLocationid() {
        return locationid;
    }

    public void setLocationId(Location locationid) {
        this.locationid = locationid;
    }

    public Compatibility getCompatibilityid() {
        return compatibilityid;
    }

    public void setCompatibilityId(Compatibility compatibilityid) {
        this.compatibilityid = compatibilityid;
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
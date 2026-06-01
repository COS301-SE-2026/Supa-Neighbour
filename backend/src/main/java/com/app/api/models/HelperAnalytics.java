package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "helper_analytics_table")
public class HelperAnalytics {
    @Id
    @Column(name = "helper_analytics_id")
    private String helperAnalyticsid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userid;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType taskTypeid;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location locationid;

    @ManyToOne
    @JoinColumn(name = "compatibility_id")
    private Compatibility compatibilityid;

    @Column(name = "average_rating")
    private float averageRating;

    @Column(name = "average_giving_rating")
    private float averageGivingRating;

    public HelperAnalytics() {
        // Default constructor
    }

    public HelperAnalytics(String helperAnalyticsid,User userid, TaskType taskTypeid, Location locationid, Compatibility compatibilityid, float averageRating, float averageGivingRating) {
        this.userid=userid;
        this.helperAnalyticsid = helperAnalyticsid;
        this.taskTypeid = taskTypeid;
        this.locationid = locationid;
        this.compatibilityid = compatibilityid;
        this.averageRating = averageRating;
        this.averageGivingRating = averageGivingRating;
    }

    public String getHelperAnalyticsid() {
        return helperAnalyticsid;
    }

    public void setid(String helperAnalyticsid) {
        this.helperAnalyticsid = helperAnalyticsid;
    }

    public User getUserid() {
        return userid;
    }

    public void setHelperid(User userid) {
        this.userid = userid;
    }

    public TaskType getTasktypeid() {
        return taskTypeid;
    }

    public void setTasktypeid(TaskType taskTypeid) {
        this.taskTypeid = taskTypeid;
    }

    public Location getLocationid() {
        return locationid;
    }

    public void setLocationid(Location locationid) {
        this.locationid = locationid;
    }

    public Compatibility getCompatibilityid() {
        return compatibilityid;
    }

    public void setCompatibilityid(Compatibility compatibilityid) {
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
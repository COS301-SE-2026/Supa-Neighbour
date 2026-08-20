package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

/**
 * Represents analytics data specific to helper users.
 * Tracks task performance, ratings, location data, and compatibility metrics for helpers.
 */
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

    @Column(name = "average_rating")
    private float averageRating;


    /**
     * Default constructor.
     */
    public HelperAnalytics() {
        // Default constructor
    }


    /**
    * Constructs a HelperAnalytics record with all fields specified.
    *
    * @param helperAnalyticsid  the helper analytics identifier
    * @param userid             the user associated with this analytics record
    * @param taskTypeid         the task type associated with this analytics record
    * @param locationid         the location associated with this analytics record
    * @param compatibilityid    the compatibility record associated with this helper
    * @param averageRating      the average rating received by the helper
    * @param averageGivingRating the average rating given by the helper
    */
    public HelperAnalytics(String helperAnalyticsid,User userid, TaskType taskTypeid, Location locationid, float averageRating) {
        this.userid=userid;
        this.helperAnalyticsid = helperAnalyticsid;
        this.taskTypeid = taskTypeid;
        this.locationid = locationid;
        this.averageRating = averageRating;
    }

    /**
     * Gets the helper analytics identifier.
     *
     * @return the helper analytics identifier
     */
    public String getHelperAnalyticsid() {
        return helperAnalyticsid;
    }

    /**
     * Sets the helper analytics identifier.
     *
     * @param helperAnalyticsid the helper analytics identifier
     */
    public void setHelperAnalyticsid(String helperAnalyticsid) {
        this.helperAnalyticsid = helperAnalyticsid;
    }

    /**
     * Gets the user associated with this analytics record.
     *
     * @return the user
     */
    public User getUserid() {
        return userid;
    }

    /**
     * Sets the user associated with this analytics record.
     *
     * @param userid the user
     */
    public void setUserid(User userid) {
        this.userid = userid;
    }
    /**
     * Get the id of a helper.
     * @return id of helper
     */
    public User getHelperid() {
        if(userid != null) {
            return userid;
        }
        return null; 
    }

    /**
     * Sets the user associated with this analytics record.
     *
     * @param userid the user
     */
    public void setHelperid(User userid) {
        this.userid = userid;
    }

    /**
     * Gets the task type associated with this analytics record.
     *
     * @return the task type
     */
    public TaskType getTasktypeid() {
        return taskTypeid;
    }

    /**
     * Sets the task type associated with this analytics record.
     *
     * @param taskTypeid the task type
     */
    public void setTasktypeid(TaskType taskTypeid) {
        this.taskTypeid = taskTypeid;
    }


    /**
     * Gets the location associated with this analytics record.
     *
     * @return the location
     */
    public Location getLocationid() {
        return locationid;
    }

    /**
     * Sets the location associated with this analytics record.
     *
     * @param locationid the location
     */
    public void setLocationid(Location locationid) {
        this.locationid = locationid;
    }

    /**
     * Gets the average rating received by the helper.
     *
     * @return the average received rating
     */
    public float getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating received by the helper.
     *
     * @param averageRating the average received rating
     */
    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }    
}

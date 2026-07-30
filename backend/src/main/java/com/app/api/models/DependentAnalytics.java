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
 * Represents analytics data specific to dependent users.
 * Tracks task metrics, ratings, and location-based statistics for dependents.
 */
@Data
@Builder
@Entity
@Table(name = "dependent_analytics_table")

public class DependentAnalytics {
    @Id
    @Column(name = "dependent_analytics_id")
    private String dependentanalyticsid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userid;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType tasktypeid;

    @Column(name = "total_tasks")
    private int totaltasks;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location locationid;

    @Column(name = "average_rating")
    private float aveeragerating;

    @Column(name = "average_giving_rating")
    private float averagegivingrating;

    /**
     * Constructs a DependentAnalytics record with all fields specified.
     *
     * @param dependentanalyticsid  the dependent analytics identifier
     * @param userid                the user associated with this analytics record
     * @param tasktypeid            the task type associated with this analytics record
     * @param totaltasks            the total number of tasks completed by the dependent
     * @param locationid            the location associated with this analytics record
     * @param aveeragerating        the average rating received by the dependent
     * @param averagegivingrating   the average rating given by the dependent
     */
    public DependentAnalytics(String dependentanalyticsid, User userid, TaskType tasktypeid, int totaltasks, Location locationid,float aveeragerating, float averagegivingrating) {
        this.dependentanalyticsid = dependentanalyticsid;
        this.userid = userid;
        this.tasktypeid = tasktypeid;
        this.totaltasks = totaltasks;
        this.locationid = locationid;
        this.aveeragerating = aveeragerating;
        this.averagegivingrating = averagegivingrating;
    }

    /**
     * Default constructor.
     */
    public DependentAnalytics(){

    }

    /**
     * Gets the dependent analytics identifier.
     *
     * @return the dependent analytics identifier
     */
    public String getDependentanalyticsid() {
        return dependentanalyticsid;
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
     * Gets the task type associated with this analytics record.
     *
     * @return the task type
     */
    public TaskType getTasktypeid() {
        return tasktypeid;
    }

    /**
     * Gets the total number of tasks completed by the dependent.
     *
     * @return the total tasks
     */
    public int getTotaltasks() {
        return totaltasks;
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
     * Gets the average rating received by the dependent.
     *
     * @return the average received rating
     */
    public float getAveeragerating() {
        return aveeragerating;
    }

    /**
     * Gets the average rating given by the dependent.
     *
     * @return the average given rating
     */
    public float getAveragegivingrating() {
        return averagegivingrating;
    }

    /**
     * Sets the dependent analytics identifier.
     *
     * @param dependentanalyticsid the dependent analytics identifier
     */
    public void setDependentanalyticsid(String dependentanalyticsid) {
        this.dependentanalyticsid = dependentanalyticsid;
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
     * Sets the task type associated with this analytics record.
     *
     * @param tasktypeid the task type
     */
    public void setTasktypeid(TaskType tasktypeid) {
        this.tasktypeid = tasktypeid;
    }

    /**
     * Sets the total number of tasks completed by the dependent.
     *
     * @param totaltasks the total tasks
     */
    public void setTotaltasks(int totaltasks) {
        this.totaltasks = totaltasks;
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
     * Sets the average rating received by the dependent.
     *
     * @param aveeragerating the average received rating
     */
    public void setAveeragerating(float aveeragerating) {
        this.aveeragerating = aveeragerating;
    }

    /**
     * Sets the average rating given by the dependent.
     *
     * @param averagegivingrating the average given rating
     */
    public void setAveragegivingrating(float averagegivingrating) {
        this.averagegivingrating = averagegivingrating;
    }

}

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

    public DependentAnalytics(String dependentanalyticsid, User userid, TaskType tasktypeid, int totaltasks, Location locationid,float aveeragerating, float averagegivingrating) {
        this.dependentanalyticsid = dependentanalyticsid;
        this.userid = userid;
        this.tasktypeid = tasktypeid;
        this.totaltasks = totaltasks;
        this.locationid = locationid;
        this.aveeragerating = aveeragerating;
        this.averagegivingrating = averagegivingrating;
    }

    public DependentAnalytics()
    {

    }
    
    public String getDependentanalyticsid() {
        return dependentanalyticsid;
    }

    public User getUserid() {
        return userid;
    }

    public TaskType getTasktypeid() {
        return tasktypeid;
    }

    public int getTotaltasks() {
        return totaltasks;
    }

    public Location getLocationid() {
        return locationid;
    }

    public float getAveeragerating() {
        return aveeragerating;
    }

    public float getAveragegivingrating() {
        return averagegivingrating;
    }

    public void setDependentanalyticsid(String dependentanalyticsid) {
        this.dependentanalyticsid = dependentanalyticsid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public void setTasktypeid(TaskType tasktypeid) {
        this.tasktypeid = tasktypeid;
    }

    public void setTotaltasks(int totaltasks) {
        this.totaltasks = totaltasks;
    }

    public void setLocationid(Location locationid) {
        this.locationid = locationid;
    }

    public void setAveeragerating(float aveeragerating) {
        this.aveeragerating = aveeragerating;
    }

    public void setAveragegivingrating(float averagegivingrating) {
        this.averagegivingrating = averagegivingrating;
    }

}

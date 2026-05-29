package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "dependent_analytics_table")

public class DependentAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dependent_analytics_id_seq")
    @Column(name = "dependent_analytics_id")
    private int dependentanalyticsid;

    @ManyToMany
    @JoinColumn(name = "user_id")
    private int userid;

    @Column(name = "tasktype_id")
    private int tasktypeid;

    @Column(name = "total_tasks")
    private int totaltasks;

    @Column(name = "location_id")
    private int locationid;

    @Column(name = "average_rating")
    private float aveeragerating;

    @Column(name = "average_giving_rating")
    private float averagegivingrating;

    public DependentAnalytics(int dependentanalyticsid, int userid, int tasktypeid, int totaltasks, int locationid, float aveeragerating, float averagegivingrating) {
        this.dependentanalyticsid = dependentanalyticsid;
        this.userid = userid;
        this.tasktypeid = tasktypeid;
        this.totaltasks = totaltasks;
        this.locationid = locationid;
        this.aveeragerating = aveeragerating;
        this.averagegivingrating = averagegivingrating;
    }   

    public int getDependentanalyticsid() {
        return dependentanalyticsid;
    }
    public int getUserid() {
        return userid;
    }
    public int getTasktypeid() {
        return tasktypeid;
    }
    public int getTotaltasks() {
        return totaltasks;
    }
    public int getLocationid() {
        return locationid;
    }
    public float getAveeragerating() {
        return aveeragerating;
    }
    public float getAveragegivingrating() {
        return averagegivingrating;
    }

    public void setDependentanalyticsid(int dependentanalyticsid) {
        this.dependentanalyticsid = dependentanalyticsid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }
    public void setTasktypeid(int tasktypeid) {
        this.tasktypeid = tasktypeid;
    }
    public void setTotaltasks(int totaltasks) {
        this.totaltasks = totaltasks;
    }
    public void setLocationid(int locationid) {
        this.locationid = locationid;
    }
    public void setAveeragerating(float aveeragerating) {
        this.aveeragerating = aveeragerating;
    }
    public void setAveragegivingrating(float averagegivingrating) {
        this.averagegivingrating = averagegivingrating;
    }

}

package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dependentanalyticstable")

public class DependentAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dependentanalyticsid")
    private int dependentanalyticsid;
    @Column(name = "userid")
    private int userid;
    @Column(name = "tasktypeid")
    private int tasktypeid;
    @Column(name = "totaltasks")
    private int totaltasks;
    @Column(name = "locationid")
    private int locationid;
    @Column(name = "aveeragerating")
    private float aveeragerating;
    @Column(name = "averagegivingrating")
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

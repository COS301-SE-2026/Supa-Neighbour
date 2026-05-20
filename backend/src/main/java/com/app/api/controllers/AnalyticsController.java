package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "analyticstable")

public class Analytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analyticsid")
    private int analyticsid;
    @Column(name = "taskid")
    private int taskid;
    @Column(name = "adminid")
    private String adminid;
    @Column(name = "helpertypeid")
    private String helpertypeid;
    @Column(name = "dependenttypeid")
    private String dependenttypeid;

    public Analytics() {
    }

    public Analytics(int analyticsid, int taskid, String adminid, String helpertypeid,String dependenttypeid) {
        this.analyticsid = analyticsid;
        this.taskid = taskid;
        this.adminid = adminid;
        this.helpertypeid = helpertypeid;
        this.dependenttypeid = dependenttypeid;
    }

    public int getAnalyticsid() {
        return analyticsid;
    }   

    public void setAnalyticsid(int analyticsid) {
        this.analyticsid = analyticsid;
    }

    public int getTaskid() {
        return taskid;
    }   

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getAdminid() {
        return adminid;
    }

    public void setAdminid(String adminid) {
        this.adminid = adminid;
    }

    public String getHelpertypeid() {
        return helpertypeid;
    }

    public void setHelpertypeid(String helpertypeid) {
        this.helpertypeid = helpertypeid;
    }

    public String getDependenttypeid() {
        return dependenttypeid;
    }

    public void setDependenttypeid(String dependenttypeid) {
        this.dependenttypeid = dependenttypeid;
    }

}

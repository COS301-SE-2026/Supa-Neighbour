package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "AnalyticsTable")
public class Analytics
{
    // 1. fields
    
    @Id
    @Column(name = "AnalyticsID")
    private int analyticsId;

    @Column(name = "TaskID")
    private Integer taskId;

    
    @Column(name = "AdminID")
    private Integer adminId;

    @Column(name = "HelperTypeID")
    private String helperTypeId;

    @Column(name = "DependentTypeID")
    private String dependentTypeId;

    // 2. constuctor
    public Analytics() {}

    // 3.getters and setters

    public int getAnalyticsId() { return analyticsId; }
    public void setAnalyticsId(int analyticsId) { this.analyticsId = analyticsId;}
    
    public Integer getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public Integer getAdminId() { return adminId;}
    public void setAdminId(Integer adminId) { this.adminId = adminId;}

     public String getHelperTypeId() { return helperTypeId;}
    public void setHelperTypeId(String helperTypeId) { this.helperTypeId = helperTypeId; }

    public String getDependentTypeId() { return dependentTypeId; }
    public void setDependentTypeId(String dependentTypeId) { this.dependentTypeId = dependentTypeId;}

}
package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DependentTable")
public class Dependent
{
    // 1. fields
    @Id
    @Column(name = "DependentID")
    private int dependentId;

    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "TaskTypeID")
    private Integer taskTypeId;

    @Column(name = "CompatibleID")
    private Integer compatibleId;


    // 2. constructor
    public Dependent() {}

    // 3 getters and settes
    public int getDependentId() { return dependentId;}
    public void setDependentId(int dependentId) { this.dependentId =  dependentId;}

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getTaskTypeId() { return taskTypeId; }
    public void setTaskTypeId(Integer taskTypeId) { this.taskTypeId = taskTypeId; }

    public Integer getCompatibleId() { return compatibleId; }
    public void setCompatibleId(Integer compatibleId) { this.compatibleId = compatibleId; }
}

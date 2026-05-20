package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "helpertable")
public class Helper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helperid")
    private int id;
    @Column(name = "userid")
    private String userId;
    @Column(name = "tasktypeid")
    private String taskTypeId;
    @Column(name = "badgeid")
    private String badgeId;
    @Column(name = "compatibilityid")
    private String compatibilityId;

    public Helper(int id, String userId, String taskTypeId, String badgeId, String compatibilityId) {
        this.id = id;
        this.userId = userId;
        this.taskTypeId = taskTypeId;
        this.badgeId = badgeId;
        this.compatibilityId = compatibilityId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTaskTypeId() {
        return taskTypeId;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public String getCompatibilityId() {
        return compatibilityId;
    }

    public void setTaskTypeId(String taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public void setCompatibilityId(String compatibilityId) {
        this.compatibilityId = compatibilityId;
    }
    
}

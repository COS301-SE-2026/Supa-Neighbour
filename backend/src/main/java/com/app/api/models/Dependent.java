package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dependenttable")
public class Dependent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dependentid")
    private int id;
    @Column(name = "userid")
    private int userId;
    @Column(name = "tasktypeid")
    private String taskTypeId;
    @Column(name = "compatibilityid")
    private String compatibilityId;

    public Dependent(int userId, String taskTypeId, String compatibilityId) {
        this.userId = userId;
        this.taskTypeId = taskTypeId;
        this.compatibilityId = compatibilityId;
    }
}

package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents an analytics record in the system.
 */
@Entity
@Table(name = "AnalyticsTable")
public class Analytics {

    /** The analytics ID. */
    @Id
    @Column(name = "AnalyticsID")
    private int analyticsId;

    /** The task ID. */
    @Column(name = "TaskID")
    private Integer taskId;

    /** The admin ID. */
    @Column(name = "AdminID")
    private Integer adminId;

    /** The helper type ID. */
    @Column(name = "HelperTypeID")
    private String helperTypeId;

    /** The dependent type ID. */
    @Column(name = "DependentTypeID")
    private String dependentTypeId;

    /**
     * Default constructor required by JPA.
     */
    public Analytics() {
        // default constructor needed by jpa
    }

    /**
     * Gets the analytics ID.
     * @return the analytics ID
     */
    public int getAnalyticsId() {
        return analyticsId;
    }

    /**
     * Sets the analytics ID.
     * @param analyticsId the analytics ID
     */
    public void setAnalyticsId(int analyticsId) {
        this.analyticsId = analyticsId;
    }

    /**
     * Gets the task ID.
     * @return the task ID
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * Sets the task ID.
     * @param taskId the task ID
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Gets the admin ID.
     * @return the admin ID
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * Sets the admin ID.
     * @param adminId the admin ID
     */
    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    /**
     * Gets the helper type ID.
     * @return the helper type ID
     */
    public String getHelperTypeId() {
        return helperTypeId;
    }

    /**
     * Sets the helper type ID.
     * @param helperTypeId the helper type ID
     */
    public void setHelperTypeId(String helperTypeId) {
        this.helperTypeId = helperTypeId;
    }

    /**
     * Gets the dependent type ID.
     * @return the dependent type ID
     */
    public String getDependentTypeId() {
        return dependentTypeId;
    }

    /**
     * Sets the dependent type ID.
     * @param dependentTypeId the dependent type ID
     */
    public void setDependentTypeId(String dependentTypeId) {
        this.dependentTypeId = dependentTypeId;
    }
}

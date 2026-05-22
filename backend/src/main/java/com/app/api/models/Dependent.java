package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a dependent profile in the system.
 */
@Entity
@Table(name = "DependentTable")
public class Dependent {

    /** The dependent ID. */
    @Id
    @Column(name = "DependentID")
    private int dependentId;

    /** The user ID. */
    @Column(name = "UserID")
    private Integer userId;

    /** The task type ID. */
    @Column(name = "TaskTypeID")
    private Integer taskTypeId;

    /** The compatible ID. */
    @Column(name = "CompatibleID")
    private Integer compatibleId;

    /**
     * Default constructor.
     */
    public Dependent() {
        // needed by jpa
    }

    /**
     * Gets the dependent ID.
     * @return the dependent ID
     */
    public int getDependentId() {
        return dependentId;
    }

    /**
     * Sets the dependent ID.
     * @param dependentId the dependent ID
     */
    public void setDependentId(int dependentId) {
        this.dependentId = dependentId;
    }

    /**
     * Gets the user ID.
     * @return the user ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     * @param userId the user ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Gets the task type ID.
     * @return the task type ID
     */
    public Integer getTaskTypeId() {
        return taskTypeId;
    }

    /**
     * Sets the task type ID.
     * @param taskTypeId the task type ID
     */
    public void setTaskTypeId(Integer taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    /**
     * Gets the compatible ID.
     * @return the compatible ID
     */
    public Integer getCompatibleId() {
        return compatibleId;
    }

    /**
     * Sets the compatible ID.
     * @param compatibleId the compatible ID
     */
    public void setCompatibleId(Integer compatibleId) {
        this.compatibleId = compatibleId;
    }
}

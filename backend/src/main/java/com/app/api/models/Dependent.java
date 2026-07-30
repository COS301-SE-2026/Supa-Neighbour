package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
/**
 * Represents a dependent profile in the system.
 */
@Data
@Getter
@Setter
@Entity
@Table(name = "Dependent_table")
public class Dependent {

    /** The dependent ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dependent_id")
    private int dependentId;

    /** The user ID. */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    /** The task type ID. */
    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType taskTypeId;

    /**
     * Default constructor required by JPA.
     */
    public Dependent() {
        // needed by jpa
    }

    /**
     * Constructs a new {@code Dependent} with the specified identifier,
     * associated user, and task type.
     *
     * @param dependentId the unique identifier of the dependent.
     * @param userId the user associated with this dependent.
     * @param taskTypeId the task type associated with this dependent.
     */
    public Dependent(int dependentId, User userId, TaskType taskTypeId) {
        this.dependentId = dependentId;
        this.userId = userId;
        this.taskTypeId = taskTypeId;
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
    public User getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     * @param userId the user ID
     */
    public void setUserId(User userId) {
        this.userId = userId;
    }

    /**
     * Gets the task type ID.
     * @return the task type ID
     */
    public TaskType getTaskTypeId() {
        return taskTypeId;
    }

    /**
     * Sets the task type ID.
     * @param taskTypeId the task type ID
     */
    public void setTaskTypeId(TaskType taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

}

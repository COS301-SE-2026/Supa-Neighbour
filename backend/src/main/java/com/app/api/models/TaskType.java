package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a type of task that can be requested in the system.
 * Defines task categories, specialist requirements, and XP rewards.
 */
@Data
@Builder
@Entity
@Table(name = "task_type_table")
public class TaskType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_type_id")
    private int tasktypeid;

    @ManyToOne
    @JoinColumn(name = "associated_badge_id")
    private Badges badgeid;

    @Column(name = "type_description")
    private String description;

    @Column(name = "needs_specialist")
    private boolean needsSpecialist;

    @Column(name = "xp_worth")
    private int xpWorth;

    /**
     * Default constructor.
     */
    public TaskType() {
    }

    /**
     * Constructs a task type with all fields.
     *
     * @param tasktypeid the task type ID
     * @param badgeid the associated badge
     * @param description the task description
     * @param needsSpecialist whether a specialist is required
     * @param xpWorth the XP reward for completing the task
     */
    public TaskType(int tasktypeid, Badges badgeid, String description, boolean needsSpecialist, int xpWorth) {
        this.tasktypeid = tasktypeid;
        this.badgeid = badgeid;
        this.description = description;
        this.needsSpecialist = needsSpecialist;
        this.xpWorth = xpWorth;
    }

    /**
     * Gets the task type ID.
     *
     * @return the task type ID
     */
    public int getTasktypeid() {
        return tasktypeid;
    }

    /**
     * Gets the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether a specialist is needed.
     *
     * @return true if a specialist is required, false otherwise
     */
    public boolean isNeedsSpecialist() {
        return needsSpecialist;
    }

    /**
     * Gets the XP reward value.
     *
     * @return the XP reward
     */
    public int getXpWorth() {
        return xpWorth;
    }

    /**
     * Gets the associated badge.
     *
     * @return the associated badge
     */
    public Badges getBadgeid() {
        return badgeid;
    }

    /**
     * Sets the associated badge.
     *
     * @param badgeid the associated badge
     */
    public void setBadgeid(Badges badgeid) {
        this.badgeid = badgeid;
    }

    /**
     * Sets the task description.
     *
     * @param description the task description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets whether a specialist is required.
     *
     * @param needsSpecialist true if a specialist is required
     */
    public void setNeedsSpecialist(boolean needsSpecialist) {
        this.needsSpecialist = needsSpecialist;
    }

    /**
     * Sets the XP reward value.
     *
     * @param xpWorth the XP reward value
     */
    public void setXpWorth(int xpWorth) {
        this.xpWorth = xpWorth;
    }
}

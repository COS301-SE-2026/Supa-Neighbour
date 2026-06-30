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
 * Represents a helper user who provides assistance with tasks.
 * Helpers specialize in certain task types and can earn badges.
 */
@Data
@Builder
@Entity
@Table(name = "helper_table")
public class Helper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helper_id")
    private int helperid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userid;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType taskTypeid;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badges badgeid;

    /**
     * Constructs a Helper with all fields specified.
     *
     * @param helperid    the helper identifier
     * @param userid      the user associated with this helper
     * @param taskTypeid  the task type the helper specializes in
     * @param badgeid     the badge earned by the helper
     */
    public Helper(int helperid, User userid, TaskType taskTypeid, Badges badgeid) {
        this.helperid = helperid;
        this.userid = userid;
        this.taskTypeid = taskTypeid;
        this.badgeid = badgeid;
    }

    /**
     * Default constructor.
     */
    public Helper(){

    }

    /**
     * Sets the helper identifier.
     *
     * @param helperid the helper identifier
     */
    public void setHelperid(int helperid) {
        this.helperid = helperid;
    }

    /**
     * Sets the user associated with this helper.
     *
     * @param userid the user
     */
    public void setUserid(User userid) {
        this.userid = userid;
    }

    /**
     * Gets the helper identifier.
     *
     * @return the helper identifier
     */
    public int getHelperid() {
        return helperid;
    }

    /**
     * Gets the user associated with this helper.
     *
     * @return the user
     */
    public User getUserid() {
        return userid;
    }

    /**
     * Gets the task type the helper specializes in.
     *
     * @return the task type
     */
    public TaskType getTaskTypeid() {
        return taskTypeid;
    }

    /**
     * Gets the badge earned by the helper.
     *
     * @return the badge
     */
    public Badges getBadgeid() {
        return badgeid;
    }

    /**
     * Sets the task type the helper specializes in.
     *
     * @param taskTypeid the task type
     */
    public void setTaskTypeid(TaskType taskTypeid) {
        this.taskTypeid = taskTypeid;
    }

    /**
     * Sets the badge earned by the helper.
     *
     * @param badgeid the badge
     */
    public void setBadgeid(Badges badgeid) {
        this.badgeid = badgeid;
    }

}

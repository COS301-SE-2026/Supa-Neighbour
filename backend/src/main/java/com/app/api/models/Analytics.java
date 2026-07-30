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
 * Represents an analytics record within the application.
 * <p>
 * This entity maps to the {@code analytics_table} database table and
 * stores references to a task, administrator, helper analytics,
 * and dependent analytics.
 * </p>
 */
@Data
@Builder
@Entity
@Table(name = "analytics_table")
public class Analytics {

    /** the unique identifier of the analytics table */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analytics_id")
    private int analyticsid;

    /** task associated with the analytics tables */
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskInvoice taskid;
    /** the administrator associated with the analytics record */
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin adminid;

    /**the helper analytics associated with the record */
    @ManyToOne
    @JoinColumn(name = "helper_type_id")
    private HelperAnalytics helpertypeid;

    /** the dependent information associated with this record */
    @ManyToOne
    @JoinColumn(name = "dependent_type_id")
    private DependentAnalytics dependenttypeid;

    /** the default constructor required by JPA */
    public Analytics() {
    }

    /**
     * Constructs an analytics record with all fields.
     *
     * @param analyticsid the unique identifier of the analytics record
     * @param taskid the associated task
     * @param adminid the associated administrator
     * @param helpertypeid the associated helper analytics
     * @param dependenttypeid the associated dependent analytics
     */
    public Analytics(int analyticsid, TaskInvoice taskid, Admin adminid, HelperAnalytics helpertypeid,DependentAnalytics dependenttypeid) {
        this.analyticsid = analyticsid;
        this.taskid = taskid;
        this.adminid = adminid;
        this.helpertypeid = helpertypeid;
        this.dependenttypeid = dependenttypeid;
    }

    /**
     * Returns the analytics record identifier.
     *
     * @return the analytics ID
     */
    public int getAnalyticsid() {
        return analyticsid;
    }

    /**
     * Sets the analytics record identifier.
     * 
     * @param analyticsid the unique idetifier of the analytics record
     */
    public void setAnalyticsid(int analyticsid) {
        this.analyticsid = analyticsid;
    }

    /**
     * Returns the taskid associated with the analytics record
     *
     * @return the tasksid
     */
    public TaskInvoice getTaskid() {
        return taskid;
    }

    /**
     * Sets the task identifier
     * 
     * @param taskid the unique idetifier of the task
     */
    public void setTaskid(TaskInvoice taskid) {
        this.taskid = taskid;
    }

    /**
     * Returns the analytics record identifier.
     * 
     * @return administrator's id
     */
    public Admin getAdminid() {
        return adminid;
    }

    /**
     * Sets the admin identifier
     * 
     * @param adminid the unique idetifier of the administrator
     */
    public void setAdminid(Admin adminid) {
        this.adminid = adminid;
    }

    /**
     * Returns the Helper's identifier
     * 
     * @return Helper's id
     */
    public HelperAnalytics getHelpertypeid() {
        return helpertypeid;
    }

    /**
     * Sets the helper identifier
     * 
     * @param helpertypeid the unique idetifier of the helper
     */
    public void setHelpertypeid(HelperAnalytics helpertypeid) {
        this.helpertypeid = helpertypeid;
    }

    /**
     * Returns the Dependent's identifier
     * 
     * @return Dependent's id
     */
    public DependentAnalytics getDependenttypeid() {
        return dependenttypeid;
    }

    /**
     * Sets the Dependent's identifier
     * 
     * @param dependenttypeid the unique idetifier of the dependent
     */
    public void setDependenttypeid(DependentAnalytics dependenttypeid) {
        this.dependenttypeid = dependenttypeid;
    }
}

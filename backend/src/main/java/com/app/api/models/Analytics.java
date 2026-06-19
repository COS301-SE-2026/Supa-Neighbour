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
 * Represents analytics data for tracking system metrics.
 * Stores information about tasks, admins, and user type analytics.
 */
@Data
@Builder
@Entity
@Table(name = "analytics_table")
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analytics_id")
    private int analyticsid;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskInvoice taskid;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin adminid;

    @ManyToOne
    @JoinColumn(name = "helper_type_id")
    private HelperAnalytics helpertypeid;

    @ManyToOne
    @JoinColumn(name = "dependent_type_id")
    private DependentAnalytics dependenttypeid;

    /**
     * Default constructor.
     */
    public Analytics() {
    }

    /**
     * Constructs an Analytics record with all fields specified.
     *
     * @param analyticsid      the analytics identifier
     * @param taskid           the task invoice associated with this analytics record
     * @param adminid          the admin associated with this analytics record
     * @param helpertypeid     the helper analytics type associated with this record
     * @param dependenttypeid  the dependent analytics type associated with this record
     */
    public Analytics(int analyticsid, TaskInvoice taskid, Admin adminid, HelperAnalytics helpertypeid,DependentAnalytics dependenttypeid) {
        this.analyticsid = analyticsid;
        this.taskid = taskid;
        this.adminid = adminid;
        this.helpertypeid = helpertypeid;
        this.dependenttypeid = dependenttypeid;
    }

    /**
     * Gets the analytics identifier.
     *
     * @return the analytics identifier
     */
    public int getAnalyticsid() {
        return analyticsid;
    }

    /**
     * Sets the analytics identifier.
     *
     * @param analyticsid the analytics identifier
     */
    public void setAnalyticsid(int analyticsid) {
        this.analyticsid = analyticsid;
    }

    /**
     * Gets the task invoice associated with this analytics record.
     *
     * @return the task invoice
     */
    public TaskInvoice getTaskid() {
        return taskid;
    }

    /**
     * Sets the task invoice associated with this analytics record.
     *
     * @param taskid the task invoice
     */ 
    public void setTaskid(TaskInvoice taskid) {
        this.taskid = taskid;
    }

    /**
     * Gets the admin associated with this analytics record.
     *
     * @return the admin
     */
    public Admin getAdminid() {
        return adminid;
    }

    /**
     * Sets the admin associated with this analytics record.
     *
     * @param adminid the admin
     */
    public void setAdminid(Admin adminid) {
        this.adminid = adminid;
    }

    /**
     * Gets the helper analytics type associated with this record.
     *
     * @return the helper analytics type
     */
    public HelperAnalytics getHelpertypeid() {
        return helpertypeid;
    }

    /**
     * Sets the helper analytics type associated with this record.
     *
     * @param helpertypeid the helper analytics type
     */
    public void setHelpertypeid(HelperAnalytics helpertypeid) {
        this.helpertypeid = helpertypeid;
    }

    /**
     * Gets the dependent analytics type associated with this record.
     *
     * @return the dependent analytics type
     */
    public DependentAnalytics getDependenttypeid() {
        return dependenttypeid;
    }

    /**
     * Sets the dependent analytics type associated with this record.
     *
     * @param dependenttypeid the dependent analytics type
     */
    public void setDependenttypeid(DependentAnalytics dependenttypeid) {
        this.dependenttypeid = dependenttypeid;
    }
}

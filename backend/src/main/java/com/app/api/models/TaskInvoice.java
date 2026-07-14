package com.app.api.models;
import java.sql.Date;

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
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;



/**
 * Represents a task assignment between a helper and dependent.
 * Contains task details, dates, ratings, and reviews from all parties involved.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_invoice_table")
public class TaskInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private int taskid;

    @ManyToOne
    @JoinColumn(name = "helper_id")
    private Helper helperid;

    @ManyToOne
    @JoinColumn(name = "dependent_id")
    private Dependent dependentid;


    @Column(name = "is_immediate")
    private boolean isImmediate;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location locationid;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType tasktypeid;

    @Column(name = "needs_specialist")
    private boolean needsspecialist;

    @ManyToOne
    @JoinColumn(name = "signed_admin_id")
    private Admin signedadminid;

    @Column(name = "start_date")
    private Date startdate;

    @Column(name = "end_date")
    private Date enddate;

    @ManyToOne
    @JoinColumn(name = "helper_badge_id")
    private Badges helperbadgeid;


    @Column(name = "dependent_rating_review")
    private String dependentRatingreview;

    @Column(name = "helper_rating_review")
    private String helperRatingreview;

    @Column(name = "admin_review")
    private String adminReview;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "compatibility_id")
    private Compatibility compatibilityid;

    /**
     * Gets the task identifier.
     *
     * @return the task identifier
     */
    public int getTaskid() {
        return taskid;
    }

    /**
     * Sets the task identifier.
     *
     * @param taskid the task identifier
     */
    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    /**
     * Gets the helper assigned to the task.
     *
     * @return the helper
     */
    public Helper getHelperid() {
        return helperid;
    }

    /**
     * Sets the helper assigned to the task.
     *
     * @param helperid the helper
     */
    public void setHelperid(Helper helperid) {
        this.helperid = helperid;
    }

    /**
     * Gets the dependent requesting the task.
     *
     * @return the dependent
     */
    public Dependent getDependentid() {
        return dependentid;
    }

    /**
     * Sets the dependent requesting the task.
     *
     * @param dependentid the dependent
     */
    public void setDependentid(Dependent dependentid) {
        this.dependentid = dependentid;
    }

    /**
     * Returns whether the task requires immediate assistance.
     *
     * @return true if the task is immediate, false otherwise
     */
    public boolean isImmediate() {
        return isImmediate;
    }

        /**
     * Sets whether the task requires immediate assistance.
     *
     * @param isImmediate true if the task is immediate, false otherwise
     */
    public void setImmediate(boolean isImmediate) {
        this.isImmediate = isImmediate;
    }

    /**
     * Gets the location where the task takes place.
     *
     * @return the location
     */
    public Location getLocationid() {
        return locationid;
    }

    /**
     * Sets the location where the task takes place.
     *
     * @param locationid the location
     */
    public void setLocationid(Location locationid) {
        this.locationid = locationid;
    }

    /**
     * Gets the type of task being performed.
     *
     * @return the task type
     */
    public TaskType getTasktypeid() {
        return tasktypeid;
    }

    /**
     * Sets the type of task being performed.
     *
     * @param tasktypeid the task type
     */
    public void setTasktypeid(TaskType tasktypeid) {
        this.tasktypeid = tasktypeid;
    }

    /**
     * Returns whether the task requires a specialist helper.
     *
     * @return true if a specialist is needed, false otherwise
     */
    public boolean isNeedsspecialist() {
        return needsspecialist;
    }

    /**
     * Sets whether the task requires a specialist helper.
     *
     * @param needsspecialist true if a specialist is needed, false otherwise
     */
    public void setNeedsspecialist(boolean needsspecialist) {
        this.needsspecialist = needsspecialist;
    }

    /**
     * Gets the admin who signed off on the task.
     *
     * @return the signed admin
     */
    public Admin getSignedadminid() {
        return signedadminid;
    }

    /**
     * Sets the admin who signed off on the task.
     *
     * @param signedadminid the signed admin
     */
    public void setSignedadminid(Admin signedadminid) {
        this.signedadminid = signedadminid;
    }

    /**
     * Gets the date the task begins.
     *
     * @return the start date
     */
    public Date getStartdate() {
        return startdate;
    }

    /**
     * Sets the date the task begins.
     *
     * @param startdate the start date
     */
    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    /**
     * Gets the date the task ends.
     *
     * @return the end date
     */
    public Date getEnddate() {
        return enddate;
    }

    /**
     * Sets the date the task ends.
     *
     * @param enddate the end date
     */
    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    /**
     * Gets the status of task
     *
     * @return status of task
     */
    public String getStatus() {
        return status;
    }

    
    /**
     * Sets the status of the task
     *
     * @param status the status of task
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the badge awarded to the helper for the task.
     *
     * @return the helper badge
     */
    public Badges getHelperbadgeid() {
        return helperbadgeid;
    }

    /**
     * Sets the badge awarded to the helper for the task.
     *
     * @param helperbadgeid the helper badge
     */
    public void setHelperbadgeid(Badges helperbadgeid) {
        this.helperbadgeid = helperbadgeid;
    }

    /**
     * Gets the dependent's rating and review of the task.
     *
     * @return the dependent rating review
     */
    public String getDependentratingid() {
        return dependentRatingreview;
    }

    /**
     * Sets the dependent's rating and review of the task.
     *
     * @param dependentRatingreview the dependent rating review
     */
    public void setDependentratingreview(String dependentRatingreview) {
        this.dependentRatingreview = dependentRatingreview;
    }

    /**
     * Gets the dependent's rating and review of the task.
     *
     * @returnthe dependent rating review
     */
    public String getDependentratingreview() {
        return dependentRatingreview;
    }

    /**
     * Gets the helper's rating and review of the task.
     *
     * @return the helper rating review
     */
    public String getHelperRatingreview() {
        return helperRatingreview;
    }

    /**
     * Sets the helper's rating and review of the task.
     *
     * @param helperRatingreview the helper rating review
     */
    public void setHelperRatingreview(String helperRatingreview) {
        this.helperRatingreview = helperRatingreview;
    }

    /**
     * Gets the admin's review of the task.
     *
     * @return the admin review
     */
    public String getAdminReview() {
        return adminReview;
    }

    /**
     * Sets the admin's review of the task.
     *
     * @param adminReview the admin review
     */
    public void setAdminreview(String adminReview) {
        this.adminReview = adminReview;
    }

    /**
     * Gets the compatibility record between the helper and dependent.
     *
     * @return the compatibility record
     */
    public Compatibility getCompatibilityid() {
        return compatibilityid;
    }

    /**
     * Sets the compatibility record between the helper and dependent.
     *
     * @param compatibilityid the compatibility record
     */
    public void setCompatibilityid(Compatibility compatibilityid) {
        this.compatibilityid = compatibilityid;
    }

}

package com.app.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Date;
import java.util.List;


/**
 * Represents a task in the system.
 */
@Entity
@Table(name = "task_invoice_table")
public class Task {

    /** The task ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private int taskId;


    /** The helper ID. */
    @Column(name = "helper_id")
    private Integer helperId;

    /** The dependent ID. */
    @Column(name = "dependent_id")
    private Integer dependentId;

    /** Whether the task is immediate. */
    @Column(name = "is_immediate")
    private boolean isImmediate;

    /** The location ID. */
    @Column(name = "location_id")
    private Integer locationId;

    /** The task type ID. */
    @Column(name = "task_type_id")
    private Integer taskTypeId;

    /** Whether the task needs a specialist. */
    @Column(name = "needs_specialist")
    private boolean needsSpecialist;

    /** The signed admin ID. */
    @Column(name = "signed_admin_id")
    private Integer signedAdminId;

    /** The start date. */
    @Column(name = "start_date")
    private Date startDate;

    /** The expected start time. */
    @Column(name = "start_time")
    private java.time.LocalTime startTime;

    /** The end date. */
    @Column(name = "end_date")
    private Date endDate;

    /** The helper badge ID. */
    @Column(name = "helper_badge_id")
    private Integer helperBadgeId;

    /** The dependent rating ID. */
    @Column(name = "dependent_rating_review")
    private String dependentRatingId;

    /** The helper rating ID. */
    @Column(name = "helper_rating_review")
    private String helperRatingId;

    /** The admin review. */
    @Column(name = "admin_review")
    private String adminReview;

    /** The compatibility ID. */
    @Column(name = "compatibility_id")
    private Integer compatibilityId;

    /** The task status. */
    @Column(name = "status")
    private String status;

    @Column(name = "title")
    private String title;

    @Column(name = "instructions")
    private String instructions;

    @JsonIgnore
    @OneToMany(mappedBy = "taskid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskImage> images;


    /**
     * Default constructor required by JPA.
     */
    public Task() {
        // needed by jpa
    }

    /**
     * Gets the task ID.
     * @return the task ID
     */
    public int getTaskId() {
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
     * Gets the helper ID.
     * @return the helper ID
     */
    public Integer getHelperId() {
        return helperId;
    }

    /**
     * Sets the helper ID.
     * @param helperId the helper ID
     */
    public void setHelperId(Integer helperId) {
        this.helperId = helperId;
    }

    /**
     * Gets the dependent ID.
     * @return the dependent ID
     */
    public Integer getDependentId() {
        return dependentId;
    }

    /**
     * Sets the dependent ID.
     * @param dependentId the dependent ID
     */
    public void setDependentId(Integer dependentId) {
        this.dependentId = dependentId;
    }

    /**
     * Returns whether the task is immediate.
     * @return true if immediate
     */
    public boolean isImmediate() {
        return isImmediate;
    }

    /**
     * Sets whether the task is immediate.
     * @param isImmediate true if immediate
     */
    public void setImmediate(boolean isImmediate) {
        this.isImmediate = isImmediate;
    }

    /**
     * Gets the location ID.
     * @return the location ID
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * Sets the location ID.
     * @param locationId the location ID
     */
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
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
     * Returns whether the task needs a specialist.
     * @return true if specialist needed
     */
    public boolean isNeedsSpecialist() {
        return needsSpecialist;
    }

    /**
     * Sets whether the task needs a specialist.
     * @param needsSpecialist true if specialist needed
     */
    public void setNeedsSpecialist(boolean needsSpecialist) {
        this.needsSpecialist = needsSpecialist;
    }

    /**
     * Gets the signed admin ID.
     * @return the signed admin ID
     */
    public Integer getSignedAdminId() {
        return signedAdminId;
    }

    /**
     * Sets the signed admin ID.
     * @param signedAdminId the signed admin ID
     */
    public void setSignedAdminId(Integer signedAdminId) {
        this.signedAdminId = signedAdminId;
    }

    /**
     * Gets the start date.
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     * @param startDate the start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    /**
     * Gets the expected start time.
     * @return the start time as HH:mm:ss
     */
    public java.time.LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the expected start time.
     * @param startTime the start time as HH:mm:ss
     */
    public void setStartTime(java.time.LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end date.
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     * @param endDate the end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the helper badge ID.
     * @return the helper badge ID
     */
    public Integer getHelperBadgeId() {
        return helperBadgeId;
    }

    /**
     * Sets the helper badge ID.
     * @param helperBadgeId the helper badge ID
     */
    public void setHelperBadgeId(Integer helperBadgeId) {
        this.helperBadgeId = helperBadgeId;
    }

    /**
     * Gets the dependent rating ID.
     * @return the dependent rating ID
     */
    public String getDependentRatingId() {
        return dependentRatingId;
    }

    /**
     * Sets the dependent rating ID.
     * @param dependentRatingId the dependent rating ID
     */
    public void setDependentRatingId(String dependentRatingId) {
        this.dependentRatingId = dependentRatingId;
    }

    /**
     * Gets the helper rating ID.
     * @return the helper rating ID
     */
    public String getHelperRatingId() {
        return helperRatingId;
    }

    /**
     * Sets the helper rating ID.
     * @param helperRatingId the helper rating ID
     */
    public void setHelperRatingId(String helperRatingId) {
        this.helperRatingId = helperRatingId;
    }

    /**
     * Gets the admin review.
     * @return the admin review
     */
    public String getAdminReview() {
        return adminReview;
    }

    /**
     * Sets the admin review.
     * @param adminReview the admin review
     */
    public void setAdminReview(String adminReview) {
        this.adminReview = adminReview;
    }

    /**
     * Gets the compatibility ID.
     * @return the compatibility ID
     */
    public Integer getCompatibilityId() {
        return compatibilityId;
    }

    /**
     * Sets the compatibility ID.
     * @param compatibilityId the compatibility ID
     */
    public void setCompatibilityId(Integer compatibilityId) {
        this.compatibilityId = compatibilityId;
    }

    /**
     * Gets the task status.
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the task status.
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the title of the task.
     *
     * @return the task title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the task.
     *
     * @param title the task title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the instructions for the task.
     *
     * @return the task instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets the instructions for the task.
     *
     * @param instructions the task instructions
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Gets the list of images attached to the task.
     *
     * @return the task images
     */
    public List<TaskImage> getImages() {
        return images;
    }

    /**
     * Sets the list of images attached to the task.
     *
     * @param images the task images
     */
    public void setImages(List<TaskImage> images) {
        this.images = images;
    }

}

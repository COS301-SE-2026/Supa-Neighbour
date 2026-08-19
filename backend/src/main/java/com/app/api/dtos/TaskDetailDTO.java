package com.app.api.dtos;

import java.sql.Date;

/**
 * Represents a task along with resolved requester and helper display names,
 * since the Task entity itself only stores their IDs.
 */
public class TaskDetailDTO {

    /** The task ID. */
    private int taskId;

    /** The helper ID. */
    private Integer helperId;

    /** The dependent ID. */
    private Integer dependentId;

    /** Whether the task is immediate. */
    private boolean isImmediate;

    /** The location ID. */
    private Integer locationId;

    /** The task type ID. */
    private Integer taskTypeId;

    /** Whether the task needs a specialist. */
    private boolean needsSpecialist;

    /** The signed admin ID. */
    private Integer signedAdminId;

    /** The start date. */
    private Date startDate;

    /** The end date. */
    private Date endDate;

    /** The helper badge ID. */
    private Integer helperBadgeId;

    /** The dependent rating ID. */
    private String dependentRatingId;

    /** The helper rating ID. */
    private String helperRatingId;

    /** The admin review. */
    private String adminReview;

    /** The compatibility ID. */
    private Integer compatibilityId;

    /** The task status. */
    private String status;

    /** Display name of the dependent who requested the task. */
    private String requesterName;

    /** Display name of the helper assigned to the task, if any. */
    private String helperName;

    /** User ID of the dependent who requested the task. */
    private Integer requesterUserId;

    /** The task title. */
    private String title;

    /** The task instructions. */
    private String instructions;


    /**
     * Default constructor required for serialization.
     */
    public TaskDetailDTO() {
        // needed for serialization
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
     * Gets the requester's display name.
     * @return the requester name
     */
    public String getRequesterName() {
        return requesterName;
    }

    /**
     * Sets the requester's display name.
     * @param requesterName the requester name
     */
    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    /**
     * Gets the helper's display name.
     * @return the helper name
     */
    public String getHelperName() {
        return helperName;
    }

    /**
     * Sets the helper's display name.
     * @param helperName the helper name
     */
    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    /**
     * Gets the requester's user ID.
     * @return the requester user ID
     */
    public Integer getRequesterUserId() {
        return requesterUserId;
    }

    /**
     * Sets the requester's user ID.
     * @param requesterUserId the requester user ID
     */
    public void setRequesterUserId(Integer requesterUserId) {
        this.requesterUserId = requesterUserId;
    }


    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

}

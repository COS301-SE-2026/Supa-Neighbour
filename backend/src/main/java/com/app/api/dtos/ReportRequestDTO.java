package com.app.api.dtos;

/**
 * Request body for PUT /api/report.
 */
public class ReportRequestDTO {

    /** One of: USER, POST, COMMENT, TASK_DISPUTE. */
    private String reportType;

    /** Populated only when reportType = USER. */
    private Integer reportedUserId;

    /** Populated only when reportType = POST. */
    private Integer reportedPostId;

    /** Populated only when reportType = COMMENT. */
    private Integer reportedCommentId;

    /** Populated only when reportType = TASK_DISPUTE. */
    private Integer taskId;

    /** One of: NO_SHOW, INCOMPLETE, DAMAGE — only for TASK_DISPUTE. */
    private String disputeReason;

    /** Short reason label supplied by the reporter. */
    private String reason;

    /** Free-text description of the incident. */
    private String description;

    /**
     * Gets the report type.
     *
     * @return the report type
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * Sets the report type.
     *
     * @param reportType the report type
     */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    /**
     * Gets the reported user ID.
     *
     * @return the reported user ID
     */
    public Integer getReportedUserId() {
        return reportedUserId;
    }

    /**
     * Sets the reported user ID.
     *
     * @param reportedUserId the reported user ID
     */
    public void setReportedUserId(Integer reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    /**
     * Gets the reported post ID.
     *
     * @return the reported post ID
     */
    public Integer getReportedPostId() {
        return reportedPostId;
    }

    /**
     * Sets the reported post ID.
     *
     * @param reportedPostId the reported post ID
     */
    public void setReportedPostId(Integer reportedPostId) {
        this.reportedPostId = reportedPostId;
    }

    /**
     * Gets the reported comment ID.
     *
     * @return the reported comment ID
     */
    public Integer getReportedCommentId() {
        return reportedCommentId;
    }

    /**
     * Sets the reported comment ID.
     *
     * @param reportedCommentId the reported comment ID
     */
    public void setReportedCommentId(Integer reportedCommentId) {
        this.reportedCommentId = reportedCommentId;
    }

    /**
     * Gets the task ID.
     *
     * @return the task ID
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * Sets the task ID.
     *
     * @param taskId the task ID
     */
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    /**
     * Gets the dispute reason.
     *
     * @return the dispute reason
     */
    public String getDisputeReason() {
        return disputeReason;
    }

    /**
     * Sets the dispute reason.
     *
     * @param disputeReason the dispute reason
     */
    public void setDisputeReason(String disputeReason) {
        this.disputeReason = disputeReason;
    }

    /**
     * Gets the reason.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason.
     *
     * @param reason the reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}

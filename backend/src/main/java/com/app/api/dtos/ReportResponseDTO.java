package com.app.api.dtos;

import java.sql.Timestamp;


public class ReportResponseDTO {

    private Integer reportId;
    private String reportType;
    private String status;
    private Integer adminId;
    private Integer reporterUserId;
    private Integer reportedUserId;
    private Integer reportedPostId;
    private Integer reportedCommentId;
    private Integer taskId;
    private String disputeReason;
    private String reason;
    private Timestamp createdAt;

    /**
     * Constructs a {@code ReportResponseDTO} with all fields.
     *
     * @param reportId the unique report ID
     * @param reportType the type of report
     * @param status the current status
     * @param adminId the ID of the assigned admin, or null
     * @param reporterUserId the ID of the user who filed the report
     * @param reportedUserId the reported user ID, or null
     * @param reportedPostId the reported post ID, or null
     * @param reportedCommentId the reported comment ID, or null
     * @param taskId the task ID for disputes, or null
     * @param disputeReason the dispute reason, or null
     * @param reason the short reason label
     * @param createdAt when the report was created
     */
    public ReportResponseDTO(Integer reportId, String reportType, String status,
            Integer adminId, Integer reporterUserId,
            Integer reportedUserId, Integer reportedPostId,
            Integer reportedCommentId, Integer taskId,
            String disputeReason, String reason, Timestamp createdAt) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.status = status;
        this.adminId = adminId;
        this.reporterUserId = reporterUserId;
        this.reportedUserId = reportedUserId;
        this.reportedPostId = reportedPostId;
        this.reportedCommentId = reportedCommentId;
        this.taskId = taskId;
        this.disputeReason = disputeReason;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    /**
     * Gets the report ID.
     *
     * @return the report ID
     */
    public Integer getReportId() {
        return reportId;
    }

    /**
     * Gets the report type.
     *
     * @return the report type
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the admin ID.
     *
     * @return the admin ID, or null if unassigned
     */
    public Integer getAdminId() {
        return adminId;
    }

    /**
     * Gets the reporter user ID.
     *
     * @return the reporter user ID
     */
    public Integer getReporterUserId() {
        return reporterUserId;
    }

    /**
     * Gets the reported user ID.
     *
     * @return the reported user ID, or null
     */
    public Integer getReportedUserId() {
        return reportedUserId;
    }

    /**
     * Gets the reported post ID.
     *
     * @return the reported post ID, or null
     */
    public Integer getReportedPostId() {
        return reportedPostId;
    }

    /**
     * Gets the reported comment ID.
     *
     * @return the reported comment ID, or null
     */
    public Integer getReportedCommentId() {
        return reportedCommentId;
    }

    /**
     * Gets the task ID.
     *
     * @return the task ID, or null
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * Gets the dispute reason.
     *
     * @return the dispute reason, or null
     */
    public String getDisputeReason() {
        return disputeReason;
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
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
}

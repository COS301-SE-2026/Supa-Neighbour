package com.app.api.dtos;

/**
 * Request body for PATCH /api/report.
 * 
 */
public class PatchReportDTO {

    /** ID of the report to update. Required. */
    private Integer reportId;

    /**
     * New lifecycle status.
     * Valid values: {@code submitted}, {@code assigned}, {@code reviewed}.
     */
    private String status;

    /**
     * Violation category determined by the admin.
     * e.g. {@code HARASSMENT}, {@code PRIVACY_VIOLATION}, {@code TASK_NO_SHOW}.
     */
    private String violationType;

    /**
     * Severity assessed by the admin.
     * Valid values: {@code MINOR}, {@code MODERATE}, {@code SEVERE}.
     */
    private String severity;

    /**
     * Action actually taken after review.
     * e.g. {@code WARNING}, {@code SUSPEND_7D}, {@code BAN}.
     */
    private String actualAction;

    /** Optional free-text notes written by the admin - not exposed to the reporter. */
    private String adminNotes;

    /**
     * Gets the report ID.
     *
     * @return the report ID
     */
    public Integer getReportId() {
        return reportId;
    }

    /**
     * Sets the report ID.
     *
     * @param reportId the report ID
     */
    public void setReportId(Integer reportId) {
        this.reportId = reportId;
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
     * Sets the status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the violation type.
     *
     * @return the violation type
     */
    public String getViolationType() {
        return violationType;
    }

    /**
     * Sets the violation type.
     *
     * @param violationType the violation type
     */
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    /**
     * Gets the severity.
     *
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the severity.
     *
     * @param severity the severity
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Gets the actual action taken.
     *
     * @return the actual action
     */
    public String getActualAction() {
        return actualAction;
    }

    /**
     * Sets the actual action taken.
     *
     * @param actualAction the actual action
     */
    public void setActualAction(String actualAction) {
        this.actualAction = actualAction;
    }

    /**
     * Gets the admin notes.
     *
     * @return the admin notes
     */
    public String getAdminNotes() {
        return adminNotes;
    }

    /**
     * Sets the admin notes.
     *
     * @param adminNotes the admin notes
     */
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}

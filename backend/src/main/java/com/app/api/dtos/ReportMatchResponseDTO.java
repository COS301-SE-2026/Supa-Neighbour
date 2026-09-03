package com.app.api.dtos;

/**
 * Response body for POST /api/report/match.
 * <p>
 * Confirms which admin was assigned to the report and the updated status.
 * </p>
 */
public class ReportMatchResponseDTO {

    private Integer reportId;
    private Integer assignedAdminId;
    private String status;

    /**
     * Constructs a {@code ReportMatchResponseDTO}.
     *
     * @param reportId the report that was matched
     * @param assignedAdminId the user_id of the admin now assigned to it
     * @param status the updated report status (always {@code assigned})
     */
    public ReportMatchResponseDTO(Integer reportId, Integer assignedAdminId, String status) {
        this.reportId = reportId;
        this.assignedAdminId = assignedAdminId;
        this.status = status;
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
     * Gets the assigned admin's user ID.
     *
     * @return the assigned admin user ID
     */
    public Integer getAssignedAdminId() {
        return assignedAdminId;
    }

    /**
     * Gets the updated report status.
     *
     * @return the report status
     */
    public String getStatus() {
        return status;
    }
}

package com.app.api.dtos;

import java.sql.Timestamp;

/**
 * Response body for PATCH /api/report - 200 OK
 * 
 */
public class PatchReportResponseDTO {

    private Integer reportId;
    private String status;
    private String actualAction;
    private Timestamp resolvedAt;

    /**
     * Constructs a {@code PatchReportResponseDTO}.
     *
     * @param reportId the report ID
     * @param status the updated status
     * @param actualAction the action taken by the admin
     * @param resolvedAt the timestamp when the report was resolved, or null
     */
    public PatchReportResponseDTO(Integer reportId, String status,
            String actualAction, Timestamp resolvedAt) {
        this.reportId = reportId;
        this.status = status;
        this.actualAction = actualAction;
        this.resolvedAt = resolvedAt;
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
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
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
     * Gets the resolved timestamp.
     *
     * @return the resolved timestamp, or null if not yet resolved
     */
    public Timestamp getResolvedAt() {
        return resolvedAt;
    }
}

package com.app.api.dtos;

import java.time.LocalDate;

/**
 * Response DTO for an admin application.
 */
public class AdminApplicationDTO {

    private Integer applicationId;
    private String applicationStatus;
    private LocalDate applicationDate;
    private String justification;
    private String rejectionReason;
    private LocalDate reviewedDate;

    /**
     * Constructs an AdminApplicationDTO.
     */
    public AdminApplicationDTO(Integer applicationId, String applicationStatus, LocalDate applicationDate, String justification,
            String rejectionReason, LocalDate reviewedDate) {
        this.applicationId = applicationId;
        this.applicationStatus = applicationStatus;
        this.applicationDate = applicationDate;
        this.justification = justification;
        this.rejectionReason = rejectionReason;
        this.reviewedDate = reviewedDate;
    }

    public Integer getApplicationId() { return applicationId; }
    public String getApplicationStatus() { return applicationStatus; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public String getJustification() { return justification; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDate getReviewedDate() { return reviewedDate; }
}

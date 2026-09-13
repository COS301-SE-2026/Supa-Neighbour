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
    private Integer userId;
    private String username;
    private Integer reviewedByAdminId;

    /**
     * Constructs an AdminApplicationDTO.
     */
    public AdminApplicationDTO(Integer applicationId, String applicationStatus, LocalDate applicationDate, String justification,
            String rejectionReason, LocalDate reviewedDate, Integer userId, String username, Integer reviewedByAdminId) {
        this.applicationId = applicationId;
        this.applicationStatus = applicationStatus;
        this.applicationDate = applicationDate;
        this.justification = justification;
        this.rejectionReason = rejectionReason;
        this.reviewedDate = reviewedDate;
        this.userId = userId;
        this.username = username;
        this.reviewedByAdminId = reviewedByAdminId;
    }

    public Integer getApplicationId() { return applicationId; }
    public String getApplicationStatus() { return applicationStatus; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public String getJustification() { return justification; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDate getReviewedDate() { return reviewedDate; }
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public Integer getReviewedByAdminId() { return reviewedByAdminId; }
}

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

    /**
     * Returns the application id.
     *
     * @return the application id
     */
    public Integer getApplicationId() { 
        return applicationId; 
    }

    /**
     * Returns the application status.
     * 
     * @return the application status
     */
    public String getApplicationStatus() { 
        return applicationStatus; 
    }

    /**
     * Returns the application date.
     * 
     * @return the application date
     */
    public LocalDate getApplicationDate() { 
        return applicationDate; 
    }

    /**
     * Returns the justification.
     * 
     * @return the justification
     */
    public String getJustification() { 
        return justification; 
    }

    /**
     * Returns the rejection reason.
     * 
     * @return the rejection reason
     */
    public String getRejectionReason() { 
        return rejectionReason; 
    }

    /**
     * Returns the reviewed date.
     * 
     * @return the reviewed date
     */
    public LocalDate getReviewedDate() { 
        return reviewedDate; 
    }

    /**
     * Returns the user id.
     * 
     * @return the user id
     */
    public Integer getUserId() {
        return userId; 
    }

    /**
     * Returns the username.
     * 
     * @return the username
     */
    public String getUsername() { 
        return username; 
    }

    /**
     * Returns the admin id who reviewed the application.
     * 
     * @return the admin id who reviewed the application
     */
    public Integer getReviewedByAdminId() { 
        return reviewedByAdminId; 
    }
}

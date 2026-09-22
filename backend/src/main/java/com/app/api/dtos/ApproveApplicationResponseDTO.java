package com.app.api.dtos;

import java.time.LocalDate;

public class ApproveApplicationResponseDTO {

    private Integer applicationId;
    private String applicationStatus;
    private Integer reviewedByAdminId;
    private LocalDate reviewedDate;
    private Integer newAdminId;

    /**
     * Constructs an ApproveApplicationResponseDTO.
     *
     * @param applicationId the approved application id
     * @param applicationStatus the updated status (always Approved)
     * @param reviewedByAdminId the user_id of the super admin who approved
     * @param reviewedDate date of review
     * @param newAdminId the admin_id of the newly provisioned admin row
     */
    public ApproveApplicationResponseDTO(Integer applicationId, String applicationStatus,
            Integer reviewedByAdminId, LocalDate reviewedDate, Integer newAdminId) {
        this.applicationId = applicationId;
        this.applicationStatus = applicationStatus;
        this.reviewedByAdminId = reviewedByAdminId;
        this.reviewedDate = reviewedDate;
        this.newAdminId = newAdminId;
    }

    /** @return the application id */
    public Integer getApplicationId() { 
        return applicationId; 
    }

    /** @return the application status */
    public String getApplicationStatus() { 
        return applicationStatus; 
    }

    /** @return the reviewing super admin's user id */
    public Integer getReviewedByAdminId() { 
        return reviewedByAdminId; 
    }

    /** @return the review date */
    public LocalDate getReviewedDate() { 
        return reviewedDate; 
    }

    /** @return the new admin_id from admin_table */
    public Integer getNewAdminId() {
         return newAdminId; 
    }
}

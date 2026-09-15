package com.app.api.dtos;

import java.time.LocalDate;


public class RejectApplicationResponseDTO {

    private Integer applicationId;
    private String applicationStatus;
    private Integer reviewedByAdminId;
    private LocalDate reviewedDate;

    /**
     * Constructs a RejectApplicationResponseDTO.
     *
     * @param applicationId the rejected application id
     * @param applicationStatus the updated status (always Rejected)
     * @param reviewedByAdminId the user_id of the super admin who rejected
     * @param reviewedDate date reviewed
     */
    public RejectApplicationResponseDTO(Integer applicationId, String applicationStatus,
            Integer reviewedByAdminId, LocalDate reviewedDate) {
        this.applicationId = applicationId;
        this.applicationStatus = applicationStatus;
        this.reviewedByAdminId = reviewedByAdminId;
        this.reviewedDate = reviewedDate;
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
}

package com.app.api.dtos;

/**
 * Data Transfer Object for Admin Application Rejections.
 * AdminApplicationRejectDTO
 */
public class AdminApplicationRejectDTO {
    
    private String rejectionReason;

    public AdminApplicationRejectDTO() {

    }

    public AdminApplicationRejectDTO(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}



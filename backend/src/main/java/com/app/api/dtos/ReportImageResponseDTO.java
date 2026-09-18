package com.app.api.dtos;

import java.time.LocalDateTime;

public class ReportImageResponseDTO {

    private Integer reportImageId;
    private String imageUrl;
    private LocalDateTime uploadedAt;

    /**
     * Constructs a new {@code ReportImageResponseDTO} with all fields populated.
     *
     * @param reportImageId the unique identifier of the report image; should not be {@code null}
     * @param imageUrl      the URL where the image can be accessed; should not be {@code null}
     * @param uploadedAt    the timestamp when the image was uploaded; should not be {@code null}
     */
    public ReportImageResponseDTO(Integer reportImageId, String imageUrl, LocalDateTime uploadedAt) {
        this.reportImageId = reportImageId;
        this.imageUrl = imageUrl;
        this.uploadedAt = uploadedAt;
    }

    /**
     * Returns the unique identifier of the report image.
     *
     * @return the report image ID as an {@link Integer}
     */
    public Integer getReportImageId() { 
        return reportImageId; 
    }

    /**
     * Returns the URL where the image can be accessed.
     *
     * @return the image URL as a {@link String}
     */
    public String getImageUrl() { 
        return imageUrl; 
    }
    /**
     * Returns the timestamp when the image was uploaded.
     *
     * @return the upload timestamp as a {@link java.time.LocalDateTime}
     */
    public LocalDateTime getUploadedAt() { 
        return uploadedAt; 
    }
}

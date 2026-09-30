package com.app.api.dtos;

import jakarta.validation.constraints.NotBlank;

public class ReportImageLinkRequestDTO {

    @NotBlank(message = "imageUrl is required")
    private String imageUrl;

    /**
     * Returns the image URL associated with the report.
     *
     * @return the image URL as a {@link String}; may be {@code null} if not set,
     *         though validation should prevent this in valid requests
     */
    public String getImageUrl() {
         return imageUrl; 
    }

    /**
     * Sets the image URL associated with the report.
     *
     * @param imageUrl the image URL to set; must not be {@code null} or blank
     *                 for the request to pass validation
     */
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
    }

}

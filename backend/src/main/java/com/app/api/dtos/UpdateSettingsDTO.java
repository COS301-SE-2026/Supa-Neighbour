package com.app.api.dtos;


/**
 * Data Transfer Object representing a request to update a user's
 * application settings.
 *
 * <p>Fields left as {@code null} are treated as "no change" by the
 * service layer, allowing partial updates (e.g. changing only the
 * theme mode without affecting visibility settings).</p>
 */
public class UpdateSettingsDTO {
    private Boolean showStatus;
    private Boolean showPhoneNo;
    private String mode;

    /**
     * * Default constructor required for JSON deseriazation.
     */
    public UpdateSettingsDTO() {

    }

    /**
     * Returns whether online status visibility should be updated.
     *
     * @return the new show-status value, or {@code null} if unchanged
     */
    public Boolean getShowStatus(){
        return showStatus;
    }

    /**
     * Sets the show-Status value
     * 
     * @param showStatus the new showstatus value
     */
    public void setShowStatus(Boolean showStatus){
        this.showStatus = showStatus;
    }

    /**
     * Returns whether phone number visibility should be updated.
     *
     * @return the new show-phone-number value, or {@code null} if unchanged
     */
    public Boolean getShowPhoneNo() {
        return showPhoneNo;
    }

    /**
     * Sets the show-phone-number value.
     *
     * @param showPhoneNo the new show-phone-number value
     */
    public void setShowPhoneNo(Boolean showPhoneNo) {
        this.showPhoneNo = showPhoneNo;
    }

    /**
     * Returns the new theme mode.
     *
     * @return the new mode value, or {@code null} if unchanged
     */
    public String getMode() {
        return mode;
    }

     /**
     * Sets the theme mode.
     *
     * @param mode the new mode value
     */   
    public void setMode(String mode) {
        this.mode = mode;
    }

    
}

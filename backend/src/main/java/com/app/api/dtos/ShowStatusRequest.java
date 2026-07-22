package com.app.api.dtos;

/**
 * Data Transfer Object (DTO) representing a request to update
 * a user's online status visibility setting.
 */
public class ShowStatusRequest {
    
    private Boolean showStatus;

    /**
     * Default constructor.
     */
    public ShowStatusRequest(){

    }
    /**
     * Returns whether the user's online status should be visible.
     *
     * @return {@code true} if the status should be visible;
     *         {@code false} otherwise
     */
    public Boolean getshowStatus(){
        return showStatus;
    }

    /**
     * Sets whether the user's online status should be visible.
     *
     * @param showStatus {@code true} to make the status visible;
     *                   {@code false} to hide it
     */
    public void setShowStatus(Boolean showStatus){
        this.showStatus = showStatus;
    }
}

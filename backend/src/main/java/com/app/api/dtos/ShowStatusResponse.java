package com.app.api.dtos;

/**
 * Data Transfer Object (DTO) representing the response returned after
 * retrieving or updating a user's status visibility setting.
 */
public class ShowStatusResponse {
    
    private boolean showStatus;

     /**
     * Constructs a new {@code ShowStatusResponse}.
     *
     * @param showStatus {@code true} if the user's online status is
     *                   visible; {@code false} otherwise
     */
    public ShowStatusResponse(boolean showStatus){
        this.showStatus = showStatus;
    }

    /**
     * Returns whether the user's online status is visible.
     *
     * @return {@code true} if the user's status is visible;
     *         {@code false} otherwise
     */
    public boolean getShowStatus(){
        return showStatus;
    }

    /**
     * Sets whether the user's online status is visible.
     *
     * @param showStatus {@code true} to make the user's status visible;
     *                   {@code false} to hide it
     */
    public void setShowStatus(boolean showStatus){
        this.showStatus = showStatus;
    }
}

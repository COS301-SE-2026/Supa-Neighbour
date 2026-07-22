package com.app.api.dtos;

/**
 * Data Transfer Object (DTO) representing the user's application
 * theme mode.
 * <p>
 * Used when retrieving or updating the user's preferred application
 * theme.
 * </p>
 */
public class ModeResponse {
    private String mode;

    

    /**
     * Constructs a new {@code ModeResponse}.
     *
     * @param mode the user's application theme mode
     */
    public ModeResponse(String mode){
        this.mode = mode;
    }

    /**
     * Returns the user's application theme mode.
     *
     * @return the current theme mode
     */
    public String getMode(){
        return mode;
    }

    /**
     * Sets the user's application theme mode.
     *
     * @param mode the theme mode to set
     */
    public void setMode(String mode){
        this.mode = mode;
    }
}

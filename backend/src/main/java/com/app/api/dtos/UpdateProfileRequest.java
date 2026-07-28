package com.app.api.dtos;

import java.util.List;

/**
 * Data Transfer Object representing a request to update a user's profile.
 *
 * <p>The request may include a new first name, last name, and, for
 * helpers, an updated list of skills.</p>
 */
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private List<String> skills;

    /**
     * Creates an empty profile update request.
     */
    public UpdateProfileRequest(){

    }

    /**
     * Returns the user's first name.
     *
     * @return the first name, or {@code null} if it was not provided
     */
    public String getFirstName(){
        return firstName;
    }

     /**
     * Sets the user's first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return the last name, or {@code null} if it was not provided
     */
    public String getLastName(){
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    /**
     * Returns the helper's updated skills.
     *
     * @return the list of skills, or {@code null} if no skills were provided
     */
    public List<String> getSkills(){
        return skills;
    }

    /**
     * Sets the helper's skills.
     *
     * @param skills the list of skills to set
     */
    public void setSkills(List<String> skills){
        this.skills = skills;
    }

    /**
     * Determines whether the request contains any profile updates.
     *
     * @return {@code true} if no first name, last name, or skills were
     *         provided; otherwise {@code false}
     */
    public boolean isEmpty() {
        return firstName == null && lastName == null && skills == null;
    }
}

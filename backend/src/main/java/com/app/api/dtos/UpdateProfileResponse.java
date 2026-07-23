package com.app.api.dtos;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing the response returned after a
 * user's profile has been successfully updated.
 *
 * <p>The response includes a confirmation message, the updated display
 * name, and the helper's updated skills if applicable.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileResponse {
    private String message;
    private String displayName;
    private List<String> skills;

    /**
     * Creates a profile update response.
     *
     * @param message the confirmation message
     * @param displayName the user's updated display name
     * @param skills the helper's updated skills, or {@code null} if
     *               the skills were not modified
     */
    public UpdateProfileResponse(String message, String displayName, List<String> skills){
        this.skills = skills;
        this.displayName = displayName;
        this.message = message;
    }

    /**
     * Returns the confirmation message.
     *
     * @return the confirmation message
     */
    public String getMessage(){
        return message;
    }

    /**
     * Returns the user's updated display name.
     *
     * @return the updated display name
     */
    public String getDisplayName(){
        return displayName;
    }

    /**
     * Returns the helper's updated skills.
     *
     * @return the updated list of skills, or {@code null} if the
     *         skills were not modified
     */
    public List<String> getSkills(){
        return skills;
    }
}

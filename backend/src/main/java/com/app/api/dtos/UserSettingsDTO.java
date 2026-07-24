package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing a user's full settings view,
 * combining their profile information and address details.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSettingsDTO {

    private UserProfileResponse profile;
    private AddressDTO address;

    /**
     * Creates a user settings response.
     *
     * @param profile the user's profile information
     * @param address the user's address information
     */
    public UserSettingsDTO(UserProfileResponse profile, AddressDTO address) {
        this.profile = profile;
        this.address = address;
    }

    /**
     * Returns the user's profile information.
     *
     * @return the profile
     */
    public UserProfileResponse getProfile() {
        return profile;
    }

    /**
     * Returns the user's address information.
     *
     * @return the address
     */
    public AddressDTO getAddress() {
        return address;
    }
}

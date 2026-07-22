package com.app.api.dtos;

public class UserSettingsDTO {
 
    
    private UserProfileResponse userProfileResponse;
    private AddressDTO addressDTO;

    public UserSettingsDTO(UserProfileResponse userProfileResponse,AddressDTO addressDTO)
    {
        this.addressDTO = addressDTO;
        this.userProfileResponse= userProfileResponse;
    }

    public UserProfileResponse getUserProfileResponse() {
        return userProfileResponse;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }
    
}

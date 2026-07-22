package com.app.api.dtos;
import java.time.Instant;

/**
 * Response DTO containing a user's settings and profile information.
 */
public class UserSettingsResponseDTO {
    
    private int userId;
    private Instant lastSeen;
    private String username;
    private String firstName;
    private String lastName;
    private AddressDTO addressDTO;

    public UserSettingsResponseDTO(int userId,Instant lastSeen, String username,String firstName,String lastName,AddressDTO addressdDto)
    {
        this.lastSeen =lastSeen;
        this.userId= userId;
        this.firstName= firstName;
        this.lastName = lastName;
        this.username = username;
        this.addressDTO=  addressdDto;
    }

    public int getUserId() {
        return userId;
    }

    public Instant getLastSeen() {
        return lastSeen;
    }


    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public AddressDTO getAddressDTO(){
        return addressDTO;
    }

    
}

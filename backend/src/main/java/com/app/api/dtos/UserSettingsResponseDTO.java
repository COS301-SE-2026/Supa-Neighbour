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


    /**
     * Constructs a new UserSettingsResponseDTO with the specified user details.
     *
     * @param userId     the unique identifier of the user
     * @param lastSeen   the timestamp when the user was last seen
     * @param username   the username of the user
     * @param firstName  the first name of the user
     * @param lastName   the last name of the user
     * @param addressDTO the address information of the user
     */
    public UserSettingsResponseDTO(int userId,Instant lastSeen, String username,String firstName,String lastName,AddressDTO addressdDto){
        this.lastSeen =lastSeen;
        this.userId= userId;
        this.firstName= firstName;
        this.lastName = lastName;
        this.username = username;
        this.addressDTO=  addressdDto;
    }


        /**
     * Retrieves the unique identifier of the user.
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }


        /**
     * Retrieves the timestamp when the user was last seen.
     *
     * @return the last seen timestamp as an Instant
     */
    public Instant getLastSeen() {
        return lastSeen;
    }


        /**
     * Retrieves the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }


        /**
     * Retrieves the first name of the user.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }


        /**
     * Retrieves the last name of the user.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

        /**
     * Retrieves the address information of the user.
     *
     * @return the AddressDTO containing address details
     */
    public AddressDTO getAddressDTO(){
        return addressDTO;
    }
}

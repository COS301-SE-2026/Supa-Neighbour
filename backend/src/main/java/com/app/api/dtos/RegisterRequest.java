package com.app.api.dtos;

import java.sql.Date;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for user registration requests.
 * This class encapsulates all the required information for registering a new user.
 * 
 * @author Your Name
 * @version 1.0
 */
@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private String userType;
    private String username;
    private Integer addressId;
    private Integer badgeId;
    private Integer ratingId;

    /**
     * Gets the first name of the user.
     * 
     * @return the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the user.
     * 
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the password for the user account.
     * 
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the phone number of the user.
     * 
     * @return the user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the date of birth of the user.
     * 
     * @return the user's date of birth as a {@link java.sql.Date} object
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Gets the gender of the user.
     * 
     * @return the user's gender (e.g., "MALE", "FEMALE", "OTHER")
     */
    public String getGender() {
        return gender;
    }

    /**
     * Gets the type of user account.
     * 
     * @return the user type (e.g., "ADMIN", "CUSTOMER", "DRIVER", "VENDOR")
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Gets the username for the user account.
     * 
     * @return the unique username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the address ID associated with the user.
     * 
     * @return the address identifier, or null if not set
     */
    public Integer getAddressId() {
        return addressId;
    }

    /**
     * Gets the badge ID associated with the user.
     * 
     * @return the badge identifier, or null if not set
     */
    public Integer getBadgeId() {
        return badgeId;
    }

    /**
     * Gets the rating ID associated with the user.
     * 
     * @return the rating identifier, or null if not set
     */
    public Integer getRatingId() {
        return ratingId;
    }
}

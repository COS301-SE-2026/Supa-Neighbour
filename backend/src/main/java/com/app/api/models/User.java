package com.app.api.models;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a user in the system.
 */
@Entity
@Table(name = "UserTable")
public class User {

    /** The user ID. */
    @Id
    @Column(name = "UserID")
    private int id;

    /** The user password. */
    @Column(name = "UserPassword")
    private String password;

    /** The user first name. */
    @Column(name = "UserName")
    private String firstName;

    /** The user last name. */
    @Column(name = "UserSurname")
    private String lastName;

    /** The user email. */
    @Column(name = "UserEmail")
    private String email;

    /** The user phone number. */
    @Column(name = "UserPhoneNumber")
    private String phoneNumber;

    /** The user gender. */
    @Column(name = "UserGender")
    private String gender;

    /** The user date of birth. */
    @Column(name = "UserDOB")
    private Date dateOfBirth;

    /** The user address ID. */
    @Column(name = "UserAddressID")
    private int addressId;

    /** The user badge ID. */
    @Column(name = "UserBadgeID")
    private String badgeId;

    /** The user rating ID. */
    @Column(name = "UserRatingID")
    private String ratingId;

    /** The user type ID. */
    @Column(name = "UserTypeID")
    private String typeId;

    /**
     * Default constructor.
     */
    public User() {
    }

    /**
     * Gets the user ID.
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID.
     * @param id the user ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the first name.
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name.
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number.
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the gender.
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender.
     * @param gender the gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the date of birth.
     * @return the date of birth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the date of birth.
     * @param dateOfBirth the date of birth
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the address ID.
     * @return the address ID
     */
    public int getAddressId() {
        return addressId;
    }

    /**
     * Sets the address ID.
     * @param addressId the address ID
     */
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    /**
     * Gets the badge ID.
     * @return the badge ID
     */
    public String getBadgeId() {
        return badgeId;
    }

    /**
     * Sets the badge ID.
     * @param badgeId the badge ID
     */
    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    /**
     * Gets the rating ID.
     * @return the rating ID
     */
    public String getRatingId() {
        return ratingId;
    }

    /**
     * Sets the rating ID.
     * @param ratingId the rating ID
     */
    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    /**
     * Gets the type ID.
     * @return the type ID
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * Sets the type ID.
     * @param typeId the type ID
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}

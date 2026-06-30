package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Represents a user in the Supa-Neighbour application.
 * Users can be helpers, dependents, or admins with associated profiles, ratings, and badges.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userid;

    @Column(name = "user_name")
    private String firstName;

    @Column(name = "user_surname")
    private String lastName;

    @Column(name = "user_password")
    private String password;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_phone_number")
    private String phoneNumber;

    @Column(name = "user_dob")
    private Date dateOfBirth;

    @Column(name = "user_gender")
    private String gender;

    @ManyToOne
    @JoinColumn(name = "user_address_id")
    private Address addressid;

    @ManyToOne
    @JoinColumn(name = "user_badge_id")
    private Badges badgeid;

    @ManyToOne
    @JoinColumn(name = "user_rating_id")
    private Ratings ratingid;

    @Column(name = "user_type")
    private String userType;

    /**
     * Gets the user identifier.
     *
     * @return the user identifier
     */
    public int getUserid() {
        return userid;
    }

    /**
     * Sets the user ID.
     * @param userid the user ID
     */
    public void setUserid(int userid) {
        this.userid = userid;
    }


    /**
     * Sets the user's first name.
     *
     * @return the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }


    /**
    *  Sets the user's last name.
    *
    * @param lastName the last name
    */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the user's date of birth.
     *
     * @return the date of birth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the user's date of birth.
     *
     * @param dateOfBirth the date of birth
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the user's gender.
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the user's gender.
     *
     * @param gender the gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the address associated with the user.
     *
     * @return the address
     */
    public Address getAddressid() {
        return addressid;
    }

    /**
     * Sets the address associated with the user.
     *
     * @param addressid the address
     */
    public void setAddressid(Address addressid) {
        this.addressid = addressid;
    }

    /**
     * Gets the badge associated with the user.
     *
     * @return the badge
     */
    public Badges getBadgeid() {
        return badgeid;
    }


    /**
     * Sets the badge associated with the user.
     *
     * @param badgeid the badge
     */
    public void setBadgeid(Badges badgeid) {
        this.badgeid = badgeid;
    }

    /**
     * Gets the rating associated with the user.
     *
     * @return the rating
     */
    public Ratings getRatingid() {
        return ratingid;
    }


    /**
     * Sets the rating associated with the user.
     *
     * @param ratingid the rating
     */
    public void setRatingid(Ratings ratingid) {
        this.ratingid = ratingid;
    }

    /**
     * Gets the type of user.
     *
     * @return the user type (helper, dependent, or admin)
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Sets the type of user.
     *
     * @param userType the user type (helper, dependent, or admin)
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }
}

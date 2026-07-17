package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * Represents a user within the application.
 * <p>
 * This entity maps to the {@code user_table} database table and stores
 * personal information, authentication details, address, badge, rating,
 * and user type.
 * </p>
 */
@Data
@Getter
@Setter
@Entity
@Table(name = "user_table")
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userid;

    /**
     * The Firebase authentication UID associated with the user.
     */
    @Column(name = "user_firebase_uid", unique = true)
    private String firebaseUid;

    /**
     * Indicates whether the user's email has been verified.
     */
    @Column(name = "user_email_verified")
    private boolean emailVerified;

    /**
     * Indicates whether the user's phone number has been verified.
     */
    @Column(name = "user_phone_verified")
    private boolean phoneVerified;
    /**
     * The username of the user
     */
    @Column(name = "user_username", unique = true)
    private String username;

    /** user's first name */
    @Column(name = "user_name")
    private String firstName;

    /** user's surname */
    @Column(name = "user_surname")
    private String lastName;

    /** user's email */
    @Column(name = "user_email")
    private String email;
    /** user's phone number */
    @Column(name = "user_phone_number")
    private String phoneNumber;
    /** user's date of birth */
    @Column(name = "user_dob")
    private Date dateOfBirth;
    /** user's gender */
    @Column(name = "user_gender")
    private String gender;
    /** user's address id */
    @ManyToOne
    @JoinColumn(name = "user_address_id")
    private Address addressid;
    /** user's badge id */
    @ManyToOne
    @JoinColumn(name = "user_badge_id")
    private Badges badgeid;
     /** user's rating id */
    @ManyToOne
    @JoinColumn(name = "user_rating_id")
    private Ratings ratingid;
    /** user's type */
    @Column(name = "user_type")
    private String userType;

    /**
     * Default constructor required by JPA.
     */
    public User() {
    }

    /**
     * Constructs a user with all attributes.
     *
     * @param userid the unique identifier of the user
     * @param firebaseUid the Firebase authentication UID
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param password the user's password
     * @param email the user's email address
     * @param phoneNumber the user's phone number
     * @param dateOfBirth the user's date of birth
     * @param gender the user's gender
     * @param addressid the user's associated address
     * @param badgeid the badge assigned to the user
     * @param ratingid the rating associated with the user
     * @param userType the user's type or role
     */
    public User(int userid,String firebaseUid, String firstName, String lastName, String email,String phoneNumber, Date dateOfBirth, String gender,Address addressid, Badges badgeid, Ratings ratingid, String userType) {
        this.userid = userid;
        this.firebaseUid = firebaseUid;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.addressid = addressid;
        this.badgeid = badgeid;
        this.ratingid = ratingid;
        this.userType = userType;
    }

}

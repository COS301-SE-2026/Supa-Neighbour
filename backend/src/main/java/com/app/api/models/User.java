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
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userid;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

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
     * Default constructor required by JPA.
     */
    public User() {
    }

    public User(int userid,String firebaseUid, String firstName, String lastName, String password, String email,String phoneNumber, Date dateOfBirth, String gender,Address addressid, Badges badgeid, Ratings ratingid, String userType) {
        this.userid = userid;
        this.firebaseUid = firebaseUid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.firebaseUid = firebaseUid;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.addressid = addressid;
        this.badgeid = badgeid;
        this.ratingid = ratingid;
        this.userType = userType;
    }

}


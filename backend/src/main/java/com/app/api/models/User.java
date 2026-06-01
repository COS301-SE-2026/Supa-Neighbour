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

@Data
@Builder
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

    public User() {
    }

    public User(int userid, String firstName, String lastName, String password, String email,String phoneNumber, Date dateOfBirth, String gender,Address addressid, Badges badgeid, Ratings ratingid, String userType) {
        this.userid = userid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.addressid = addressid;
        this.badgeid = badgeid;
        this.ratingid = ratingid;
        this.userType = userType;
    }

    public int getUserid() {
        return userid;
    }

    public void setid(int userid) {
        this.userid = userid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Address getAddressid() {
        return addressid;
    }

    public void setAddressid(Address addressid) {
        this.addressid = addressid;
    }

    public Badges getBadgeid() {
        return badgeid;
    }

    public void setBadgeid(Badges badgeid) {
        this.badgeid = badgeid;
    }

    public Ratings getRatingid() {
        return ratingid;
    }

    public void setRatingid(Ratings ratingid) {
        this.ratingid = ratingid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
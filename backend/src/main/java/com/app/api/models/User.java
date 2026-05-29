package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "user_table")
public class User 
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
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

    @OneToOne
    @JoinColumn(name = "user_address_id")
    private Address addressid;

    @OneToMany
    @JoinColumn(name = "user_badge_id")
    private Badges badgeid;

    @OneToOne
    @JoinColumn(name = "user_rating_review")
    private Ratings ratingid;

    @Column(name = "user_type_id")
    private String typeid;

    public User() {
    }

    public User(int userid, String firstName, String lastName, String password, String email, String phoneNumber, Date dateOfBirth, String gender, Address addressid, Badges badgeid, Ratings ratingid, String typeid) {
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
        this.typeid = typeid;
    }

    public int getUserid() {
        return userid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }  
    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;      
    }

    public Address getAddressId() {
        return addressid;
    }

    public Badges getBadgeId() {
        return badgeid;
    }

    public Ratings getRatingId() {
        return ratingid;
    }

    public String getTypeId() {
        return typeid;
    }

    public void setId(int userid) {
        this.userid = userid;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddressId(Address addressid) {
        this.addressid = addressid;
    }

    public void setBadgeId(Badges badgeid) {
        this.badgeid = badgeid;
    }

    public void setRatingId(Ratings ratingid) {
        this.ratingid = ratingid;
    }

    public void setTypeId(String typeid) {
        this.typeid = typeid;
    }
}

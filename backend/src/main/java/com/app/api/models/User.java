package com.app.api.models;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UserTable")
public class User 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private int addressId;
    private String badgeId;
    private String ratingId;
    private String typeId;

    public User() {
    }

    public User(int id, String firstName, String lastName, String password, String email, String phoneNumber, Date dateOfBirth, String gender, int addressId, String badgeId, String ratingId, String typeId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.addressId = addressId;
        this.badgeId = badgeId;
        this.ratingId = ratingId;
        this.typeId = typeId;
    }

    public int getId() {
        return id;
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

    public int getAddressId() {
        return addressId;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public String getRatingId() {
        return ratingId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}

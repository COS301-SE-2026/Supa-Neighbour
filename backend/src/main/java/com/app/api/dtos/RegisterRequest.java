package com.app.api.dtos;
import java.sql.Date;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private Date dateOfBirth;
    private String gender;
    private String userType;

    private Integer addressId;
    private Integer badgeId;
    private Integer ratingId;
    

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
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
    public Integer getAddressId() {
        return addressId;
    }
    public Integer getBadgeId() {
        return badgeId;
    }
    public Integer getRatingId() {
        return ratingId;
    }
    public String getUserType() {
        return userType;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
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
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    public void setBadgeId(Integer badgeId) {
        this.badgeId = badgeId;
    }
    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }

    
}

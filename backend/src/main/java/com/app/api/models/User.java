package com.app.api.models;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "UserTable")
public class User
{

    @Id
    @Column(name = "UserID") //basically u use the column anntotation to make the column names in shema.sql
    private int id;

    @Column(name = "UserPassword")
    private String password;

    @Column(name = "UserName")
    private String firstName;

    @Column(name = "UserSurname")
    private String lastName;

    @Column(name = "UserEmail")
    private String email;

    @Column(name = "UserPhoneNumber")
    private String phoneNumber;

    @Column(name = "UserGender")
    private String gender;

    @Column(name = "UserDOB")
    private Date dateOfBirth;

    @Column(name = "UserAddressID")
    private int addressId;

    @Column(name = "UserBadgeID")
    private String badgeId;

    @Column(name = "UserRatingID")
    private String ratingId;

    @Column(name = "UserTypeID")
    private String typeId;

    public User() {}

    /// getters and setters: 

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }

    public String getBadgeId() { return badgeId; }
    public void setBadgeId(String badgeId) { this.badgeId = badgeId; }

    public String getRatingId() { return ratingId; }
    public void setRatingId(String ratingId) { this.ratingId = ratingId; }

    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }
}

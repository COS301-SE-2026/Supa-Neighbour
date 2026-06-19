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

/**
 * Represents an administrator user in the system.
 * Admins have elevated privileges and manage system operations.
 */
@Data
@Builder
@Entity
@Table(name = "admin_table")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private int adminid;

    @Column(name = "admin_password")
    private String adminpassword;

    @Column(name = "admin_email")
    private String email;

    @Column(name = "admin_name")
    private String adminname;

    @Column(name = "admin_surname")
    private String adminsurname;

    @Column(name = "admin_phone_number")
    private String adminphonenumber;

    @Column(name = "admin_create_date")
    private Date admincreatedate;

    @Column(name = "admin_access_level")
    private int adminaccesslevel;

    @ManyToOne
    @JoinColumn(name = "admin_address_id")
    private Address adminaddressid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userid;

    /**
     * Default constructor.
     */
    public Admin() {
    }


    /**
     * Constructs an Admin with all fields specified.
     *
     * @param adminid           the admin identifier
     * @param adminpassword     the admin password
     * @param email             the admin email
     * @param adminname         the admin name
     * @param adminsurname      the admin surname
     * @param adminphonenumber  the admin phone number
     * @param admincreatedate   the admin creation date
     * @param adminaccesslevel  the admin access level
     * @param adminaddressid    the address associated with the admin
     * @param userid            the user associated with the admin
     */
    public Admin(int adminid, String adminpassword, String email, String adminname, String adminsurname, String adminphonenumber, Date admincreatedate, int adminaccesslevel, Address adminaddressid, User userid) {
        this.adminid = adminid;
        this.adminpassword = adminpassword;
        this.email = email;
        this.adminname = adminname;
        this.adminsurname = adminsurname;
        this.adminphonenumber = adminphonenumber;
        this.admincreatedate = admincreatedate;
        this.adminaccesslevel = adminaccesslevel;
        this.adminaddressid = adminaddressid;
        this.userid = userid;

    }

    /**
     * Gets the user id.
     *
     * @return the user identifier
     */
    public User getUser(){
        return userid;
    }

    /**
     * Sets the uer identifier.
     *
     * @param userid the uer identifier
     */
    void setUser(User userid){
        this.userid=userid;
    }

       /**
     * Gets the admin id.
     *
     * @return the admin id
     */
    public int getAdminid() {
        return adminid;
    }   

    /**
     * Sets the admin id
     *
     * @param adminid the admin identifier
     */
    public void setAdminid(int adminid) {
        this.adminid = adminid;
    }

    /**
     * Gets the amdin password
     *
     * @return the amdin password
     */
    public String getAdminpassword() {
        return adminpassword;
    }

    /**
     * Sets the admin password.
     *
     * @param adminpassword the admin password
     */
    public void setAdminpassword(String adminpassword) {
        this.adminpassword = adminpassword;
    }

    /**
     * Gets the admin email
     *
     * @return the admin email
     */
    public String getAdminEmail() {
        return email;
    }   

    /**
     * Sets the amdin email
     *
     * @param email the amdin email
     */
    public void setAdminEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the name associated with the amdin
     *
     * @return the admin name
     */
    public String getAdminname() {
        return adminname;
    }

    /**
     * Sets the name associated with the admin.
     *
     * @param adminname the admin name
     */
    public void setAdminname(String adminname) {
        this.adminname = adminname;
    }

    /**
     * Gets the surname associated with the admin.
     *
     * @return the admin surname
     */
    public String getAdminsurname() {
        return adminsurname;
    }

    /**
     * Sets the surname associated with the admin.
     *
     * @param adminsurname the admin surname
     */
    public void setAdminsurname(String adminsurname) {
        this.adminsurname = adminsurname;
    }

    /**
     * Gets the phone number associated with the admin.
     *
     * @return the admin phone number
     */
    public String getAdminphonenumber() {
        return adminphonenumber;
    }

    /**
     * Sets the phone number associated with the admin.
     *
     * @param adminphonenumber the admin phone number
     */
    public void setAdminphonenumber(String adminphonenumber) {
        this.adminphonenumber = adminphonenumber;
    }

    /**
     * Gets the date the admin account was created.
     *
     * @return the admin creation date
     */
    public Date getAdmincreatedate() {
        return admincreatedate;
    }

    /**
     * Sets the date the admin account was created.
     *
     * @param admincreatedate the admin creation date
     */
    public void setAdmincreatedate(Date admincreatedate) {
        this.admincreatedate = admincreatedate;
    }

    /**
     * Gets the access level of the admin.
     *
     * @return the admin access level
     */
    public int getAdminaccesslevel() {
        return adminaccesslevel;
    }

    /**
     * Sets the access level of the admin.
     *
     * @param adminaccesslevel the admin access level
     */
    public void setAdminaccesslevel(int adminaccesslevel) {
        this.adminaccesslevel = adminaccesslevel;
    }

    /**
     * Gets the address associated with the admin.
     *
     * @return the admin address
     */
    public Address getAdminaddressid() {
        return adminaddressid;
    }

    /**
     * Sets the address associated with the admin.
     *
     * @param adminaddressid the admin address
     */
    public void setAdminaddressid(Address adminaddressid) {
        this.adminaddressid = adminaddressid;
    }
}

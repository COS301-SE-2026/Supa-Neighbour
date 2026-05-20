package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "adminstable")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminid")
    private int adminid;
    @Column(name = "adminpassword")
    private String adminpassword;
    @Column(name = "email")
    private String email;
    @Column(name = "adminname")
    private String adminname;
    @Column(name = "adminsurname")
    private String adminsurname;
    @Column(name = "adminphonenumber")
    private String adminphonenumber;
    @Column(name = "admincreatedate")
    private Date admincreatedate;
    @Column(name = "adminaccesslevel")
    private int adminaccesslevel;
    @Column(name = "adminaddressid")
    private int adminaddressid;
    @Column(name = "userid")
    private int userid;

    public Admin() {
    }

    public Admin(int adminid, String adminpassword, String email, String adminname, String adminsurname, String adminphonenumber, Date admincreatedate, int adminaccesslevel, int adminaddressid, int userid) {
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

    public int getAdminid() {
        return adminid;
    }   

    public void setAdminid(int adminid) {
        this.adminid = adminid;
    }

    public String getAdminpassword() {
        return adminpassword;
    }

    public void setAdminpassword(String adminpassword) {
        this.adminpassword = adminpassword;
    }

    public String getEmail() {
        return email;
    }   

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname;
    }

    public String getAdminsurname() {
        return adminsurname;
    }

    public void setAdminsurname(String adminsurname) {
        this.adminsurname = adminsurname;
    }

    public String getAdminphonenumber() {
        return adminphonenumber;
    }

    public void setAdminphonenumber(String adminphonenumber) {
        this.adminphonenumber = adminphonenumber;
    }

    public Date getAdmincreatedate() {
        return admincreatedate;
    }

    public void setAdmincreatedate(Date admincreatedate) {
        this.admincreatedate = admincreatedate;
    }

    public int getAdminaccesslevel() {
        return adminaccesslevel;
    }

    public void setAdminaccesslevel(int adminaccesslevel) {
        this.adminaccesslevel = adminaccesslevel;
    }

    public int getAdminaddressid() {
        return adminaddressid;
    }

    public void setAdminaddressid(int adminaddressid) {
        this.adminaddressid = adminaddressid;
    }
    
}

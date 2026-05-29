package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "adminst_able")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_id_seq")
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

    @OneToOne
    @JoinColumn(name = "admin_address_id")
    private Address adminaddressid;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User userid;

    public Admin() {
    }

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

    public User getUser()
    {
        return userid;
    }

    void getUser(User userid)
    {
        this.userid=userid;
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

    public Address getAdminaddressid() {
        return adminaddressid;
    }

    public void setAdminaddressid(Address adminaddressid) {
        this.adminaddressid = adminaddressid;
    }
    
}

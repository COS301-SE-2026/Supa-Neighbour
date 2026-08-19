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
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Represents an administrator user in the system.
 * Admins have elevated privileges and manage system operations.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "admin_table")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private int adminid;

    @Column(name = "admin_create_date")
    private Date admincreatedate;

    @Column(name = "admin_access_level")
    private int adminaccesslevel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User userid;
   
    /**
     * Gets the user id.
     *
     * @return the user identifier
     */
    public User getUserid(){
        return userid;
    }

    
    /**
     * Sets the user identifier.
     *
     * @param userid the user identifier
     */
    public void setUserid(User userid) {
        this.userid = userid;
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
    
}

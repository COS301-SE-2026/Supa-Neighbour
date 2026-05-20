package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "taskinvoicetable")
public class TaskInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskinvoiceid")
    private int id;
    @Column(name = "helperid")
    private int helperid;
    @Column(name = "dependentid")
    private int dependentid;
    @Column(name = "isImmediate")
    private boolean isImmediate;
    @Column(name = "locationid")
    private int locationid;
    @Column(name = "tasktypeid")
    private int tasktypeid;
    @Column(name = "needsspecialist")
    private boolean needsspecialist;
    @Column(name = "signedadminid")
    private int signedadminid;
    @Column(name = "startdate")
    private Date startdate;
    @Column(name = "enddate")
    private Date enddate;
    @Column(name = "helperbadgeid")
    private String helperbadgeid;
    @Column(name = "dependentbadgeid")
    private String dependentbadgeid;
    @Column(name = "dependentratingid")
    private String dependentratingid;
    @Column(name = "helperratingid")
    private String helperratingid;
    @Column(name = "adminreview")
    private String adminreview;
    @Column(name = "compatibilityid")
    private int compatibilityid;

    public TaskInvoice(int id, int helperid, int dependentid, boolean isImmediate, int locationid, int tasktypeid, boolean needsspecialist, int signedadminid, Date startdate, Date enddate, String helperbadgeid, String dependentbadgeid, String dependentratingid, String helperratingid, String adminreview, int compatibilityid) {
        this.id = id;
        this.helperid = helperid;
        this.dependentid = dependentid;
        this.isImmediate = isImmediate;
        this.locationid = locationid;
        this.tasktypeid = tasktypeid;
        this.needsspecialist = needsspecialist;
        this.signedadminid = signedadminid;
        this.startdate = startdate;
        this.enddate = enddate;
        this.helperbadgeid = helperbadgeid;
        this.dependentbadgeid = dependentbadgeid;
        this.dependentratingid = dependentratingid;
        this.helperratingid = helperratingid;
        this.adminreview = adminreview;
        this.compatibilityid = compatibilityid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHelperid() {
        return helperid;
    }

    public void setHelperid(int helperid) {
        this.helperid = helperid;
    }

    public int getDependentid() {
        return dependentid;
    }

    public void setDependentid(int dependentid) {
        this.dependentid = dependentid;
    }

    public boolean isImmediate() {
        return isImmediate;
    }

    public void setImmediate(boolean isImmediate) {
        this.isImmediate = isImmediate;
    }

    public int getLocationid() {
        return locationid;
    }

    public void setLocationid(int locationid) {
        this.locationid = locationid;
    }

    public int getTasktypeid() {
        return tasktypeid;
    }

    public void setTasktypeid(int tasktypeid) {
        this.tasktypeid = tasktypeid;
    }

    public boolean isNeedsspecialist() {
        return needsspecialist;
    }

    public void setNeedsspecialist(boolean needsspecialist) {
        this.needsspecialist = needsspecialist;
    }

    public int getSignedadminid() {
        return signedadminid;
    }

    public void setSignedadminid(int signedadminid) {
        this.signedadminid = signedadminid;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getHelperbadgeid() {
        return helperbadgeid;
    }

    public void setHelperbadgeid(String helperbadgeid) {
        this.helperbadgeid = helperbadgeid;
    }

    public String getDependentbadgeid() {
        return dependentbadgeid;
    }

    public void setDependentbadgeid(String dependentbadgeid) {
        this.dependentbadgeid = dependentbadgeid;
    }

    public String getDependentratingid() {
        return dependentratingid;
    }

    public void setDependentratingid(String dependentratingid) {
        this.dependentratingid = dependentratingid;
    }

    public String getHelperratingid() {
        return helperratingid;
    }

    public void setHelperratingid(String helperratingid) {
        this.helperratingid = helperratingid;
    }

    public String getAdminreview() {
        return adminreview;
    }

    public void setAdminreview(String adminreview) {
        this.adminreview = adminreview;
    }

    public int getCompatibilityid() {
        return compatibilityid;
    }

    public void setCompatibilityid(int compatibilityid) {
        this.compatibilityid = compatibilityid;
    }

}
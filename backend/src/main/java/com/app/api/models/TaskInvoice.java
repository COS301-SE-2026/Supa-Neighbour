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
@Table(name = "task_invoice_table")
public class TaskInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_invoice_id_seq")
    @Column(name = "task_id")
    private int taskid;

    @OneToOne
    @JoinColumn(name = "helper_id")
    private Helper helperid;

    @OneToOne
    @JoinColumn(name = "dependent_id")
    private Dependent dependentid;

    
    @Column(name = "is_immediate")
    private boolean isImmediate;

    @OneToOne
    @JoinColumn(name = "location_id")
    private Location locationid;

    @OneToMany
    @JoinColumn(name = "task_type_id")
    private TaskType tasktypeid;

    @Column(name = "needs_specialist")
    private boolean needsspecialist;

    @OneToOne
    @JoinColumn(name = "signed_admin_id")
    private Admin signedadminid;

    @Column(name = "start_date")
    private Date startdate;

    @Column(name = "end_date")
    private Date enddate;

    @OneToMany
    @JoinColumn(name = "helper_badge_id")
    private Badges helperbadgeid;

    @OneToMany
    @JoinColumn(name = "dependent_rating_review")
    private String dependentRatingreview;

    @Column(name = "helper_rating_review")
    private String helperRatingreview;

    @Column(name = "admin_review")
    private String adminReview;

    @Column(name = "compatibility_id")
    private Compatibility compatibilityid;

    public TaskInvoice(int taskid, Helper helperid, Dependent dependentid, boolean isImmediate, Location locationid, TaskType tasktypeid, boolean needsspecialist, Admin signedadminid, Date startdate, Date enddate, Badges helperbadgeid, String dependentRatingreview, String helpeRatingreview, String adminReview, Compatibility compatibilityid) {
        this.taskid = taskid;
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
        this.dependentRatingreview = dependentRatingreview;
        this.helperRatingreview = helperRatingreview;
        this.adminReview = adminReview;
        this.compatibilityid = compatibilityid;
    }

    public int geTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public Helper getHelperid() {
        return helperid;
    }

    public void setHelperid(Helper helperid) {
        this.helperid = helperid;
    }

    public Dependent getDependentid() {
        return dependentid;
    }

    public void setDependentid(Dependent dependentid) {
        this.dependentid = dependentid;
    }

    public boolean isImmediate() {
        return isImmediate;
    }

    public void setImmediate(boolean isImmediate) {
        this.isImmediate = isImmediate;
    }

    public Location getLocationid() {
        return locationid;
    }

    public void setLocationid(Location locationid) {
        this.locationid = locationid;
    }

    public TaskType getTasktypeid() {
        return tasktypeid;
    }

    public void setTasktypeid(TaskType tasktypeid) {
        this.tasktypeid = tasktypeid;
    }

    public boolean isNeedsspecialist() {
        return needsspecialist;
    }

    public void setNeedsspecialist(boolean needsspecialist) {
        this.needsspecialist = needsspecialist;
    }

    public Admin getSignedadminid() {
        return signedadminid;
    }

    public void setSignedadminid(Admin signedadminid) {
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

    public Badges getHelperbadgeid() {
        return helperbadgeid;
    }

    public void setHelperbadgeid(Badges helperbadgeid) {
        this.helperbadgeid = helperbadgeid;
    }

    public String getDependentratingid() {
        return dependentRatingreview;
    }

    public void setDependentratingreview(String dependentRatingreview) {
        this.dependentRatingreview = dependentRatingreview;
    }

    public String getHelperRatingreview() {
        return helperRatingreview;
    }

    public void setHelperRatingreview(String helperRatingreview) {
        this.helperRatingreview = helperRatingreview;
    }

    public String getAdminReview() {
        return adminReview;
    }

    public void setAdminreview(String adminReview) {
        this.adminReview = adminReview;
    }

    public Compatibility getCompatibilityid() {
        return compatibilityid;
    }

    public void setCompatibilityid(Compatibility compatibilityid) {
        this.compatibilityid = compatibilityid;
    }

}
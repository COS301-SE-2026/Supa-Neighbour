package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Date;



@Entity
@Table(name = "TaskInvoiceTable")
public class Task
{
    // 1. fields

    @Id
    @Column(name  = "TaskID")
    private int taskId;

   @Column(name = "HelperID")
    private Integer helperId;

    @Column(name = "DependentID")
    private Integer dependentId;

    @Column(name = "IsImmediate")
    private boolean isImmediate;

    @Column(name = "LocationID")
    private Integer locationId;

    @Column(name = "TaskTypeID")
    private Integer taskTypeId;

    @Column(name = "NeedsSpecialist")
    private boolean needsSpecialist;

    @Column(name = "SignedAdminID")
    private Integer signedAdminId;

    @Column(name = "StartDate")
    private Date startDate;

    @Column(name = "EndDate")
    private Date endDate;

    @Column(name = "HelperBadgeID")
    private String helperBadgeId;

    @Column(name = "DependentRatingID")
    private String dependentRatingId;

    @Column(name = "HelperRatingID")
    private String helperRatingId;

    @Column(name = "AdminReview")
    private String adminReview;

    @Column(name = "CompatibilityID")
    private Integer compatibilityId;


    // 2. constructor

    public Task() {}


    // 3. getters and setters

  public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public Integer getHelperId() { return helperId; }
    public void setHelperId(Integer helperId) { this.helperId = helperId; }

    public Integer getDependentId() { return dependentId; }
    public void setDependentId(Integer dependentId) { this.dependentId = dependentId; }

    public boolean isImmediate() { return isImmediate; }
    public void setImmediate(boolean isImmediate) { this.isImmediate = isImmediate; }

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    public Integer getTaskTypeId() { return taskTypeId; }
    public void setTaskTypeId(Integer taskTypeId) { this.taskTypeId = taskTypeId; }

    public boolean isNeedsSpecialist() { return needsSpecialist; }
    public void setNeedsSpecialist(boolean needsSpecialist) { this.needsSpecialist = needsSpecialist; }

    public Integer getSignedAdminId() { return signedAdminId; }
    public void setSignedAdminId(Integer signedAdminId) { this.signedAdminId = signedAdminId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getHelperBadgeId() { return helperBadgeId; }
    public void setHelperBadgeId(String helperBadgeId) { this.helperBadgeId = helperBadgeId; }

    public String getDependentRatingId() { return dependentRatingId; }
    public void setDependentRatingId(String dependentRatingId) { this.dependentRatingId = dependentRatingId; }

    public String getHelperRatingId() { return helperRatingId; }
    public void setHelperRatingId(String helperRatingId) { this.helperRatingId = helperRatingId; }

    public String getAdminReview() { return adminReview; }
    public void setAdminReview(String adminReview) { this.adminReview = adminReview; }

    public Integer getCompatibilityId() { return compatibilityId; }
    public void setCompatibilityId(Integer compatibilityId) { this.compatibilityId = compatibilityId; }


}

package com.app.api.models;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "analytics_table")

public class Analytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analytics_id_seq")
    @Column(name = "analytics_id")
    private int analyticsid;

    @OneToMany
    @JoinColumn(name = "task_id")
    private TaskInvoice taskid;

    @Column(name = "admin_id")
    private Admin adminid;

    @Column(name = "helper_type_id")
    private HelperAnalytics helpertypeid;

    @Column(name = "dependent_type_id")
    private DependentAnalytics dependenttypeid;

    public Analytics() {
    }

    public Analytics(int analyticsid, TaskInvoice taskid, Admin adminid, HelperAnalytics helpertypeid,DependentAnalytics dependenttypeid) {
        this.analyticsid = analyticsid;
        this.taskid = taskid;
        this.adminid = adminid;
        this.helpertypeid = helpertypeid;
        this.dependenttypeid = dependenttypeid;
    }

    public int getAnalyticsid() {
        return analyticsid;
    }   

    public void setAnalyticsid(int analyticsid) {
        this.analyticsid = analyticsid;
    }

    public TaskInvoice getTaskid() {
        return taskid;
    }   

    public void setTaskid(TaskInvoice taskid) {
        this.taskid = taskid;
    }

    public Admin getAdminid() {
        return adminid;
    }

    public void setAdminid(Admin adminid) {
        this.adminid = adminid;
    }

    public HelperAnalytics getHelpertypeid() {
        return helpertypeid;
    }

    public void setHelpertypeid(HelperAnalytics helpertypeid) {
        this.helpertypeid = helpertypeid;
    }

    public DependentAnalytics getDependenttypeid() {
        return dependenttypeid;
    }

    public void setDependenttypeid(DependentAnalytics dependenttypeid) {
        this.dependenttypeid = dependenttypeid;
    }

}

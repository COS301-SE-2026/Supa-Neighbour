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
@Table(name = "helper_table")
public class Helper {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "helper_id_seq")
    @Column(name = "helper_id")
    private int helperid;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User userid;

    @OneToMany
    @JoinColumn(name = "tasktype_id")
    private TaskType taskTypeid;

    @OneToMany
    @JoinColumn(name = "badge_id")
    private Badges badgeid;

    @OneToMany
    @JoinColumn(name = "compatibility_id")
    private Compatibility compatibilityid;

    public Helper(int helperid, User userid, TaskType taskTypeid, Badges badgeid, Compatibility compatibilityid) {
        this.helperid = helperid;
        this.userid = userid;
        this.taskTypeid = taskTypeid;
        this.badgeid = badgeid;
        this.compatibilityid = compatibilityid;
    }

    public void setHelperid(int helperid) {
        this.helperid = helperid;
    }

    public void setUserId(User userid) {
        this.userid = userid;
    }

    public int getHelperid() {
        return helperid;
    }

    public User getUserid() {
        return userid;
    }

    public TaskType getTaskTypeid() {
        return taskTypeid;
    }

    public Badges getBadgeid() {
        return badgeid;
    }

    public Compatibility getCompatibilityid() {
        return compatibilityid;
    }

    public void setTaskTypeid(TaskType taskTypeid) {
        this.taskTypeid = taskTypeid;
    }

    public void setBadgeid(Badges badgeid) {
        this.badgeid = badgeid;
    }

    public void setCompatibilityid(Compatibility compatibilityid) {
        this.compatibilityid = compatibilityid;
    }
    
}

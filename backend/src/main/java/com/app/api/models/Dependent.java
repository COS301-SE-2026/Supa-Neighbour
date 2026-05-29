package com.app.api.models;

import java.sql.Date;

import org.springframework.security.config.annotation.web.oauth2.client.OAuth2ClientSecurityMarker;

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
@Table(name = "dependent_table")
public class Dependent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dependent_id_seq")
    @Column(name = "dependentid")
    private int dependentid;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User userid;

    @Column(name = "task_type_id")
    private TaskType taskTypeid;

    @Column(name = "compatibility_id")
    private Compatibility compatibilityid;

    public Dependent(int dependent,User userid, TaskType taskTypeid, Compatibility compatibilityid) {
        this.dependentid=dependentid;
        this.userid = userid;
        this.taskTypeid = taskTypeid;
        this.compatibilityid = compatibilityid;
    }
}

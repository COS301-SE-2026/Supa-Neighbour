package com.app.api.models;

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
 * Represents a dependent user who requires assistance with tasks.
 * Dependents are matched with helpers based on task types and compatibility.
 */
@Data
@Builder
@Entity
@Table(name = "dependent_table")
public class Dependent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dependent_id")
    private int dependentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userid;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType taskTypeid;


    /**
     * Constructs an dependent record with all fields specified.
     *
     * @param dependentid      the dependednt identifier
     * @param userid           the user identifier
     * @param taskTypeid       task type identifier
     */
    public Dependent(int dependentid,User userid, TaskType taskTypeid) {
        this.dependentId=dependentid;
        this.userid = userid;
        this.taskTypeid = taskTypeid;
    }

    /**
     * Default constructor.
     */
    public Dependent(){
        
    }

    /**
     * Simply a getter for the dependent id
     * @return the id of the the Dependent
     */
    public int getDependentId() {
        return dependentId;
    }
    
}

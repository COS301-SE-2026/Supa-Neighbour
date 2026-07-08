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


@Data
@Builder
@Entity
@Table(name = "helper_skill_table")
public class HelperSkill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helper_skill_id")
    private int helperSkillid;

    @ManyToOne
    @JoinColumn(name = "helper_id")
    private Helper helperid;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType taskTypeid;

    /**
     * Constructs a HelperSkill with all fields specified.
     *
     * @param helperSkillid the helper skill identifier
     * @param helperid      the helper who has this skill
     * @param taskTypeid    the task type this skill covers
     */
    public HelperSkill(int helperSkillis, Helper helperid, TaskType taskTypeid){
        this.helperSkillid = helperSkillid;
        this.helperid = helperid;
        this.taskTypeid = taskTypeid;
    }

    /**
     * Default Contructor
     */
    public HelperSkill(){
        
    }

    /**
     * Gets the helper skill identifier.
     *
     * @return the helper skill identifier
     */
    public int getHelperSkillid(){
        return helperSkillid;
    }

    /**
     * Sets the helper skill identifier.
     *
     * @param helperSkillid the helper skill identifier
     */
    public void setHelperSkillid(int helperSkillid){
        this.helperSkillid = helperSkillid;
    }

     /**
     * Gets the helper who has this skill.
     *
     * @return the helper
     */
    public Helper getHelperid(){
        return helperid;
    }


    /**
     * Sets the helper who has this skill.
     *
     * @param helperid the helper
     */
    public void setHelperid(Helper helperid){
        this.helperid = helperid;
    }

    /**
     * Gets the task type this skill covers.
     *
     * @return the task type
     */
    public TaskType getTaskTypeid(){
        return taskTypeid;
    }

    /**
     * Sets the task type this skill covers.
     *
     * @param taskTypeid the task type
     */
    public void setTaskTypeid(TaskType taskTypeid) {
        this.taskTypeid = taskTypeid;
    }
}

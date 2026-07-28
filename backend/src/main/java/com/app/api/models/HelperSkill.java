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
 * Represents a helper user who provides assistance with tasks.
 * Helpers specialize in certain task types and can earn badges.
 */
@Data
@Builder
@Entity
@Table(name = "helper_skill_table")
public class HelperSkill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helper_skill_id")
    private int helperSkillId;

    @ManyToOne
    @JoinColumn(name = "helper_id")
    private Helper helperId;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType taskTypeId;

    /**
     * Constructs a HelperSkill with all fields specified.
     *
     * @param helperSkillId the helper skill identifier
     * @param helperId      the helper associated with this skill
     * @param taskTypeId    the task type associated with this skill
     */
    public HelperSkill(int helperSkillId, Helper helperId, TaskType taskTypeId) {
        this.helperSkillId = helperSkillId;
        this.helperId = helperId;
        this.taskTypeId = taskTypeId;
    }
    /**
     * Default constructor for JPA.
     */
    public HelperSkill() {
    }

    /**
     * Gets the helper skill identifier.
     *
     * @return the helper skill identifier
     */
    public int getHelperSkillId() {
        return helperSkillId;
    }   

    /**
     * Sets the helper skill identifier.
     *
     * @param helperSkillId the helper skill identifier to set
     */
    public void setHelperSkillId(int helperSkillId) {
        this.helperSkillId = helperSkillId;
    }

    /**
     * Gets the helper associated with this helper skill.
     *
     * @return the helper
     */
    public Helper getHelperId() {
        return helperId;
    }

    /**
     * Sets the helper associated with this helper skill.
     *
     * @param helperId the helper to set
     */
    public void setHelperId(Helper helperId) {
        this.helperId = helperId;
    }
    /**
     * Gets the task type identifier for this helper skill.
     *
     * @return the task type identifier
     */     

    public TaskType getTaskTypeId() {
        return taskTypeId;
    }
    /**
     * Sets the task type identifier for this helper skill.
     *
     * @param taskTypeId the task type identifier to set
     */
    public void setTaskTypeId(TaskType taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

}

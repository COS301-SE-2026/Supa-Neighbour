package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing a recently completed task.
 *
 * <p>This DTO contains summary information about a helper's recently
 * completed task, including its identifier, type, and completion date.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecentTaskDTO {
    private int taskId;
    private String type;
    private String endDate;

    /**
     * Creates a recent task data transfer object.
     *
     * @param taskId the unique identifier of the task
     * @param type the type of the completed task
     * @param endDate the date on which the task was completed
     */
    public RecentTaskDTO(int taskId, String type, String endDate){
        this.taskId = taskId;
        this.type = type;
        this.endDate = endDate;
    }

    /**
     * Returns the task identifier.
     *
     * @return the task identifier
     */
    public int gettaskId(){
        return taskId;
    }
    /**
     * Returns the task type.
     *
     * @return the task type
     */
    public String getType(){
        return type;
    }

    /**
     * Returns the task completion date.
     *
     * @return the completion date
     */
    public String getDate(){
        return endDate;
    }
}

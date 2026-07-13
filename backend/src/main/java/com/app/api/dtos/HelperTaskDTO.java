package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing a task associated with a helper.
 *
 * <p>This DTO contains summary information about a task, including its
 * type, status, schedule, neighbourhood, and experience points awarded.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HelperTaskDTO {
    

    private int taskId;
    private String taskType;
    private String status;
    private String startDate;
    private String endDate;
    private String neighbourhood;
    private Integer xpAwarded;

    /**
     * Creates a helper task data transfer object.
     *
     * @param taskId the unique identifier of the task
     * @param taskType the type of task
     * @param status the current status of the task
     * @param startDate the scheduled start date of the task
     * @param endDate the completion or scheduled end date of the task
     * @param neighbourhood the neighbourhood where the task is located
     * @param xpAwarded the experience points awarded for the task, if applicable
     */
    public HelperTaskDTO(int taskId, String taskType, String status, String startDate, String endDate, String neighbourhood, Integer xpAwarded){
        this.taskId = taskId;
        this.taskType = taskType;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.neighbourhood = neighbourhood;
        this.xpAwarded = xpAwarded;
    }

    /**
     * Returns the task identifier.
     *
     * @return the task identifier
     */

    public int getTaskId(){
        return taskId;
    }

    /**
     * Returns the type of task.
     *
     * @return the task type
     */
    public String getTaskType(){
        return taskType;
    }

    /**
     * Returns the current status of the task.
     *
     * @return the task status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the task's start date.
     *
     * @return the start date
     */
    public String getStartDate(){
        return startDate;
    }

    /**
     * Returns the task's end date.
     *
     * @return the end date
     */
    public String getEndDate(){
        return endDate;
    }

    /**
     * Returns the neighbourhood in which the task is located.
     *
     * @return the neighbourhood name
     */
    public String getNeighbourhood(){
        return neighbourhood;
    }

    /**
     * Returns the experience points awarded for completing the task.
     *
     * @return the experience points awarded, or {@code null} if not applicable
     */
    public Integer getXpAwarded(){
        return xpAwarded;
    }
}

package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HelperTaskDTO {
    

    private int taskId;
    private String taskType;
    private String status;
    private String startDate;
    private String endDate;
    private String neighbourhood;
    private Integer xpAwarded;

    public HelperTaskDTO(int taskId, String taskType, String status, String startDate, String endDate, String neighbourhood, Integer xpAwarded){
        this.taskId = taskId;
        this.taskType = taskType;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.neighbourhood = neighbourhood;
        this.xpAwarded = xpAwarded;
    }

    public int getTaskId(){
        return taskId;
    }

    public String getTaskType(){
        return taskType;
    }

    public String getStatus() {
        return status;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    public String getNeighbourhood(){
        return neighbourhood;
    }

    public Integer getXpAwarded(){
        return xpAwarded;
    }
}

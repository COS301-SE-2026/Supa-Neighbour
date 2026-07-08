package com.app.api.dtos;


public class RecentTaskDTO {
    private int taskId;
    private String type;
    private String endDate;

    public RecentTaskDTO(int taskId, String type, String endDate){
        this.taskId = taskId;
        this.type = type;
        this.endDate = endDate;
    }

    public int gettaskId(){
        return taskId;
    }
    public String getType(){
        return type;
    }

    public String getDate(){
        return endDate;
    }
}

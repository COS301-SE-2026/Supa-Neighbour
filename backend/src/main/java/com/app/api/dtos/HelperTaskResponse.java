package com.app.api.dtos;

import java.util.List;

public class HelperTaskResponse {
    
    private int helperId;
    private int total;
    private List<HelperTaskDTO> tasks;

    public HelperTaskResponse(int helperId, int total, List<HelperTaskDTO> tasks){
        this.helperId = helperId;
        this.total = total;
        this.tasks = tasks;
    }

    public int getHelperId(){
        return helperId;
    }

    public int getTotal(){
        return total;
    }

    public List<HelperTaskDTO> getTasks(){
        return tasks;
    }
}

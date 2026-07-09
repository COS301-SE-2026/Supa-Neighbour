package com.app.api.dtos;

import java.util.List;

/**
 * Data Transfer Object representing a helper's task history.
 *
 * <p>This response contains the helper's identifier, the total number
 * of associated tasks, and a collection of task summaries.</p>
 */
public class HelperTaskResponse {
    
    private int helperId;
    private int total;
    private List<HelperTaskDTO> tasks;

    /**
     * Creates a helper task response.
     *
     * @param helperId the unique identifier of the helper
     * @param total the total number of tasks associated with the helper
     * @param tasks the list of task summaries
     */
    public HelperTaskResponse(int helperId, int total, List<HelperTaskDTO> tasks){
        this.helperId = helperId;
        this.total = total;
        this.tasks = tasks;
    }

    /**
     * Returns the helper's unique identifier.
     *
     * @return the helper identifier
     */
    public int getHelperId(){
        return helperId;
    }

    /**
     * Returns the total number of tasks.
     *
     * @return the total task count
     */
    public int getTotal(){
        return total;
    }

    /**
     * Returns the helper's tasks.
     *
     * @return the list of task summaries
     */
    public List<HelperTaskDTO> getTasks(){
        return tasks;
    }
}

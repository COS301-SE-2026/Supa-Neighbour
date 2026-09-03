package com.app.api.events;

public class TaskStartedEvent {
    private final  int requesterUserId;
    private final int taskId;
    private final String helperName ;

    /**
     * Constructs a new TaskStartedEvent with the specified requester, task, and helper details.
     * 
     * @param requesterUserId The unique identifier of the user who originally created
     *                        and requested the task. This user will receive the
     *                        notification that the task has started.
     * @param taskId The unique identifier of the task that has been started
     * @param helperName The display name or username of the helper who is working
     *                   on the task, used for personalizing notification messages
     * @throws IllegalArgumentException if requesterUserId or taskId is less than or equal to 0,
     *         or if helperName is null or empty
     */
    public TaskStartedEvent(int requesterUserId, int taskId, String helperName) {
        this.requesterUserId = requesterUserId;
        this.taskId = taskId;
        this.helperName = helperName;
    }

    /**
     * Returns the unique identifier of the task requester who should be notified
     * that their task has been started.
     * 
     * <p>The requester is the user who originally created and posted the task
     * requesting assistance from helpers.
     * 
     * @return The requester's user ID
     */

    public int getRequesterUserId() {
        return requesterUserId;
    }

    /**
     * Returns the unique identifier of the task that has been started.
     * 
     * <p>This ID is used to link the notification to the specific task
     * and for generating deep links to the task details page.
     * 
     * @return The task ID
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * Returns the display name or username of the helper who is working on the task.
     * 
     * <p>This name is used in notification messages to inform the requester
     * about who is handling their task (e.g., "{helperName} has started working on your task").
     * 
     * @return The helper's display name
     */
    public String getHelperName() {
        return helperName;
    }
}

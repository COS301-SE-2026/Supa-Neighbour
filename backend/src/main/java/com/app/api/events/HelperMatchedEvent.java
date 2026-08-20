package com.app.api.events;

/**
 * Published when a helper is newly matched to a task (a TaskInvitation row
 * was created). Consumed after the enclosing transaction commits, so a
 * notification is only sent if the invitation actually persisted.
 */
public class HelperMatchedEvent {

    private final int helperUserId;
    private final int taskId;
    private final String taskTitle;

    /**
     * Constructs a new HelperMatchedEvent with the specified helper, task, and title information.
     * 
     * @param helperUserId The unique identifier of the helper who has been matched to the task
     * @param taskId The unique identifier of the task that the helper has been matched to
     * @param taskTitle The title or brief description of the task for notification purposes
     * @throws IllegalArgumentException if helperUserId or taskId is less than or equal to 0,
     *         or if taskTitle is null or empty
     */
    public HelperMatchedEvent(int helperUserId, int taskId, String taskTitle) {
        this.helperUserId = helperUserId;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
    }

    /**
     * Returns the unique identifier of the helper who has been matched to the task.
     * 
     * @return The helper's user ID
     */
    public int getHelperUserId() {
        return helperUserId;
    }

    /**
     * Returns the unique identifier of the task that the helper has been matched to.
     * 
     * @return The task ID
     */
    public int getTaskId() {
        return taskId;
    }

     /**
     * Returns the title of the task for inclusion in notification messages.
     * 
     * @return The task title string
     */
    public String getTaskTitle() {
        return taskTitle;
    }
}

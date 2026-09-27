package com.app.api.events;

public class TaskAcceptedEvent {
    private final int taskId;
    private final int helperId;
    private final int requesterId;

    /**
     * Creates an event fired when a task is accepted.
     *
     * @param taskId the accepted task ID
     * @param helperId the helper assigned to the task
     * @param requesterId the requester who accepted the task
     */
    public TaskAcceptedEvent(int taskId, int helperId, int requesterId) {
        this.taskId = taskId;
        this.helperId = helperId;
        this.requesterId = requesterId;
    }

    /**
     * Gets the accepted task ID.
     *
     * @return the task ID
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * Gets the helper ID.
     *
     * @return the helper ID
     */
    public int getHelperId() {
        return helperId;
    }

    /**
     * Gets the requester ID.
     *
     * @return the requester ID
     */
    public int getRequesterId() {
        return requesterId;
    }
}

package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing a single in-app notification.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {
    private int notificationId;
    private String type;
    private String entityId;
    private String title;
    private String body;
    private boolean isRead;
    private String createdAt;

     /**
     * Constructs a new NotificationDTO with all fields.
     *
     * @param notificationId The unique identifier of the notification
     * @param type The type/category of the notification
     * @param entityId The identifier of the associated entity
     * @param title The notification title
     * @param body The notification body content
     * @param isRead Whether the notification has been read
     * @param createdAt The creation timestamp as a string
     */
    public NotificationDTO(int notificationId, String type, String entityId, String title,
                            String body, boolean isRead, String createdAt) {
        this.notificationId = notificationId;
        this.type = type;
        this.entityId = entityId;
        this.title = title;
        this.body = body;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier of the notification.
     *
     * @return The notification ID
     */
    public int getNotificationId() {
        return notificationId;
    }

    /**
     * Returns the type/category of the notification.
     *
     * @return The notification type (e.g., "SYSTEM", "COMMENT", "LIKE", "FOLLOW")
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the identifier of the entity associated with this notification.
     *
     * @return The entity identifier (e.g., post ID, user ID, comment ID)
     */
    public String getEntityId() {
        return entityId;
    }

        /**
     * Returns the title/short summary of the notification.
     *
     * @return The notification title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the detailed body/content of the notification message.
     *
     * @return The notification body content
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns whether the notification has been read by the user.
     *
     * @return {@code true} if the notification has been read, {@code false} otherwise
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Returns the creation timestamp of the notification as a string.
     *
     * @return The creation timestamp in ISO-8601 format (e.g., "2026-09-01T10:30:00Z")
     */
    public String getCreatedAt() {
        return createdAt;
    }
}

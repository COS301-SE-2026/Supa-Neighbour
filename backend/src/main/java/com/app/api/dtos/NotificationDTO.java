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

    public int getNotificationId() {
        return notificationId;
    }

    public String getType() {
        return type;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
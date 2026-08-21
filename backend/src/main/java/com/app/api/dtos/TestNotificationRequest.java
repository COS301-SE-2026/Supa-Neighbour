package com.app.api.dtos;

/**
 * Request DTO for sending a test notification from the Flutter app.
 * Used by the /api/test/notification endpoint for development testing.
 */
public class TestNotificationRequest {
    
    private String fcmToken;
    private String title;
    private String body;
    private String type;
    private String entityId;

    // Default constructor
    public TestNotificationRequest() {
    }

    // Getters and Setters
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
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

    /**
     * Default constructor required for JSON deserialization.
     */
    public TestNotificationRequest() {
        // Intentionally empty – needed for Jackson/Firebase serialization.
    }

    /**
     * Gets the FCM token.
     *
     * @return the FCM token
     */
    public String getFcmToken() {
        return fcmToken;
    }

    /**
     * Sets the FCM token.
     *
     * @param fcmToken the FCM token
     */
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    /**
     * Gets the notification title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the notification title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the notification body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the notification body.
     *
     * @param body the body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the notification type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the notification type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the entity ID.
     *
     * @return the entity ID
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Sets the entity ID.
     *
     * @param entityId the entity ID
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}

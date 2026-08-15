package com.app.api.models;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a registered device (via its FCM token) belonging to a user.
 * <p>
 * This entity maps to the {@code user_device_table} database table.
 * A single user may have multiple rows here (one per device/install),
 * enabling push notifications to fan out to all of a user's active devices.
 * </p>
 */
@Getter
@Setter
@Entity
@Table
public class UserDevice {
    /**
     * The unique identifier for this device registration.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_device_id")
    private int userDeviceId;


    /**
     * The user this device belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The current FCM registration token for this device.
     */
    @Column(name = "fcm_token", unique = true)
    private String fcmToken;

    /**
     * When this token was last registered or refreshed.
     */
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public UserDevice(){

    }


    /**
     * Constructs a device registration with all attributes.
     *
     * @param userDeviceId the unique identifier of the device registration
     * @param user the user this device belongs to
     * @param fcmToken the FCM registration token
     * @param updatedAt when this token was last registered or refreshed
     */
    public UserDevice(int userDeviceId, User user, String fcmToken, Timestamp updatedAt){
        this.userDeviceId = userDeviceId;
        this.user = user;
        this.fcmToken = fcmToken;
        this.updatedAt = updatedAt;
    }
}

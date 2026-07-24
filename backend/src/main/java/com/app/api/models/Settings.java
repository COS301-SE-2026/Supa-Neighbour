package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.Instant;

/**
 * Entity representing a user's application settings.
 * <p>
 * Stores user-specific preferences such as online visibility,
 * phone number visibility, theme mode, and the timestamp of the
 * user's last recorded activity. Each settings record is associated
 * with exactly one user.
 * </p>
 */
@Entity
@Table(name = "settings_table")
public class Settings {

    /**
     * Enumeration of the supported application theme modes.
     */
    public enum ThemeMode {
        LIGHT,
        DARK
    }
    @Id
    @Column
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "last_seen")
    private Instant lastSeen;

    @Column(name = "show_status", nullable = false)
    private Boolean showStatus = true;

    @Column(name = "show_phone_no", nullable = false)
    private Boolean showPhoneNo = true;


    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ThemeMode mode = ThemeMode.LIGHT;

    /**
     * Returns the ID of the user associated with these settings.
     *
     * @return the user's unique identifier
     */
    public Integer getUserId(){
        return userId;
    }

     /**
     * Returns the user associated with these settings.
     *
     * @return the associated user
     */
    public User getUser(){
        return user;
    }

    /**
     * Sets the user associated with these settings.
     *
     * @param user the user to associate
     */
    public void setUser(User user){
        this.user = user;
    }

    /**
     * Returns the timestamp indicating when the user was last seen.
     *
     * @return the last seen timestamp
     */
    public Instant getLastSeen(){
        return lastSeen;
    }

    /**
     * Sets the timestamp indicating when the user was last seen.
     *
     * @param lastSeen the last seen timestamp
     */
    public void setLastSeen(Instant lastSeen){
        this.lastSeen = lastSeen;
    }

    /**
     * Returns whether the user's online status is visible.
     *
     * @return {@code true} if the user's status is visible;
     *         {@code false} otherwise
     */
    public Boolean getShowStatus(){
        return showStatus;
    }

    /**
     * Sets whether the user's online status is visible.
     *
     * @param showStatus {@code true} to make the status visible;
     *                   {@code false} to hide it
     */
    public void setShowStatus(Boolean showStatus){
        this.showStatus = showStatus;
    }

    /**
     * Returns whether the user's phone number is visible.
     *
     * @return {@code true} if the phone number is visible;
     *         {@code false} otherwise
     */
    public Boolean getShowPhoneNo(){
        return showPhoneNo;
    }

    /**
     * Sets whether the user's phone number is visible.
     *
     * @param showPhoneNo {@code true} to make the phone number visible;
     *                    {@code false} to hide it
     */
    public void setShowPhoneNo(Boolean showPhoneNo){
        this.showPhoneNo = showPhoneNo;
    }

    /**
     * Returns the user's preferred application theme mode.
     *
     * @return the current theme mode
     */
    public ThemeMode getMode(){
        return mode;
    }

    /**
     * Sets the user's preferred application theme mode.
     *
     * @param mode the theme mode to set
     */
    public void setMode(ThemeMode mode){
        this.mode = mode;
    }
    
}

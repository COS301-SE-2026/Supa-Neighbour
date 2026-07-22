package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing a user's online status
 * information.
 * <p>
 * Contains the user's visibility setting and, when visible, whether
 * the user is currently online and the timestamp of their last
 * recorded activity.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserStatusResponse {
    private Boolean visible;
    private Boolean online;
    private Instant lastSeen;

     /**
     * Constructs a response containing only the user's visibility
     * setting.
     *
     * @param visible {@code true} if the user's status is visible;
     *                {@code false} otherwise
     */
    public UserStatusResponse(boolean visible){
        this.visible = visible;
    }

    /**
     * Constructs a response containing the user's visibility,
     * online status, and last seen timestamp.
     *
     * @param visible {@code true} if the user's status is visible;
     *                {@code false} otherwise
     * @param online {@code true} if the user is currently online;
     *               {@code false} otherwise
     * @param lastSeen the timestamp when the user was last seen
     */
    public UserStatusResponse(boolean visible, boolean online, Instant lastSeen){
        this.visible = visible;
        this.online = online;
        this.lastSeen = lastSeen;
    }

    /**
     * Returns whether the user's status is visible.
     *
     * @return {@code true} if the status is visible;
     *         {@code false} otherwise
     */
    public Boolean getVisible(){
        return visible;
    }

    /**
     * Sets whether the user's status is visible.
     *
     * @param visible {@code true} to make the status visible;
     *                {@code false} otherwise
     */
    public void setVisible(boolean visible){
        this.visible = visible;
    }

    /**
     * Returns whether the user is currently online.
     *
     * @return {@code true} if the user is online;
     *         {@code false} otherwise
     */
    public Boolean getOnline(){
        return online;
    }

    /**
     * Sets whether the user is currently online.
     *
     * @param online {@code true} if the user is online;
     *               {@code false} otherwise
     */
    public void setOnline(boolean online){
        this.online = online;
    }

    /**
     * Returns the timestamp when the user was last seen online.
     *
     * @return the last seen timestamp
     */
    public Instant getlastSeen(){
        return lastSeen;
    }

    /**
     * Sets the timestamp when the user was last seen online.
     *
     * @param lastSeen the last seen timestamp
     */
    public void setLastSeen(Instant lastSeen){
        this.lastSeen = lastSeen;
    }
}

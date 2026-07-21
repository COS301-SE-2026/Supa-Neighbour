package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserStatusResponse {
    private Boolean visible;
    private Boolean online;
    private Instant lastSeen;

    public UserStatusResponse(boolean visible){
        this.visible = visible;
    }

    public UserStatusResponse(boolean visible, boolean online, Instant lastSeen){
        this.visible = visible;
        this.online = online;
        this.lastSeen = lastSeen;
    }

    public Boolean getVisible(){
        return visible;
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public Boolean getOnline(){
        return online;
    }

    public void setOnline(boolean online){
        this.online = online;
    }

    public Instant getlastSeen(){
        return lastSeen;
    }

    public void setLastSeen(Instant lastSeen){
        this.lastSeen = lastSeen;
    }
}

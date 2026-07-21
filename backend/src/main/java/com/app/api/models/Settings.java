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

@Entity
@Table(name = "settings_table")
public class Settings {

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

    public Settings(){

    }

    public Integer getUserId(){
        return userId;
    }

    public User getUser(){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Instant getLastSeen(){
        return lastSeen;
    }

    public void setLastSeen(Instant lastSeen){
        this.lastSeen = lastSeen;
    }

    public Boolean getShowStatus(){
        return showStatus;
    }

    public void setShowStatus(Boolean showStatus){
        this.showStatus = showStatus;
    }

    public Boolean getShowPhoneNo(){
        return showPhoneNo;
    }

    public void setShowPhoneNo(Boolean showPhoneNo){
        this.showPhoneNo = showPhoneNo;
    }

    public ThemeMode getMode(){
        return mode;
    }

    public void setMode(ThemeMode mode){
        this.mode = mode;
    }
    
}

package com.app.api.dtos;


public class DeviceTokenRequestDTO {
    private String fcmToken;

    DeviceTokenRequestDTO(){

    }

    public String getFcmToken(){
        return fcmToken;
    }

    public void setFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }
}

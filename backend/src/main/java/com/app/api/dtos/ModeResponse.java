package com.app.api.dtos;

public class ModeResponse {
    private String mode;

    

    public ModeResponse(String mode){
        this.mode = mode;
    }

    public String getMode(){
        return mode;
    }

    public void setMode(String mode){
        this.mode = mode;
    }
}

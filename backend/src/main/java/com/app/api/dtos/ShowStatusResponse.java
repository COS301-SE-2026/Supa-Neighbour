package com.app.api.dtos;

public class ShowStatusResponse {
    
    private boolean showStatus;

    public ShowStatusResponse(boolean showStatus){
        this.showStatus = showStatus;
    }

    public boolean getShowStatus(){
        return showStatus;
    }

    public void setShowStatus(boolean showStatus){
        this.showStatus = showStatus;
    }

    
}

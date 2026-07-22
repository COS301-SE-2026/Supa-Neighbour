package com.app.api.dtos;

import org.checkerframework.checker.units.qual.m;

public class UpdateSettingsDTO {
    
    private Boolean showStatus;
    private Boolean showPhoneNo;
    private String mode;

    public UpdateSettingsDTO(Boolean showStatus,Boolean showPhoneNo,String mode)
    {
        this.mode = mode;
        this.showPhoneNo = showPhoneNo;
        this.showStatus = showStatus;
    }

    public Boolean getShowStatus() {
        return showStatus;
    }

    public Boolean getShowPhoneNo() {
        return showPhoneNo;
    }

    public String getMode() {
        return mode;
    }

    

}

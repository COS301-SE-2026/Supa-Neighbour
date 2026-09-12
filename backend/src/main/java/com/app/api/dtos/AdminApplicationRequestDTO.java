package com.app.api.dtos;

/**
 * Data Transfer Object for Admin Application Requests.
 * AdminApplicationRequestDTO
 */
public class AdminApplicationRequestDTO {
    private Integer userId;
    private String justification;

    public AdminApplicationRequestDTO() {
    }

    public AdminApplicationRequestDTO(Integer userId, String justification) {
        this.userId = userId;
        this.justification = justification;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getJustification() {
        return justification;
    }

    public void setUserIt(Integer userId) {
        this.userId = userId;
    }

    public void setJustification(String justification) {
        this.justification= justification;
    }
}



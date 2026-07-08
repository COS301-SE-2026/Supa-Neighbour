package com.app.api.dtos;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileResponse {
    private String message;
    private String displayName;
    private List<String> skills;

    public UpdateProfileResponse(String message, String displayName, List<String> skills){
        this.skills = skills;
        this.displayName = displayName;
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public String getDisplayName(){
        return displayName;
    }

    public List<String> getSkills(){
        return skills;
    }
}

package com.app.api.dtos;

import lombok.Data;

@Data
public class SuggestionRequestDTO {
    private String violationType;
    private String severity;

    public String getViolationType(){
        return violationType;
    }

    public String getSeverity(){
        return severity;
    }
}

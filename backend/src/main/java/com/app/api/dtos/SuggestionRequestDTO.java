package com.app.api.dtos;

import lombok.Data;

@Data
public class SuggestionRequestDTO {
    private String violationType;
    private String severity;

    /**
     * Gets the type of violation.
     * 
     * @return the violation type as a {@code String}, or {@code null} if not set
     */
    public String getViolationType(){
        return violationType;
    }

    /**
     * Gets the severity level of the violation.
     * 
     * @return the severity level as a {@code String}, or {@code null} if not set
     */
    public String getSeverity(){
        return severity;
    }
}

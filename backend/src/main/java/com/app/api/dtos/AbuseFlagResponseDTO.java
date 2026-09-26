package com.app.api.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AbuseFlagResponseDTO (
    Long flagId,
    String patternType, 
    String patternTypeDisplay,
    Integer zoneId,
    Double metricValue,
    String status, 
    int occurrenceCount,
    LocalDateTime firstDetectedAt,
    LocalDateTime lastDetectedAt,
    UUID runId,
    List<Participant> participants

){
    public record Participant(int userId, String role) {}
}

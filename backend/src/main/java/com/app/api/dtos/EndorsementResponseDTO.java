package com.app.api.dtos;

import java.time.LocalDateTime;

/**
 * A single endorsement as returned to clients.
 *
 * @param endorsementId    surrogate key
 * @param endorserId       user who gave the endorsement
 * @param endorseeId       user who received it
 * @param skillTag         skill tag key
 * @param skillDisplayName human-readable skill name
 * @param category         skill category, may be {@code null}
 * @param zoneId           zone the endorsement was made in
 * @param taskId           backing task, {@code null} when not task-tied
 * @param weight           endorsement weight
 * @param createdAt        creation timestamp
 */
public record EndorsementResponseDTO(
    Integer endorsementId,
    Integer endorserId,
    Integer endorseeId,
    String skillTag,
    String skillDisplayName,
    String category,
    Integer zoneId,
    Integer taskId,
    Integer weight,
    LocalDateTime createdAt
    )
    {
}



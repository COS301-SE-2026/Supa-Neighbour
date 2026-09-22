package com.app.api.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
 
/**
 * Request body for {@code POST /api/endorsements}.
 *
 * <p>Note that there is deliberately no {@code endorserId} field: the endorser is
 * always resolved from the Firebase ID token so a caller cannot endorse on
 * someone else's behalf.</p>
 *
 * @param endorseeId the user receiving the endorsement
 * @param skillTag   the approved skill tag being endorsed
 * @param zoneId     the zone the endorsement is attributed to
 * @param taskId     optional completed task backing the endorsement
 * @param weight     optional weight from 1 to 5; defaults to 1 when omitted
 */
public record CreateEndorsementRequestDTO(
    @NotNull(message ="endorseeId is required")
    Integer endorseeId,

    @NotBlank(message="skillTag is required")
    @Size(max=50, message = "skilltag must be at most 50 characters")
    String skillTag,

    Integer taskId
)
{
    
}


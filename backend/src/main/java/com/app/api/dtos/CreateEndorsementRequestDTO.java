package com.app.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for creating an endorsement.
 *
 * @param endorseeId the user being endorsed
 * @param skillTag   the skill tag being endorsed for
 * @param taskId     the task this endorsement is tied to, or {@code null}
 */
public record CreateEndorsementRequestDTO(
    @NotNull Integer endorseeId,
    @NotBlank String skillTag,
    Integer taskId
) {

}



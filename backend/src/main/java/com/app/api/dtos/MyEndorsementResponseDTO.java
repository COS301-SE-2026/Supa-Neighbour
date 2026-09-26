package com.app.api.dtos;
import java.util.List;
/**
 * Response for {@code GET /api/endorsements/me} — every endorsement the caller
 * has received, grouped by skill tag.
 *
 * @param userId            the caller
 * @param totalEndorsements total across all skills
 * @param skills            one group per skill tag, heaviest group first
 */
public record MyEndorsementResponseDTO(
    Integer userId,
    int totalEndorsements,
    List<SkillGroup> skills
    
) {
    /**
     * One skill tag and the endorsements received under it.
     *
     * @param skillTag     skill tag key
     * @param displayName  human-readable skill name
     * @param category     skill category, may be {@code null}
     * @param count        number of endorsements in this group
     * @param totalWeight  summed weight of this group
     * @param endorsements the endorsements themselves, newest first
     */
    public record SkillGroup(
        String skillTag,
        String displayName,
        String category,
        int count,
        long totalWeight,
        List<EndorsementResponseDTO> endorsements
    ) {
    }
}



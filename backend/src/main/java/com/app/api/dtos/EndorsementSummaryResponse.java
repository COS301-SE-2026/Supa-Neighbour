package com.app.api.dtos;

import java.time.LocalDateTime;
import java.util.List;
 
/**
 * Response for {@code GET /api/endorsements/me/summary} — a compact shape sized
 * for profile cards rather than a full listing.
 *
 * @param userId            the caller
 * @param totalEndorsements total endorsements received
 * @param totalWeight       summed weight across all skills
 * @param distinctSkills    number of distinct skills endorsed
 * @param distinctEndorsers number of distinct people who endorsed the caller
 * @param topSkills         the highest-weighted skills, capped by the service
 * @param lastEndorsedAt    most recent endorsement timestamp, {@code null} if none
 */
public record EndorsementSummaryResponse(
    Integer userId,
    long totalEndorsements,
    long totalWeight,
    int distinctSkills,
    long distinctEndorsers,
    List<SkillSummary> topSkill,
    LocalDateTime lastEndorsedAt
) {
/**
 * Per-skill totals with no nested endorsement list.
 *
 * @param skillTag    skill tag key
 * @param displayName human-readable skill name
 * @param category    skill category, may be {@code null}
 * @param count       endorsements received for this skill
 * @param totalWeight summed weight for this skill
 */ 
    public record SkillSummary(
        String skillTag,
        String displayName,
        long count,
        long totalWeight
    ) {
    }
}


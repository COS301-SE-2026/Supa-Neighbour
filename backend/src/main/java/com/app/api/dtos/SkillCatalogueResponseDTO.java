package com.app.api.dtos;

import java.util.List;

/**
 * Response for {@code GET /api/endorsements/skills} — the approved skill tags
 * grouped by category.
 *
 * @param totalSkills number of approved skills returned
 * @param categories  category groups, alphabetical
 */
public record SkillCatalogueResponseDTO(int totalSkills,List<CategoryGroup> categories) {


/**
 * One category and its skills.
 *
 * @param category the category name, or the uncategorised bucket label
 * @param skills   skills in that category, alphabetical by display name
 */
    public record CategoryGroup(String category,List<SkillItem> skills){
    }

/**
* A single catalogue entry.
 *
 * @param skillTag    skill tag key clients submit when endorsing
 * @param displayName human-readable name
 */
    public record SkillItem(String skillTag,String displayName) {
    }
}


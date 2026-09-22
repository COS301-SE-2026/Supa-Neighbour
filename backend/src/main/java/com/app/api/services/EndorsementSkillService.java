package com.app.api.services;

import com.app.api.models.EndorsementSkill;
import com.app.api.repositories.EndorsementSkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndorsementSkillService {

    private final EndorsementSkillsRepository endorsementSkillRepository;

    public EndorsementSkillService(
            EndorsementSkillsRepository endorsementSkillRepository) {
        this.endorsementSkillRepository = endorsementSkillRepository;
    }

    /**
     * Get all endorsement skills.
     */
    public List<EndorsementSkill> getAllSkills() {
        return endorsementSkillRepository.findAll();
    }

    /**
     * Find an endorsement skill by its skill tag.
     */
    public EndorsementSkill getSkillByTag(String skillTag) {
        return endorsementSkillRepository.findBySkillTag(skillTag)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Endorsement skill not found: " + skillTag));
    }

    /**
     * Find a skill if it exists, otherwise create it.
     */
    public EndorsementSkill getOrCreateSkill(String skillTag) {

        if (skillTag == null || skillTag.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Skill tag cannot be empty");
        }

        String cleanedTag = skillTag.trim();

        return endorsementSkillRepository.findBySkillTag(cleanedTag)
                .orElseGet(() -> {
                    EndorsementSkill skill = new EndorsementSkill();
                    skill.setSkillTag(cleanedTag);

                    return endorsementSkillRepository.save(skill);
                });
    }

    /**
     * Require a skill to already exist.
     *
     * This is the method used by:
     *
     * EndorsementSkill skill =
     *     requireUsableSkill(request.skillTag());
     */
    public EndorsementSkill requireUsableSkill(String skillTag) {

        if (skillTag == null || skillTag.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Skill tag cannot be empty");
        }

        return endorsementSkillRepository.findBySkillTag(skillTag.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Endorsement skill does not exist: "
                                        + skillTag));
    }

    /**
     * Create a new endorsement skill.
     */
    public EndorsementSkill createSkill(String skillTag) {

        if (skillTag == null || skillTag.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Skill tag cannot be empty");
        }

        String cleanedTag = skillTag.trim();

        if (endorsementSkillRepository
                .findBySkillTag(cleanedTag)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Endorsement skill already exists: " + cleanedTag);
        }

        EndorsementSkill skill = new EndorsementSkill();
        skill.setSkillTag(cleanedTag);

        return endorsementSkillRepository.save(skill);
    }

    /**
     * Delete an endorsement skill by its skill tag.
     */
    public void deleteSkill(String skillTag) {

        EndorsementSkill skill = requireUsableSkill(skillTag);

        endorsementSkillRepository.delete(skill);
    }
}



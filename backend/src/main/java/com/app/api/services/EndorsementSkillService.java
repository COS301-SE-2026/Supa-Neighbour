package com.app.api.services;

import com.app.api.models.EndorsementSkill;
import com.app.api.repositories.EndorsementSkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndorsementSkillService {

    private final EndorsementSkillsRepository endorsementSkillsRepository;

    /**
     * Constructs the service with its repository dependency.
     *
     * @param endorsementSkillsRepository the skill catalogue repository
     */
    public EndorsementSkillService(EndorsementSkillsRepository endorsementSkillsRepository) {
        this.endorsementSkillsRepository = endorsementSkillsRepository;
    }

    /**
    * Looks up an approved, usable skill by its tag.
    *
    * @param skillTag the tag to resolve
    * @return the matching skill
    * @throws IllegalArgumentException if the tag is blank or not registered
    */
    public EndorsementSkill requireUsableSkill(String skillTag) {
        if(skillTag == null || skillTag.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill tag cannot be empty");
        }

        return endorsementSkillsRepository.findBySkillTag(skillTag.trim())
            .orElseThrow(() -> new IllegalArgumentException("skill tag "+skillTag+" is not registered"));
    }

    /**
    * Returns every skill in the catalogue, approved or not.
     *
     * @return all skills
     */
    public List<EndorsementSkill> getAllSkills() {
        return endorsementSkillsRepository.findAll();
    }
}




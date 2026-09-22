package com.app.api.services;

import com.app.api.models.EndorsementSkill;
import com.app.api.repositories.EndorsementSkillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndorsementSkillService {

    private final EndorsementSkillsRepository endorsementSkillsRepository;

    public EndorsementSkillService(EndorsementSkillsRepository endorsementSkillsRepository) {
        this.endorsementSkillsRepository = endorsementSkillsRepository;
    }

    public EndorsementSkill requireUsableSkill(String skillTag) {
        if(skillTag == null || skillTag.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill tag cannot be empty");
        }

        return endorsementSkillsRepository.findBySkillTag(skillTag.trim())
            .orElseThrow(() -> new IllegalArgumentException("skill tag "+skillTag+" is not registered"));
    }

    public List<EndorsementSkill> getAllSkills() {
        return endorsementSkillsRepository.findAll();
    }
}




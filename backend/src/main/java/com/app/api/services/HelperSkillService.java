package com.app.api.services;

import java.util.List;
import org.springframework.stereotype.Service;

import com.app.api.models.HelperSkill;
import com.app.api.repositories.HelperSkillRepository;

/**
 * Service layer for managing helper operations.
 * Provides CRUD functionality for Helper entities.
 */

@Service
public class HelperSkillService {
    
    private final HelperSkillRepository helperSkillRepository;

    public HelperSkillService(HelperSkillRepository helperSkillRepository) {
        this.helperSkillRepository = helperSkillRepository;
    }

    public List<HelperSkill> getAllHelpersSkills()
    {
        return helperSkillRepository.findAll();
    }

    public HelperSkill getHelperSkillById(int id)
    {
        return helperSkillRepository.findById(id).orElse(null);
    }

    public HelperSkill saveHelperSkill(HelperSkill helperSkill)
    {
        if(helperSkill == null)
        {
            return null;
        }
        return helperSkillRepository.save(helperSkill);
    }

    public HelperSkill updateHelperSkill(int id, HelperSkill updated)
    {
        HelperSkill existing = helperSkillRepository.findById(id).orElse(null);
        if(existing == null)
        {
            return null;
        }
        existing.setHelperId(updated.getHelperId());
        existing.setTaskTypeId(updated.getTaskTypeId());
        return helperSkillRepository.save(existing);
    }

    public boolean deleteHelperSkill(int id)
    {
        if(!helperSkillRepository.existsById(id))
        {
            return false;
        }

        helperSkillRepository.deleteById(id);
        return true;
    }
}

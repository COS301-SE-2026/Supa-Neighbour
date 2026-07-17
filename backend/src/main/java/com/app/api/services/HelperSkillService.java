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
    
    /**
     * Repository for HelperSkill entities.
     */
    private final HelperSkillRepository helperSkillRepository;


    /**
     * Constructs a HelperSkillService with the specified repository.
     *
     * @param helperSkillRepository the repository for HelperSkill entities
     */
    public HelperSkillService(HelperSkillRepository helperSkillRepository) {
        this.helperSkillRepository = helperSkillRepository;
    }


    /**
     * Retrieves all helper skill records from the repository.
     *
     * @return a list of all helper skill records
     */
    public List<HelperSkill> getAllHelpersSkills(){
        return helperSkillRepository.findAll();
    }

    /**
     * Retrieves a helper skill record by its identifier.
     *
     * @param id the helper skill identifier
     * @return the helper skill record if found, or null if no record exists with the given id
     */
    public HelperSkill getHelperSkillById(int id){
        return helperSkillRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new helper skill record to the repository.
     *
     * @param helperSkill the helper skill record to save
     * @return the saved helper skill record, or null if the provided record is null
     */
    public HelperSkill saveHelperSkill(HelperSkill helperSkill){
        if(helperSkill == null){
            return null;
        }
        return helperSkillRepository.save(helperSkill);
    }

    /**
     * Updates an existing helper skill record with the provided details.
     *
     * @param id      the identifier of the helper skill record to update
     * @param updated the helper skill object containing the updated fields
     * @return the updated helper skill record, or null if no record exists with the given id
     */
    public HelperSkill updateHelperSkill(int id, HelperSkill updated){
        HelperSkill existing = helperSkillRepository.findById(id).orElse(null);
        if(existing == null){
            return null;
        }
        existing.setHelperId(updated.getHelperId());
        existing.setTaskTypeId(updated.getTaskTypeId());
        return helperSkillRepository.save(existing);
    }

    /**
     * Deletes a helper skill record by its identifier.
     *
     * @param id the identifier of the helper skill record to delete
     * @return {@code true} if a record with the given id existed and was deleted,
     *         {@code false} if no record exists with the given id
     */
    public boolean deleteHelperSkill(int id){
        if(!helperSkillRepository.existsById(id)){
            return false;
        }

        helperSkillRepository.deleteById(id);
        return true;
    }
}

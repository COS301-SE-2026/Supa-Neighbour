package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Compatibility;
import com.app.api.repositories.CompatibilityRepository;

/**
 * Service layer for managing compatibility operations.
 * Provides CRUD functionality for Compatibility entities.
 */
@Service
public class CompatibilityService {

    @Autowired
    private CompatibilityRepository compatibilityRepository;

    // Get all
    /**
     * Retrieves all compatibility records from the repository.
     *
     * @return a list of all compatibility records
     */
    public List<Compatibility> getAllCompatibility() {
        return compatibilityRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a compatibility record by its identifier.
     *
     * @param id the compatibility identifier
     * @return the compatibility record if found, or null if no record exists with the given id
     */
    public Compatibility getCompatibilityById(int id) {
        return compatibilityRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new compatibility record to the repository.
     *
     * @param compatibility the compatibility record to save
     * @return the saved compatibility record, or null if the provided compatibility is null
     */
    public Compatibility saveCompatibility(Compatibility compatibility) {
        if(compatibility == null) {
            return null;
        }
        return compatibilityRepository.save(compatibility);
    }

    // Update
    /**
     * Updates an existing compatibility record with the provided details.
     *
     * @param id      the identifier of the compatibility record to update
     * @param updated the compatibility object containing the updated fields
     * @return the updated compatibility record, or null if no record exists with the given id
     */
    public Compatibility updateCompatibility(int id, Compatibility updated) {
        Compatibility existing = compatibilityRepository.findById(id).orElse(null);

        if (existing == null){
             return null;
        }
        
        existing.setCompatibilityScore(updated.getCompatibilityScore());
        existing.setCompatibilityColour(updated.getCompatibilityColour());
        existing.setDependentid(updated.getDependentid());
        existing.setHelperid(updated.getHelperid());

        return compatibilityRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a compatibility record by its identifier.
     *
     * @param id the identifier of the compatibility record to delete
     */
    public void deleteCompatibility(int id) {
        compatibilityRepository.deleteById(id);
    }
}

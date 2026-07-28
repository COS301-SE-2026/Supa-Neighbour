package com.app.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.api.models.Dependent;
import com.app.api.repositories.DependentRepository;

/**
 * Service layer for managing dependent operations.
 * Provides CRUD functionality for Dependent entities.
 */
@Service
public class DependentService {

    private final DependentRepository dependentRepository;

    /**
     * Constructs the services with its required repository dependency.
     *
     * @param dependentRepository repository providing analytics data for posts
     */
    public DependentService(DependentRepository dependentRepository) {
        this.dependentRepository = dependentRepository;
    }

    // Get all
    /**
     * Retrieves all dependents from the repository.
     *
     * @return a list of all dependents
     */
    public List<Dependent> getAllDependents() {
        return dependentRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a dependent by their identifier.
     *
     * @param id the dependent identifier
     * @return the dependent if found, or null if no dependent exists with the given id
     */
    public Dependent getDependentById(int id) {
        return dependentRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new dependent to the repository.
     *
     * @param dependent the dependent to save
     * @return the saved dependent, or null if the provided dependent is null
     */
    public Dependent saveDependent(Dependent dependent) {
        if(dependent == null){
             return null;
        }
        return dependentRepository.save(dependent);
    }

    // Update
    /**
     * Updates an existing dependent with the provided details.
     *
     * @param id      the identifier of the dependent to update
     * @param updated the dependent object containing the updated fields
     * @return the updated dependent, or null if no dependent exists with the given id
     */
    public Dependent updateDependent(int id, Dependent updated) {
        Dependent existing = dependentRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }
        
        existing.setUserId(updated.getUserId());
        existing.setTaskTypeId(updated.getTaskTypeId());

        return dependentRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a dependent by their identifier.
     *
     * @param id the identifier of the dependent to delete
     */
    public void deleteDependent(int id) {
        dependentRepository.deleteById(id);
    }
}

package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Dependent;
import com.app.api.repositories.DependentRepository;

/**
 * Service layer for managing dependent operations.
 * Provides CRUD functionality for Dependent entities.
 */
@Service
public class DependentService {

    @Autowired
    private DependentRepository dependentRepository;

    /**
     * Retrieves all dependents from the repository.
     *
     * @return a list of all dependents
     */
    public List<Dependent> getAllDependents() {
        return dependentRepository.findAll();
    }

    /**
     * Retrieves a dependent by their identifier.
     *
     * @param id the dependent identifier
     * @return the dependent if found, or null if no dependent exists with the given id
     */
    public Dependent getDependentById(int id) {
        return dependentRepository.findById(id).orElse(null);
    }

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
        existing.setUserid(updated.getUserid());
        existing.setTaskTypeid(updated.getTaskTypeid());

        return dependentRepository.save(existing);
    }

    /**
     * Deletes a dependent by their identifier.
     *
     * @param id the identifier of the dependent to delete
     */
    public void deleteDependent(int id) {
        dependentRepository.deleteById(id);
    }
}

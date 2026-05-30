package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Dependent;
import com.app.api.repositories.DependentRepository;

/**
 * Dependent service.
 */
@Service
public class DependentService {

    @Autowired
    private DependentRepository dependentRepository;

    /**
     * Get all dependents.
     * @return list of dependents
     */
    public List<Dependent> getAllDependents() {
        return dependentRepository.findAll();
    }

    /**
     * Get dependent by id.
     * @param id dependent id
     * @return dependent
     */
    public Dependent getDependentById(int id) {
        return dependentRepository.findById(id).orElse(null);
    }

    /**
     * Save dependent.
     * @param dependent dependent
     * @return saved dependent  
     */
    public Dependent saveDependent(Dependent dependent) {
        return dependentRepository.save(dependent);
    }
}

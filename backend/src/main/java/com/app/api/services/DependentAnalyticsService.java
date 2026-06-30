package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.DependentAnalytics;
import com.app.api.repositories.DependentAnalyticsRepository;

/**
 * Service layer for managing dependent analytics operations.
 * Provides CRUD functionality for DependentAnalytics entities.
 */
@Service
public class DependentAnalyticsService {

    @Autowired
    private DependentAnalyticsRepository dependentAnalyticsRepository;

    // Get all
    /**
     * Retrieves all dependent analytics records from the repository.
     *
     * @return a list of all dependent analytics records
     */
    public List<DependentAnalytics> getAllDependentAnalytics() {
        return dependentAnalyticsRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a dependent analytics record by its identifier.
     *
     * @param id the dependent analytics identifier
     * @return the dependent analytics record if found, or null if the id is null
     *         or no record exists with the given id
     */
    public DependentAnalytics getDependentAnalyticsById(String id) {
        if (id == null) {
            return null;    
        }
        return dependentAnalyticsRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new dependent analytics record to the repository.
     *
     * @param dependentAnalytics the dependent analytics record to save
     * @return the saved dependent analytics record, or null if the provided record is null
     */
    public DependentAnalytics saveDependentAnalytics(DependentAnalytics dependentAnalytics) {
        if(dependentAnalytics == null) {
            return null;
        }

        return dependentAnalyticsRepository.save(dependentAnalytics);
    }

    // Update
    /**
     * Updates an existing dependent analytics record with the provided details.
     *
     * @param id      the identifier of the dependent analytics record to update
     * @param updated the dependent analytics object containing the updated fields
     * @return the updated dependent analytics record, or null if the id or updated
     *         object is null, or no record exists with the given id
     */
    public DependentAnalytics updateDependentAnalytics(String id, DependentAnalytics updated) {
        if(id == null || updated == null) {
            return null;
        }

        DependentAnalytics existing = dependentAnalyticsRepository.findById(id).orElse(null);
    
        if (existing == null){
             return null;
        }

        existing.setUserid(updated.getUserid());
        existing.setTasktypeid(updated.getTasktypeid());
        existing.setAveeragerating(updated.getAveeragerating());
        existing.setLocationid(updated.getLocationid());
        existing.setAveragegivingrating(updated.getAveragegivingrating());
        existing.setTotaltasks(updated.getTotaltasks());


        return dependentAnalyticsRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a dependent analytics record by its identifier.
     *
     * @param id the identifier of the dependent analytics record to delete
     */
    public void deleteDependentAnalytics(String id) {
        if (id == null) {
            return;
        }
        
        dependentAnalyticsRepository.deleteById(id);
    }
}

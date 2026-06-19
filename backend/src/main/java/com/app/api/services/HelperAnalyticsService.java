package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.HelperAnalytics;
import com.app.api.repositories.HelperAnalyticsRepository;

/**
 * Service layer for managing helper analytics operations.
 * Provides CRUD functionality for HelperAnalytics entities.
 */
@Service
public class HelperAnalyticsService {

    @Autowired
    private HelperAnalyticsRepository helperAnalyticsRepository;

    /**
     * Retrieves all helper analytics records from the repository.
     *
     * @return a list of all helper analytics records
     */
    public List<HelperAnalytics> getAllHelperAnalytics() {
        return helperAnalyticsRepository.findAll();
    }

    /**
     * Retrieves a helper analytics record by its identifier.
     *
     * @param id the helper analytics identifier
     * @return the helper analytics record if found, or null if the id is null
     *         or no record exists with the given id
     */
    public HelperAnalytics getHelperAnalyticsById(String id) {
        if(id == null) {
            return null;
        }
        return helperAnalyticsRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new helper analytics record to the repository.
     *
     * @param helperAnalytics the helper analytics record to save
     * @return the saved helper analytics record, or null if the provided record is null
     */
    public HelperAnalytics saveHelperAnalytics(HelperAnalytics helperAnalytics) {
        if(helperAnalytics == null) {
            return null;
        }
        return helperAnalyticsRepository.save(helperAnalytics);
    }

    /**
     * Updates an existing helper analytics record with the provided details.
     *
     * @param id      the identifier of the helper analytics record to update
     * @param updated the helper analytics object containing the updated fields
     * @return the updated helper analytics record, or null if the id or updated
     *         object is null, or no record exists with the given id
     */
    public HelperAnalytics updateHelperAnalytics(String id, HelperAnalytics updated) {
        if(id == null || updated == null) {
            return null;
        }
        HelperAnalytics existing = helperAnalyticsRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setHelperid(updated.getUserid());
        existing.setTasktypeid(updated.getTasktypeid());
        existing.setAverageGivingRating(updated.getAverageGivingRating());
        existing.setAverageRating(updated.getAverageRating());
        existing.setCompatibilityid(updated.getCompatibilityid());
        existing.setLocationid(updated.getLocationid());


        return helperAnalyticsRepository.save(existing);
    }

    /**
     * Deletes a helper analytics record by its identifier.
     *
     * @param id the identifier of the helper analytics record to delete
     */
    public void deleteHelperAnalytics(String id) {
        if(id == null){ 
            return;
        }
        helperAnalyticsRepository.deleteById(id);
    }
}

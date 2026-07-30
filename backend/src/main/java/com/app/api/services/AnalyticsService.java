package com.app.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.api.models.Analytics;
import com.app.api.repositories.AnalyticsRepository;

/**
 * Service layer for managing analytics operations.
 * Provides CRUD functionality for Analytics entities.
 */
@Service
public class AnalyticsService {

    
    private AnalyticsRepository analyticsRepository;

    /**
     * Constructs the repository with its required repository dependency.
     *
     * @param analyticsRepository repository providing analytics data for analytics
     */
    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    // Get all
    /**
     * Retrieves all analytics records from the repository.
     *
     * @return a list of all analytics records
     */
    public List<Analytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves an analytics record by its identifier.
     *
     * @param id the analytics identifier
     * @return the analytics record if found, or null if no record exists with the given id
     */
    public Analytics getAnalyticsById(int id) {
        return analyticsRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new analytics record to the repository.
     *
     * @param analytics the analytics record to save
     * @return the saved analytics record, or null if the provided analytics is null
     */
    public Analytics saveAnalytics(Analytics analytics) {
        if(analytics == null) {
            return null;
        }
        return analyticsRepository.save(analytics);
    }

    // Update
    /**
     * Updates an existing analytics record with the provided details.
     *
     * @param id      the identifier of the analytics record to update
     * @param updated the analytics object containing the updated fields
     * @return the updated analytics record, or null if no record exists with the given id
     */
    public Analytics updateAnalytics(int id, Analytics updated) {
        Analytics existing = analyticsRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setTaskid(updated.getTaskid());
        existing.setAdminid(updated.getAdminid());
        existing.setHelpertypeid(updated.getHelpertypeid());
        existing.setDependenttypeid(updated.getDependenttypeid());

        return analyticsRepository.save(existing);
    }

    // Delete
    /**
     * Deletes an analytics record by its identifier.
     *
     * @param id the identifier of the analytics record to delete
     */
    public void deleteAnalytics(int id) {
        analyticsRepository.deleteById(id);
    }
}

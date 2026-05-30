package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.DependentAnalytics;
import com.app.api.repositories.DependentAnalyticsRepository;

/**
 * Dependent analytics service.
 */
@Service
public class DependentAnalyticsService {

    @Autowired
    private DependentAnalyticsRepository dependentAnalyticsRepository;

    /**
     * Get all dependent analytics.
     * @return list of dependent analytics
     */
    public List<DependentAnalytics> getAllDependentAnalytics() {
        return dependentAnalyticsRepository.findAll();
    }

    /**
     * Get dependent analytics by id.
     * @param id dependent analytics id
     * @return dependent analytics
     */
    public DependentAnalytics getDependentAnalyticsById(String id) {
        return dependentAnalyticsRepository.findById(id).orElse(null);
    }

    /**
     * Save dependent analytics.
     * @param dependentAnalytics dependent analytics
     * @return saved dependent analytics
     */
    public DependentAnalytics saveDependentAnalytics(DependentAnalytics dependentAnalytics) {
        return dependentAnalyticsRepository.save(dependentAnalytics);
    }
}
package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.HelperAnalytics;
import com.app.api.repositories.HelperAnalyticsRepository;

/**
 * HelperAnalytics service.
 */
@Service
public class HelperAnalyticsService {

    @Autowired
    private HelperAnalyticsRepository helperAnalyticsRepository;

    /**
     * Get all helper analytics.
     * @return list of helper analytics
     */
    public Iterable<HelperAnalytics> getAllHelperAnalytics() {
        return helperAnalyticsRepository.findAll();
    }

    /**
     * Get helper analytics by id.
     * @param id helper analytics id
     * @return helper analytics
     */
    public HelperAnalytics getHelperAnalyticsById(int id) {
        return helperAnalyticsRepository.findById(id).orElse(null);
    }

    /**
     * Save helper analytics.
     * @param helperAnalytics helper analytics
     * @return saved helper analytics
     */
    public HelperAnalytics saveHelperAnalytics(HelperAnalytics helperAnalytics) {
        return helperAnalyticsRepository.save(helperAnalytics);
    }
}
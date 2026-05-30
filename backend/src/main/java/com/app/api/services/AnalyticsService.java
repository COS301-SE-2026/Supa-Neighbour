package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Admin;
import com.app.api.models.Analytics;
import com.app.api.repositories.AnalyticsRepository;

/**
 * Analytics service.
 */
@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    /**
     * Get all analytics.
     * @return list of analytics
     */
    public List<Analytics> getAllAdmins() {
        return analyticsRepository.findAll();
    }

    /**
     * Get analytics by id.
     * @param id analytics id
     * @return analytics
     */
    public Analytics getAdminById(int id) {
        return analyticsRepository.findById(id).orElse(null);
    }

    /**
     * Save analytics.
     * @param analytics analytics
     * @return saved analytics
     */
    public Analytics saveAdmin(Analytics admin) {
        return analyticsRepository.save(admin);
    }
}

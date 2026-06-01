package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.HelperAnalytics;
import com.app.api.repositories.HelperAnalyticsRepository;

@Service
public class HelperAnalyticsService {

    @Autowired
    private HelperAnalyticsRepository helperAnalyticsRepository;

    // Get all
    public List<HelperAnalytics> getAllHelperAnalytics() {
        return helperAnalyticsRepository.findAll();
    }

    // Get by id
    public HelperAnalytics getHelperAnalyticsById(String id) {
        if(id == null) return null;
        return helperAnalyticsRepository.findById(id).orElse(null);
    }

    // Create
    public HelperAnalytics saveHelperAnalytics(HelperAnalytics helperAnalytics) {
        if(helperAnalytics == null) return null;
        return helperAnalyticsRepository.save(helperAnalytics);
    }

    // Update
    public HelperAnalytics updateHelperAnalytics(String id, HelperAnalytics updated) {
        if(id == null || updated == null) return null;
        HelperAnalytics existing = helperAnalyticsRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUserid(updated.getUserid());
        existing.setTasktypeid(updated.getTasktypeid());
        existing.setAverageGivingRating(updated.getAverageGivingRating());
        existing.setAverageRating(updated.getAverageRating());
        existing.setCompatibilityid(updated.getCompatibilityid());
        existing.setLocationid(updated.getLocationid());


        return helperAnalyticsRepository.save(existing);
    }

    // Delete
    public void deleteHelperAnalytics(String id) {
        if(id == null) return;
        helperAnalyticsRepository.deleteById(id);
    }
}
package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Analytics;
import com.app.api.repositories.AnalyticsRepository;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    // Get all
    public List<Analytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }

    // Get by id
    public Analytics getAnalyticsById(int id) {
        return analyticsRepository.findById(id).orElse(null);
    }

    // Create
    public Analytics saveAnalytics(Analytics analytics) {
        if(analytics == null) return null;
        return analyticsRepository.save(analytics);
    }

    // Update
    public Analytics updateAnalytics(int id, Analytics updated) {
        Analytics existing = analyticsRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setTaskid(updated.getTaskid());
        existing.setAdminid(updated.getAdminid());
        existing.setHelpertypeid(updated.getHelpertypeid());
        existing.setDependenttypeid(updated.getDependenttypeid());

        return analyticsRepository.save(existing);
    }

    // Delete
    public void deleteAnalytics(int id) {
        analyticsRepository.deleteById(id);
    }
}
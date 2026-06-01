package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.DependentAnalytics;
import com.app.api.repositories.DependentAnalyticsRepository;

@Service
public class DependentAnalyticsService {

    @Autowired
    private DependentAnalyticsRepository dependentAnalyticsRepository;

    // Get all
    public List<DependentAnalytics> getAllDependentAnalytics() {
        return dependentAnalyticsRepository.findAll();
    }

    // Get by id
    public DependentAnalytics getDependentAnalyticsById(String id) {
        return dependentAnalyticsRepository.findById(id).orElse(null);
    }

    // Create
    public DependentAnalytics saveDependentAnalytics(DependentAnalytics dependentAnalytics) {
        return dependentAnalyticsRepository.save(dependentAnalytics);
    }

    // Update
    public DependentAnalytics updateDependentAnalytics(String id, DependentAnalytics updated) {
        DependentAnalytics existing = dependentAnalyticsRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setUserid(updated.getUserid());
        existing.setTasktypeid(updated.getTasktypeid());
        existing.setAveeragerating(updated.getAveeragerating());
        existing.setLocationid(updated.getLocationid());
        existing.setAveragegivingrating(updated.getAveragegivingrating());
        existing.setTotaltasks(updated.getTotaltasks());


        return dependentAnalyticsRepository.save(existing);
    }

    // Delete
    public void deleteDependentAnalytics(String id) {
        
        dependentAnalyticsRepository.deleteById(id);
    }
}
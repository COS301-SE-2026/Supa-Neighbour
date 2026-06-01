package com.app.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.app.api.models.DependentAnalytics;
import com.app.api.services.DependentAnalyticsService;

/**
 * Dependent analytics controller.
 */
@RestController
@RequestMapping("/api/dependent-analytics")
public class DependentAnalyticsController {

    @Autowired
    private DependentAnalyticsService dependentAnalyticsService;

    /**
     * Get all dependent analytics.
     * @return dependent analytics
     */
    @GetMapping
    public List<DependentAnalytics> getAllDependentAnalytics() {
        return dependentAnalyticsService.getAllDependentAnalytics();
    }

    /**
     * Get dependent analytics by id.
     * @param id dependent analytics id
     * @return dependent analytics
     */
    @GetMapping("api/dependent-analytics/{id}")
    public DependentAnalytics getDependentAnalyticsById(@PathVariable String id) {
        return dependentAnalyticsService.getDependentAnalyticsById(id);
    }

    /**
     * Create dependent analytics.
     * @param dependentAnalytics dependent analytics
     * @return saved dependent analytics
     */
    @PostMapping
    public DependentAnalytics createDependentAnalytics(@RequestBody DependentAnalytics dependentAnalytics) {
        return dependentAnalyticsService.saveDependentAnalytics(dependentAnalytics);
    }
}
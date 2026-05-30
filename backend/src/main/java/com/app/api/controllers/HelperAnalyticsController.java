package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.HelperAnalytics;
import com.app.api.services.HelperAnalyticsService;

/**
 * HelperAnalytics controller.
 */
@RestController
@RequestMapping("api/helper-analytics")
public class HelperAnalyticsController {

    @Autowired
    private HelperAnalyticsService helperAnalyticsService;

    /**
     * Get all helper analytics.
     * @return helper analytics
     */
    @GetMapping
    public List<HelperAnalytics> getAllHelperAnalytics() {
        return helperAnalyticsService.getAllHelperAnalytics();
    }

    /**
     * Get helper analytics by id.
     * @param id helper analytics id
     * @return helper analytics
     */
    @GetMapping("api/helper-analytics/{id}")
    public HelperAnalytics getHelperAnalyticsById(@PathVariable int id) {
        return helperAnalyticsService.getHelperAnalyticsById(id);
    }

    /**
     * Create helper analytics.
     * @param helperAnalytics helper analytics
     * @return saved helper analytics
     */
    @PostMapping
    public HelperAnalytics createHelperAnalytics(@RequestBody HelperAnalytics helperAnalytics) {
        return helperAnalyticsService.saveHelperAnalytics(helperAnalytics);
    }
}
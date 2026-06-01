package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.DependentAnalytics;
import com.app.api.services.DependentAnalyticsService;

@RestController
@RequestMapping("/api/dependent-analytics")
public class DependentAnalyticsController {

    @Autowired
    private DependentAnalyticsService dependentAnalyticsService;

    // GET /api/dependent-analytics
    @GetMapping
    public ResponseEntity<List<DependentAnalytics>> getAllDependentAnalytics() {
        return ResponseEntity.ok(dependentAnalyticsService.getAllDependentAnalytics());
    }

    // GET /api/dependent-analytics/1
    @GetMapping("/{id}")
    public ResponseEntity<DependentAnalytics> getDependentAnalyticsById(@PathVariable String id) {
        DependentAnalytics dependentAnalytics = dependentAnalyticsService.getDependentAnalyticsById(id);
        if (dependentAnalytics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dependentAnalytics);
    }

    // POST /api/dependent-analytics
    @PostMapping
    public ResponseEntity<DependentAnalytics> createDependentAnalytics(@RequestBody DependentAnalytics dependentAnalytics) {
        DependentAnalytics saved = dependentAnalyticsService.saveDependentAnalytics(dependentAnalytics);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/dependent-analytics/1
    @PutMapping("/{id}")
    public ResponseEntity<DependentAnalytics> updateDependentAnalytics(@PathVariable String id, @RequestBody DependentAnalytics dependentAnalytics) {
        DependentAnalytics existing = dependentAnalyticsService.getDependentAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        DependentAnalytics updated = dependentAnalyticsService.updateDependentAnalytics(id, dependentAnalytics);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/dependent-analytics/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDependentAnalytics(@PathVariable String id) {
        DependentAnalytics existing = dependentAnalyticsService.getDependentAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        dependentAnalyticsService.deleteDependentAnalytics(id);
        return ResponseEntity.noContent().build();
    }
}
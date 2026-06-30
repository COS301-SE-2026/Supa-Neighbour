package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.DependentAnalytics;
import com.app.api.services.DependentAnalyticsService;

@RestController
@RequestMapping("/api/dependent-analytics")
public class DependentAnalyticsController {

    @Autowired
    private DependentAnalyticsService dependentAnalyticsService;

    // GET /api/dependent-analytics
    /**
     * Retrieves all dependent analytics records.
     *
     * @return a response containing the list of dependent analytics records
     */
    @GetMapping
    public ResponseEntity<List<DependentAnalytics>> getAllDependentAnalytics() {
        return ResponseEntity.ok(dependentAnalyticsService.getAllDependentAnalytics());
    }

    // GET /api/dependent-analytics/1
    /**
     * Retrieves a dependent analytics record by its ID.
     *
     * @param id the dependent analytics ID
     * @return a response containing the dependent analytics record, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<DependentAnalytics> getDependentAnalyticsById(@PathVariable String id) {
        DependentAnalytics dependentAnalytics = dependentAnalyticsService.getDependentAnalyticsById(id);
        if (dependentAnalytics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dependentAnalytics);
    }

    // POST /api/dependent-analytics
    /**
     * Creates a new dependent analytics record.
     *
     * @param dependentAnalytics the dependent analytics data to create
     * @return a response containing the created dependent analytics record
     */
    @PostMapping
    public ResponseEntity<DependentAnalytics> createDependentAnalytics(@RequestBody DependentAnalytics dependentAnalytics) {
        DependentAnalytics saved = dependentAnalyticsService.saveDependentAnalytics(dependentAnalytics);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/dependent-analytics/1
    /**
     * Updates an existing dependent analytics record.
     *
     * @param id the dependent analytics ID
     * @param dependentAnalytics the updated dependent analytics data
     * @return a response containing the updated dependent analytics record, or 404 if not found
     */
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
    /**
     * Deletes a dependent analytics record by its ID.
     *
     * @param id the dependent analytics ID
     * @return a response with no content, or 404 if not found
     */
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

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

import com.app.api.models.Analytics;
import com.app.api.services.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // GET /api/analytics
    /**
     * Retrieves all analytics records.
     *
     * @return a response containing the list of analytics records
     */
    @GetMapping
    public ResponseEntity<List<Analytics>> getAllAnalytics() {
        return ResponseEntity.ok(analyticsService.getAllAnalytics());
    }

    // GET /api/analytics/1
    /**
     * Retrieves an analytics record by its ID.
     *
     * @param id the analytics ID
     * @return a response containing the analytics record, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Analytics> getAnalyticsById(@PathVariable int id) {
        Analytics analytics = analyticsService.getAnalyticsById(id);
        if (analytics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analytics);
    }

    // POST /api/analytics
    /**
     * Creates a new analytics record.
     *
     * @param analytics the analytics data to create
     * @return a response containing the created analytics record
     */
    @PostMapping
    public ResponseEntity<Analytics> createAnalytics(@RequestBody Analytics analytics) {
        Analytics saved = analyticsService.saveAnalytics(analytics);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/analytics/1
    /**
     * Updates an existing analytics record.
     *
     * @param id the analytics ID
     * @param analytics the updated analytics data
     * @return a response containing the updated analytics record, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Analytics> updateAnalytics(@PathVariable int id, @RequestBody Analytics analytics) {
        Analytics existing = analyticsService.getAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Analytics updated = analyticsService.updateAnalytics(id, analytics);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/analytics/1
    /**
     * Deletes an analytics record by its ID.
     *
     * @param id the analytics ID
     * @return a response with no content, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalytics(@PathVariable int id) {
        Analytics existing = analyticsService.getAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        analyticsService.deleteAnalytics(id);
        return ResponseEntity.noContent().build();
    }
}

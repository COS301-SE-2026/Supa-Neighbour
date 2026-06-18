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

import com.app.api.models.HelperAnalytics;
import com.app.api.services.HelperAnalyticsService;

@RestController
@RequestMapping("/api/helper-analytics")
public class HelperAnalyticsController {

    @Autowired
    private HelperAnalyticsService helperAnalyticsService;

    /**
     * Retrieves all helper analytics records.
     *
     * @return a response containing the list of helper analytics records
     */
    @GetMapping
    public ResponseEntity<List<HelperAnalytics>> getAllHelperAnalytics() {
        return ResponseEntity.ok(helperAnalyticsService.getAllHelperAnalytics());
    }

    /**
     * Retrieves a helper analytics record by its ID.
     *
     * @param id the helper analytics ID
     * @return a response containing the helper analytics record, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<HelperAnalytics> getHelperAnalyticsById(@PathVariable String id) {
        HelperAnalytics helperAnalytics = helperAnalyticsService.getHelperAnalyticsById(id);
        if (helperAnalytics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(helperAnalytics);
    }

    /**
     * Creates a new helper analytics record.
     *
     * @param helperAnalytics the helper analytics data to create
     * @return a response containing the created helper analytics record
     */
    @PostMapping
    public ResponseEntity<HelperAnalytics> createHelperAnalytics(@RequestBody HelperAnalytics helperAnalytics) {
        HelperAnalytics saved = helperAnalyticsService.saveHelperAnalytics(helperAnalytics);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing helper analytics record.
     *
     * @param id the helper analytics ID
     * @param helperAnalytics the updated helper analytics data
     * @return a response containing the updated helper analytics record, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<HelperAnalytics> updateHelperAnalytics(@PathVariable String id, @RequestBody HelperAnalytics helperAnalytics) {
        HelperAnalytics existing = helperAnalyticsService.getHelperAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        HelperAnalytics updated = helperAnalyticsService.updateHelperAnalytics(id, helperAnalytics);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a helper analytics record by its ID.
     *
     * @param id the helper analytics ID
     * @return a response with no content, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHelperAnalytics(@PathVariable String id) {
        HelperAnalytics existing = helperAnalyticsService.getHelperAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        helperAnalyticsService.deleteHelperAnalytics(id);
        return ResponseEntity.noContent().build();
    }
}

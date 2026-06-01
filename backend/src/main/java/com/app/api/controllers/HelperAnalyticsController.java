package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.HelperAnalytics;
import com.app.api.services.HelperAnalyticsService;

@RestController
@RequestMapping("/api/helper-analytics")
public class HelperAnalyticsController {

    @Autowired
    private HelperAnalyticsService helperAnalyticsService;

    // GET /api/helper-analytics
    @GetMapping
    public ResponseEntity<List<HelperAnalytics>> getAllHelperAnalytics() {
        return ResponseEntity.ok(helperAnalyticsService.getAllHelperAnalytics());
    }

    // GET /api/helper-analytics/1
    @GetMapping("/{id}")
    public ResponseEntity<HelperAnalytics> getHelperAnalyticsById(@PathVariable String id) {
        HelperAnalytics helperAnalytics = helperAnalyticsService.getHelperAnalyticsById(id);
        if (helperAnalytics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(helperAnalytics);
    }

    // POST /api/helper-analytics
    @PostMapping
    public ResponseEntity<HelperAnalytics> createHelperAnalytics(@RequestBody HelperAnalytics helperAnalytics) {
        HelperAnalytics saved = helperAnalyticsService.saveHelperAnalytics(helperAnalytics);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/helper-analytics/1
    @PutMapping("/{id}")
    public ResponseEntity<HelperAnalytics> updateHelperAnalytics(@PathVariable String id, @RequestBody HelperAnalytics helperAnalytics) {
        HelperAnalytics existing = helperAnalyticsService.getHelperAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        HelperAnalytics updated = helperAnalyticsService.updateHelperAnalytics(id, helperAnalytics);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/helper-analytics/1
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
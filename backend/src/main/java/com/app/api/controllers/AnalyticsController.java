package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Analytics;
import com.app.api.services.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // GET /api/analytics
    @GetMapping
    public ResponseEntity<List<Analytics>> getAllAnalytics() {
        return ResponseEntity.ok(analyticsService.getAllAnalytics());
    }

    // GET /api/analytics/1
    @GetMapping("/{id}")
    public ResponseEntity<Analytics> getAnalyticsById(@PathVariable int id) {
        Analytics analytics = analyticsService.getAnalyticsById(id);
        if (analytics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analytics);
    }

    // POST /api/analytics
    @PostMapping
    public ResponseEntity<Analytics> createAnalytics(@RequestBody Analytics analytics) {
        Analytics saved = analyticsService.saveAnalytics(analytics);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/analytics/1
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
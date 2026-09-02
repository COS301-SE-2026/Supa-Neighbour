package com.app.api.controllers;

import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Operations for managing analytics records")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Basic Analytics Controller Constructor
     * @param analyticsService service for the analytics 
     */
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // GET /api/analytics
    /**
     * Retrieves all analytics records.
     *
     * @return a response containing the list of analytics records
     */
    @GetMapping
    @Operation(summary = "Get all analytics records", description = "Retrieves a list of all analytics records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved analytics records")
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
    @Operation(summary = "Get analytics by ID", description = "Retrieves a single analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics record found"),
        @ApiResponse(responseCode = "404", description = "Analytics record not found", content = @Content)
    })
    public ResponseEntity<Analytics> getAnalyticsById(
        @Parameter(description = "ID of the analytics record to retrieve", example = "1")
        @PathVariable int id
    ) {
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
    @Operation(summary = "Create a new analytics record", description = "Creates a new analytics record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Analytics record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid analytics data", content = @Content)
    })
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
    @Operation(summary = "Update an analytics record", description = "Updates an existing analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics record updated successfully"),
        @ApiResponse(responseCode = "404", description = "Analytics record not found", content = @Content)
    })
    public ResponseEntity<Analytics> updateAnalytics(
        @Parameter(description = "ID of the analytics record to update", example = "1")
        @PathVariable int id,
        @RequestBody Analytics analytics
    ) {
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
    @Operation(summary = "Delete an analytics record", description = "Deletes an analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Analytics record deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Analytics record not found", content = @Content)
    })
    public ResponseEntity<Void> deleteAnalytics(
        @Parameter(description = "ID of the analytics record to delete", example = "1")
        @PathVariable int id
    ) {
        Analytics existing = analyticsService.getAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        analyticsService.deleteAnalytics(id);
        return ResponseEntity.noContent().build();
    }
}

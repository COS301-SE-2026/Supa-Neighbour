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

import com.app.api.models.HelperAnalytics;
import com.app.api.services.HelperAnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HelperAnalyticsController
 */
@RestController
@RequestMapping("/api/helper-analytics")
@Tag(name = "Helper Analytics", description = "Operations for managing helper analytics records")
public class HelperAnalyticsController {

    private final HelperAnalyticsService helperAnalyticsService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param helperAnalyticsService service providing analytics data for helpers
     */
    public HelperAnalyticsController(HelperAnalyticsService helperAnalyticsService) {
        this.helperAnalyticsService = helperAnalyticsService;
    }

    // GET /api/helper-analytics
    /**
     * Retrieves all helper analytics records.
     *
     * @return a response containing the list of helper analytics records
     */
    @GetMapping
    @Operation(summary = "Get all helper analytics", description = "Retrieves a list of all helper analytics records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved helper analytics records")
    public ResponseEntity<List<HelperAnalytics>> getAllHelperAnalytics() {
        return ResponseEntity.ok(helperAnalyticsService.getAllHelperAnalytics());
    }

    // GET /api/helper-analytics/1
    /**
     * Retrieves a helper analytics record by its ID.
     *
     * @param id the helper analytics ID
     * @return a response containing the helper analytics record, or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get helper analytics by ID", description = "Retrieves a single helper analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Helper analytics record found"),
        @ApiResponse(responseCode = "404", description = "Helper analytics record not found", content = @Content)
    })
    public ResponseEntity<HelperAnalytics> getHelperAnalyticsById(
        @Parameter(description = "ID of the helper analytics record to retrieve", example = "HELPER_001")
        @PathVariable String id
    ) {
        HelperAnalytics helperAnalytics = helperAnalyticsService.getHelperAnalyticsById(id);
        if (helperAnalytics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(helperAnalytics);
    }

    // POST /api/helper-analytics
    /**
     * Creates a new helper analytics record.
     *
     * @param helperAnalytics the helper analytics data to create
     * @return a response containing the created helper analytics record
     */
    @PostMapping
    @Operation(summary = "Create a new helper analytics record", description = "Creates a new helper analytics record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Helper analytics record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid helper analytics data", content = @Content)
    })
    public ResponseEntity<HelperAnalytics> createHelperAnalytics(@RequestBody HelperAnalytics helperAnalytics) {
        HelperAnalytics saved = helperAnalyticsService.saveHelperAnalytics(helperAnalytics);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/helper-analytics/1
    /**
     * Updates an existing helper analytics record.
     *
     * @param id the helper analytics ID
     * @param helperAnalytics the updated helper analytics data
     * @return a response containing the updated helper analytics record, or 404 if not found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a helper analytics record", description = "Updates an existing helper analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Helper analytics record updated successfully"),
        @ApiResponse(responseCode = "404", description = "Helper analytics record not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid helper analytics data", content = @Content)
    })
    public ResponseEntity<HelperAnalytics> updateHelperAnalytics(
        @Parameter(description = "ID of the helper analytics record to update", example = "HELPER_001")
        @PathVariable String id,
        @RequestBody HelperAnalytics helperAnalytics
    ) {
        HelperAnalytics existing = helperAnalyticsService.getHelperAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        HelperAnalytics updated = helperAnalyticsService.updateHelperAnalytics(id, helperAnalytics);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/helper-analytics/1
    /**
     * Deletes a helper analytics record by its ID.
     *
     * @param id the helper analytics ID
     * @return a response with no content, or 404 if not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a helper analytics record", description = "Deletes a helper analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Helper analytics record deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Helper analytics record not found", content = @Content)
    })
    public ResponseEntity<Void> deleteHelperAnalytics(
        @Parameter(description = "ID of the helper analytics record to delete", example = "HELPER_001")
        @PathVariable String id
    ) {
        HelperAnalytics existing = helperAnalyticsService.getHelperAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        helperAnalyticsService.deleteHelperAnalytics(id);
        return ResponseEntity.noContent().build();
    }
}

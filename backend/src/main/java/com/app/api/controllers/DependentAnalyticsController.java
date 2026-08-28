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

import com.app.api.models.DependentAnalytics;
import com.app.api.services.DependentAnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * DependentAnalyticsController
 */
@RestController
@RequestMapping("/api/dependent-analytics")
@Tag(name = "Dependent Analytics", description = "Operations for managing dependent analytics records")
public class DependentAnalyticsController {

    private final DependentAnalyticsService dependentAnalyticsService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param dependentAnalyticsService service providing analytics data for dependents
     */
    public DependentAnalyticsController(DependentAnalyticsService dependentAnalyticsService) {
        this.dependentAnalyticsService = dependentAnalyticsService;
    }

    // GET /api/dependent-analytics
    /**
     * Retrieves all dependent analytics records.
     *
     * @return a response containing the list of dependent analytics records
     */
    @GetMapping
    @Operation(summary = "Get all dependent analytics", description = "Retrieves a list of all dependent analytics records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved dependent analytics records")
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
    @Operation(summary = "Get dependent analytics by ID", description = "Retrieves a single dependent analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dependent analytics record found"),
        @ApiResponse(responseCode = "404", description = "Dependent analytics record not found", content = @Content)
    })
    public ResponseEntity<DependentAnalytics> getDependentAnalyticsById(
        @Parameter(description = "ID of the dependent analytics record to retrieve", example = "DEP_001")
        @PathVariable String id
    ) {
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
    @Operation(summary = "Create a new dependent analytics record", description = "Creates a new dependent analytics record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dependent analytics record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid dependent analytics data", content = @Content)
    })
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
    @Operation(summary = "Update a dependent analytics record", description = "Updates an existing dependent analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dependent analytics record updated successfully"),
        @ApiResponse(responseCode = "404", description = "Dependent analytics record not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid dependent analytics data", content = @Content)
    })
    public ResponseEntity<DependentAnalytics> updateDependentAnalytics(
        @Parameter(description = "ID of the dependent analytics record to update", example = "DEP_001")
        @PathVariable String id,
        @RequestBody DependentAnalytics dependentAnalytics
    ) {
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
    @Operation(summary = "Delete a dependent analytics record", description = "Deletes a dependent analytics record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Dependent analytics record deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Dependent analytics record not found", content = @Content)
    })
    public ResponseEntity<Void> deleteDependentAnalytics(
        @Parameter(description = "ID of the dependent analytics record to delete", example = "DEP_001")
        @PathVariable String id
    ) {
        DependentAnalytics existing = dependentAnalyticsService.getDependentAnalyticsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        dependentAnalyticsService.deleteDependentAnalytics(id);
        return ResponseEntity.noContent().build();
    }
}

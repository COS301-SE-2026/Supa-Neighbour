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

import com.app.api.models.Compatibility;
import com.app.api.services.CompatibilityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing compatibility
 */
@RestController
@RequestMapping("/api/compatibility")
@Tag(name = "Compatibility", description = "Operations for managing compatibility records")
public class CompatibilityController {

    private final CompatibilityService compatibilityService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param compatibilityService service providing analytics data for dependents
     */
    public CompatibilityController(CompatibilityService compatibilityService) {
        this.compatibilityService = compatibilityService;
    }

    // GET /api/compatibility
    /**
     * Retrieves all comments.
     *
     * @return a list of all compatibility
     */
    @GetMapping
    @Operation(summary = "Get all compatibility records", description = "Retrieves a list of all compatibility records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved compatibility records")
    public ResponseEntity<List<Compatibility>> getAllCompatibility() {
        return ResponseEntity.ok(compatibilityService.getAllCompatibility());
    }

    // GET /api/compatibility/1
    /**
     * Retrieves a comment by its ID.
     *
     * @param id the comment ID
     * @return the compatibility if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get compatibility by ID", description = "Retrieves a single compatibility record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compatibility record found"),
        @ApiResponse(responseCode = "404", description = "Compatibility record not found", content = @Content)
    })
    public ResponseEntity<Compatibility> getCompatibilityById(
        @Parameter(description = "ID of the compatibility record to retrieve", example = "1")
        @PathVariable int id
    ) {
        Compatibility compatibility = compatibilityService.getCompatibilityById(id);
        if (compatibility == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(compatibility);
    }

    // POST /api/compatibility
    /**
     * Creates a new comment.
     *
     * @param compatibility the comment to create
     * @return the created compatibility with HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new compatibility record", description = "Creates a new compatibility record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Compatibility record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid compatibility data", content = @Content)
    })
    public ResponseEntity<Compatibility> createCompatibility(@RequestBody Compatibility compatibility) {
        Compatibility saved = compatibilityService.saveCompatibility(compatibility);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/compatibility/1
    /**
     * Updates an existing comment.
     *
     * @param id the ID of the comment to update
     * @param compatility the updated compatibility data
     * @return the updated compatibility if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a compatibility record", description = "Updates an existing compatibility record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compatibility record updated successfully"),
        @ApiResponse(responseCode = "404", description = "Compatibility record not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid compatibility data", content = @Content)
    })
    public ResponseEntity<Compatibility> updateCompatibility(
        @Parameter(description = "ID of the compatibility record to update", example = "1")
        @PathVariable int id,
        @RequestBody Compatibility compatibility
    ) {
        Compatibility existing = compatibilityService.getCompatibilityById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Compatibility updated = compatibilityService.updateCompatibility(id, compatibility);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/compatibility/1
    /**
     * Deletes a comment by its ID.
     *
     * @param id the ID of the compatibility to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a compatibility record", description = "Deletes a compatibility record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Compatibility record deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Compatibility record not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCompatibility(
        @Parameter(description = "ID of the compatibility record to delete", example = "1")
        @PathVariable int id
    ) {
        Compatibility existing = compatibilityService.getCompatibilityById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        compatibilityService.deleteCompatibility(id);
        return ResponseEntity.noContent().build();
    }
}

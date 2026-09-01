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

import com.app.api.models.Dependent;
import com.app.api.services.DependentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing the dependant
 */
@RestController
@RequestMapping("/api/dependents")
@Tag(name = "Dependents", description = "Operations for managing dependents")
public class DependentController {

    private final DependentService dependentService;

    /**
     * Constructs the controller with its required service dependency.
     *
     * @param dependentService service providing analytics data for dependents
     */
    public DependentController(DependentService dependentService) {
        this.dependentService = dependentService;
    }

    // GET /api/dependents
    /**
     * Retrieves all comments.
     *
     * @return a list of all dependants
     */
    @GetMapping
    @Operation(summary = "Get all dependents", description = "Retrieves a list of all dependents")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved dependents")
    public ResponseEntity<List<Dependent>> getAllDependents() {
        return ResponseEntity.ok(dependentService.getAllDependents());
    }

    // GET /api/dependents/1
    /**
     * Retrieves a comment by its ID.
     *
     * @param id the dependent ID
     * @return the dependent if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get dependent by ID", description = "Retrieves a single dependent by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dependent found"),
        @ApiResponse(responseCode = "404", description = "Dependent not found", content = @Content)
    })
    public ResponseEntity<Dependent> getDependentById(
        @Parameter(description = "ID of the dependent to retrieve", example = "1")
        @PathVariable int id
    ) {
        Dependent dependent = dependentService.getDependentById(id);
        if (dependent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dependent);
    }

    // POST /api/dependents
    /**
     * Creates a new dependent.
     *
     * @param dependent the comment to create
     * @return the created dependent with HTTP 201 status
     */
    @PostMapping
    @Operation(summary = "Create a new dependent", description = "Creates a new dependent")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dependent created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid dependent data", content = @Content)
    })
    public ResponseEntity<Dependent> createDependent(@RequestBody Dependent dependent) {
        Dependent saved = dependentService.saveDependent(dependent);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing dependent.
     *
     * @param id the ID of the comment to update
     * @param dependent the updated dependent data
     * @return the updated dependent if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a dependent", description = "Updates an existing dependent by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dependent updated successfully"),
        @ApiResponse(responseCode = "404", description = "Dependent not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid dependent data", content = @Content)
    })
    public ResponseEntity<Dependent> updateDependent(
        @Parameter(description = "ID of the dependent to update", example = "1")
        @PathVariable int id,
        @RequestBody Dependent dependent
    ) {
        Dependent existing = dependentService.getDependentById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Dependent updated = dependentService.updateDependent(id, dependent);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a dependent by its ID.
     *
     * @param id the ID of the dependent to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dependent", description = "Deletes a dependent by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Dependent deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Dependent not found", content = @Content)
    })
    public ResponseEntity<Void> deleteDependent(
        @Parameter(description = "ID of the dependent to delete", example = "1")
        @PathVariable int id
    ) {
        Dependent existing = dependentService.getDependentById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        dependentService.deleteDependent(id);
        return ResponseEntity.noContent().build();
    }
}

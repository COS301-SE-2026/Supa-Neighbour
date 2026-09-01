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

import com.app.api.models.Availability;
import com.app.api.services.AvailabilityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Rest controller for helpers availability
 */
@RestController
@RequestMapping("/api/availability")
@Tag(name = "Availability", description = "Operations for managing helper availability records")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    /**
     * Basic constructor
     * @param availabilityService service handling the availability controllers
     */
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    // GET /api/availability
    /**
     * Retrieves all availability records.
     *
     * @return a response containing the list of availability records
     */
    @GetMapping
    @Operation(summary = "Get all availability records", description = "Retrieves a list of all availability records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved availability records")
    public ResponseEntity<List<Availability>> getAllAvailability() {
        return ResponseEntity.ok(availabilityService.getAllAvailability());
    }

    // GET /api/availability/1
    /**
     * Retrieves an availability record by its ID.
     *
     * @param id the availability ID
     * @return a response containing the availability record, or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get availability by ID", description = "Retrieves a single availability record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability record found"),
        @ApiResponse(responseCode = "404", description = "Availability record not found", content = @Content)
    })
    public ResponseEntity<Availability> getAvailabilityById(
        @Parameter(description = "ID of the availability record to retrieve", example = "1")
        @PathVariable int id
    ) {
        Availability availability = availabilityService.getAvailabilityById(id);
        if (availability == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(availability);
    }

    // POST /api/availability
    /**
     * Creates a new availability record.
     *
     * @param availability the availability data to create
     * @return a response containing the created availability record
     */
    @PostMapping
    @Operation(summary = "Create a new availability record", description = "Creates a new availability record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Availability record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid availability data", content = @Content)
    })
    public ResponseEntity<Availability> createAvailability(@RequestBody Availability availability) {
        Availability created = availabilityService.saveAvailability(availability);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/availability/1
    /**
     * Updates an existing availability record.
     *
     * @param id the availability ID
     * @param availability the updated availability data
     * @return a response containing the updated availability record, or 404 if not found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an availability record", description = "Updates an existing availability record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability record updated successfully"),
        @ApiResponse(responseCode = "404", description = "Availability record not found", content = @Content)
    })
    public ResponseEntity<Availability> updateAvailability(
        @Parameter(description = "ID of the availability record to update", example = "1")
        @PathVariable int id,
        @RequestBody Availability availability
    ) {
        Availability updated = availabilityService.updateAvailability(id, availability);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/availability/1
    /**
     * Deletes an availability record by its ID.
     *
     * @param id the availability ID
     * @return a response indicating success or failure
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an availability record", description = "Deletes an availability record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Availability record deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Availability record not found", content = @Content)
    })
    public ResponseEntity<Void> deleteAvailability(
        @Parameter(description = "ID of the availability record to delete", example = "1")
        @PathVariable int id
    ) {
        boolean deleted = availabilityService.deleteAvailability(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

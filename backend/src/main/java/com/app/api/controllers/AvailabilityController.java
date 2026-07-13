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
 

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

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
    public ResponseEntity<Availability> getAvailabilityById(@PathVariable int id) {
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
    public ResponseEntity<Availability> createAvailability(@RequestBody Availability availability) {
        Availability created = availabilityService.saveAvailability(availability);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/availability/1
    /**
     * Updates an existing availability record.
     *     * @param id the availability ID
     * @param availability the updated availability data
     * @return a response containing the updated availability record, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Availability> updateAvailability(@PathVariable int id, @RequestBody Availability availability) {
        Availability updated = availabilityService.updateAvailability(id, availability);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/availability/1
    /**
     * Deletes an availability record by its ID.
     *     * @param id the availability ID
     * @return a response indicating success or failure
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable int id) {
        boolean deleted = availabilityService.deleteAvailability(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
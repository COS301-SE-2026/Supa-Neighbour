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

import com.app.api.models.Location;
import com.app.api.services.LocationService;

/**
 * REST controller for Location.
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {


    private final LocationService locationService;

    /**
     * Basic Location Controller
     * @param locationService locationService
     */
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // GET /api/locations
    /**
     * Retrieves all locations.
     *
     * @return a list of all locations
     */
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    // GET /api/locations/1
    /**
     * Retrieves a location by its ID.
     *
     * @param id the like ID
     * @return the location if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable int id) {
        Location location = locationService.getLocationById(id);
        if (location == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(location);
    }

    // POST /api/locations
    /**
     * Creates a new location.
     *
     * @param location the comment to create
     * @return the created location with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        Location saved = locationService.saveLocation(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/locations/1
    /**
     * Updates an existing location.
     *
     * @param id the ID of the location to update
     * @param likes the updated likes data
     * @return the updated location if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable int id, @RequestBody Location location) {
        Location existing = locationService.getLocationById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Location updated = locationService.updateLocation(id, location);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/locations/1
    /**
     * Deletes a location by its ID.
     *
     * @param id the ID of the location to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable int id) {
        Location existing = locationService.getLocationById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}

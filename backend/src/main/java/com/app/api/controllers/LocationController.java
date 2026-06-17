package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Location;
import com.app.api.services.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // GET /api/locations
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    // GET /api/locations/1
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable int id) {
        Location location = locationService.getLocationById(id);
        if (location == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(location);
    }

    // POST /api/locations
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        Location saved = locationService.saveLocation(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/locations/1
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
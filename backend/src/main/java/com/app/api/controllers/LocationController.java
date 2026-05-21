package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Location;
import com.app.api.services.LocationService;

/**
 * Location controller.
 */
@RestController
@RequestMapping("api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * Get all locations.
     * @return locations
     */
    @GetMapping
    public Iterable<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    /**
     * Get location by id.
     * @param id location id
     * @return location
     */
    @GetMapping("api/locations/{id}")
    public Location getLocationById(@PathVariable int id) {
        return locationService.getLocationById(id);
    }

    /**
     * Create location.
     * @param location location
     * @return saved location
     */
    @PostMapping
    public Location createLocation(@RequestBody Location location) {
        return locationService.saveLocation(location);
    }
}

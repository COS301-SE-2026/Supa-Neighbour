package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Location;
import com.app.api.repositories.LocationRepository;

/**
 * Location service.
 */
@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Get all locations.
     * @return list of locations
     */
    public Iterable<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    /**
     * Get location by id.
     * @param id location id
     * @return location
     */
    public Location getLocationById(int id) {
        return locationRepository.findById(id).orElse(null);
    }

    /**
     * Save location.
     * @param location location
     * @return saved location
     */
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }
}

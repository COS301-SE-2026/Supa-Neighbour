package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Location;
import com.app.api.repositories.LocationRepository;

/**
 * Service layer for managing location operations.
 * Provides CRUD functionality for Location entities.
 */
@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Retrieves all locations from the repository.
     *
     * @return a list of all locations
     */
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    /**
     * Retrieves a location by its identifier.
     *
     * @param id the location identifier
     * @return the location if found, or null if no location exists with the given id
     */
    public Location getLocationById(int id) {
        return locationRepository.findById(id).orElse(null);
    }

    /**
     * Saves a new location to the repository.
     *
     * @param location the location to save
     * @return the saved location, or null if the provided location is null
     */
    public Location saveLocation(Location location) {
        if(location == null) {
            return null;
        }
        return locationRepository.save(location);
    }

    /**
     * Updates an existing location with the provided details.
     *
     * @param id      the identifier of the location to update
     * @param updated the location object containing the updated fields
     * @return the updated location, or null if no location exists with the given id
     */
    public Location updateLocation(int id, Location updated) {
        Location existing = locationRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setLocationCentrePoint(updated.getLocationCentrePoint());
        existing.setLocationRadius(updated.getLocationRadius());
        existing.setNeighbourhoodName(updated.getNeighbourhoodName());
        existing.setNeighbourhoodid(updated.getNeighbourhoodid());

        return locationRepository.save(existing);
    }

    /**
     * Deletes a location by its identifier.
     *
     * @param id the identifier of the location to delete
     */
    public void deleteLocation(int id) {
        locationRepository.deleteById(id);
    }
}

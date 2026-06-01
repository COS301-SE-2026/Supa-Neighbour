package com.app.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.api.models.Location;
import com.app.api.repositories.LocationRepository;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    // Get all
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    // Get by id
    public Location getLocationById(int id) {
        return locationRepository.findById(id).orElse(null);
    }

    // Create
    public Location saveLocation(Location location) {
        if(location == null) return null;
        return locationRepository.save(location);
    }

    // Update
    public Location updateLocation(int id, Location updated) {
        Location existing = locationRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setLocationCenterPoint(updated.getLocationCenterPoint());
        existing.setLocationRadius(updated.getLocationRadius());
        existing.setNeighbourhoodName(updated.getNeighbourhoodName());
        existing.setNeighbourhoodid(updated.getNeighbourhoodid());

        return locationRepository.save(existing);
    }

    // Delete
    public void deleteLocation(int id) {
        locationRepository.deleteById(id);
    }
}
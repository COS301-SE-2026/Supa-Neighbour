package com.app.api.services;

import java.util.List;


import org.springframework.stereotype.Service;
import com.app.api.models.Availability;
import com.app.api.repositories.AvailabilityRepository;

/**
 * Service layer for managing availability operations.
 * Provides CRUD functionality for Availability entities.
 */
@Service
public class AvailabilityService {

    
    private final AvailabilityRepository availabilityRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }
    // Get all
    /**
     * Retrieves all availability records from the repository.
     *
     * @return a list of all availability records
     */
    public List<Availability> getAllAvailability() {
        return availabilityRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves an availability record by its identifier.
     *
     * @param id the availability identifier
     * @return the availability record if found, or null if no record exists with the given id
     */
    public Availability getAvailabilityById(int id) {
        return availabilityRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new availability record to the repository.
     *
     * @param availability the availability record to save
     * @return the saved availability record, or null if the provided availability is null
     */
    public Availability saveAvailability(Availability availability) {
        if(availability == null) {
            return null;
        }
        return availabilityRepository.save(availability);
    }

    // Update
    /**
     * Updates an existing availability record with the provided details.
     *
     * @param id      the identifier of the availability record to update
     * @param updated the availability object containing the updated fields
     * @return the updated availability record, or null if no record exists with the given id
     */
    public Availability updateAvailability(int id, Availability updated) {
        Availability existing = availabilityRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setDayofweek(updated.getDayofweek());
        existing.setTimewindow(updated.getTimewindow());
        existing.setDayofweek(updated.getDayofweek());
        existing.setIsactive(updated.isIsactive());

        return availabilityRepository.save(existing);
    }

    // Delete
    /**
     * Deletes an availability record by its identifier.
     *
     * @param id the identifier of the availability record to delete
     * @return true if the record was deleted, false otherwise
     */
    public boolean deleteAvailability(int id) {
        if (availabilityRepository.existsById(id)) {
            availabilityRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

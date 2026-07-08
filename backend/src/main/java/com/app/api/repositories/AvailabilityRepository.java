package com.app.api.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.Availability;

/**
 * Repository for availability entities
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    /**
     * Finds all Availability records associated with a specific user.
     *
     * @param userId the ID of the user
     * @return list of Availability entities for the given user
     */
}

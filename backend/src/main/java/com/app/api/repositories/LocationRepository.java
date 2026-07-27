package com.app.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.api.models.Location;

/**
 * Repository for Location entities.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    /**
     * Finds a Location entity by its neighbourhood name.
     */
    @Query("SELECT l FROM Location l WHERE l.neighbourhoodName = :name")
Optional<Location> findByNeighbourhoodName(@Param("name") String name);
}   
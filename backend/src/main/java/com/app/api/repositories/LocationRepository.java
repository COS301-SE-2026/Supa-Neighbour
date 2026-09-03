package com.app.api.repositories;

import com.app.api.models.Location;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

    /**
     * Returns the highest current neighbourhoodid value, or 0 if the table is empty.
    */
   @Query("SELECT COALESCE(MAX(l.neighbourhoodid), 0) FROM Location l")
   int findMaxNeighbourhoodid();
}

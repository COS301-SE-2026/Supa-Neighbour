package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.app.api.models.Location;

/**
 * Repository for Location entities.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByUserId(Integer userId);

    Location findLocationbyId(int neighbourhoodId);

}

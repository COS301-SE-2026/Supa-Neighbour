package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Location;
@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    
}

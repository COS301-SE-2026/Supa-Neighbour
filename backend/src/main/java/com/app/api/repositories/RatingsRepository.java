package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Ratings;
@Repository
public interface RatingsRepository extends JpaRepository<Ratings, Integer> {
    
}

package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.app.api.models.Ratings;
@Repository
public interface RatingsRepository extends CrudRepository<Ratings, Integer> {
    
}

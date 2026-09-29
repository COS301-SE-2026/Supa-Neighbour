package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Ratings;

public interface RatingsRepository extends JpaRepository<Ratings, Integer> {
}

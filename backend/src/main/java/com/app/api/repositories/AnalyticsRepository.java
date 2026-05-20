package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.app.api.models.Analytics;
@Repository
public interface AnalyticsRepository extends CrudRepository<Analytics, Integer> {
    
}
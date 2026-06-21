package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.DependentAnalytics;

/**
 * Repository for DependentAnalytics entities.
 */
@Repository
public interface DependentAnalyticsRepository extends JpaRepository<DependentAnalytics, String> {
    
}

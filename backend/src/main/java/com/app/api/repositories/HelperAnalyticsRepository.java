package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.HelperAnalytics;
 
/**
 * Repository for HelperAnalytics entities.
 */
public interface HelperAnalyticsRepository extends JpaRepository<HelperAnalytics, String> {
    
}

package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.HelperAnalytics;
 
public interface HelperAnalyticsRepository extends JpaRepository<HelperAnalytics, String> {
    
}
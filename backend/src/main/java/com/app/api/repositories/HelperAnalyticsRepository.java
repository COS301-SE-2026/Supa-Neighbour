package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.HelperAnalytics;
import java.util.Optional;
 
/**
 * Repository for HelperAnalytics entities.
 */
public interface HelperAnalyticsRepository extends JpaRepository<HelperAnalytics, String> {
    
    Optional<HelperAnalytics> findByUserid_Userid(Integer userId);
}

package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.api.models.HelperAnalytics;

import java.util.Optional;

/**
 * Repository for HelperAnalytics entities
 */
@Repository
public interface HelperAnalyticsRepository extends JpaRepository<HelperAnalytics, String> {

    /**
     * Finds a HelperAnalytics entity by the associated user id.
     * @param userId the id of the user
     * @return an Optional containing the HelperAnalytics entity if found, or empty if not found
     */
    @Query("SELECT h FROM HelperAnalytics h WHERE h.userid.userid = :userId")
    Optional<HelperAnalytics> findByUserId(@Param("userId") int userId);
}

package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.Dependent;

/**
 * Repository for Dependent entities.
 */
@Repository
public interface DependentRepository extends JpaRepository<Dependent, Integer> {
   /**
     * Finds dependents belonging to the given user id.
     *
     * @param userId the user id to filter by
     * @return matching dependents
     */
    Dependent findByUserId_Userid(int userId);
}

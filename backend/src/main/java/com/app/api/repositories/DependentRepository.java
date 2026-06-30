package com.app.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Dependent;

/**
 * Repository for Dependent entities.
 */
@Repository
public interface DependentRepository extends JpaRepository<Dependent, Integer> {
   /**
     * Finds dependents belonging to the given user id.
     *
     * @param userid the user id to filter by
     * @return matching dependents
     */
    Dependent findByUserid_Userid(int userid);
}

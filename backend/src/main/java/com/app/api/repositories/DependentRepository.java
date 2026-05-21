package com.app.api.repositories;

import com.app.api.models.Dependent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Dependent entities.
 */
@Repository
public interface DependentRepository extends CrudRepository<Dependent, Integer> {

    /**
     * Find the dependent profile associated with a given user.
     * @param userId the user's ID
     * @return the dependent profile for the user
     */
    Dependent findByUserId(int userId);
}

package com.app.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.api.models.Helper;

/**
 * Repository for performing database operations on
 * {@link Helper} entities.
 */
@Repository
public interface HelperRepository extends JpaRepository<Helper, Integer> {
     /**
     * Retrieves the helper record associated with the specified user.
     *
     * @param userId the identifier of the user
     * @return an {@link Optional} containing the corresponding
     *         {@link Helper} if one exists; otherwise an empty
     *         {@code Optional}
     */
    Optional<Helper> findByUserid_Userid(int userId);
}


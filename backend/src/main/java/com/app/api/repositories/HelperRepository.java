package com.app.api.repositories;

import java.util.Optional;
import com.app.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Helper;
import java.util.List;

/**
 * Repository for performing database operations on
 * {@link Helper} entities.
 */
public interface HelperRepository extends JpaRepository<Helper, Integer> {
    

    /**
     * Retrieves all helper records matching the given availability status.
     *
     * @param available the availability status to filter by
     * @return a list of helpers matching the given availability, possibly empty
     */
    List<Helper> findByAvailable(boolean available);

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


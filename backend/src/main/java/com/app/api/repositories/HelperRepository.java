package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Helper;
import java.util.List;
import java.util.Optional;

/**
 * Repository for performing database operations on
 * {@link Helper} entities.
 */
public interface HelperRepository extends JpaRepository<Helper, Integer> {
    

    
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


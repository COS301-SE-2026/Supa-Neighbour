package com.app.api.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Helper;
import java.util.List;
/**
 * Repository for Helper entities.
 */

public interface HelperRepository extends JpaRepository<Helper, Integer> {
    

    List<Helper> findByAvailable(boolean available);
}


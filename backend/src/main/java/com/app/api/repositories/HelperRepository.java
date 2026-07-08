package com.app.api.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Helper;

/**
 * Repository for Helper entities.
 */
@Repository
public interface HelperRepository extends JpaRepository<Helper, Integer> {
    Optional<Helper> findByUserid_Userid(int userId);
}


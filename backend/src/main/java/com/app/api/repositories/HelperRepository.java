package com.app.api.repositories;

import java.util.Optional;

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
    Optional<Helper> findByUserid_Userid(int userId);
}


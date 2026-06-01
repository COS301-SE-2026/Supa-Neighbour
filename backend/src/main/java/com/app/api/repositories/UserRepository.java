package com.app.api.repositories;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.User;

/**
 * Repository for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
}

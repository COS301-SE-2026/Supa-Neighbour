package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Likes;

/**
 * Repository for Likes entities.
 */
public interface LikesRepository extends JpaRepository<Likes,Integer> {
    
}

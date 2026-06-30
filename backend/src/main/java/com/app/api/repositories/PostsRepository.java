package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Posts;

/**
 * Repository for Posts entities.
 */
public interface PostsRepository extends JpaRepository<Posts,Integer> {
    
}

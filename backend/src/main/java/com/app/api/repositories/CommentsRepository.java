package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Comments;

/**
 * Repository for Comment entities.
 */
public interface CommentsRepository extends JpaRepository<Comments,Integer>{
    
}

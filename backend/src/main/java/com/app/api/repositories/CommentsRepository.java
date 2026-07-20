package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.api.models.Comments;
import java.util.List;

/**
 * Repository for Comment entities.
 */
public interface CommentsRepository extends JpaRepository<Comments,Integer>{
    List<Comments> findByPostId_PostId(int postId);
}

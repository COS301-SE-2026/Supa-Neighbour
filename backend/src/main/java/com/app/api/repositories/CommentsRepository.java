package com.app.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.api.models.Comments;

/**
 * Repository for Comment entities.
 */
public interface CommentsRepository extends JpaRepository<Comments, Integer> {

    /**
     * Retrieves all comments associated with the specified post.
     *
     * @param postId the ID of the post
     * @return a list of comments belonging to the specified post
     */
    List<Comments> findByPostid_Postid(int postId);
}

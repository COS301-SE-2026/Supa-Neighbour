package com.app.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.api.models.Reaction;

/**
 * Repository for Likes entities.
 */
public interface ReactionRepository extends JpaRepository<Reaction,Integer> {
    /**
     * Counts the number of reactions a user has made to a specific post.
     *
     * @param userId the unique identifier of the user
     * @param postId the unique identifier of the post
     * @return the number of reactions the user has made to the post
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    long countByUserAndPost(@Param("userId") int userId, @Param("postId") int postId);

    /**
     * Counts the total number of dislike reactions for a post.
     *
     * @param postId the unique identifier of the post
     * @return the total number of dislikes on the post
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE post_id = :postId AND reaction_type = 'dislike'", nativeQuery=true)
    long countDisLiked(@Param("postId") int postId);

    /**
     * Counts the total number of dislike reactions for a comment.
     *
     * @param commentId the unique identifier of the comment
     * @return the total number of dislikes on the comment
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE comment_id = :commentId AND reaction_type = 'dislike'", nativeQuery = true)
    long countDislikedComment(@Param("commentId") int commentId);

     /**
     * Counts the number of reactions a user has made to a specific comment.
     *
     * @param userId the unique identifier of the user
     * @param commentId the unique identifier of the comment
     * @return the number of reactions the user has made to the comment
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE user_id = :userId AND comment_id = :commentId", nativeQuery=true)
    long countByUserAndComment(@Param("userId") int userId, @Param("commentId") int commentId);

    /**
     * Finds a user's reaction of a specific type on a post.
     *
     * @param userId the unique identifier of the user
     * @param postId the unique identifier of the post
     * @param reactionType the type of reaction to retrieve (e.g. "dislike")
     * @return an {@link Optional} containing the matching reaction if one
     *         exists; otherwise an empty {@link Optional}
     */
    @Query(value = "SELECT * FROM reaction_table WHERE user_id = :userId AND post_id = :postId AND reaction_type = :reactionType", nativeQuery = true)
    Optional<Reaction> findByUserAndPostAndType(@Param("userId") int userId, @Param("postId") int postId, @Param("reactionType") String reactionType);
}

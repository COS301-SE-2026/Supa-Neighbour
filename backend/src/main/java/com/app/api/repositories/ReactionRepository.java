package com.app.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.api.models.Reaction;

/**
 * Repository for Reaction entities.
 */
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {

    /**
     * Counts the number of reactions made by a user on a specific post.
     *
     * @param userId the ID of the user
     * @param postId the ID of the post
     * @return the number of matching reactions
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    long countByUserAndPost(@Param("userId") int userId,
                            @Param("postId") int postId);

    /**
     * Counts the number of dislike reactions for a post.
     *
     * @param postId the ID of the post
     * @return the number of dislike reactions
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE post_id = :postId AND reaction_type = 'dislike'", nativeQuery = true)
    long countDisLiked(@Param("postId") int postId);

    /**
     * Counts the number of dislike reactions for a comment.
     *
     * @param commentId the ID of the comment
     * @return the number of dislike reactions
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE comment_id = :commentId AND reaction_type = 'dislike'", nativeQuery = true)
    long countDislikedComment(@Param("commentId") int commentId);

    /**
     * Counts the number of like (helpful) reactions for a comment.
     *
     * @param commentId the ID of the comment
     * @return the number of helpful reactions
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE comment_id = :commentId AND reaction_type = 'like'", nativeQuery = true)
    long countHelpfulComment(@Param("commentId") int commentId);

    /**
     * Counts the number of reactions made by a user on a specific comment.
     *
     * @param userId the ID of the user
     * @param commentId the ID of the comment
     * @return the number of matching reactions
     */
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE user_id = :userId AND comment_id = :commentId", nativeQuery = true)
    long countByUserAndComment(@Param("userId") int userId,
                               @Param("commentId") int commentId);

    /**
     * Counts the number of like (helpful) reactions for a post.
     *
     * @param postId the ID of the post
     * @return the number of helpful reactions
     */
    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.postid.postid = :postId AND r.reactionType = 'like'")
    long countHelpful(@Param("postId") int postId);

    /**
     * Finds a specific reaction made by a user on a post.
     *
     * @param userId the ID of the user
     * @param postId the ID of the post
     * @param reactionType the type of reaction to search for
     * @return an Optional containing the matching reaction if found, otherwise empty
     */
    @Query(value = "SELECT * FROM reaction_table WHERE user_id = :userId AND post_id = :postId AND reaction_type = :reactionType", nativeQuery = true)
    Optional<Reaction> findByUserAndPostAndType(@Param("userId") int userId,
                                                @Param("postId") int postId,
                                                @Param("reactionType") String reactionType);
}
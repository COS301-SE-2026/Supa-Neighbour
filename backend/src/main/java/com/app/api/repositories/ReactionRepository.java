package com.app.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.api.models.Reaction;

/**
 * Repository for managing reaction entities.
 */
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {

    /**
     * Counts the reactions made by a user on a specific post.
     *
     * @param userId the ID of the user
     * @param postId the ID of the post
     * @return the number of reactions made by the user on the post
     */
    @Query("""
        SELECT COUNT(r)
        FROM Reaction r
        WHERE r.userid.userid = :userId
        AND r.postid.postid = :postId
        """)
    long countByUserAndPost(
            @Param("userId") int userId,
            @Param("postId") int postId
    );

    /**
     * Counts the reactions made by a user on a specific comment.
     *
     * @param userId the ID of the user
     * @param commentId the ID of the comment
     * @return the number of reactions made by the user on the comment
     */
    @Query("""
        SELECT COUNT(r)
        FROM Reaction r
        WHERE r.userid.userid = :userId
        AND r.commentid.commentid = :commentId
        """)
    long countByUserAndComment(
            @Param("userId") int userId,
            @Param("commentId") int commentId
    );

    /**
     * Counts the dislike reactions on a specific post.
     *
     * @param postId the ID of the post
     * @return the number of dislike reactions
     */
    @Query("""
        SELECT COUNT(r)
        FROM Reaction r
        WHERE r.postid.postid = :postId
        AND r.reactionType = 'dislike'
        """)
    long countDisLiked(
            @Param("postId") int postId
    );

    /**
     * Counts the like reactions on a specific post.
     *
     * @param postId the ID of the post
     * @return the number of like reactions
     */
    @Query("""
        SELECT COUNT(r)
        FROM Reaction r
        WHERE r.postid.postid = :postId
        AND r.reactionType = 'like'
        """)
    long countLiked(
            @Param("postId") int postId
    );

    /**
     * Counts the dislike reactions on a specific comment.
     *
     * @param commentId the ID of the comment
     * @return the number of dislike reactions
     */
    @Query("""
        SELECT COUNT(r)
        FROM Reaction r
        WHERE r.commentid.commentid = :commentId
        AND r.reactionType = 'dislike'
        """)
    long countDislikedComment(
            @Param("commentId") int commentId
    );

    /**
     * Finds a reaction made by a user on a post with a specific type.
     *
     * @param userId the ID of the user
     * @param postId the ID of the post
     * @param reactionType the type of reaction
     * @return the matching reaction, if one exists
     */
    @Query("""
        SELECT r
        FROM Reaction r
        WHERE r.userid.userid = :userId
        AND r.postid.postid = :postId
        AND r.reactionType = :reactionType
        """)
    Optional<Reaction> findByUserAndPostAndType(
            @Param("userId") int userId,
            @Param("postId") int postId,
            @Param("reactionType") String reactionType
    );
}


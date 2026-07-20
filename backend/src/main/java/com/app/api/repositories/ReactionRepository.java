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
    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    long countByUserAndPost(@Param("userId") int userId, @Param("postId") int postId);

    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE post_id = :postId AND reaction_type = 'dislike'", nativeQuery=true)
    long countDisLiked(@Param("postId") int postId);

    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE comment_id = :commentId AND reaction_type = 'dislike'", nativeQuery = true)
    long countDislikedComment(@Param("commentId") int commentId);

    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE comment_id = :commentId AND reaction_type = 'helpful'", nativeQuery = true)
    long countHelpfulComment(@Param("commentId") int commentId);

    @Query(value = "SELECT COUNT(*) FROM reaction_table WHERE user_id = :userId AND comment_id = :commentId", nativeQuery=true)
    long countByUserAndComment(@Param("userId") int userId, @Param("commentId") int commentId);

    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.postid.postid = :postId AND r.reactionType = 'helpful'")
    long countHelpful(@Param("postId") int postId);

    @Query(value = "SELECT * FROM reaction_table WHERE user_id = :userId AND post_id = :postId AND reaction_type = :reactionType", nativeQuery = true)
    Optional<Reaction> findByUserAndPostAndType(@Param("userId") int userId, @Param("postId") int postId, @Param("reactionType") String reactionType);
}

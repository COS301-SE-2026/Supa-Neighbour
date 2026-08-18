package com.app.api.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.Instant;

import com.app.api.models.Posts;
import com.app.api.models.Reaction;
import com.app.api.dtos.CommentReactionResponseDTO;
import com.app.api.models.Comments;
import com.app.api.repositories.CommentsRepository;
import com.app.api.repositories.ReactionRepository;
import com.app.api.dtos.ReactionResponseDTO;
import com.app.api.dtos.ReactionRemovedResponseDTO;
import com.app.api.repositories.UserRepository;
import com.app.api.repositories.PostsRepository;

/**
 * Service layer for managing like operations.
 * Provides CRUD functionality for reaction entities.
 */
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;

    /**
     * Constructs the repository with its required service dependency.
     *
     * @param reactionRepository repository providing analytics data for reaction
     */
    public ReactionService(ReactionRepository reactionRepository, UserRepository userRepository,
            PostsRepository postsRepository, CommentsRepository commentsRepository) {
        this.reactionRepository = reactionRepository;
        this.userRepository = userRepository;
        this.postsRepository = postsRepository;
        this.commentsRepository = commentsRepository;
    }

    // Get all
    /**
     * Retrieves all reaction from the repository.
     *
     * @return a list of all reaction
     */
    public List<Reaction> getAllreaction() {
        return reactionRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a like by its identifier.
     *
     * @param id the like identifier
     * @return the like if found, or null if no like exists with the given id
     */
    public Reaction getLikeById(int id) {
        return reactionRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new like to the repository.
     *
     * @param like the like to save
     * @return the saved like, or null if the provided like is null
     */
    public Reaction saveLike(Reaction like) {
        if (like == null) {
            return null;
        }
        return reactionRepository.save(like);
    }

    // Update
    /**
     * Updates an existing like with the provided details.
     *
     * @param id      the identifier of the like to update
     * @param updated the like object containing the updated fields
     * @return the updated like, or null if no like exists with the given id
     */
    public Reaction updateLike(int id, Reaction updated) {
        Reaction existing = reactionRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setCommentid(updated.getCommentid());
        existing.setPostid(updated.getPostid());
        existing.setUserid(updated.getUserid());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setUpdatedAt(updated.getUpdatedAt());

        return reactionRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a like by its identifier.
     *
     * @param id the identifier of the like to delete
     */
    public void deleteLike(int id) {
        reactionRepository.deleteById(id);
    }

    /**
     * Adds a dislike reaction to a post for the specified user.
     * <p>
     * The method verifies that the post exists and that the user has not already
     * reacted to it. If the reaction is successfully created, the updated dislike
     * count for the post is returned.
     * </p>
     *
     * @param postId the unique identifier of the post to dislike
     * @param userId the unique identifier of the user adding the reaction
     * @return a {@link ReactionResponseDTO} containing the result of the operation
     *         and the updated dislike count
     * @throws ResponseStatusException if the post does not exist or the user has
     *                                 already reacted to the post
     */
    /**
     * Adds a dislike reaction to the specified post.
     *
     * @param postId the identifier of the post to react to
     * @param userId the identifier of the authenticated user
     * @return a response containing the created reaction and updated dislike count
     * @throws ResponseStatusException if the post does not exist or the user has
     *                                 already reacted to the post
     */
    public ReactionResponseDTO addDislikeReaction(int postId, int userId) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (reactionRepository.countByUserAndPost(userId, postId) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already reacted to this post");
        }

        Reaction reaction = new Reaction();
        reaction.setPostid(post);
        reaction.setUserid(userRepository.getReferenceById(userId));
        reaction.setReactionType("dislike");
        reaction.setCreatedAt(Timestamp.from(Instant.now()));

        saveReactionSafely(reaction, "You have already reacted to this post");

        long dislikeCount = reactionRepository.countDisLiked(postId);
        return new ReactionResponseDTO("Reaction added", postId, "dislike", dislikeCount);
    }

    /**
     * Adds a dislike reaction to the specified comment.
     *
     * @param commentId the identifier of the comment to react to
     * @param userId    the identifier of the authenticated user
     * @return a response containing the created reaction and updated dislike count
     * @throws ResponseStatusException if the comment does not exist or the user has
     *                                 already reacted to the comment
     */
    public CommentReactionResponseDTO addDislikeReactionToComment(int commentId, int userId) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (reactionRepository.countByUserAndComment(userId, commentId) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already reacted to this comment");
        }

        Reaction reaction = new Reaction();
        reaction.setCommentid(comment);
        reaction.setUserid(userRepository.getReferenceById(userId));
        reaction.setReactionType("dislike");
        reaction.setCreatedAt(Timestamp.from(Instant.now()));

        saveReactionSafely(reaction, "You have already reacted to this comment");

        long dislikeCount = reactionRepository.countDislikedComment(commentId);
        return new CommentReactionResponseDTO("Reaction added", commentId, "dislike", dislikeCount);
    }

    /**
     * Saves a reaction while handling duplicate reaction attempts.
     * <p>
     * If saving the reaction violates a database integrity constraint, such as a
     * unique constraint preventing duplicate reactions, a
     * {@link ResponseStatusException} with a {@code 409 CONFLICT} status is thrown.
     * </p>
     *
     * @param reaction        the reaction to save
     * @param conflictMessage the error message returned if the reaction already
     *                        exists
     * @throws ResponseStatusException if the reaction cannot be saved due to a
     *                                 data integrity violation
     */
    private void saveReactionSafely(Reaction reaction, String conflictMessage) {
        try {
            reactionRepository.save(reaction);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, conflictMessage);
        }
    }

    /**
     * Removes a user's dislike reaction from a post.
     * <p>
     * The method locates the user's existing dislike reaction, removes it from the
     * database, and returns the updated dislike count for the post.
     * </p>
     *
     * @param postId the unique identifier of the post
     * @param userId the unique identifier of the user removing the reaction
     * @return a {@link ReactionRemovedResponseDTO} containing the result of the
     *         operation and the updated dislike count
     * @throws ResponseStatusException if the user has no dislike reaction for the
     *                                 specified post
     */
    /**
     * Removes a user's dislike reaction from a post.
     *
     * @param postId the identifier of the post
     * @param userId the identifier of the authenticated user
     * @return a response confirming the reaction removal and the updated dislike
     *         count
     * @throws ResponseStatusException if the dislike reaction does not exist
     */
    public ReactionRemovedResponseDTO removeDisLikeReaction(int postId, int userId) {
        Reaction reaction = reactionRepository.findByUserAndPostAndType(userId, postId, "dislike")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no dislike reaction to remove"));

        reactionRepository.delete(reaction);

        long count = reactionRepository.countDisLiked(postId);

        return new ReactionRemovedResponseDTO("Reaction removed", postId, count);
    }

    /**
     * Adds a helpful (like) reaction to the specified post.
     *
     * @param postId the identifier of the post to react to
     * @param userId the identifier of the authenticated user
     * @return a response containing the created reaction and updated helpful
     *         reaction count
     * @throws ResponseStatusException if the post does not exist or the user has
     *                                 already reacted to the post
     */
/**
 * Adds a helpful reaction to the specified post.
 *
 * @param postId the identifier of the post
 * @param userId the identifier of the authenticated user
 * @return a response containing the created reaction and updated like count
 * @throws ResponseStatusException if the post does not exist or the user
 *         has already reacted to the post
 */
public CommentReactionResponseDTO addHelpfulReactionToPost(
        int postId,
        int userId) {

    Posts post = postsRepository.findById(postId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Post not found"));

    if (reactionRepository.countByUserAndPost(userId, postId) > 0) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "You have already reacted to this post");
    }

    Reaction reaction = new Reaction();

    reaction.setPostid(post);
    reaction.setUserid(userRepository.getReferenceById(userId));
    reaction.setReactionType("like");
    reaction.setCreatedAt(Timestamp.from(Instant.now()));

    saveReactionSafely(
            reaction,
            "You have already reacted to this post");

    long helpfulCount = reactionRepository.countLiked(postId);

    return new CommentReactionResponseDTO(
            "Reaction added",
            postId,
            "like",
            helpfulCount);
}

    /**
     * Removes a user's helpful (like) reaction from a post.
     *
     * @param postId the identifier of the post
     * @param userId the identifier of the authenticated user
     * @return a response confirming the reaction removal and the updated helpful
     *         reaction count
     * @throws ResponseStatusException if the helpful reaction does not exist
     */
    /**
     * Removes a user's helpful (like) reaction from a post.
     *
     * @param postId the identifier of the post
     * @param userId the identifier of the authenticated user
     * @return a response confirming the reaction removal and the updated helpful
     *         reaction count
     * @throws ResponseStatusException if the helpful reaction does not exist
     */
    public ReactionRemovedResponseDTO removeHelpfulReaction(int postId,int userId) {

        Reaction reaction = reactionRepository.findByUserAndPostAndType(
                userId,
                postId,
                "like")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "no helpful reaction to remove"));
        reactionRepository.delete(reaction);

        long count = reactionRepository.countLiked(postId);

        return new ReactionRemovedResponseDTO(
                "Reaction removed",
                postId,
                count);
    }
}

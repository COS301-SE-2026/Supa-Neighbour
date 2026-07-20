package com.app.api.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.app.api.models.Posts;
import com.app.api.models.Reaction;
import com.app.api.models.Comments;
import com.app.api.repositories.CommentsRepository;
import com.app.api.repositories.ReactionRepository;
import com.app.api.dtos.ReactionResponseDTO;
import com.app.api.dtos.CommentReactionResponseDTO;
import com.app.api.dtos.ReactionRemovedResponseDTO;
import  com.app.api.repositories.UserRepository;
import com.app.api.repositories.PostsRepository;
import com.app.api.dtos.ReactionRemovedResponseDTO;
import java.sql.Timestamp;
import java.time.Instant;

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
    public ReactionService(ReactionRepository reactionRepository, UserRepository userRepository,PostsRepository postsRepository, CommentsRepository commentsRepository) {
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
        if(like == null){
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
        
        if (existing == null){
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

    public ReactionResponseDTO addDislikeReaction(int postId, int userId){
        Posts post = postsRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if(reactionRepository.countByUserAndPost(userId, postId) > 0){
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

    public CommentReactionResponseDTO addDislikeReactionToComment(int commentId, int userId){
        Comments comment = commentsRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if(reactionRepository.countByUserAndComment(userId, commentId) > 0){
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

    

    private void saveReactionSafely(Reaction reaction, String conflictMessage){
        try {
            reactionRepository.save(reaction);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, conflictMessage);
        }
    }

    public ReactionRemovedResponseDTO removeDisLikeReaction(int postId, int userId) {
        Reaction reaction = reactionRepository.findByUserAndPostAndType(userId, postId, "dislike").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no dislike reaction to remove"));

        reactionRepository.delete(reaction);

        long count = reactionRepository.countDisLiked(postId);

        return new ReactionRemovedResponseDTO("Reaction removed", postId, count);
    }

    public CommentReactionResponseDTO addHelpfulReactionToPost(int postId, int userId){
    Posts post = postsRepository.findById(postId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

    if(reactionRepository.countByUserAndPost(userId, postId) > 0){
        throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already reacted to this post");
    }

    Reaction reaction = new Reaction();
    reaction.setPostid(post);
    reaction.setUserid(userRepository.getReferenceById(userId));
    reaction.setReactionType("helpful");
    reaction.setCreatedAt(Timestamp.from(Instant.now()));

    saveReactionSafely(reaction, "You have already reacted to this post");

    long helpfulCount = reactionRepository.countHelpful(postId); // see note below
    return new CommentReactionResponseDTO("Reaction added", postId, "helpful", helpfulCount);
}

        public ReactionRemovedResponseDTO removeHelpfulReaction(int postId, int userId) {
        Reaction reaction = reactionRepository.findByUserAndPostAndType(userId, postId, "helpful").orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no helpful reaction to remove"));

        reactionRepository.delete(reaction);

        long count = reactionRepository.countHelpfulComment(postId);

        return new ReactionRemovedResponseDTO("Reaction removed", postId, count);
    }

}

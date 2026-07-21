package com.app.api.services;

import java.util.List;
import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.api.dtos.CommentPostResponseDTO;
import com.app.api.dtos.CommentRequestDTO;
import com.app.api.dtos.CommentResponseDTO;
import com.app.api.models.Comments;
import com.app.api.models.Posts;
import com.app.api.repositories.CommentsRepository;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.UserRepository;
import java.time.Instant;

/**
 * Service layer for managing comment operations.
 * Provides CRUD functionality for Comments entities.
 */
@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;

    /**
     * Constructs the service with its required repository dependency.
     *
     * @param commentsRepository repository providing analytics data for comments
     */
    public CommentsService(CommentsRepository commentsRepository, PostsRepository postsRepository,
            UserRepository userRepository) {
        this.commentsRepository = commentsRepository;
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
    }

    // Get all
    /**
     * Retrieves all comments from the repository.
     *
     * @return a list of all comments
     */
    public List<Comments> getAllComments() {
        return commentsRepository.findAll();
    }

    // Get by id
    /**
     * Retrieves a comment by its identifier.
     *
     * @param id the comment identifier
     * @return the comment if found, or null if no comment exists with the given id
     */
    public Comments getCommentsById(int id) {
        return commentsRepository.findById(id).orElse(null);
    }

    // Create
    /**
     * Saves a new comment to the repository.
     *
     * @param comments the comment to save
     * @return the saved comment, or null if the provided comment is null
     */
    public Comments saveComments(Comments comments) {
        if (comments == null) {
            return null;
        }
        return commentsRepository.save(comments);
    }

    // Update
    /**
     * Updates an existing comment with the provided details.
     *
     * @param id      the identifier of the comment to update
     * @param updated the comment object containing the updated fields
     * @return the updated comment, or null if no comment exists with the given id
     */
    public Comments updateComments(int id, Comments updated) {
        Comments existing = commentsRepository.findById(id).orElse(null);

        if (existing == null) {
            return null;
        }

        existing.setUserid(updated.getUserid());
        existing.setUpdatedAt(updated.getUpdatedAt());
        existing.setCommentContent(updated.getCommentContent());
        existing.setParentCommentid(updated.getParentCommentid());
        existing.setCreatedAt(updated.getCreatedAt());
        existing.setPostid(updated.getPostid());
        existing.setUpdatedAt(updated.getUpdatedAt());
        return commentsRepository.save(existing);
    }

    // Delete
    /**
     * Deletes a comment by its identifier.
     *
     * @param id the identifier of the comment to delete
     */
    public void deleteComments(int id) {
        commentsRepository.deleteById(id);
    }

    /**
     * gets all comments based on the post
     * 
     * @param postId              used to id the post
     * @param request             to use firebase authentication
     * @param authenticatedUserId firebase authentication
     * @return
     */
    public CommentResponseDTO addCommentToPost(int postId, CommentRequestDTO request, int authenticatedUserId) {
        if (request.getCommentContent() == null || request.getCommentContent().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "commentContent is required");
        }

        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (request.getParentCommentId() != null) {
            Comments parent = commentsRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent Comment Not Found"));

            if (parent.getPostid().getPostid() != postId) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
            }
        }
        Comments comment = new Comments();

        comment.setPostid(post);
        comment.setUserid(userRepository.getReferenceById(authenticatedUserId));
        comment.setParentCommentid(request.getParentCommentId());
        comment.setCommentContent(request.getCommentContent());
        comment.setCreatedAt(Timestamp.from(Instant.now()));

        Comments saved = commentsRepository.save(comment);
        return toResponseDTO(saved);

    }

    /**
     * creates a DTO of comment response
     * 
     * @param c
     * @return
     */
    private CommentResponseDTO toResponseDTO(Comments c) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setCommentId(c.getCommentid());
        dto.setPostId(c.getPostid().getPostid());
        dto.setUserId(c.getUserid().getUserid());
        dto.setParentCommentId(c.getParentCommentid());
        dto.setCommentContent(c.getCommentContent());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }

    /**
     * get a list of comments to a post
     * 
     * @param postId uesd to find the post
     * @return the comments http status 200
     */
    public List<CommentPostResponseDTO> getAllCommentsByPostId(int postId) {
        List<Comments> comments = commentsRepository.findByPostid_Postid(postId);

        return comments.stream()
                .map(this::toCommentPostResponseDTO)
                .toList();
    }

    /**
     * Retrieves all comments for a post.
     *
     * @param postId the ID of the post
     * @param userId the authenticated user's ID
     * @return a list of comments belonging to the post
     */
    public List<CommentPostResponseDTO> getCommentsByPostId(int postId, int userId) {
        List<Comments> comments = commentsRepository.findByPostid_Postid(postId);
        return comments.stream()
                .map(this::toCommentPostResponseDTO)
                .toList();
    }

    /**
     * Retrieves all response comments for a post.
     *
     * @param postId the ID of the post
     * @param userId the authenticated user's ID
     * @return a list of comments belonging to the post
     */
    private CommentPostResponseDTO toCommentPostResponseDTO(Comments c) {
        return new CommentPostResponseDTO(
                c.getCommentid(),
                c.getPostid().getPostid(),
                c.getUserid().getUserid(),
                c.getUserid().getFirstName() + " " + c.getUserid().getLastName(),
                c.getParentCommentid(),
                c.getCommentContent(),
                c.getCreatedAt());
    }

    /**
     * Deletes a comment from a post.
     *
     * @param postId    the ID of the post
     * @param commentId the ID of the comment
     * @param userId    the authenticated user's ID
     */
    public void deleteCommentFromPost(int postId, int commentId, int userId) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (comment.getPostid().getPostid() != postId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found under post");
        }

        if (comment.getUserid().getUserid() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can onlly delete your own comments");
        }

        commentsRepository.delete(comment);
    }
}

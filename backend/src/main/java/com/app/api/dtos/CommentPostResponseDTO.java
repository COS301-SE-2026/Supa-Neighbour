package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Data Transfer Object representing a comment associated with a post.
 * 
 * <p>This DTO contains comprehensive information about a comment, including its
 * identifier, associated post, author details, content, hierarchical structure,
 * and creation timestamp. It is used to transfer comment data between the
 * service layer and API clients.</p>
 * 
 * <p>The {@code parentCommentId} field enables nested/reply comment structures,
 * where a value of {@code null} indicates a top-level comment.</p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2026-07-21
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentPostResponseDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER =
    LoggerFactory.getLogger(CommentPostResponseDTO.class);
    
    @JsonProperty("commentId")
    private final int commentId;
    
    @JsonProperty("postId")
    private final int postId;
    
    @JsonProperty("userId")
    private final int userId;
    
    @JsonProperty("userName")
    private final String userName;
    
    @JsonProperty("content")
    private final String content;
    
    @JsonProperty("parentCommentId")
    private final Integer parentCommentId;
    
    @JsonProperty("createdAt")
    private final Timestamp createdAt;

    /**
     * Constructs a new CommentPostResponseDTO with all required fields.
     * 
     * @param commentId the unique identifier of the comment (must be >= 0)
     * @param postId the identifier of the associated post (must be >= 0)
     * @param userId the identifier of the comment author (must be >= 0)
     * @param userName the display name of the comment author (must not be null or empty)
     * @param parentCommentId the identifier of the parent comment, or {@code null} for top-level comments
     * @param content the actual comment text content (must not be null or empty)
     * @param createdAt the timestamp when the comment was created (must not be null)
     * @throws IllegalArgumentException if commentId, postId, or userId is negative, or if userName/content is empty
     * @throws NullPointerException if userName, content, or createdAt is null
     */
    public CommentPostResponseDTO(int commentId, int postId, int userId, 
                                   String userName, Integer parentCommentId,
                                   String content, Timestamp createdAt) {
        // Validate numeric fields
        if (commentId < 0) {
            throw new IllegalArgumentException("commentId cannot be negative: " + commentId);
        }
        if (postId < 0) {
            throw new IllegalArgumentException("postId cannot be negative: " + postId);
        }
        if (userId < 0) {
            throw new IllegalArgumentException("userId cannot be negative: " + userId);
        }
        
        // Validate required string fields
        this.userName = Objects.requireNonNull(userName, "userName cannot be null");
        if (this.userName.trim().isEmpty()) {
            throw new IllegalArgumentException("userName cannot be empty");
        }
        
        this.content = Objects.requireNonNull(content, "content cannot be null");
        if (this.content.trim().isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        
        // Log creation for debugging
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created CommentPostResponseDTO: commentId={}, userId={}, postId={}", 
                        commentId, userId, postId);
        }
    }

    /**
     * Gets the comment ID with validation.
     * 
     * @return the comment identifier
     * @throws IllegalStateException if commentId is invalid (should never happen)
     */
    public int getCommentId() {
        if (commentId < 0) {
            LOGGER.warn("Accessing invalid commentId: {}", commentId);
        }
        return commentId;
    }

    /**
     * Gets the post ID with validation.
     * 
     * @return the post identifier
     * @throws IllegalStateException if postId is invalid (should never happen)
     */
    public int getPostId() {
        if (postId < 0) {
            LOGGER.warn("Accessing invalid postId: {}", postId);
        }
        return postId;
    }

    /**
     * Gets the user ID with validation.
     * 
     * @return the user identifier
     * @throws IllegalStateException if userId is invalid (should never happen)
     */
    public int getUserId() {
        if (userId < 0) {
            LOGGER.warn("Accessing invalid userId: {}", userId);
        }
        return userId;
    }

    /**
     * Gets the username with null-safety.
     * 
     * @return the username (never null)
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the comment content with null-safety and optional length checking.
     * 
     * @return the comment content (never null)
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the parent comment ID.
     * 
     * @return the parent comment identifier, or {@code null} for top-level comments
     */
    public Integer getParentCommentId() {
        return parentCommentId;
    }

    /**
     * Gets the creation timestamp with null-safety.
     * 
     * @return the creation timestamp (never null)
     */
    public Timestamp getCreatedAt() {
        return new Timestamp(createdAt.getTime()); // Return defensive copy for immutability
    }

    /**
     * Gets the content summary (first 50 characters) for logging purposes.
     * 
     * @return truncated content summary
     */
    public String getContentSummary() {
        if (content == null) {
            return null;
        }
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    /**
     * Checks if this comment is a top-level comment (not a reply).
     * 
     * @return {@code true} if this comment has no parent, {@code false} otherwise
     */
    public boolean isTopLevelComment() {
        return parentCommentId == null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CommentPostResponseDTO that = (CommentPostResponseDTO) obj;
        return commentId == that.commentId &&
               postId == that.postId &&
               userId == that.userId &&
               Objects.equals(userName, that.userName) &&
               Objects.equals(content, that.content) &&
               Objects.equals(parentCommentId, that.parentCommentId) &&
               Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, postId, userId, userName, content, parentCommentId, createdAt);
    }

    @Override
    public String toString() {
        return String.format("CommentPostResponseDTO{commentId=%d, postId=%d, userId=%d, " +
                           "userName='%s', content='%s', parentCommentId=%s, createdAt=%s}",
                           commentId, postId, userId, userName, 
                           getContentSummary(),
                           parentCommentId, createdAt);
    }
}

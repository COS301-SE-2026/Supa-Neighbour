package com.app.api.dtos;

import java.sql.Timestamp;

/**
 * Data Transfer Object representing the public profile of a comment.
 *
 * <p>
 * This response includes the comments on a posts information,
 * trust score, completed task statistics, skills, and reviews.
 * </p>
 */
public class CommentResponseDTO {
    private Integer commentId;
    private Integer postId;
    private Integer userId;
    private Integer parentCommentId;
    private String commentContent;
    private Timestamp createdAt;

    /**
     * Returns the comment identifier.
     *
     * @return the comment ID
     */
    public Integer getCommentId() {
        return commentId;
    }

    /**
     * Sets the comment identifier.
     *
     * @param commentId the comment ID
     */
    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    /**
     * Returns the post identifier.
     *
     * @return the post ID
     */
    public Integer getPostId() {
        return postId;
    }

    /**
     * Sets the post identifier.
     *
     * @param postId the post ID
     */
    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    /**
     * Returns the user identifier.
     *
     * @return the user ID
     */
    public Integer getuserId() {
        return userId;
    }

    /**
     * Sets the user identifier.
     *
     * @param userId the user ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Returns the parent comment identifier.
     *
     * @return the parent comment ID
     */
    public Integer getParentCommentId() {
        return parentCommentId;
    }

    /**
     * Sets the parent comment identifier.
     *
     * @param parentCommentId the parent comment ID
     */
    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    /**
     * Returns the comment content.
     *
     * @return the comment content
     */
    public String getCommentContent() {
        return commentContent;
    }

    /**
     * Sets the comment content.
     *
     * @param commentContent the comment content
     */
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    /**
     * Returns the comment creation timestamp.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the comment creation timestamp.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

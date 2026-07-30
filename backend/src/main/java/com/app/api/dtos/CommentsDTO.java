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
public class CommentsDTO {
    private int commentId;
    private int userId;
    private String authorUsername;
    private Integer parentCommentId;
    private String commentContent;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Creates a comments data transfer object.
     *
     * @param commentId       the unique identifier of the comment
     * @param userId          the unique identifier of the comment's author
     * @param authorUsername  the username of the comment's author
     * @param parentCommentId the identifier of the parent comment, or {@code null}
     *                        if this is a top-level comment
     * @param commentContent  the content of the comment
     * @param createdAt       the timestamp when the comment was created
     * @param updatedAt       the timestamp when the comment was last updated
     */
   
    /**
     * Constructs a new {@code CommentsDTO}.
     *
     * @param commentId the unique identifier of the comment
     * @param userId the unique identifier of the author
     * @param authorUsername the username of the author
     * @param parentCommentId the identifier of the parent comment, or
     *                        {@code null} if this is not a reply
     * @param commentContent the content of the comment
     * @param createdAt the timestamp when the comment was created
     * @param updatedAt the timestamp when the comment was last updated
     */
    public CommentsDTO(int commentId, int userId, String authorUsername, Integer parentCommentId, String commentContent,
            Timestamp createdAt, Timestamp updatedAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.authorUsername = authorUsername;
        this.parentCommentId = parentCommentId;
        this.commentContent = commentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the unique identifier of the comment.
     *
     * @return the comment identifier
     */
    public int getCommentId() {
        return commentId;

    }

    /**
     * Returns the unique identifier of the comment's author.
     *
     * @return the user identifier
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the username of the comment's author.
     *
     * @return the author's username
     */
    public String getAuthorUsername() {
        return authorUsername;
    }

    /**
     * Returns the identifier of the parent comment.
     *
     * @return the parent comment identifier, or {@code null} if this comment
     *         is not a reply
     */
    public Integer getParentCommentId() {
        return parentCommentId;
    }

    /**
     * Returns the content of the comment.
     *
     * @return the comment content
     */
    public String getcommentContent() {
        return commentContent;
    }

    /**
     * Returns the timestamp when the comment was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the timestamp when the comment was last updated.
     *
     * @return the last update timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}

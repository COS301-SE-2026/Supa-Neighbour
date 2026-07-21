package com.app.api.dtos;

import java.sql.Timestamp;

/**
 * Data Transfer Object representing the public profile of a comment.
 *
 * <p>This response includes the comments on a posts information,
 * trust score, completed task statistics, skills, and reviews.</p>
 */
public class CommentsDTO {
    private int commentId;
    private int userId;
    private String authorUsername;
    private Integer parentCommentId;
    private String commentContent;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public CommentsDTO(int commentId, int userId, String authorUsername, Integer parentCommentId, String commentContent, Timestamp createdAt, Timestamp updatedAt){
        this.commentId = commentId;
        this.userId = userId;
        this.authorUsername = authorUsername;
        this.parentCommentId = parentCommentId;
        this.commentContent = commentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the number of unique neighbours helped.
     *
     * @return the number of neighbours helped
     */
    public int getCommentId(){
        return commentId;

    }

    /**
     * Returns the number of unique neighbours helped.
     *
     * @return the number of neighbours helped
     */
    public int getUserId(){
        return userId;
    }

    /**
     * Returns the number of unique neighbours helped.
     *
     * @return the number of neighbours helped
     */
    public String getAuthorUsername(){
        return authorUsername;
    }

        /**
     * Returns the number of unique neighbours helped.
     *
     * @return the number of neighbours helped
     */
    public Integer getParentCommentId(){
        return parentCommentId;
    }

        /**
     * Returns the number of unique neighbours helped.
     *
     * @return the number of neighbours helped
     */
    public String getcommentContent(){
        return commentContent;
    }

        /**
     * Returns the number of unique neighbours helped.
     *
     * @return the number of neighbours helped
     */
    public Timestamp getCreatedAt(){
        return createdAt;
    }

        /**
     * Returns the number of unique neighbours helped.
     *
     * @return the number of neighbours helped
     */
    public Timestamp getUpdatedAt(){
        return updatedAt;
    }
}

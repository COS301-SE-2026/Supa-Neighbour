package com.app.api.dtos;

import java.sql.Timestamp;

/**
 * Data Transfer Object (DTO) representing a comment returned by the API.
 * <p>
 * This object contains the details of a comment, including the associated
 * post, author, optional parent comment, content, and creation timestamp.
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
     * Returns the unique identifier of the comment.
     *
     * @return the comment identifier
     */
    public Integer getCommentId(){
        return commentId;
    }

    /**
     * Sets the unique identifier of the comment.
     *
     * @param commentId the comment identifier
     */
    public void setCommentId(Integer commentId){
        this.commentId = commentId;
    }

    /**
     * Returns the unique identifier of the associated post.
     *
     * @return the post identifier
     */
    public Integer getPostId(){
        return postId;
    }

    /**
     * Sets the unique identifier of the associated post.
     *
     * @param postId the post identifier
     */
    public void setPostId(Integer postId){
        this.postId = postId;
    }

    /**
     * Returns the unique identifier of the comment's author.
     *
     * @return the user identifier
     */
    public Integer getuserId(){
        return userId;
    }

    /**
     * Sets the unique identifier of the comment's author.
     *
     * @param userId the user identifier
     */
    public void setUserId(Integer userId){
        this.userId = userId;
    }

    /**
     * Returns the identifier of the parent comment.
     *
     * @return the parent comment identifier, or {@code null} if this comment
     *         is not a reply
     */
    public Integer getParentCommentId(){
        return parentCommentId;
    }

    /**
     * Sets the identifier of the parent comment.
     *
     * @param parentCommentId the parent comment identifier
     */
    public void setParentCommentId(Integer parentCommentId){
        this.parentCommentId = parentCommentId;
    }

    /**
     * Returns the content of the comment.
     *
     * @return the comment content
     */
    public String getCommentContent(){
        return commentContent;
    }

    /**
     * Sets the content of the comment.
     *
     * @param commentContent the comment content
     */
    public void setCommentContent(String commentContent){
        this.commentContent = commentContent;
    }
    /**
     * Returns the timestamp when the comment was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt(){
        return createdAt;
    }

     /**
     * Sets the timestamp when the comment was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt;
    }
}

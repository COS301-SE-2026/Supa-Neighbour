package com.app.api.dtos;

/**
 * Data Transfer Object (DTO) representing a request to create a comment.
 * <p>
 * This object contains the content of the comment and, optionally, the
 * identifier of the parent comment if the new comment is a reply.
 * </p>
 */
public class CommentRequestDTO {
    private String commentContent;
    private Integer parentCommentId;

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
     * @param parentCommentId the parent comment identifier, or {@code null}
     *                        if this comment is not a reply
     */
    public void setParentCommentId(Integer parentCommentId){
        this.parentCommentId = parentCommentId;
    }
}

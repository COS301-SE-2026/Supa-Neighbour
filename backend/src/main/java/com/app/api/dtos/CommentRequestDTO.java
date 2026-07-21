package com.app.api.dtos;

public class CommentRequestDTO {
    private String commentContent;
    private Integer parentCommentId;

    /**
    * Returns the comment text.
    *
     * @return the comment content
     */
    public String getCommentContent(){
        return commentContent;
    }

    /**
     * Sets the comment text.
     *
     * @param commentContent the comment content
     */
    public void setCommentContent(String commentContent){
        this.commentContent = commentContent;
    }

    /**
     * Returns the parent comment identifier.
     *
    * @return the parent comment ID, or null if this is a top-level comment
    */
    public Integer getParentCommentId(){
        return parentCommentId;
    }

    /**
     * Sets the parent comment identifier.
    *
    * @param parentCommentId the parent comment ID
    */
    public void setParentCommentId(Integer parentCommentId){
        this.parentCommentId = parentCommentId;
    }
}

package com.app.api.dtos;

public class CommentRequestDTO {
    private String commentContent;
    private Integer parentCommentId;

    public String getCommentContent(){
        return commentContent;
    }

    public void setCommentContent(String commentContent){
        this.commentContent = commentContent;
    }

    public Integer getParentCommentId(){
        return parentCommentId;
    }

    public void setParentCommentId(Integer parentCommentId){
        this.parentCommentId = parentCommentId;
    }
}

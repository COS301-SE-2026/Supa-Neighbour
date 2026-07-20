package com.app.api.dtos;
import java.sql.Timestamp;

public class CommentResponseDTO {
    private Integer commentId;
    private Integer postId;
    private Integer userId;
    private Integer parentCommentId;
    private String commentContent;
    private Timestamp createdAt;

    public Integer getCommentId(){
        return commentId;
    }

    public void setCommentId(Integer commentId){
        this.commentId = commentId;
    }

    public Integer getPostId(){
        return postId;
    }

    public void setPostId(Integer postId){
        this.postId = postId;
    }

    public Integer getuserId(){
        return userId;
    }

    public void setUserId(Integer userId){
        this.userId = userId;
    }

    public Integer getParentCommentId(){
        return parentCommentId;
    }

    public void setParentCommentId(Integer parentCommentId){
        this.parentCommentId = parentCommentId;
    }

    public String getCommentContent(){
        return commentContent;
    }

    public void setCommentContent(String commentContent){
        this.commentContent = commentContent;
    }
    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt;
    }
}   

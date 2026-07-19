package com.app.api.dtos;

import java.sql.Timestamp;


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

    public int getCommentId(){
        return commentId;

    }

    public int getUserId(){
        return userId;
    }

    public String getAuthorUsername(){
        return authorUsername;
    }

    public Integer getParentCommentId(){
        return parentCommentId;
    }

    public String getcommentContent(){
        return commentContent;
    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public Timestamp getUpdatedAt(){
        return updatedAt;
    }
}

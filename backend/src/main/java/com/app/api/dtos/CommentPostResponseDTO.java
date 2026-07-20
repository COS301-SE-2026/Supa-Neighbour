package com.app.api.dtos;

import java.time.LocalDate;
import java.sql.Timestamp;

public class CommentPostResponseDTO {
    
    private int commentId;
    private int postId;
    private int userId;
    private String userName;
    private String content;
    private Integer parentCommentId;
    private Timestamp createdAt;

    public CommentPostResponseDTO(Integer commentId, int postId, int userId, String userName,
                                   Integer parentCommentId, String content, Timestamp createdAt) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getCommentId() {
        return commentId;
    }

    public int getPostId() {
        return postId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }

    

    
}

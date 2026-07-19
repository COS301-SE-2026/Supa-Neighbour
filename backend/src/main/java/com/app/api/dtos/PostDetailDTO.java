package com.app.api.dtos;

import java.sql.Timestamp;
import java.util.List;

public class PostDetailDTO {
    private int postId;
    private int userId;
    private String authorUsername;
    private String postContent;
    private String mediaUrl;
    private String category;
    private long likeCount;
    private long dislikeCount;
    private List<CommentsDTO> comments;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PostDetailDTO(int postId, int userId, String authorUsername, String postContent,String mediaUrl, String category, long likeCount, long dislikeCount, List<CommentsDTO> comments, Timestamp createdAt, Timestamp updatedAt){
        this.postId = postId;
        this.userId = userId;
        this.authorUsername = authorUsername;
        this.postContent = postContent;
        this.mediaUrl = mediaUrl;
        this.category = category;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getPostId(){
        return postId;
    }

    public int getUserId(){
        return userId;
    }

    public String getauthorUsername(){
        return authorUsername;
    }

    public String getPostContent(){
        return postContent;
    }

    public String mediaUrl(){
        return mediaUrl;
    }

    public String getCategory(){
        return category;
    }

    public long getlikeCount(){
        return likeCount;
    }

    public long getdislikeCount(){
        return dislikeCount;
    }

    public List<CommentsDTO> getComments(){
        return comments;
    }

    public Timestamp getcreatedAt(){
        return createdAt;
    }

    public Timestamp getUpdatedAt(){
        return updatedAt;
    }
}

package com.app.api.dtos;
import java.sql.Timestamp;

/**
 * Represents a single post item in the bulletin board feed response (7.1).
 * Shape matches the documented API contract exactly.
 */
public class PostFeedItemDTO {
    private int postId;
    private int userId;
    private String authorUsername;
    private String postContent;
    private String mediaUrl;
    private String category;
    private long likeCount;
    private long disLikeCount;
    private long commentCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PostFeedItemDTO(int postId, int userId, String authorUsername, String postContent, String mediaUrl, String category, long LikeCount, long DisLikeCount, long commentCount,Timestamp createdAt, Timestamp updatedAt){
        this.postId = postId;
        this.userId = userId;
        this.authorUsername = authorUsername;
        this.postContent = postContent;
        this.mediaUrl = mediaUrl;
        this.category = category;
        this.likeCount = LikeCount;
        this.disLikeCount = DisLikeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getPostId(){
        return postId;
    }

    public int getUserId(){
        return userId;
    }

    public String getAuthorUsername(){
        return authorUsername;
    }

    public String getPostContent(){
        return postContent;
    }

    public String getMediaUrl(){
        return mediaUrl;
    }

    public String getCategory(){
        return category;
    }

    public long getDislikeCount(){
        return disLikeCount;
    }

    public long getLikeCount(){
        return likeCount;
    }

    public long getCommentCount(){
        return commentCount;
    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public Timestamp getUpdatedAt(){
        return updatedAt;
    }
}

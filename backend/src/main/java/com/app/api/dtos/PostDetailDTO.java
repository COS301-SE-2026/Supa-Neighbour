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

    /**
     * Creates a detailed representation of a post.
     *
     * @param postId         the post ID
     * @param userId         the author's user ID
     * @param authorUsername the author's username
     * @param postContent    the post content
     * @param mediaUrl       the image URL
     * @param category       the post category
     * @param likeCount      the number of likes
     * @param dislikeCount   the number of dislikes
     * @param comments       the comments on the post
     * @param createdAt      the creation timestamp
     * @param updatedAt      the last update timestamp
     */
    public PostDetailDTO(int postId, int userId, String authorUsername, String postContent, String mediaUrl,
            String category, long likeCount, long dislikeCount, List<CommentsDTO> comments, Timestamp createdAt,
            Timestamp updatedAt) {
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

    /**
     * Returns the post identifier.
     *
     * @return the post ID
     */
    public int getPostId() {
        return postId;
    }

    /**
     * Returns the author's user identifier.
     *
     * @return the user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the author's username.
     *
     * @return the author's username
     */
    public String getauthorUsername() {
        return authorUsername;
    }

    /**
     * Returns the post content.
     *
     * @return the post content
     */
    public String getPostContent() {
        return postContent;
    }

    /**
     * Returns the media URL.
     *
     * @return the media URL
     */
    public String mediaUrl() {
        return mediaUrl;
    }

    /**
     * Returns the post category.
     *
     * @return the post category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the number of like reactions.
     *
     * @return the like count
     */
    public long getlikeCount() {
        return likeCount;
    }

    /**
     * Returns the number of dislike reactions.
     *
     * @return the dislike count
     */
    public long getdislikeCount() {
        return dislikeCount;
    }

    /**
     * Returns the comments associated with the post.
     *
     * @return the list of comments
     */
    public List<CommentsDTO> getComments() {
        return comments;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return the creation timestamp
     */
    public Timestamp getcreatedAt() {
        return createdAt;
    }

    /**
     * Returns the updates timestamp.
     *
     * @return the updated timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}

package com.app.api.dtos;

import java.sql.Timestamp;

/**
 * Represents a single post item in the bulletin board feed response.
 * <p>
 * This Data Transfer Object (DTO) contains the details of a post displayed
 * in the bulletin board feed, including the author information, post
 * content, reaction counts, comment count, and timestamps.
 * </p>
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

    /**
     * Constructs a new {@code PostFeedItemDTO}.
     *
     * @param postId the unique identifier of the post
     * @param userId the unique identifier of the author
     * @param authorUsername the username of the author
     * @param postContent the textual content of the post
     * @param mediaUrl the URL of the attached media, if any
     * @param category the category assigned to the post
     * @param likeCount the total number of likes
     * @param dislikeCount the total number of dislikes
     * @param commentCount the total number of comments
     * @param createdAt the timestamp when the post was created
     * @param updatedAt the timestamp when the post was last updated
     */
    /**
     * Creates a new bulletin board feed item.
     *
     * @param postId         the unique identifier of the post
     * @param userId         the unique identifier of the post's author
     * @param authorUsername the username of the post's author
     * @param postContent    the content of the post
     * @param mediaUrl       the URL of any media attached to the post
     * @param category       the category assigned to the post
     * @param likeCount      the number of helpful reactions on the post
     * @param disLikeCount   the number of dislike reactions on the post
     * @param commentCount   the number of comments on the post
     * @param createdAt      the timestamp when the post was created
     * @param updatedAt      the timestamp when the post was last updated
     */
    public PostFeedItemDTO(int postId, int userId, String authorUsername, String postContent, String mediaUrl,
            String category, long likeCount, long disLikeCount, long commentCount, Timestamp createdAt,
            Timestamp updatedAt) {
        this.postId = postId;
        this.userId = userId;
        this.authorUsername = authorUsername;
        this.postContent = postContent;
        this.mediaUrl = mediaUrl;
        this.category = category;
        this.likeCount = likeCount;
        this.disLikeCount = disLikeCount;
        this.commentCount = commentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the unique identifier of the post.
     *
     * @return the post identifier
     */
    /**
     * Returns the unique identifier of the post.
     *
     * @return the post identifier
     */
    public int getPostId() {
        return postId;
    }

    /**
     * Returns the unique identifier of the post's author.
     *
     * @return the user identifier
     */
    /**
     * Returns the unique identifier of the author.
     *
     * @return the author identifier
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the username of the post's author.
     *
     * @return the author's username
     */
    /**
     * Returns the username of the author.
     *
     * @return the author's username
     */
    public String getAuthorUsername() {
        return authorUsername;
    }

    /**
     * Returns the content of the post.
     *
     * @return the post content
     */
    /**
     * Returns the content of the post.
     *
     * @return the post content
     */
    public String getPostContent() {
        return postContent;
    }

    /**
     * Returns the URL of the media attached to the post.
     *
     * @return the media URL, or {@code null} if no media is attached
     */
    /**
     * Returns the URL of the attached media.
     *
     * @return the media URL, or {@code null} if no media is attached
     */
    public String getMediaUrl() {
        return mediaUrl;
    }

    /**
     * Returns the category assigned to the post.
     *
     * @return the post category
     */
    /**
     * Sets the URL of the attached media.
     *
     * @return the media URL, or {@code null} if no media is attached
     */
    public void setMediaUrl(String mediaUrl){
        this.mediaUrl = mediaUrl;
    }

    /**
     * Returns the category of the post.
     *
     * @return the post category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the total number of dislikes on the post.
     *
     * @return the dislike count
     */
    /**
     * Returns the number of dislike reactions on the post.
     *
     * @return the dislike reaction count
     */
    public long getDislikeCount() {
        return disLikeCount;
    }

    /**
     * Returns the total number of likes on the post.
     *
     * @return the like count
     */
    /**
     * Returns the number of helpful reactions on the post.
     *
     * @return the helpful reaction count
     */
    public long getLikeCount() {
        return likeCount;
    }

    /**
     * Returns the total number of comments on the post.
     *
     * @return the comment count
     */
    /**
     * Returns the number of comments on the post.
     *
     * @return the comment count
     */
    public long getCommentCount() {
        return commentCount;
    }

    /**
     * Returns the timestamp when the post was created.
     *
     * @return the creation timestamp
     */
    /**
     * Returns the timestamp when the post was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the timestamp when the post was last updated.
     *
     * @return the last update timestamp
     */
    /**
     * Returns the timestamp when the post was last updated.
     *
     * @return the last updated timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}

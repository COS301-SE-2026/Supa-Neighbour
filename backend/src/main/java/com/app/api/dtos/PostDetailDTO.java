package com.app.api.dtos;

import java.sql.Timestamp;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing the detailed view of a post.
 * <p>
 * This object contains the post's information, reaction counts, associated
 * comments, and timestamps.
 * </p>
 */
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
     * Constructs a new {@code PostDetailDTO}.
     *
     * @param postId the unique identifier of the post
     * @param userId the unique identifier of the author
     * @param authorUsername the username of the author
     * @param postContent the textual content of the post
     * @param mediaUrl the URL of the attached media, if any
     * @param category the category assigned to the post
     * @param likeCount the total number of likes
     * @param dislikeCount the total number of dislikes
     * @param comments the comments associated with the post
     * @param createdAt the timestamp when the post was created
     * @param updatedAt the timestamp when the post was last updated
     */
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

    /**
     * Returns the unique identifier of the post.
     *
     * @return the post identifier
     */
    public int getPostId(){
        return postId;
    }

    /**
     * Returns the unique identifier of the author.
     *
     * @return the author identifier
     */
    public int getUserId(){
        return userId;
    }

    /**
     * Returns the username of the author.
     *
     * @return the author's username
     */
    public String getauthorUsername(){
        return authorUsername;
    }

    /**
     * Returns the content of the post.
     *
     * @return the post content
     */
    public String getPostContent(){
        return postContent;
    }

    /**
     * Returns the URL of the attached media.
     *
     * @return the media URL, or {@code null} if no media is attached
     */
    public String getMediaUrl(){
        return mediaUrl;
    }

    /**
     * Returns the URL of the attached media.
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
    public String getCategory(){
        return category;
    }

    /**
     * Returns the total number of likes on the post.
     *
     * @return the like count
     */
    public long getlikeCount(){
        return likeCount;
    }

    /**
     * Returns the total number of dislikes on the post.
     *
     * @return the dislike count
     */
    public long getdislikeCount(){
        return dislikeCount;
    }

    /**
     * Returns the comments associated with the post.
     *
     * @return the list of comments
     */
    public List<CommentsDTO> getComments(){
        return comments;
    }

    /**
     * Returns the timestamp when the post was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getcreatedAt(){
        return createdAt;
    }

    /**
     * Returns the timestamp when the post was last updated.
     *
     * @return the last update timestamp
     */
    public Timestamp getUpdatedAt(){
        return updatedAt;
    }
}

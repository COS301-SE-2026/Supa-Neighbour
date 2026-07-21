package com.app.api.dtos;

import java.util.List;

/**
 * Full response envelope for GET /api/bulletin/posts (7.1).
 */
public class PostFeedResponseDTO {
    private String neighbourhoodZone;
    private int page;
    private long totalPosts;
    private List<PostFeedItemDTO> posts;

    /**
     * Creates a bulletin board feed item.
     *
     * @param postId         the post identifier
     * @param userId         the author's user identifier
     * @param authorUsername the author's username
     * @param postContent    the post content
     * @param mediaUrl       the media URL
     * @param category       the post category
     * @param likeCount      the number of likes
     * @param disLikeCount   the number of dislikes
     * @param commentCount   the number of comments
     * @param createdAt      the creation timestamp
     * @param updatedAt      the last update timestamp
     */
    public PostFeedResponseDTO(String neighbourhoodZone, int page, long totalPosts, List<PostFeedItemDTO> posts) {
        this.neighbourhoodZone = neighbourhoodZone;
        this.page = page;
        this.totalPosts = totalPosts;
        this.posts = posts;
    }

    /**
     * Returns the neighbourhood zone.
     * 
     * @return the neighbourhood zone (never null)
     */
    public String getNeighbourhoosZone() {
        return neighbourhoodZone;
    }

    /**
     * Returns the current page number.
     * 
     * @return the page number (>= 0)
     */
    public int getPage() {
        return page;
    }

    /**
     * Returns the total number of posts.
     * 
     * @return the total posts count (>= 0)
     */
    public List<PostFeedItemDTO> getPosts() {
        return posts;
    }

    /**
     * Returns the total number of posts.
     * 
     * @return the total posts count (>= 0)
     */
    public long getTotalPosts() {
        return totalPosts;
    }

}

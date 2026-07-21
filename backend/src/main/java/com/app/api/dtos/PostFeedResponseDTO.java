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
     * Constructs a new {@code PostFeedResponseDTO}.
     *
     * @param neighbourhoodZone the name of the neighbourhood
     * @param page the current page number
     * @param totalPosts the total number of posts
     * @param posts the list of posts in the current page
     */
    public PostFeedResponseDTO(String neighbourhoodZone, int page, long totalPosts, List<PostFeedItemDTO> posts){
        this.neighbourhoodZone = neighbourhoodZone;
        this.page = page;
        this.totalPosts = totalPosts;
        this.posts = posts;
    }


    /**
     * Returns the name of the neighbourhood.
     *
     * @return the neighbourhood name
     */
    public String getNeighbourhoodZone(){
        return neighbourhoodZone;
    }

    /**
     * Returns the current page number.
     *
     * @return the page number
     */
    public int getPage(){
        return page;
    }

    /**
     * Returns the posts in the current page.
     *
     * @return the list of posts
     */
    public List<PostFeedItemDTO> getPosts(){
        return posts;
    }
    /**
     * Returns the total number of posts.
     *
     * @return the total number of posts
     */
    public long getTotalPosts(){
        return totalPosts;
    }

}

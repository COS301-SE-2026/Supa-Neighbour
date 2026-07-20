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
   
    public PostFeedResponseDTO(String neighbourhoodZone, int page, long totalPosts, List<PostFeedItemDTO> posts){
        this.neighbourhoodZone = neighbourhoodZone;
        this.page = page;
        this.totalPosts = totalPosts;
        this.posts = posts;
    }

    public String getNeighbourhoosZone(){
        return neighbourhoodZone;
    }

    public int getPage(){
        return page;
    }

    public List<PostFeedItemDTO> getPosts(){
        return posts;
    }

    public long getTotalPosts(){
        return totalPosts;
    }
}

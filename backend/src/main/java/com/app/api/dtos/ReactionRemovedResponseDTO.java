package com.app.api.dtos;

public class ReactionRemovedResponseDTO {
    private String message;
    private Integer postId;
    private long dislikeCount;

    public ReactionRemovedResponseDTO(String message, Integer postId, long dislikeCount){
        this.message = message;
        this.postId = postId;
        this.dislikeCount = dislikeCount;
    }

    public String getMessage(){
        return message;
    }

    public Integer getPostId(){
        return postId;
    }

    public long getDislikedCount(){
        return dislikeCount;
    }
}

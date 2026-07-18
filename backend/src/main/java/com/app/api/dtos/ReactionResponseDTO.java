package com.app.api.dtos;


public class ReactionResponseDTO {
    private String message;
    private Integer postId;
    private String reactionType;
    private long dislikeCount;

    public ReactionResponseDTO(String message, Integer postId, String reactionType, long dislikeCount){
        this.message = message;
        this.postId = postId;
        this.reactionType = reactionType;
        this.dislikeCount = dislikeCount;
    }

    public String getMessage(){
        return message;
    }

    public Integer getPostId(){
        return postId;
    }

    public String getReactionType(){
        return reactionType;
    }

    public long getdislikeCount(){
        return dislikeCount;
    }
}

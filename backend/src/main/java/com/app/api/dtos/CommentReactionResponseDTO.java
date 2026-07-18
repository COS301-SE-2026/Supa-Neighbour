package com.app.api.dtos;


public class CommentReactionResponseDTO {
    private String message;
    private Integer commentId;
    private String reactionType;
    private long dislikeCount;

    public CommentReactionResponseDTO(String message, Integer commentId, String reactionType, long dislikeCount){
        this.message = message;
        this.commentId = commentId;
        this.reactionType = reactionType;
        this.dislikeCount = dislikeCount;
    }

    public String getMessage() { 
        return message; 
    }
    public Integer getCommentId() { 
        return commentId; 
    }
    public String getReactionType() { 
        return reactionType; 
    }
    public long getDisLikeCount() { 
        return dislikeCount; 
    }

}

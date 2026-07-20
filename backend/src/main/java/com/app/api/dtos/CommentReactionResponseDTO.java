package com.app.api.dtos;


/**
 * Data Transfer Object (DTO) representing the response returned after
 * adding a reaction to a comment.
 * <p>
 * This object contains the outcome of the operation, the comment identifier,
 * the reaction type, and the updated dislike count for the comment.
 * </p>
 */
public class CommentReactionResponseDTO {
    private String message;
    private Integer commentId;
    private String reactionType;
    private long dislikeCount;

    /**
     * Constructs a new {@code CommentReactionResponseDTO}.
     *
     * @param message a message describing the result of the operation
     * @param commentId the unique identifier of the comment
     * @param reactionType the type of reaction that was added
     * @param dislikeCount the updated number of dislike reactions
     */
    public CommentReactionResponseDTO(String message, Integer commentId, String reactionType, long dislikeCount){
        this.message = message;
        this.commentId = commentId;
        this.reactionType = reactionType;
        this.dislikeCount = dislikeCount;
    }

    /**
     * Returns the result message.
     *
     * @return the operation result message
     */
    public String getMessage() { 
        return message; 
    }
    /**
     * Returns the identifier of the comment.
     *
     * @return the comment identifier
     */
    public Integer getCommentId() { 
        return commentId; 
    }
    /**
     * Returns the type of reaction.
     *
     * @return the reaction type
     */
    public String getReactionType() { 
        return reactionType; 
    }
    /**
     * Returns the total number of dislike reactions for the comment.
     *
     * @return the dislike count
     */
    public long getDisLikeCount() { 
        return dislikeCount; 
    }

}

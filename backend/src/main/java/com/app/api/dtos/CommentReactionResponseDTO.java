package com.app.api.dtos;

/**
 * Data Transfer Object representing the response returned after
 * adding a reaction to a comment or post.
 */
public class CommentReactionResponseDTO {

    private String message;
    private Integer commentId;
    private String reactionType;
    private long dislikeCount;

    /**
     * Creates a new comment reaction response.
     *
     * @param message the response message
     * @param commentId the ID of the comment
     * @param reactionType the type of reaction
     * @param dislikeCount the current number of dislike reactions
     */
    public CommentReactionResponseDTO(
            String message,
            Integer commentId,
            String reactionType,
            long dislikeCount) {
        this.message = message;
        this.commentId = commentId;
        this.reactionType = reactionType;
        this.dislikeCount = dislikeCount;
    }

    /**
     * Returns the response message.
     *
     * @return the response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the comment identifier.
     *
     * @return the comment ID
     */
    public Integer getCommentId() {
        return commentId;
    }

    /**
     * Returns the reaction type.
     *
     * @return the reaction type
     */
    public String getReactionType() {
        return reactionType;
    }

    /**
     * Returns the number of dislike reactions.
     *
     * @return the dislike count
     */
    public long getDisLikeCount() {
        return dislikeCount;
    }
}


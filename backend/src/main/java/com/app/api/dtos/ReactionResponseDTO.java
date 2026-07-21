package com.app.api.dtos;

/**
 * Data Transfer Object returned after a reaction has been successfully added.
 */
/**
 * Data Transfer Object (DTO) representing the response returned after
 * adding a reaction to a post.
 * <p>
 * This object contains the outcome of the operation, the post identifier,
 * the reaction type, and the updated dislike count for the post.
 * </p>
 */
public class ReactionResponseDTO {
    private String message;
    private Integer postId;
    private String reactionType;
    private long dislikeCount;

    /**
     * Constructs a new {@code ReactionResponseDTO}.
     *
     * @param message a message describing the result of the operation
     * @param postId the unique identifier of the post
     * @param reactionType the type of reaction that was added
     * @param dislikeCount the updated number of dislike reactions
     */
    /**
     * Creates a reaction response.
     *
     * @param message      the confirmation message
     * @param postId       the identifier of the post
     * @param reactionType the type of reaction that was added
     * @param dislikeCount the updated number of dislike reactions
     */
    public ReactionResponseDTO(String message, Integer postId, String reactionType, long dislikeCount) {
        this.message = message;
        this.postId = postId;
        this.reactionType = reactionType;
        this.dislikeCount = dislikeCount;
    }

    /**
     * Returns the result message.
     *
     * @return the operation result message
     */
    /**
     * Returns the confirmation message.
     *
     * @return the confirmation message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the identifier of the post.
     *
     * @return the post identifier
     */
    /**
     * Returns the identifier of the post.
     *
     * @return the post identifier
     */
    public Integer getPostId() {
        return postId;
    }

    /**
     * Returns the type of reaction.
     *
     * @return the reaction type
     */
    /**
     * Returns the type of reaction that was added.
     *
     * @return the reaction type
     */
    public String getReactionType() {
        return reactionType;
    }

    /**
     * Returns the total number of dislike reactions for the post.
     *
     * @return the dislike count
     */
    /**
     * Returns the updated dislike reaction count.
     *
     * @return the number of dislike reactions
     */
    public long getdislikeCount() {
        return dislikeCount;
    }
}

package com.app.api.dtos;

/**
 * Data Transfer Object (DTO) representing the response returned after
 * removing a reaction from a post.
 * <p>
 * This object contains the outcome of the operation, the post identifier,
 * and the updated dislike count for the post.
 * </p>
 */
/**
 * Data Transfer Object returned after a reaction has been successfully removed.
 */
public class ReactionRemovedResponseDTO {
    private String message;
    private Integer postId;
    private long dislikeCount;

    /**
     * Constructs a new {@code ReactionRemovedResponseDTO}.
     *
     * @param message a message describing the result of the operation
     * @param postId the unique identifier of the post
     * @param dislikeCount the updated number of dislike reactions
     */
    /**
     * Creates a reaction removal response.
     *
     * @param message      the confirmation message
     * @param postId       the identifier of the post
     * @param dislikeCount the updated number of dislike reactions
     */
    public ReactionRemovedResponseDTO(String message, Integer postId, long dislikeCount) {
        this.message = message;
        this.postId = postId;
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
     * Returns the total number of dislike reactions remaining on the post.
     *
     * @return the dislike count
     */
    /**
     * Returns the updated dislike reaction count.
     *
     * @return the number of dislike reactions
     */
    public long getDislikedCount() {
        return dislikeCount;
    }
}

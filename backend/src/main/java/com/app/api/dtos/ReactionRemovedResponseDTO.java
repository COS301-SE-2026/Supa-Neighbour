package com.app.api.dtos;

/**
 * Data Transfer Object returned after a reaction has been successfully removed.
 */
public class ReactionRemovedResponseDTO {
    private String message;
    private Integer postId;
    private long dislikeCount;

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
    public Integer getPostId() {
        return postId;
    }

    /**
     * Returns the updated dislike reaction count.
     *
     * @return the number of dislike reactions
     */
    public long getDislikedCount() {
        return dislikeCount;
    }
}

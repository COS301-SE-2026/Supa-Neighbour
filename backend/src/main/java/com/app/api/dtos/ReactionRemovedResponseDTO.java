package com.app.api.dtos;

/**
 * Data Transfer Object (DTO) representing the response returned after
 * removing a reaction from a post.
 * <p>
 * This object contains the outcome of the operation, the post identifier,
 * and the updated dislike count for the post.
 * </p>
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
    public ReactionRemovedResponseDTO(String message, Integer postId, long dislikeCount){
        this.message = message;
        this.postId = postId;
        this.dislikeCount = dislikeCount;
    }

    /**
     * Returns the result message.
     *
     * @return the operation result message
     */
    public String getMessage(){
        return message;
    }

    /**
     * Returns the identifier of the post.
     *
     * @return the post identifier
     */
    public Integer getPostId(){
        return postId;
    }

    /**
     * Returns the total number of dislike reactions remaining on the post.
     *
     * @return the dislike count
     */
    public long getDislikedCount(){
        return dislikeCount;
    }
}

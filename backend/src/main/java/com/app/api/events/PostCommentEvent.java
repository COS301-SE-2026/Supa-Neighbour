package com.app.api.events;

public class PostCommentEvent {
    private final int postAuthorUserId;
    private final int postId;
    private final String commentorName;

    /**
     * Constructs a new PostCommentEvent with the specified post author, post, and commentor details.
     * 
     * @param postAuthorUserId The unique identifier of the user who created the original post
     *                         and will receive the notification
     * @param postId The unique identifier of the post that received the new comment
     * @param commentorName The display name or username of the user who added the comment
     * @throws IllegalArgumentException if postAuthorUserId or postId is less than or equal to 0,
     *         or if commentorName is null or empty
     */
    public PostCommentEvent(int postAuthorUserId, int postId, String commentorName){
        this.postAuthorUserId = postAuthorUserId;
        this.postId = postId;
        this.commentorName = commentorName;
    }

    /**
     * Returns the unique identifier of the post author who should be notified
     * about the new comment on their post.
     * 
     * @return The post author's user ID
     */
    public int getPostAuthorUserId(){
        return postAuthorUserId;
    }

    /**
     * Returns the unique identifier of the post that received the new comment.
     * 
     * @return The post ID
     */
    public int getPostId(){
        return postId;
    }

    /**
     * Returns the display name or username of the user who added the comment.
     * 
     * <p>This name is used in notification messages to inform the post author
     * about who commented on their post (e.g., "{commentorName} commented on your post").
     * 
     * @return The commentor's display name
     */
    public String getCommentorName(){
        return commentorName;
    }
}

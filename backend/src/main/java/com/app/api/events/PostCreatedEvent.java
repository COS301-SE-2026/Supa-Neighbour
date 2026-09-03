package com.app.api.events;
import java.util.List;

import com.app.api.listeners.TaskNotificationListener;


public class PostCreatedEvent {
    private final List<Integer> recipientUserIds;
    private final int postId;
    private final String postPreview;

    /**
     * Constructs a new PostCreatedEvent with the specified recipients, post, and preview information.
     * 
     * @param recipientUserIds The list of user IDs who should receive notifications
     *                         about this new post creation
     * @param postId The unique identifier of the newly created post
     * @param postPreview A brief preview or summary of the post content for
     *                    inclusion in notification messages
     * @throws IllegalArgumentException if recipientUserIds is null or empty,
     *         if postId is less than or equal to 0,
     *         or if postPreview is null or empty
     */
    public PostCreatedEvent(List<Integer> recipientUserIds, int postId, String postPreview){
        this.recipientUserIds = recipientUserIds;
        this.postId = postId;
        this.postPreview = postPreview;
    }

    /**
     * Returns the list of user IDs who should receive notifications about
     * the newly created post.
     * 
     * <p>The notification service iterates through this list and sends
     * individual notifications to each recipient.
     * 
     * @return An immutable list of recipient user IDs
     * @see TaskNotificationListener#onPostCreated(PostCreatedEvent)
     */
    public List<Integer> getRecipientUserIds(){
        return recipientUserIds;
    }

    /**
     * Returns the unique identifier of the newly created post.
     * 
     * <p>This ID is used to link the notification to the specific post
     * content and for generating deep links to the post.
     * 
     * @return The post ID
     */
    public int getPostById(){
        return postId;
    }

    /**
     * Returns a brief preview or summary of the post content.
     * 
     * <p>The preview is typically a truncated version of the post content
     * (e.g., first 100 characters) and is used in notification messages to
     * give recipients context about what was posted without requiring them
     * to immediately visit the full post.
     * 
     * @return The post preview text
     */
    public String getPostPreview(){
        return postPreview;
    }
}

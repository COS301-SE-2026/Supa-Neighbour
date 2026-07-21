package com.app.api.dtos;

public class CreatePostRequest {
    private String postContent;
    private String mediaUrl;
    private String category;

    /**
     * Creates an empty post creation request.
     */
    public CreatePostRequest() {

    }

    /**
     * Creates a new post creation request.
     *
     * @param postContent the post content
     * @param mediaUrl    the image URL
     * @param category    the post category
     */
    public CreatePostRequest(String postContent, String mediaUrl, String category) {
        this.postContent = postContent;
        this.mediaUrl = mediaUrl;
        this.category = category;
    }

    /**
     * Returns the post content.
     *
     * @return the post content
     */
    public String getPostContent() {
        return postContent;
    }

    /**
     * Sets the post content.
     *
     * @param postContent the post content
     */
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    /**
     * Returns the media URL.
     *
     * @return the media URL
     */
    public String getMediaUrl() {
        return mediaUrl;
    }

    /**
     * Sets the media URL.
     *
     * @param mediaUrl the media URL
     */
    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    /**
     * Returns the post category.
     *
     * @return the post category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the post category.
     *
     * @param category the post category
     */
    public void setCategory(String category) {
        this.category = category;
    }
}

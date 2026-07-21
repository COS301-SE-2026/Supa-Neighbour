package com.app.api.dtos;


/**
 * Data Transfer Object (DTO) representing a request to create a new post.
 * <p>
 * This object contains the post content, an optional media URL, and the
 * category associated with the post.
 * </p>
 */
public class CreatePostRequest {
    private String postContent;
    private String mediaUrl;
    private String category;


    /**
     * Default contructor
     */
    public CreatePostRequest(){

    }
    /**
     * Constructs a new {@code CreatePostRequest}.
     *
     * @param postContent the textual content of the post
     * @param mediaUrl the URL of the attached media, if any
     * @param category the category assigned to the post
     */
    public CreatePostRequest(String postContent, String mediaUrl, String category){
        this.postContent = postContent;
        this.mediaUrl = mediaUrl;
        this.category = category;
    }

    /**
     * Returns the content of the post.
     *
     * @return the post content
     */
    public String getPostContent(){
        return postContent;
    }

    /**
     * Sets the content of the post.
     *
     * @param postContent the post content
     */
    public void setPostContent(String postContent){
        this.postContent = postContent;
    }

    /**
     * Returns the URL of the attached media.
     *
     * @return the media URL, or {@code null} if no media is attached
     */
    public String getMediaUrl(){
        return mediaUrl;
    }

    /**
     * Sets the URL of the attached media.
     *
     * @param mediaUrl the media URL
     */
    public void setMediaUrl(String mediaUrl){
        this.mediaUrl = mediaUrl;
    }

    /**
     * Returns the category of the post.
     *
     * @return the post category
     */
    public String getCategory(){
        return category;
    }

    /**
     * Sets the category of the post.
     *
     * @param category the post category
     */
    public void setCategory(String category){
        this.category = category;
    }
}

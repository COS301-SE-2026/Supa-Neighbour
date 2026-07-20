package com.app.api.dtos;

import com.google.api.services.storage.Storage.Projects.HmacKeys.Create;

public class CreatePostRequest {
    private String postContent;
    private String mediaUrl;
    private String category;

    public CreatePostRequest(){

    }
    public CreatePostRequest(String postContent, String mediaUrl, String category){
        this.postContent = postContent;
        this.mediaUrl = mediaUrl;
        this.category = category;
    }

    public String getPostContent(){
        return postContent;
    }

    public void setPostContent(String postContent){
        this.postContent = postContent;
    }

    public String getMediaUrl(){
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl){
        this.mediaUrl = mediaUrl;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }
}


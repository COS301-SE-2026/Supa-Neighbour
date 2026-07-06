package com.app.api.dtos;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingResponse {
    private String message;
    private int taskId;
    private String reviewSnippet;
    private String rating;

    public RatingResponse(String message, int taskId, String rating, String reviewString){
        this.message = message;
        this.taskId = taskId;
        this.rating = rating;
        this.reviewSnippet = reviewString;
    }

    public String getMessage(){
        return message;
    }

    public int getTaskId(){
        return taskId;
    }

    public String getRating(){
        return rating;
    }

    public String getReviewSnippet(){
        return reviewSnippet;
    }
}

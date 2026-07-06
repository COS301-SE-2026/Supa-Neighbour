package com.app.api.dtos;
import jakarta.validation.constraints.NotBlank;

public class RatingRequest {
    @NotBlank(message = "rating is required")
    private String rating;

    private String reviewSnippet;

    public String getRating(){
        return rating;
    }

    public String getReviewSnippet(){
        return reviewSnippet;
    }

    public void setRating(String rating){
        this.rating = rating;
    }

    public void setReviewSnippet(String reviewSnippet){
        this.reviewSnippet = reviewSnippet;
    }

}

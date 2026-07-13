package com.app.api.dtos;
import jakarta.validation.constraints.NotBlank;


/**
 * Data Transfer Object representing a request to submit a task rating.
 *
 * <p>The request contains the selected rating and an optional review
 * snippet provided by the dependent after a completed task.</p>
 */
public class RatingRequest {
    @NotBlank(message = "rating is required")
    private String rating;

    private String reviewSnippet;

    /**
     * Returns the selected rating.
     *
     * @return the rating value
     */
    public String getRating(){
        return rating;
    }

    /**
     * Returns the optional review snippet.
     *
     * @return the review snippet, or {@code null} if none was provided
     */
    public String getReviewSnippet(){
        return reviewSnippet;
    }

     /**
     * Sets the rating value.
     *
     * @param rating the rating to assign
     */
    public void setRating(String rating){
        this.rating = rating;
    }

    /**
     * Sets the optional review snippet.
     *
     * @param reviewSnippet the review snippet to assign
     */
    public void setReviewSnippet(String reviewSnippet){
        this.reviewSnippet = reviewSnippet;
    }
}

package com.app.api.dtos;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


/**
 * Data Transfer Object representing a request to submit a task rating.
 *
 * <p>The request contains the selected rating and an optional review
 * snippet provided by the dependent after a completed task.</p>
 */
public class RatingRequest {
    @NotNull(message = "rating is required")
    @Min(value = 1, message = "rating must be between 1 and 5")
    @Max(value = 5, message = "rating must be between 1 and 5")
    private Integer rating;

    private String reviewSnippet;

    /**
     * Returns the selected rating.
     *
     * @return the rating value
     */
    public Integer getRating(){
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
    public void setRating(Integer rating){
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

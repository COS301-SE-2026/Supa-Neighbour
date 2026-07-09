package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Data Transfer Object representing a review left for a helper.
 *
 * <p>This DTO contains the rating provided by the dependent, an optional
 * review snippet, and the date on which the review was recorded.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDTO {
    private String rating;
    private String snippet;
    private String date;

    /**
     * Creates a review data transfer object.
     *
     * @param rating the rating assigned to the helper
     * @param snippet the optional review snippet
     * @param date the date the review was recorded
     */
    public ReviewDTO(String rating, String snippet, String date){
        this.rating = rating;
        this.snippet = snippet;
        this.date = date;
    }

    /**
     * Returns the rating assigned to the helper.
     *
     * @return the rating
     */
    public String getRating(){
        return rating;
    }
    /**
     * Returns the review snippet.
     *
     * @return the review snippet, or {@code null} if none was provided
     */
    public String getSnippet(){
        return snippet;
    }

    /**
     * Returns the date the review was recorded.
     *
     * @return the review date
     */
    public String getDate(){
        return date;
    }

}

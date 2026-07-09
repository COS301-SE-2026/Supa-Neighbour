package com.app.api.dtos;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object representing the response returned after a
 * rating has been successfully submitted.
 *
 * <p>The response contains a confirmation message, the task identifier,
 * the submitted rating, and the optional review snippet.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingResponse {
    private String message;
    private int taskId;
    private String reviewSnippet;
    private String rating;

    /**
     * Creates a rating response.
     *
     * @param message the confirmation message
     * @param taskId the identifier of the rated task
     * @param rating the submitted rating
     * @param reviewString the optional review snippet
     */
    public RatingResponse(String message, int taskId, String rating, String reviewString){
        this.message = message;
        this.taskId = taskId;
        this.rating = rating;
        this.reviewSnippet = reviewString;
    }


    /**
     * Returns the confirmation message.
     *
     * @return the confirmation message
     */
    public String getMessage(){
        return message;
    }

    /**
     * Returns the identifier of the rated task.
     *
     * @return the task identifier
     */
    public int getTaskId(){
        return taskId;
    }

    /**
     * Returns the submitted rating.
     *
     * @return the rating
     */
    public String getRating(){
        return rating;
    }

    /**
     * Returns the submitted review snippet.
     *
     * @return the review snippet, or {@code null} if none was provided
     */
    public String getReviewSnippet(){
        return reviewSnippet;
    }
}

package com.app.api.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The verification result returned to the app after a completion photo is submitted.
 * <p>
 * Deliberately contains no task coordinates and no image URL: the resident's location is private,
 * and stored images are read through short-lived SAS URLs, not raw blob URLs.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationResultDTO {

    private int verificationId;
    private int taskId;
    private String status;
    private Double score;
    private Boolean locationVerified;
    private Double distanceM;
    private Double geofenceRadiusM;
    private List<String> reasons;
    private String aiInsight;
    private Integer completionImageId;

    /**
     * Default constructor required for JSON (de)serialisation.
     */
    public VerificationResultDTO() {
    }

    /**
     * Creates a fully populated result.
     *
     * @param verificationId    the id of the stored verification row
     * @param taskId            the task the photo was submitted for
     * @param status            VERIFIED, NEEDS_REVIEW or FAILED
     * @param score             the combined score between 0 and 1
     * @param locationVerified  true/false when a location check was possible, otherwise null
     * @param distanceM         distance in metres between the helper and the task, or null
     * @param geofenceRadiusM   the radius the distance was checked against
     * @param reasons           reason codes explaining the status (may be empty)
     * @param aiInsight         the model's one-sentence insight, or null if the AI was unavailable
     * @param completionImageId the stored completion image, or null if none was stored (exact duplicate)
     */
    public VerificationResultDTO(int verificationId, int taskId, String status, Double score,
            Boolean locationVerified, Double distanceM, Double geofenceRadiusM, List<String> reasons,
            String aiInsight, Integer completionImageId) {
        this.verificationId = verificationId;
        this.taskId = taskId;
        this.status = status;
        this.score = score;
        this.locationVerified = locationVerified;
        this.distanceM = distanceM;
        this.geofenceRadiusM = geofenceRadiusM;
        this.reasons = reasons;
        this.aiInsight = aiInsight;
        this.completionImageId = completionImageId;
    }

    /**
     * Gets the verification id.
     *
     * @return the verification id
     */
    public int getVerificationId() {
        return verificationId;
    }

    /**
     * Gets the task id.
     *
     * @return the task id
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * Gets the verification status.
     *
     * @return VERIFIED, NEEDS_REVIEW or FAILED
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the combined score.
     *
     * @return the score between 0 and 1
     */
    public Double getScore() {
        return score;
    }

    /**
     * Gets whether the helper was inside the geofence.
     *
     * @return true/false, or null if the location could not be checked
     */
    public Boolean getLocationVerified() {
        return locationVerified;
    }

    /**
     * Gets the distance between the helper and the task.
     *
     * @return the distance in metres, or null if unknown
     */
    public Double getDistanceM() {
        return distanceM;
    }

    /**
     * Gets the geofence radius used.
     *
     * @return the radius in metres
     */
    public Double getGeofenceRadiusM() {
        return geofenceRadiusM;
    }

    /**
     * Gets the reason codes.
     *
     * @return reason codes explaining the status
     */
    public List<String> getReasons() {
        return reasons;
    }

    /**
     * Gets the AI insight.
     *
     * @return the one-sentence insight, or null
     */
    public String getAiInsight() {
        return aiInsight;
    }

    /**
     * Gets the stored completion image id.
     *
     * @return the image id, or null if no image was stored
     */
    public Integer getCompletionImageId() {
        return completionImageId;
    }
}

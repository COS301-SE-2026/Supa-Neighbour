package com.app.api.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single image attached to a task.
 * A task can have multiple images (one-to-many from TaskInvoice).
 *
 * Images can be either reference images or completion images.
 * Completion images may additionally contain capture metadata used
 * for validation and anti-spoofing.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_image_table")
public class TaskImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_image_id")
    private int taskImageId;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskInvoice taskid;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    /**
     * Indicates whether the image is a reference image or
     * a completion image.
     */
    @Column(name = "image_type", nullable = false)
    private String imageType;

    /**
     * AI-generated labels associated with the image.
     * Stored as JSON in the database.
     */
    @Column(name = "ai_labels", columnDefinition = "jsonb")
    private String aiLabels;

    /**
     * Confidence score produced by the AI analysis.
     */
    @Column(name = "ai_confidence")
    private Double aiConfidence;

    /**
     * AI-generated insight about the image.
     */
    @Column(name = "ai_insight")
    private String aiInsight;

    /**
     * Source from which the image was captured.
     */
    @Column(name = "capture_source")
    private String captureSource;

    /**
     * Timestamp recorded by the client when the image was captured.
     */
    @Column(name = "client_captured_at")
    private LocalDateTime clientCapturedAt;

    /**
     * Latitude recorded by the client at capture time.
     */
    @Column(name = "client_lat")
    private Double clientLat;

    /**
     * Longitude recorded by the client at capture time.
     */
    @Column(name = "client_lng")
    private Double clientLng;

    /**
     * GPS accuracy reported by the client in metres.
     */
    @Column(name = "client_accuracy_m")
    private Double clientAccuracyM;

    /**
     * Identifier for the device used to capture the image.
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     * Camera manufacturer.
     */
    @Column(name = "camera_make")
    private String cameraMake;

    /**
     * Camera model.
     */
    @Column(name = "camera_model")
    private String cameraModel;

    /**
     * Indicates whether GPS information was available
     * when the image was captured.
     */
    @Column(name = "has_gps")
    private Boolean hasGps;

    /**
     * Indicates whether the client capture timestamp
     * passed the capture-time validation.
     */
    @Column(name = "capture_time_valid")
    private Boolean captureTimeValid;

    /**
     * SHA-256 or equivalent hash of the image used to
     * detect duplicate images.
     */
    @Column(name = "image_hash", length = 64)
    private String imageHash;

    @Column(name = "uploaded_at", insertable = false, updatable = false)
    private LocalDateTime uploadedAt;

    /**
     * Gets the task image identifier.
     *
     * @return the task image id
     */
    public int getTaskImageId() {
        return taskImageId;
    }

    /**
     * Sets the task image identifier.
     *
     * @param taskImageId the task image id
     */
    public void setTaskImageId(int taskImageId) {
        this.taskImageId = taskImageId;
    }

    /**
     * Gets the task this image belongs to.
     *
     * @return the task
     */
    public TaskInvoice getTaskid() {
        return taskid;
    }

    /**
     * Sets the task this image belongs to.
     *
     * @param taskid the task
     */
    public void setTaskid(TaskInvoice taskid) {
        this.taskid = taskid;
    }

    /**
     * Gets the URL of the image.
     *
     * @return the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image.
     *
     * @param imageUrl the image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the image type.
     *
     * @return the image type
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * Sets the image type.
     *
     * @param imageType the image type
     */
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    /**
     * Gets the AI labels.
     *
     * @return the AI labels as JSON
     */
    public String getAiLabels() {
        return aiLabels;
    }

    /**
     * Sets the AI labels.
     *
     * @param aiLabels the AI labels as JSON
     */
    public void setAiLabels(String aiLabels) {
        this.aiLabels = aiLabels;
    }

    /**
     * Gets the AI confidence score.
     *
     * @return the AI confidence score
     */
    public Double getAiConfidence() {
        return aiConfidence;
    }

    /**
     * Sets the AI confidence score.
     *
     * @param aiConfidence the AI confidence score
     */
    public void setAiConfidence(Double aiConfidence) {
        this.aiConfidence = aiConfidence;
    }

    /**
     * Gets the AI insight.
     *
     * @return the AI insight
     */
    public String getAiInsight() {
        return aiInsight;
    }

    /**
     * Sets the AI insight.
     *
     * @param aiInsight the AI insight
     */
    public void setAiInsight(String aiInsight) {
        this.aiInsight = aiInsight;
    }

    /**
     * Gets the capture source.
     *
     * @return the capture source
     */
    public String getCaptureSource() {
        return captureSource;
    }

    /**
     * Sets the capture source.
     *
     * @param captureSource the capture source
     */
    public void setCaptureSource(String captureSource) {
        this.captureSource = captureSource;
    }

    /**
     * Gets the client capture timestamp.
     *
     * @return the client capture timestamp
     */
    public LocalDateTime getClientCapturedAt() {
        return clientCapturedAt;
    }

    /**
     * Sets the client capture timestamp.
     *
     * @param clientCapturedAt the client capture timestamp
     */
    public void setClientCapturedAt(LocalDateTime clientCapturedAt) {
        this.clientCapturedAt = clientCapturedAt;
    }

    /**
     * Gets the client latitude.
     *
     * @return the client latitude
     */
    public Double getClientLat() {
        return clientLat;
    }

    /**
     * Sets the client latitude.
     *
     * @param clientLat the client latitude
     */
    public void setClientLat(Double clientLat) {
        this.clientLat = clientLat;
    }

    /**
     * Gets the client longitude.
     *
     * @return the client longitude
     */
    public Double getClientLng() {
        return clientLng;
    }

    /**
     * Sets the client longitude.
     *
     * @param clientLng the client longitude
     */
    public void setClientLng(Double clientLng) {
        this.clientLng = clientLng;
    }

    /**
     * Gets the client GPS accuracy.
     *
     * @return the GPS accuracy in metres
     */
    public Double getClientAccuracyM() {
        return clientAccuracyM;
    }

    /**
     * Sets the client GPS accuracy.
     *
     * @param clientAccuracyM the GPS accuracy in metres
     */
    public void setClientAccuracyM(Double clientAccuracyM) {
        this.clientAccuracyM = clientAccuracyM;
    }

    /**
     * Gets the device identifier.
     *
     * @return the device identifier
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device identifier.
     *
     * @param deviceId the device identifier
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the camera manufacturer.
     *
     * @return the camera manufacturer
     */
    public String getCameraMake() {
        return cameraMake;
    }

    /**
     * Sets the camera manufacturer.
     *
     * @param cameraMake the camera manufacturer
     */
    public void setCameraMake(String cameraMake) {
        this.cameraMake = cameraMake;
    }

    /**
     * Gets the camera model.
     *
     * @return the camera model
     */
    public String getCameraModel() {
        return cameraModel;
    }

    /**
     * Sets the camera model.
     *
     * @param cameraModel the camera model
     */
    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    /**
     * Gets whether GPS information was available.
     *
     * @return true if GPS was available
     */
    public Boolean getHasGps() {
        return hasGps;
    }

    /**
     * Sets whether GPS information was available.
     *
     * @param hasGps whether GPS was available
     */
    public void setHasGps(Boolean hasGps) {
        this.hasGps = hasGps;
    }

    /**
     * Gets whether the capture time was valid.
     *
     * @return true if the capture time was valid
     */
    public Boolean getCaptureTimeValid() {
        return captureTimeValid;
    }

    /**
     * Sets whether the capture time was valid.
     *
     * @param captureTimeValid whether the capture time was valid
     */
    public void setCaptureTimeValid(Boolean captureTimeValid) {
        this.captureTimeValid = captureTimeValid;
    }

    /**
     * Gets the image hash.
     *
     * @return the image hash
     */
    public String getImageHash() {
        return imageHash;
    }

    /**
     * Sets the image hash.
     *
     * @param imageHash the image hash
     */
    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }

    /**
     * Gets the timestamp when the image was uploaded.
     *
     * @return the upload timestamp
     */
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}

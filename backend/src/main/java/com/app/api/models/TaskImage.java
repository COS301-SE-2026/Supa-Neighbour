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
     * Gets the timestamp when the image was uploaded.
     *
     * @return the upload timestamp
     */
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}

package com.app.api.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_image_table")
public class ReportImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_image_id")
    private Integer reportImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    /**
     * Default no-argument constructor required by JPA.
     *
     * <p>This constructor is declared {@code protected} to prevent direct
     * instantiation by application code while still allowing the JPA provider
     * to create instances via reflection.</p>
     */
    protected ReportImage() {
        // JPA requires a no-arg constructor
    }

    /**
     * Constructs a new {@code ReportImage} with the specified report and image URL.
     *
     * <p>The {@code uploadedAt} timestamp is not set by this constructor and
     * should be assigned separately if required.</p>
     *
     * @param report   the {@link Report} to which this image belongs; must not be {@code null}
     * @param imageUrl the URL of the image; must not be {@code null}
     */
    public ReportImage(Report report, String imageUrl) {
        this.report = report;
        this.imageUrl = imageUrl;
    }

    /**
     * Returns the unique identifier of this report image.
     *
     * @return the report image ID, or {@code null} if the entity has not yet been persisted
     */
    public Integer getReportImageId() {
        return reportImageId;
    }

     /**
     * Returns the {@link Report} associated with this image.
     *
     * @return the owning report; never {@code null} for a persisted entity
     */
    public Report getReport() {
        return report;
    }

    /**
     * Sets the {@link Report} associated with this image.
     *
     * @param report the report to associate with this image; must not be {@code null}
     */
    public void setReport(Report report) {
        this.report = report;
    }

    /**
     * Returns the URL of the image.
     *
     * @return the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

     /**
     * Sets the URL of the image.
     *
     * @param imageUrl the image URL to set; must not be {@code null}
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns the date and time when the image was uploaded.
     *
     * @return the upload timestamp, or {@code null} if not set
     */
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}

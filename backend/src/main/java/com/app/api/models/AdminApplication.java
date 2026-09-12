package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "admin_application_table")
public class AdminApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "application_status", nullable = false, length = 20)
    private String applicationStatus; // "Pending" | "Approved" | "Rejected"

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedByUser; // nullable until reviewed

    @Column(name = "reviewed_date")
    private LocalDate reviewedDate;

    @Column(name = "justification", columnDefinition = "text")
    private String justification;

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;

     /**
     * Protected no-arg constructor required by JPA.
     *
     * <p>Should not be called directly by application code; use
     * {@link #AdminApplication(User, String)} instead.</p>
     */
    protected AdminApplication() {
    }

    /**
     * Creates a new admin application for the given user with a justification.
     *
     * <p>The application is initialized with a status of {@code "Pending"} and
     * an {@link #applicationDate} set to the current date.</p>
     *
     * @param user          the user submitting the application; must not be {@code null}
     * @param justification the reason the user is applying for admin privileges
     */
    public AdminApplication(User user, String justification) {
        this.user = user;
        this.justification = justification;
        this.applicationStatus = "Pending";
        this.applicationDate = LocalDate.now();
    }


    /**
     * Returns the unique identifier of this application.
     *
     * @return the application ID, or {@code null} if the entity has not yet been persisted
     */
    public Integer getApplicationId() {
        return applicationId;
    }

    /**
     * Returns the user who submitted this application.
     *
     * @return the applying {@link User}
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the current status of this application.
     *
     * @return the status, one of {@code "Pending"}, {@code "Approved"}, or {@code "Rejected"}
     */
    public String getApplicationStatus() {
        return applicationStatus;
    }

    /**
     * Updates the status of this application.
     *
     * @param applicationStatus the new status, one of {@code "Pending"},
     *                          {@code "Approved"}, or {@code "Rejected"}
     */
    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    /**
     * Returns the date on which this application was submitted.
     *
     * @return the application date
     */
    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    /**
     * Returns the user who reviewed this application.
     *
     * @return the reviewing {@link User}, or {@code null} if the application
     *         has not yet been reviewed
     */
    public User getReviewedByUser() {
        return reviewedByUser;
    }

    /**
     * Sets the user who reviewed this application.
     *
     * @param reviewedByUser the reviewing {@link User}
     */
    public void setReviewedByUser(User reviewedByUser) {
        this.reviewedByUser = reviewedByUser;
    }

    /**
     * Returns the date on which this application was reviewed.
     *
     * @return the review date, or {@code null} if the application has not yet been reviewed
     */
    public LocalDate getReviewedDate() {
        return reviewedDate;
    }

     /**
     * Sets the date on which this application was reviewed.
     *
     * @param reviewedDate the review date
     */
    public void setReviewedDate(LocalDate reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    /**
     * Returns the justification provided by the applicant.
     *
     * @return the justification text, or {@code null} if none was provided
     */
    public String getJustification() {
        return justification;
    }

    /**
     * Returns the reason this application was rejected, if applicable.
     *
     * @return the rejection reason, or {@code null} if the application was
     *         not rejected or no reason was recorded
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

     /**
     * Sets the reason this application was rejected.
     *
     * @param rejectionReason the reason for rejection
     */
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}

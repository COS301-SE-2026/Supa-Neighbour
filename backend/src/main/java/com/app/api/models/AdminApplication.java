package com.app.api.models;

import jakarta.persistence.*;
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

    protected AdminApplication() {
        // required by JPA
    }

    public AdminApplication(User user, String justification) {
        this.user = user;
        this.justification = justification;
        this.applicationStatus = "Pending";
        this.applicationDate = LocalDate.now();
    }

    // --- Getters and setters ---

    public Integer getApplicationId() {
        return applicationId;
    }

    public User getUser() {
        return user;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public User getReviewedByUser() {
        return reviewedByUser;
    }

    public void setReviewedByUser(User reviewedByUser) {
        this.reviewedByUser = reviewedByUser;
    }

    public LocalDate getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(LocalDate reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    public String getJustification() {
        return justification;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}


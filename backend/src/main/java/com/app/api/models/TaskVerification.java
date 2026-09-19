package com.app.api.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "task_verification_table")
public class TaskVerification{
    public enum VerificationStatus {
        VERIFIED,
        NEEDS_REVIEW,
        FAILED
    }

    public enum ResidentDecision {
        CONFIRM,
        DISPUTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private int verificationId;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private TaskInvoice task;

    @ManyToOne
    @JoinColumn(name = "completion_image_id")
    private TaskImage completionImage;

    @Column(name = "score")
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VerificationStatus status;

    @Column(name = "location_verified")
    private Boolean locationVerified;

    @Column(name = "distance_m")
    private Double distanceM;

    @Column(name = "geofence_radius_m")
    private Double geofenceRadiusM;

    @Column(name = "reasons", columnDefinition = "jsonb")
    private String reasons;

    @Enumerated(EnumType.STRING)
    @Column(name = "resident_decision")
    private ResidentDecision residentDecision;

    @Column(name = "decision_note")
    private String decisonNote;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
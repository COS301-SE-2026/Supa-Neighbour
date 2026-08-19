package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "report_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "report_type", nullable = false, length = 20)
    private String reportType;

    @Column(name = "reporter_user_id", nullable = false)
    private Integer reporterUserId;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "submitted";

    @Column(name = "admin_id")
    private Integer adminId;

    @Column(name = "reported_user_id")
    private Integer reportedUserId;

    @Column(name = "reported_post_id")
    private Integer reportedPostId;

    @Column(name = "reported_comment_id")
    private Integer reportedCommentId;

    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "dispute_reason", length = 30)
    private String disputeReason;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "violation_type", length = 30)
    private String violationType;

    @Column(name = "severity", length = 10)
    private String severity;

    @Column(name = "suggested_action", length = 30)
    private String suggestedAction;

    @Column(name = "actual_action", length = 30)
    private String actualAction;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "resolved_at")
    private Timestamp resolvedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Timestamp(System.currentTimeMillis());
        }

        if (status == null) {
            status = "submitted";
        }
    }
}

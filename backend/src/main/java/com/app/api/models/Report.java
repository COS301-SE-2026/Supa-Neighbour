package com.app.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Date;

import com.azure.core.annotation.Post;

import graphql.language.Comment;

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

    @Column(name = "report_user_id")
    private User reporterUserId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "admin_id")
    private Admin adminId;

    @Column(name = "reported_user_id")
    private User reportedUserId;

    @Column(name = "reported_post_id")
    private Post reportedPostId;

    @Column(name = "reported_comment_id")
    private Comment reportedCommentId;

    @Column(name = "task_id")
    private Task taskId;

    @Column(name = "dispute_reason", length = 30)
    private String disputeReason;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "description,", columnDefinition = "TEXT")
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
    private Date createdAt;

    @Column(name = "resolved_at")
    private Date resolvedAt;

}

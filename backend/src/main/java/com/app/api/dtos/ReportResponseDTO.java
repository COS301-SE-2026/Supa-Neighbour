package com.app.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ReportResponseDTO {
    
    private Integer reportId;
    private String reportType;
    private String status;
    private Integer reportedUserId;
    private Integer reportedPostId;
    private Integer reportedCommentId;
    private Integer taskId;
    private String disputeReason;
    private String reason;
    private String actualAction;
    private Timestamp createdAt;
    private Timestamp resolvedAt;
    private Object details;
}

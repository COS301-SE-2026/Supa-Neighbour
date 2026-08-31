package com.app.api.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for a PATCH (partial update) on a report.
 * Only non-null fields are applied.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReportDTO {

    /** Admin assigned to handle the report. */
    private Integer adminId;

    /** New status for the report (e.g. "under_review", "resolved", "dismissed"). */
    private String status;

    /** Severity level assessed by admin (e.g. "low", "medium", "high"). */
    private String severity;

    /** Action suggested by the system or reporter. */
    private String suggestedAction;

    /** Action actually taken by admin. */
    private String actualAction;
}

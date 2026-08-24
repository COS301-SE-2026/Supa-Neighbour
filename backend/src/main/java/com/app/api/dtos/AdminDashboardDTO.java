package com.app.api.dtos;

import java.util.Map;
public class AdminDashboardDTO {
    private Long assignedReportsCount;
    private Map<String, Long> reportsByType;
    private Long completedReports;
    private Long reviewedReports;

    /**
     * Constructs a new AdminDashboardDTO with the specified statistics.
     * 
     * @param assignedReportsCount the total number of reports currently assigned to helpers
     * @param reportsByType a map containing report counts grouped by report type
     *                      (e.g., {"Theft": 5, "Noise": 3, "Damage": 2})
     * @param completedReports the total number of reports that have been completed
     * @param reviewedReports the total number of reports that have been reviewed
     */
    public AdminDashboardDTO(Long assignedReportsCount, Map<String, Long> reportsByType, Long completedReports, Long reviewedReports){
        this.assignedReportsCount = assignedReportsCount;
        this.reportsByType = reportsByType;
        this.completedReports = completedReports;
        this.reviewedReports = reviewedReports; 
    }

    /**
     * Gets the total number of reports currently assigned to helpers.
     * 
     * @return the count of assigned reports, or null if not set
     */
    public Long getAssignedReportsCount(){
        return assignedReportsCount;
    }

    /**
     * Gets the breakdown of reports by their type.
     * 
     * @return a Map where keys are report type names and values are the count of 
     *         reports of that type, or null if not set
     */
    public Map<String, Long> getReportsByType(){
        return reportsByType;
    }

    /**
     * Gets the total number of reports that have been marked as completed.
     * 
     * @return the count of completed reports, or null if not set
     */
    public Long getCompletedReports(){
        return completedReports;
    }

    /**
     * Gets the total number of reports that have been reviewed.
     * 
     * @return the count of reviewed reports, or null if not set
     */
    public Long getReviewedReports(){
        return reviewedReports;
    }
}

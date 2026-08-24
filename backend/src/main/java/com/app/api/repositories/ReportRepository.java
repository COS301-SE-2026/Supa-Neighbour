package com.app.api.repositories;
import com.app.api.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer>{
    
    /**
     * Returns all reports filed by a given reporter, optionally filtered
     * by status and/or reportType. Either filter can be null to skip it.
     *
     * @param reporterUserId the user who submitted the reports
     * @param status optional status filter (submitted/assigned/reviewed)
     * @param reportType optional report type filter (USER/POST/COMMENT/TASK_DISPUTE)
     * @return matching reports, most recent first
     */
    @Query("SELECT r FROM Report r WHERE r.reporterUserId = :reporterUserId " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:reportType IS NULL OR r.reportType = :reportType) " +
           "ORDER BY r.createdAt DESC")
    List<Report> findMyReports(
        @Param("reporterUserId") Integer reporterUserId, 
        @Param("status") String status,
        @Param("reportType") String reportType
    );

    /**
     * Counts the total number of reports currently assigned to a specific admin.
     * <p>
     * This query counts all reports with status 'assigned' that are associated with
     * the given admin ID.
     * </p>
     * 
     * @param adminId the ID of the admin whose assigned reports are being counted
     * @return the total number of assigned reports for the specified admin
     */
    @Query(value = "SELECT COUNT(*) FROM report_table WHERE admin_id = :adminId AND status = 'assigned'", nativeQuery = true)
    Long countAssignedReports(@Param("adminId") int adminId);

     /**
     * Counts the total number of reports that have been reviewed by a specific admin.
     * <p>
     * This query counts all reports with status 'reviewed' that are associated with
     * the given admin ID.
     * </p>
     * 
     * @param adminId the ID of the admin whose reviewed reports are being counted
     * @return the total number of reviewed reports for the specified admin
     */
    @Query(value = "SELECT COUNT(*) FROM report_table WHERE admin_id = :adminId AND status = 'reviewed'", nativeQuery = true)
    Long countReviewedReports(@Param("adminId") int adminId);

    /**
     * Counts assigned reports grouped by their type for a specific admin.
     * <p>
     * This query returns a breakdown of assigned reports by report type, allowing
     * administrators to see which types of reports are most common among their
     * assigned workload.
     * </p>
     * <p>
     * The result is returned as a list of Object arrays, where each array contains:
     * <ul>
     *     <li>index 0: the report type (String)</li>
     *     <li>index 1: the count of reports of that type (Long)</li>
     * </ul>
     * </p>
     * 
     * @param adminId the ID of the admin whose assigned reports are being analyzed
     * @return a List of Object[] containing report type and count pairs
     * @see #countAssignedReports(int) for the total count without grouping
     */
    @Query(value = "SELECT report_type, COUNT(*) FROM report_table WHERE admin_id = :adminId AND status = 'assigned' GROUP BY report_type", nativeQuery = true)
    List<Object[]> countAssignedReportsByType(@Param("adminId") int adminId);

    /**
     * Counts the total number of completed reports for a specific admin.
     * <p>
     * This query counts all reports that have been resolved (have a non-null
     * {@code resolved_at} timestamp) and are associated with the given admin ID.
     * A report is considered completed when it has a resolution timestamp,
     * regardless of its current status.
     * </p>
     * 
     * @param adminId the ID of the admin whose completed reports are being counted
     * @return the total number of completed (resolved) reports for the specified admin
     */
    @Query(value = "SELECT COUNT(*) FROM report_table WHERE admin_id = :adminId AND resolved_at IS NOT NULL", nativeQuery = true)
    Long countCompletedReports(@Param("adminId") int adminId);
}


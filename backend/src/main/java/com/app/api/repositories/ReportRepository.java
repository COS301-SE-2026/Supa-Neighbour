package com.app.api.repositories;

import com.app.api.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Report entities.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    /**
     * Find all reports submitted by a specific user.
     *
     * @param reporterUserId the ID of the reporting user
     * @return list of reports submitted by that user
     */
    List<Report> findByReporterUserId(int reporterUserId);

    /**
     * Find all reports with a specific status.
     *
     * @param status the status to filter by (e.g. "submitted", "assigned", "reviewed")
     * @return list of reports matching the given status
     */
    List<Report> findByStatus(String status);

    /**
     * Find all reports currently assigned to a given admin user.
     *
     * @param adminId the user_id of the admin
     * @return list of reports assigned to that admin
     */
    List<Report> findByAdminId(Integer adminId);

    /**
     * Find reports assigned to an admin filtered by status.
     *
     * @param adminId the admin's user_id
     * @param status the status to filter by
     * @return filtered list of reports
     */
    List<Report> findByAdminIdAndStatus(Integer adminId, String status);

    /**
     * Find reports assigned to an admin filtered by report type.
     *
     * @param adminId the admin's user_id
     * @param reportType one of USER, POST, COMMENT, TASK_DISPUTE
     * @return filtered list of reports
     */
    List<Report> findByAdminIdAndReportType(Integer adminId, String reportType);

    /**
     * Find reports assigned to an admin filtered by both status and report type.
     *
     * @param adminId the admin's user_id
     * @param status the status to filter by
     * @param reportType one of USER, POST, COMMENT, TASK_DISPUTE
     * @return filtered list of reports
     */
    List<Report> findByAdminIdAndStatusAndReportType(Integer adminId, String status, String reportType);
}

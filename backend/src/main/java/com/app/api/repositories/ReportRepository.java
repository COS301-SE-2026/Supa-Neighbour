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
}


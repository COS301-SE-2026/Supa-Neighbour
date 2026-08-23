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
     * @param status the status to filter by (e.g. "submitted", "resolved")
     * @return list of reports matching the given status
     */
    List<Report> findByStatus(String status);
}

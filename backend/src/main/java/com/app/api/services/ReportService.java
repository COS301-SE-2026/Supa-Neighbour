package com.app.api.services;

import com.app.api.dtos.UpdateReportDTO;
import com.app.api.models.Report;
import com.app.api.repositories.ReportRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service layer for report-related business logic.
 */
@Service
public class ReportService {

    /** The report repository. */
    private final ReportRepository reportRepository;

    /**
     * Constructs a ReportService with the required repository.
     *
     * @param reportRepository the report repository
     */
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Retrieves all reports in the system.
     *
     * @return list of all reports
     */
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    /**
     * Retrieves a single report by its ID.
     *
     * @param reportId the ID of the report
     * @return the matching report
     * @throws ResponseStatusException 404 if not found
     */
    public Report getReportById(int reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    /**
     * Replaces all updatable fields of an existing report (full update).
     *
     * @param reportId the ID of the report to update
     * @param updates  the new report data
     * @return the updated report
     * @throws ResponseStatusException 404 if not found
     */
    @Transactional
    public Report replaceReport(int reportId, Report updates) {
        Report existing = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        existing.setStatus(updates.getStatus() != null ? updates.getStatus() : existing.getStatus());
        existing.setAdminId(updates.getAdminId());
        existing.setDisputeReason(updates.getDisputeReason());
        existing.setReason(updates.getReason());
        existing.setDescription(updates.getDescription());
        existing.setViolationType(updates.getViolationType());
        existing.setSeverity(updates.getSeverity());
        existing.setSuggestedAction(updates.getSuggestedAction());
        existing.setActualAction(updates.getActualAction());

        if (updates.getActualAction() != null && existing.getResolvedAt() == null) {
            existing.setResolvedAt(new Timestamp(System.currentTimeMillis()));
        }

        return reportRepository.save(existing);
    }

    /**
     * Applies a partial update to an existing report.
     * Only non-null fields in the DTO are applied.
     *
     * @param reportId the ID of the report to patch
     * @param patch    the fields to update
     * @return the updated report
     * @throws ResponseStatusException 404 if not found
     */
    @Transactional
    public Report patchReport(int reportId, UpdateReportDTO patch) {
        Report existing = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        if (patch.getAdminId() != null) {
            existing.setAdminId(patch.getAdminId());
        }
        if (patch.getStatus() != null) {
            existing.setStatus(patch.getStatus());
        }
        if (patch.getSeverity() != null) {
            existing.setSeverity(patch.getSeverity());
        }
        if (patch.getSuggestedAction() != null) {
            existing.setSuggestedAction(patch.getSuggestedAction());
        }
        if (patch.getActualAction() != null) {
            existing.setActualAction(patch.getActualAction());
            if (existing.getResolvedAt() == null) {
                existing.setResolvedAt(new Timestamp(System.currentTimeMillis()));
            }
        }

        return reportRepository.save(existing);
    }
}

package com.app.api.services;

import com.app.api.models.Report;
import com.app.api.repositories.ReportRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import com.app.api.dtos.ReportResponseDTO;
import com.app.api.services.ReportDetailService;

@Service
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final ReportDetailService reportDetailService;

    public ReportService(ReportRepository reportRepository, ReportDetailService reportDetailService){
        this.reportRepository = reportRepository;
        this.reportDetailService = reportDetailService;
    }

    /**
     * Retrieves all reports filed by the given user, optionally filtered
     * by status and reportType, mapped to the leaner user-facing shape.
     *
     * @param reporterUserId the resolved user ID from the Firebase token
     * @param status optional status filter, normalized to lowercase
     * @param reportType optional report type filter, normalized to uppercase
     * @return the user's reports, most recent first
     */
    public List<ReportResponseDTO> getReportsOfUser(Integer userId, String status, String reportType){
        String normalizedStatus = status == null ? null  : status.trim().toLowerCase();
        String normalizedReportType = reportType == null ? null: reportType.trim().toUpperCase();

        List<Report> reports = reportRepository.findMyReports(userId, normalizedStatus, normalizedReportType);

        return reports.stream().map(r -> new ReportResponseDTO(
            r.getReportId(),
            r.getReportType(), 
            r.getStatus(), 
            r.getReportedUserId(),
            r.getReportedPostId(),
            r.getReportedCommentId(),
            r.getTaskId(),
            r.getDisputeReason(),
            r.getReason(), 
            r.getActualAction(),
            r.getCreatedAt(),
            r.getResolvedAt(),
            reportDetailService.resolveDetails(r)
        )).toList();
    }
}


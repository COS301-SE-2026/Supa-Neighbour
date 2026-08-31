package com.app.api.services;

import com.app.api.dtos.PatchReportDTO;
import com.app.api.dtos.PatchReportResponseDTO;
import com.app.api.dtos.ReportRequestDTO;
import com.app.api.dtos.ReportDTO;
import com.app.api.dtos.AdminDashboardDTO;
import com.app.api.dtos.ReportResponseDTO;
import com.app.api.models.Report;
import com.app.api.repositories.UserRepository;
import com.app.api.repositories.ReportRepository;
import com.app.api.models.User;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * Service layer for report-related business logic.
 */
@Service
public class ReportService {

    private static final Set<String> VALID_REPORT_TYPES =
            new HashSet<>(Arrays.asList("USER", "POST", "COMMENT", "TASK_DISPUTE"));

    private static final Set<String> VALID_STATUSES =
            new HashSet<>(Arrays.asList("submitted", "assigned", "reviewed"));

    private static final Set<String> VALID_SEVERITIES =
            new HashSet<>(Arrays.asList("MINOR", "MODERATE", "SEVERE"));

    /** The report repository. */
    private final ReportRepository reportRepository;
    private final ReportDetailService reportDetailService;

    private final UserRepository userRepository;

    /**
     * Constructs a ReportService with the required repository.
     *
     * @param reportRepository the report repository
     */
    public ReportService(ReportRepository reportRepository, UserRepository userRepository, ReportDetailService reportDetailService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository; 
        this.reportDetailService = reportDetailService;
    }

    /**
     * Returns all reports currently assigned to the given admin, with optional
     * filtering by status and/or reportType 
     * @param adminId the user_id of the requesting admin
     * @param status optional status filter - one of submitted, assigned, reviewed
     * @param reportType optional type filter - one of USER, POST, COMMENT, TASK_DISPUTE
     * @return list of matching {@link ReportResponseDTO} objects
 * @throws ResponseStatusException 400 if an unrecognised filter value is supplied
     */
    public List<ReportResponseDTO> getReportsForAdmin(
            int adminId, String status, String reportType) {

        if (status != null && !VALID_STATUSES.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "status must be one of: submitted, assigned, reviewed");
        }
        if (reportType != null && !VALID_REPORT_TYPES.contains(reportType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "reportType must be one of: USER, POST, COMMENT, TASK_DISPUTE");
        }

        List<Report> reports;

        if (status != null && reportType != null) {
            reports = reportRepository
                    .findByAdminIdAndStatusAndReportType(adminId, status, reportType);
        } else if (status != null) {
            reports = reportRepository.findByAdminIdAndStatus(adminId, status);
        } else if (reportType != null) {
            reports = reportRepository.findByAdminIdAndReportType(adminId, reportType);
        } else {
            reports = reportRepository.findByAdminId(adminId);
        }

        return toResponseList(reports);
    }

    /**
     * Submits a new report on behalf of the authenticated user.
     * @param reporterUserId the authenticated user's ID
     * @param dto the submitted report payload
     * @return a {@link ReportDTO} for the newly created report
     * @throws ResponseStatusException 400 if the payload fails validation
     */
    @Transactional
    public ReportResponseDTO submitReport(int reporterUserId, ReportRequestDTO dto) {
        validateSubmitPayload(dto);

        Report report = new Report();
        report.setReporterUserId(reporterUserId);
        report.setReportType(dto.getReportType());
        report.setStatus("submitted");
        report.setReason(dto.getReason());
        report.setDescription(dto.getDescription());

        switch (dto.getReportType()) {
            case "USER":
                report.setReportedUserId(dto.getReportedUserId());
                break;
            case "POST":
                report.setReportedPostId(dto.getReportedPostId());
                break;
            case "COMMENT":
                report.setReportedCommentId(dto.getReportedCommentId());
                break;
            case "TASK_DISPUTE":
                report.setTaskId(dto.getTaskId());
                report.setDisputeReason(dto.getDisputeReason());
                break;
            default:
                break;
        }

        Report saved = reportRepository.save(report);
        return toResponse(saved);
    }

    /**
     * Admin partially updates a report - sets status, verdict fields, and/or     * @param adminUserId the user_id of the calling admin     * @param dto the patch payload - must include {@code reportId}
     * @return a {@link PatchReportResponseDTO} with the updated fields
     * @throws ResponseStatusException 400 for invalid enum values,
     * 403 if report not assigned to this admin,
     * 404 if report not found
     */
    @Transactional
    public PatchReportResponseDTO patchReport(int adminUserId, PatchReportDTO dto) {
        if (dto.getReportId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "reportId is required");
        }

        Report existing = reportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Report not found"));

        if (existing.getAdminId() == null
                || existing.getAdminId() != adminUserId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Not authorized to update this report");
        }

        if (dto.getStatus() != null) {
            if (!VALID_STATUSES.contains(dto.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "status must be one of: submitted, assigned, reviewed");
            }
            existing.setStatus(dto.getStatus());
        }

        if (dto.getViolationType() != null) {
            existing.setViolationType(dto.getViolationType());
        }

        if (dto.getSeverity() != null) {
            if (!VALID_SEVERITIES.contains(dto.getSeverity())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "severity must be one of: MINOR, MODERATE, SEVERE");
            }
            existing.setSeverity(dto.getSeverity());
        }

        if (dto.getActualAction() != null) {
            existing.setActualAction(dto.getActualAction());
            if (existing.getResolvedAt() == null) {
                existing.setResolvedAt(new Timestamp(System.currentTimeMillis()));
            }
        }

        Report saved = reportRepository.save(existing);

        return new PatchReportResponseDTO(
                saved.getReportId(),
                saved.getStatus(),
                saved.getActualAction(),
                saved.getResolvedAt());
    }

    
    private void validateSubmitPayload(ReportRequestDTO dto) {
        if (dto.getReportType() == null || !VALID_REPORT_TYPES.contains(dto.getReportType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "reportType must be one of: USER, POST, COMMENT, TASK_DISPUTE");
        }

        switch (dto.getReportType()) {
            case "USER":
                if (dto.getReportedUserId() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "reportedUserId is required for reportType USER");
                }
                if (dto.getReportedPostId() != null
                        || dto.getReportedCommentId() != null
                        || dto.getTaskId() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid report payload for reportType USER");
                }
                break;
            case "POST":
                if (dto.getReportedPostId() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "reportedPostId is required for reportType POST");
                }
                if (dto.getReportedUserId() != null
                        || dto.getReportedCommentId() != null
                        || dto.getTaskId() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid report payload for reportType POST");
                }
                break;
            case "COMMENT":
                if (dto.getReportedCommentId() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "reportedCommentId is required for reportType COMMENT");
                }
                if (dto.getReportedUserId() != null
                        || dto.getReportedPostId() != null
                        || dto.getTaskId() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid report payload for reportType COMMENT");
                }
                break;
            case "TASK_DISPUTE":
                if (dto.getTaskId() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "taskId is required for reportType TASK_DISPUTE");
                }
                if (dto.getDisputeReason() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "disputeReason is required for reportType TASK_DISPUTE");
                }
                if (dto.getReportedUserId() != null
                        || dto.getReportedPostId() != null
                        || dto.getReportedCommentId() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid report payload for reportType TASK_DISPUTE");
                }
                break;
            default:
                break;
        }
    }

    private ReportResponseDTO toResponse(Report r) {
        return new ReportResponseDTO(
                r.getReportId(),
                r.getReportType(),
                r.getStatus(),
                r.getAdminId(),
                r.getReporterUserId(),
                r.getReportedUserId(),
                r.getReportedPostId(),
                r.getReportedCommentId(),
                r.getTaskId(),
                r.getDisputeReason(),
                r.getReason(),
                r.getCreatedAt());
    }

    private List<ReportResponseDTO> toResponseList(List<Report> reports) {
        return reports.stream()
                .map(r -> this.toResponse(r))
                .toList();
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
    public List<ReportDTO> getReportsOfUser(Integer userId, String status, String reportType){
        String normalizedStatus = status == null ? null  : status.trim().toLowerCase();
        String normalizedReportType = reportType == null ? null: reportType.trim().toUpperCase();

        List<Report> reports = reportRepository.findMyReports(userId, normalizedStatus, normalizedReportType);

        return reports.stream().map(r -> new ReportDTO(
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

    /**
     * Retrieves dashboard statistics for an admin user.
     * <p>
     * This method fetches comprehensive analytics data for the admin dashboard,
     * including counts of assigned, completed, and reviewed reports, as well as
     * a breakdown of assigned reports by their type. The method first validates
     * that the user exists and has admin privileges before retrieving the data.
     * </p>
     * 
     * @param userId the ID of the user requesting the dashboard data
     * @return an {@link AdminDashboardDTO} containing the admin's dashboard statistics
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the user 
     *         with the given ID does not exist
     * @throws ResponseStatusException with {@link HttpStatus#FORBIDDEN} if the user 
     *         is not an admin
     */
    public AdminDashboardDTO getAdminDashboard(int userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!Boolean.TRUE.equals(user.getIsAdmin())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an admin");
        }

        Long assignedCount = reportRepository.countAssignedReports(userId);
        Long completedCount = reportRepository.countCompletedReports(userId);
        Long reviewedCount = reportRepository.countReviewedReports(userId);

        Map<String, Long> reportsByType = new HashMap<>();
        for(Object[] row: reportRepository.countAssignedReportsByType(userId)){
            reportsByType.put((String) row[0], ((Number) row[1]).longValue());
        }

        return new AdminDashboardDTO(assignedCount, reportsByType, completedCount, reviewedCount);
    }
}


    private List<ReportResponseDTO> toResponseList(List<Report> reports) {
        return reports.stream()
                .map(r -> this.toResponse(r))
                .toList();
    }
}

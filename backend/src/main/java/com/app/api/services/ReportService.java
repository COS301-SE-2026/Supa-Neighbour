package com.app.api.services;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.app.api.dtos.ReportResponseDTO;
import com.app.api.models.Report;
import com.app.api.repositories.ReportRepository;
import com.app.api.dtos.AdminDashboardDTO;
import com.app.api.models.User;
import com.app.api.repositories.UserRepository;
import java.util.Map;
import org.springframework.http.HttpStatus;
@Service
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final ReportDetailService reportDetailService;

    private final UserRepository userRepository;
    /**
     * Constructs a new ReportService with the required dependencies.
     * 
     * @param reportRepository the repository for Report entity operations
     * @param reportDetailService the service for ReportDetail business logic
     */
    public ReportService(ReportRepository reportRepository, ReportDetailService reportDetailService, UserRepository userRepository){
        this.reportRepository = reportRepository;
        this.reportDetailService = reportDetailService;
        this.userRepository = userRepository;
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



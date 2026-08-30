package com.app.api.controllers;

import com.app.api.dtos.PatchReportDTO;
import com.app.api.dtos.PatchReportResponseDTO;
import com.app.api.dtos.ReportRequestDTO;
import com.app.api.dtos.ReportResponseDTO;
import com.app.api.repositories.AdminRepository;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.ReportService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**

 REST controller providing endpoints for managing reports.
*/
@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;
    private final FirebaseAuthService firebaseAuthService;
    private final AdminRepository adminRepository;

    /**
     * Constructs a ReportController with its required dependencies.
     *
     * @param reportService       the report service
     * @param firebaseAuthService the Firebase authentication service
     * @param adminRepository     used to verify the caller holds an admin record
     */
    public ReportController(ReportService reportService,
            FirebaseAuthService firebaseAuthService,
            AdminRepository adminRepository) {
        this.reportService = reportService;
        this.firebaseAuthService = firebaseAuthService;
        this.adminRepository = adminRepository;
    }

    /**
     * Returns all reports currently assigned to the requesting admin, with
     * optional filtering by {@code status} and/or {@code reportType}.
     *
     * @param authHeader the Firebase Bearer token
     * @param status optional filter — one of {@code submitted},
     *                   {@code assigned}, {@code reviewed}
     * @param reportType optional filter — one of {@code USER}, {@code POST},
     *                   {@code COMMENT}, {@code TASK_DISPUTE}
     * @return 200 with the filtered list, 401 on bad token, 403 if not admin
     */
    @Operation(summary = "Get reports assigned to the calling admin (9.4)")
    @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid filter value")
    @ApiResponse(responseCode = "401", description = "Unauthorized — invalid or missing token")
    @ApiResponse(responseCode = "403", description = "User is not an admin")
    @GetMapping
    public ResponseEntity<?> getAssignedReports(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reportType) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            requireAdmin(userId);
            List<ReportResponseDTO> reports =
                    reportService.getReportsForAdmin(userId, status, reportType);
            return ResponseEntity.ok(reports);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason()));
        }
    }

    /**
     * Submits a new report on behalf of the authenticated user.
     *
     * @param authHeader the Firebase Bearer token
     * @param dto        the report payload
     * @return 201 with the created report, 400 on validation failure,
     *         401 on bad token, 404 if the referenced entity does not exist
     */
    @Operation(summary = "Submit a new report (9.6)")
    @ApiResponse(responseCode = "201", description = "Report submitted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid report payload for reportType")
    @ApiResponse(responseCode = "401", description = "Unauthorized — invalid or missing token")
    @ApiResponse(responseCode = "404", description = "Target entity not found")
    @PutMapping
    public ResponseEntity<?> submitReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ReportRequestDTO dto) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            ReportResponseDTO created = reportService.submitReport(userId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason()));
        }
    }

    /**
     * Admin updates the status and/or verdict of an existing report.
     *
     * @param authHeader the Firebase Bearer token
     * @param dto        the patch payload — must include {@code reportId}
     * @return 200 with the updated summary, 400 on invalid enum values,
     *         401 on bad token, 403 if not admin or report not assigned to caller,
     *         404 if report not found
     */
    @Operation(summary = "Admin updates a report status/verdict (9.7)")
    @ApiResponse(responseCode = "200", description = "Report updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid field value")
    @ApiResponse(responseCode = "401", description = "Unauthorized — invalid or missing token")
    @ApiResponse(responseCode = "403", description = "Not authorized to update this report")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @PatchMapping
    public ResponseEntity<?> patchReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PatchReportDTO dto) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            requireAdmin(userId);
            PatchReportResponseDTO result = reportService.patchReport(userId, dto);
            return ResponseEntity.ok(result);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason()));
        }
    }

    /**
     * Verifies the given user ID has a row in {@code admin_table}.
     * Throws 403 if not.
     *
     * @param userId the user ID to check
     */
    private void requireAdmin(int userId) {
        boolean isAdmin = adminRepository.findAll()
                .stream()
                .anyMatch(a -> a.getUserid() != null
                        && a.getUserid().getUserid() == userId);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is not an admin");
        }
    }
}

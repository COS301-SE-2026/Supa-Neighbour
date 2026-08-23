package com.app.api.controllers;

import com.app.api.dtos.UpdateReportDTO;
import com.app.api.models.Report;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.ReportService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller providing endpoints for managing reports.
 * <p>
 * All endpoints require a valid Firebase Bearer token in the
 * Authorization header. Endpoints are accessible under {@code /api/report}.
 * </p>
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    /** Service containing report business logic. */
    private final ReportService reportService;

    /** Service used to authenticate Firebase tokens. */
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a ReportController with the required services.
     *
     * @param reportService the report service
     * @param firebaseAuthService the Firebase auth service
     */
    public ReportController(ReportService reportService, FirebaseAuthService firebaseAuthService) {
        this.reportService = reportService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Retrieves all reports, or a single report when an ID is provided.
     * Without a path variable this returns the full list; calling
     * {@code GET /api/report/{reportId}} returns one report.
     *
     * @param authHeader the Firebase Bearer token
     * @return HTTP 200 with the list of all reports, or 401 on auth failure
     */
    @Operation(summary = "Get all reports")
    @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    @GetMapping
    public ResponseEntity<?> getAllReports(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            List<Report> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Retrieves a single report by its ID.
     *
     * @param authHeader the Firebase Bearer token
     * @param reportId the ID of the report to retrieve
     * @return HTTP 200 with the report, 404 if not found, or 401 on auth failure
     */
    @Operation(summary = "Get a report by ID")
    @ApiResponse(responseCode = "200", description = "Report found")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    @GetMapping("/{reportId}")
    public ResponseEntity<?> getReportById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int reportId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            Report report = reportService.getReportById(reportId);
            return ResponseEntity.ok(report);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Fully replaces the updatable fields of an existing report.
     * Use this when submitting a complete updated version of a report
     * (e.g. admin completes the review and sets all outcome fields at once).
     *
     * @param authHeader the Firebase Bearer token
     * @param reportId the ID of the report to update
     * @param updates the report body containing the new values
     * @return HTTP 200 with the updated report, 404 if not found, or 401 on auth failure
     */
    @Operation(summary = "Fully update a report by ID")
    @ApiResponse(responseCode = "200", description = "Report updated successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    @PutMapping("/{reportId}")
    public ResponseEntity<?> replaceReport(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int reportId,
            @RequestBody Report updates) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            Report updated = reportService.replaceReport(reportId, updates);
            return ResponseEntity.ok(updated);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }

    /**
     * Partially updates a report, applying only the fields present in the request body.
     * Useful for single-field updates such as changing the status or assigning an admin.
     *
     * @param authHeader the Firebase Bearer token
     * @param reportId the ID of the report to patch
     * @param patch the DTO containing the fields to update
     * @return HTTP 200 with the updated report, 404 if not found, or 401 on auth failure
     */
    @Operation(summary = "Partially update a report by ID")
    @ApiResponse(responseCode = "200", description = "Report patched successfully")
    @ApiResponse(responseCode = "404", description = "Report not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    @PatchMapping("/{reportId}")
    public ResponseEntity<?> patchReport(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int reportId,
            @RequestBody UpdateReportDTO patch) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
            Report updated = reportService.patchReport(reportId, patch);
            return ResponseEntity.ok(updated);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

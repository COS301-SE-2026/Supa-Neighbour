package com.app.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.AdminDashboardDTO;
import com.app.api.dtos.ReportResponseDTO;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.ReportService;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    
    private final ReportService reportService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructor for ReportController with dependency injection.
     * 
     * @param reportService Report service for business operations
     * @param firebaseAuthService Firebase authentication service
     */
    public ReportController(ReportService reportService, FirebaseAuthService firebaseAuthService){
        this.reportService = reportService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Retrieves all reports submitted by the authenticated user, for the
     * "My Reports" tracking page. Distinct from the admin-facing report
     * list — no violationType/severity/adminNotes are exposed here.
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @param status optional filter: submitted, assigned, reviewed
     * @param reportType optional filter: USER, POST, COMMENT, TASK_DISPUTE
     * @return the user's own reports, or 401 if the token is invalid or expired
     */
    @GetMapping("/me")
    public ResponseEntity<?> getReports(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String reportType
    ){
        int userId;
        try{
            String token = authHeader.replace("Bearer ", "");
            userId = firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
        List<ReportResponseDTO> response = reportService.getReportsOfUser(userId, status, reportType);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the admin dashboard statistics for the authenticated user.
     * <p>
     * This endpoint fetches comprehensive analytics and statistics for the admin dashboard,
     * including counts of assigned, completed, and reviewed reports, as well as a breakdown
     * of reports by type. The user must be authenticated and have admin privileges to access
     * this endpoint.
     * </p>
     * <p>
     * The dashboard data includes:
     * <ul>
     *     <li>Total number of assigned reports</li>
     *     <li>Total number of completed reports</li>
     *     <li>Total number of reviewed reports</li>
     *     <li>Distribution of assigned reports by report type</li>
     * </ul>
     * </p>
     * 
     * @param authHeader the Authorization header containing the Bearer token
     *                   (format: "Bearer &lt;firebase-token&gt;")
     * @return a {@link ResponseEntity} containing:
     *         <ul>
     *             <li><b>200 OK</b> with an {@link AdminDashboardDTO} containing the 
     *                 dashboard statistics if successful</li>
     *             <li><b>401 UNAUTHORIZED</b> with message "Invalid or expired Firebase token" 
     *                 if the Firebase token is invalid or expired</li>
     *             <li><b>403 FORBIDDEN</b> if the authenticated user is not an admin</li>
     *             <li><b>404 NOT FOUND</b> if the user does not exist in the system</li>
     *         </ul>
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(
        @RequestHeader("Authorization") String authHeader
    ){
        int userId;
        try{
            String token = authHeader.replace("Bearer ","");
            userId = firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }

        AdminDashboardDTO result = reportService.getAdminDashboard(userId);
        return ResponseEntity.ok(result);
    }
}


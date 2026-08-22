package com.app.api.controllers;

import com.app.api.models.Report;
import com.app.api.services.ReportService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.app.api.dtos.ReportResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    
    private final ReportService reportService;
    private final FirebaseAuthService firebaseAuthService;

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
}


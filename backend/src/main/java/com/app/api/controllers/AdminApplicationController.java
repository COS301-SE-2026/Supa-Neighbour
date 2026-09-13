package com.app.api.controllers;

import com.app.api.dtos.AdminApplicationDTO;
import com.app.api.models.AdminApplication;
import com.app.api.models.User;
import com.app.api.models.Admin;
import com.app.api.repositories.AdminApplicationRepository;
import com.app.api.repositories.AdminRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for admin application operations.
 */
@RestController
@RequestMapping("/api/admin/applications")
@Tag(name = "Admin Applications", description = "Operations for admin applications")
public class AdminApplicationController {

    private final AdminApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final FirebaseAuthService firebaseAuthService;
    private final AdminRepository adminRepository;

    /**
     * Constructs the controller with its required dependencies.
     *
     * @param applicationRepository repository for admin applications
     * @param userRepository repository for users
     * @param firebaseAuthService service for verifying Firebase tokens
     * @param adminRepository repository for admins
     */
    public AdminApplicationController(AdminApplicationRepository applicationRepository, UserRepository userRepository,
            FirebaseAuthService firebaseAuthService, AdminRepository adminRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.firebaseAuthService = firebaseAuthService;
        this.adminRepository = adminRepository;
    }

    /**
     * Returns all admin applications submitted by the authenticated user,
     * most recent first.
     *
     * @param authHeader Firebase Bearer token
     * @return 200 with the user's applications, 401 on bad token, 404 if user not found
     */
    @GetMapping("/me")
    @Operation(
        summary = "Get my admin applications",
        description = "Returns all admin applications submitted by the authenticated user, most recent first",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getMyApplications(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            List<AdminApplicationDTO> applications = applicationRepository
                    .findByUserOrderByApplicationDateDesc(user)
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(applications);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired Firebase token"));
        }
    }



        /**
 * Returns all admin applications — for the super admin review page (10.2).
 * Caller must be an admin with access level 2 (super admin).
 * Optionally filtered by status via ?status=Pending|Approved|Rejected.
 *
 * @param authHeader Firebase Bearer token
 * @param status     optional status filter
 * @return 200 with all matching applications, 401 on bad token,
 *         403 if caller is not a super admin
 */
@GetMapping
@Operation(
    summary = "Get all admin applications (super admin only)",
    description = "Returns all admin applications with optional status filter.",
    security = @SecurityRequirement(name = "BearerAuth")
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Applications retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token"),
    @ApiResponse(responseCode = "403", description = "Super admin access required")
})
public ResponseEntity<?> getAllApplications(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(required = false) String status) {
    try {
        String token = authHeader.replace("Bearer ", "");
        int userId = firebaseAuthService.getUserIdFromToken(token);

        Admin admin = adminRepository.findByUserId(userId).orElse(null);
        if (admin == null || admin.getAdminaccesslevel() != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Super admin access required"));
        }

        List<AdminApplication> applications = (status != null && !status.isBlank())
                ? applicationRepository
                        .findByApplicationStatusOrderByApplicationDateDesc(status)
                : applicationRepository.findAllByOrderByApplicationDateDesc();

        List<AdminApplicationDTO> result = applications.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);

    } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid or expired Firebase token"));
    }
}


    private AdminApplicationDTO toDTO(AdminApplication a) {
        return new AdminApplicationDTO(
                a.getApplicationId(),
                a.getApplicationStatus(),
                a.getApplicationDate(),
                a.getJustification(),
                a.getRejectionReason(),
                a.getReviewedDate(),
                null,
                null,
                null);
    }

    private AdminApplicationDTO toSummaryDTO(AdminApplication a) {
        Integer reviewedByAdminId = (a.getReviewedByUser() != null)
                ? a.getReviewedByUser().getUserid()
                : null;

        return new AdminApplicationDTO(
                a.getApplicationId(),
                a.getApplicationStatus(),
                a.getApplicationDate(),
                a.getJustification(),
                a.getRejectionReason(),
                a.getReviewedDate(),
                a.getUser().getUserid(),
                a.getUser().getUsername(),
                reviewedByAdminId);
    }

}

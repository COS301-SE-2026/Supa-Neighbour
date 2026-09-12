package com.app.api.controllers;

import com.app.api.dtos.AdminApplicationDTO;
import com.app.api.models.AdminApplication;
import com.app.api.models.User;
import com.app.api.repositories.AdminApplicationRepository;
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

    /**
     * Constructs the controller with its required dependencies.
     *
     * @param applicationRepository repository for admin applications
     * @param userRepository repository for users
     * @param firebaseAuthService service for verifying Firebase tokens
     */
    public AdminApplicationController(AdminApplicationRepository applicationRepository, UserRepository userRepository,
            FirebaseAuthService firebaseAuthService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.firebaseAuthService = firebaseAuthService;
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

    private AdminApplicationDTO toDTO(AdminApplication a) {
        return new AdminApplicationDTO(
                a.getApplicationId(),
                a.getApplicationStatus(),
                a.getApplicationDate(),
                a.getJustification(),
                a.getRejectionReason(),
                a.getReviewedDate()
        );
    }
}

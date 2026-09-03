package com.app.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.HelperTaskResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.HelperTasksService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that provides endpoints for retrieving the
 * authenticated helper's task history.
 */
@RestController
@RequestMapping("/api/helpers")
@Tag(name = "Helper Tasks", description = "Endpoints for retrieving helper task history")
public class HelperTaskController {
    private final HelperTasksService helperTasksService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a {@code HelperTaskController} with the required services.
     *
     * @param helperTasksService service responsible for retrieving helper tasks
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the associated user ID
     */
    public HelperTaskController(HelperTasksService helperTasksService, FirebaseAuthService firebaseAuthService){
        this.helperTasksService = helperTasksService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Retrieves the task history of the authenticated helper.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the helper's
     * tasks are retrieved. Results can be filtered by status and
     * paginated using the {@code limit} and {@code offset} parameters.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @param statusFilter an optional task status used to filter the results
     * @param limit the maximum number of task records to return
     * @param offset the number of task records to skip for pagination
     * @return a {@link ResponseEntity} containing the helper's task history
     *         if the request is successful, or a 401 Unauthorized response
     *         if the Firebase token is invalid or expired
     */
    @GetMapping("/me/tasks")
    @Operation(
        summary = "Get authenticated helper's task history",
        description = "Retrieves the task history of the authenticated helper. " +
                      "Results can be filtered by status and paginated using limit and offset parameters.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task history retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content)
    })
    public ResponseEntity<?> getMyTasks(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @Parameter(description = "Optional task status filter (e.g., 'pending', 'completed', 'in-progress')", example = "completed")
        @RequestParam(required = false) String statusFilter,
        @Parameter(description = "Maximum number of task records to return", example = "20")
        @RequestParam(defaultValue = "20") int limit,
        @Parameter(description = "Number of task records to skip for pagination", example = "0")
        @RequestParam(defaultValue = "0") int offset
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            HelperTaskResponse response = helperTasksService.getAcceptedTasks(userId, statusFilter, limit, offset);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

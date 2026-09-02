package com.app.api.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.LeaderboardResponse;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.LeaderboardService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller that provides endpoints for retrieving the
 * application leaderboard.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Leaderboard", description = "Endpoints for retrieving the application leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a {@code LeaderboardController} with the required services.
     *
     * @param leaderboardService service responsible for generating leaderboard data
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the associated user ID
     */
    public LeaderboardController(LeaderboardService leaderboardService, FirebaseAuthService firebaseAuthService){
        this.leaderboardService = leaderboardService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Retrieves the leaderboard for authenticated users.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the leaderboard
     * is generated. The {@code rankBy} parameter is accepted to match
     * the API contract but currently only {@code averageRating} is
     * supported by the service implementation.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @param rankBy the ranking criterion; currently accepts
     *               {@code averageRating} or {@code xp}
     * @param limit the maximum number of leaderboard entries to return
     * @return a {@link ResponseEntity} containing the leaderboard if the
     *         request is successful, a 400 Bad Request response if
     *         {@code rankBy} is invalid, or a 401 Unauthorized response
     *         if the Firebase token is invalid or expired
     */
    @GetMapping("/leaderboard")
    @Operation(
        summary = "Get leaderboard",
        description = "Retrieves the application leaderboard for authenticated users. " +
                      "The leaderboard can be ranked by average rating or XP.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Leaderboard retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid rankBy parameter",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"rankBy must be one of: averageRating, xp\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired Firebase token",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "Invalid or expired Firebase token"
                )
            )
        )
    })
    public ResponseEntity<?> getLeaderboard(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader,
        @Parameter(description = "Ranking criterion: 'averageRating' or 'xp'", example = "averageRating")
        @RequestParam(defaultValue = "averageRating") String rankBy,
        @Parameter(description = "Maximum number of leaderboard entries to return", example = "10")
        @RequestParam(defaultValue = "10") int limit
    ){
        if(!rankBy.equals("averageRating") && !rankBy.equals("xp")){
            return ResponseEntity.badRequest().body("{\"error\": \"rankBy must be one of: averageRating, xp\"}");
        }

        try {
            String token = authHeader.replace("Bearer ", "");
            int userId = firebaseAuthService.getUserIdFromToken(token);
            LeaderboardResponse response = leaderboardService.getLeaderboard(userId, limit);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }
    }
}

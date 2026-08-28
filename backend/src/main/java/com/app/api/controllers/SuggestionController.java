package com.app.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.SuggestionRequestDTO;
import com.app.api.dtos.SuggestionResponseDTO;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.SuggestionService;
import com.app.api.services.SuggestionService.ViolationType;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/suggestion")
@Tag(name = "Suggestions", description = "Endpoints for retrieving suggested actions based on violation rules")
public class SuggestionController {

    private final FirebaseAuthService firebaseAuthService;
    private final SuggestionService suggestionService;

    /**
     * Constructs a {@code SuggestionController} with the required services.
     *
     * @param violationRuleService service that holds the violation/severity
     *                             to suggested-action lookup
     * @param firebaseAuthService service used to authenticate Firebase tokens
     *                            and retrieve the associated user ID
     */
    public SuggestionController(FirebaseAuthService firebaseAuthService, SuggestionService suggestionService){
        this.suggestionService = suggestionService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Returns the suggested action for a given violation type and severity.
     *
     * <p>The Firebase authentication token is extracted from the
     * {@code Authorization} header and validated before the lookup runs.</p>
     *
     * <p><b>TODO (post-dev-merge):</b> once {@code User.isAdmin} lands on
     * dev, add the admin check here — resolve the user via
     * {@code firebaseAuthService.getUserIdFromToken(token)}, look them up,
     * and return 403 if {@code !user.isAdmin()}. Deliberately left open for
     * now per team decision so this endpoint isn't blocked on that merge.</p>
     *
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @param violationType the violation category, matching {@link ViolationType}
     * @param severity the severity tier, matching {@link Severity}
     * @return a {@link ResponseEntity} containing the suggested action,
     *         a 400 if the enum values are invalid or no rule exists for
     *         that pair, or a 401 if the Firebase token is invalid or expired
     */
    @GetMapping
    @Operation(
        summary = "Get suggested action for violation",
        description = "Returns the suggested action for a given violation type and severity. " +
                      "Requires admin authentication.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Suggested action retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid violation type, severity, or no rule defined for the pair",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(
                    value = "Invalid violationType or severity"
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
    public ResponseEntity<?> getSuggestion(
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader, 
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Violation type and severity to look up the suggested action",
            required = true
        )
        @RequestBody SuggestionRequestDTO suggestion
    ){
        try{
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }

        SuggestionService.ViolationType parsedViolationType;
        SuggestionService.Severity parsedSeverity;
        try{
            parsedViolationType = SuggestionService.ViolationType.valueOf(
                normalizeEnumInput(suggestion.getViolationType()));
            parsedSeverity = SuggestionService.Severity.valueOf(
                normalizeEnumInput(suggestion.getSeverity()));
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Invalid violationType or severity");
        }

        SuggestionService.SuggestedAction suggestedAction = suggestionService.getSuggestedAction(parsedViolationType, parsedSeverity);
        if(suggestedAction == null){
            return ResponseEntity.badRequest().body("No rule defined for this violationType/Sevetiry pair");
        }

        return ResponseEntity.ok(new SuggestionResponseDTO(parsedViolationType, parsedSeverity, suggestedAction));

    }

    private static String normalizeEnumInput(String input) {
        return input.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}

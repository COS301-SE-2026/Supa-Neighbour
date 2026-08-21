package com.app.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.app.api.dtos.SuggestionRequestDTO;
import com.app.api.dtos.SuggestionResponseDTO;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.SuggestionService;
import com.app.api.services.SuggestionService.ViolationType;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/suggestion")
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
    public ResponseEntity<?> getSuggestion(
        @RequestHeader("Authorization") String authHeader, 
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
        if(suggestedAction  == null){
            return ResponseEntity.badRequest().body("No rule defined for this violationType/Sevetiry pair");
        }

        return ResponseEntity.ok(new SuggestionResponseDTO(parsedViolationType, parsedSeverity, suggestedAction));

    }

    private static String normalizeEnumInput(String input) {
        return input.trim().toUpperCase().replace(" ", "_").replace("-", "_");
    }
}

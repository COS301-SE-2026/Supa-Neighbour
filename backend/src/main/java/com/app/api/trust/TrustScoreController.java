package com.app.api.trust;

import com.app.api.repositories.HelperRepository;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;

/**
 * REST controller that exposes the Adaptive Trust Score Engine output
 * 
 */
@RestController
@RequestMapping("/api/helpers")
@Tag(name = "Trust Score", description = "Adaptive Trust Score Engine — explainability endpoint")
public class TrustScoreController {

    private final TrustScoreFeatureService featureService;
    private final TrustScoreModel model;
    private final HelperRepository helperRepository;
    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs the controller with its required dependencies.
     *
     * @param featureService computes the six normalised feature values
     * @param model the logistic regression model — provides weights
     * @param helperRepository used to verify the helper exists
     * @param firebaseAuthService used to authenticate the caller
     */
    public TrustScoreController(TrustScoreFeatureService featureService,mTrustScoreModel model,
            HelperRepository helperRepository, FirebaseAuthService firebaseAuthService) {

        this.featureService = featureService;
        this.model = model;
        this.helperRepository = helperRepository;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Returns the full trust score breakdown for a specific helper.
     *
     * <p>The response includes the raw model prediction, the scaled score stored in helper_analytics_table.
     * average_rating, all sixfeature values that were fed into the model, and all six learned
     * weight values that the model arrived at after gradient descent.
     * </p>
     *
     * @param authHeader Firebase Bearer token
     * @param helperId the id of the helper to evaluate
     * @return 200 with the full trust score breakdown,
     *         401 on bad token,
     *         404 if the helper does not exist
     */
    @GetMapping("/{helperId}/trust-score")
    @Operation(
        summary = "Get full trust score breakdown for a helper",
        description = "Returns the raw score, scaled score, all six feature values, "
            + "and all six learned model weights. "
            + "This is the explainability endpoint for the Adaptive Trust Score Engine.",
        security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trust score breakdown retrieved"),
        @ApiResponse(responseCode = "401",
            description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "404",
            description = "Helper not found", content = @Content)
    })
    public ResponseEntity<?> getTrustScore(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "Unique identifier of the helper", example = "1")
            @PathVariable int helperId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            firebaseAuthService.getUserIdFromToken(token);

            if (helperRepository.findById(helperId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Helper not found"));
            }

            TrustScoreFeatures features = featureService.computeFeatures(helperId);
            double rawScore = model.predict(features.toArray());
            double scaledScore = rawScore * 5.0;
            double[] weights = model.getWeights();

            TrustScoreResponseDTO response = new TrustScoreResponseDTO( helperId,
                    rawScore, scaledScore,
                    features.getCompletionRate(),
                    features.getRatingVolumeScore(),
                    features.getReportPenalty(),
                    features.getRecencyScore(),
                    features.getZoneActivity(),
                    features.getDaysActive(),
                    weights[0],
                    weights[1],
                    weights[2],
                    weights[3],
                    weights[4],
                    weights[5],
                    LocalDate.now());

            return ResponseEntity.ok(response);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired Firebase token"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getReason()));
        }
    }
}

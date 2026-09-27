package com.app.api.unit.verification;

import com.app.api.verification.VerificationEngine.Config;
import com.app.api.verification.VerificationEngine.Input;
import com.app.api.verification.VerificationEngine.Result;
import com.app.api.verification.VerificationEngine.Status;
import com.app.api.verification.VerificationEngine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * No Mockito needed: the engine has no collaborators, so every test just builds an Input
 * and checks the Result. Default weights: ai .45, geo .25, meta .15, time .15;
 * thresholds: verified .75, needs-review .45.
 */
class VerificationEngineTest {

    private final VerificationEngine engine = new VerificationEngine();

    /** Everything good: confident "task done", 20 m from the task, live camera capture, inside the window. */
    private static Input.Builder perfect() {
        return Input.builder()
                .ai(0.9, true)
                .geo(20, 75)
                .metadata("CAMERA", true, true, true)
                .withinTaskWindow(true);
    }

    @Test
    void perfectSignalsAreVerified() {
        Result r = engine.evaluate(perfect().build());

        assertEquals(Status.VERIFIED, r.status());
        assertEquals(0.955, r.score(), 1e-9);   // .9*.45 + 1*.25 + 1*.15 + 1*.15
        assertTrue(r.reasons().isEmpty());
    }

    @Test
    void aiOutageCapsAtNeedsReviewEvenWithPerfectOtherSignals() {
        Input in = Input.builder()                       // ai(...) never called = AI unavailable
                .geo(20, 75)
                .metadata("CAMERA", true, true, true)
                .withinTaskWindow(true)
                .build();

        Result r = engine.evaluate(in);

        assertEquals(Status.NEEDS_REVIEW, r.status());
        assertEquals(0.55, r.score(), 1e-9);
        assertEquals(List.of(VerificationEngine.REASON_AI_UNAVAILABLE), r.reasons());
    }

    @Test
    void aiConfidentTaskIsNotDoneNeedsReview() {
        Result r = engine.evaluate(perfect().ai(0.9, false).build());

        assertEquals(0.1, r.aiScore(), 1e-9);            // 1 - 0.9
        assertEquals(Status.NEEDS_REVIEW, r.status());
        assertTrue(r.reasons().contains(VerificationEngine.REASON_AI_SAYS_INCOMPLETE));
    }

    @Test
    void slightlyOutsideGeofenceGetsPartialCreditButCannotBeVerified() {
        Result r = engine.evaluate(perfect().geo(100, 75).build());   // 25 m outside a 75 m radius

        assertEquals(0.6667, r.geoScore(), 1e-4);
        assertTrue(r.score() >= 0.75);                    // score alone would have verified...
        assertEquals(Status.NEEDS_REVIEW, r.status());    // ...but wrong location blocks it
        assertTrue(r.reasons().contains(VerificationEngine.REASON_LOCATION_MISMATCH));
    }

    @Test
    void farFromTaskLocationGetsNoGeoCredit() {
        Result r = engine.evaluate(perfect().geo(500, 75).build());

        assertEquals(0.0, r.geoScore(), 1e-9);
        assertEquals(Status.NEEDS_REVIEW, r.status());    // .405 + 0 + .15 + .15 = .705
        assertTrue(r.reasons().contains(VerificationEngine.REASON_LOCATION_MISMATCH));
    }

    @Test
    void missingLocationCannotBeVerified() {
        Input in = Input.builder()
                .ai(0.9, true)
                .metadata("CAMERA", true, true, true)
                .withinTaskWindow(true)
                .build();

        Result r = engine.evaluate(in);

        assertEquals(Status.NEEDS_REVIEW, r.status());
        assertTrue(r.reasons().contains(VerificationEngine.REASON_LOCATION_UNAVAILABLE));
    }

    @Test
    void galleryPhotoCannotBeVerified() {
        Result r = engine.evaluate(perfect().metadata("GALLERY", true, true, true).build());

        assertEquals(Status.NEEDS_REVIEW, r.status());
        assertTrue(r.reasons().contains(VerificationEngine.REASON_NOT_LIVE_CAPTURE));
    }

    @Test
    void captureSourceComparisonIgnoresCase() {
        Result r = engine.evaluate(perfect().metadata("camera", true, true, true).build());
        assertEquals(Status.VERIFIED, r.status());
    }

    @Test
    void photoOutsideTaskWindowCannotBeVerified() {
        Result r = engine.evaluate(perfect().withinTaskWindow(false).build());

        assertEquals(Status.NEEDS_REVIEW, r.status());
        assertTrue(r.reasons().contains(VerificationEngine.REASON_OUTSIDE_TASK_WINDOW));
    }

    @Test
    void missingExifCameraInfoAloneDoesNotBlockVerification() {
        // Many phones/plugins strip EXIF - informational only.
        Result r = engine.evaluate(perfect().metadata("CAMERA", true, false, false).build());

        assertEquals(Status.VERIFIED, r.status());
        assertEquals(List.of(VerificationEngine.REASON_NO_CAMERA_METADATA), r.reasons());
    }

    @Test
    void exactHashReuseFailsImmediately() {
        Result r = engine.evaluate(perfect().exactHashReused(true).build());

        assertEquals(Status.FAILED, r.status());
        assertEquals(0.0, r.score(), 1e-9);
        assertEquals(List.of(VerificationEngine.REASON_IMAGE_REUSED), r.reasons());
    }

    @Test
    void nearDuplicateOfAnotherPhotoNeedsReview() {
        Result r = engine.evaluate(perfect().nearDuplicate(true).build());

        assertEquals(Status.NEEDS_REVIEW, r.status());
        assertEquals(0.955, r.score(), 1e-9);             // score unaffected, only blocked
        assertEquals(List.of(VerificationEngine.REASON_IMAGE_NEAR_DUPLICATE), r.reasons());
    }

    @Test
    void completionPhotoMatchingTheReferencePhotoNeedsReview() {
        Result r = engine.evaluate(perfect().matchesReference(true).build());

        assertEquals(Status.NEEDS_REVIEW, r.status());
        assertEquals(List.of(VerificationEngine.REASON_MATCHES_REFERENCE), r.reasons());
    }

    @Test
    void everythingWrongFails() {
        Input in = Input.builder()
                .ai(0.9, false)                            // AI: not done
                .geo(2000, 75)                             // far away
                .metadata("GALLERY", false, false, false)  // old gallery photo
                .withinTaskWindow(false)
                .build();

        Result r = engine.evaluate(in);

        assertEquals(Status.FAILED, r.status());
        assertEquals(0.045, r.score(), 1e-9);              // only .1 * .45 from the AI
        assertTrue(r.reasons().size() >= 5);
    }

    @Test
    void thresholdBoundariesAreInclusive() {
        // AI-only config makes the score equal to the AI sub-score, so boundaries are easy to hit.
        VerificationEngine aiOnly = new VerificationEngine(new Config(1, 0, 0, 0, 0.75, 0.45));

        assertEquals(Status.NEEDS_REVIEW, aiOnly.evaluate(Input.builder().ai(0.45, true).build()).status());
        assertEquals(Status.FAILED, aiOnly.evaluate(Input.builder().ai(0.44, true).build()).status());
        // 0.75 would be VERIFIED by score, but geo/metadata/time are missing so it is blocked
        assertEquals(Status.NEEDS_REVIEW, aiOnly.evaluate(Input.builder().ai(0.75, true).build()).status());
    }

    @Test
    void outOfRangeConfidenceIsClamped() {
        assertEquals(1.0, engine.evaluate(perfect().ai(5.0, true).build()).aiScore(), 1e-9);
        assertEquals(0.0, engine.evaluate(perfect().ai(-2.0, true).build()).aiScore(), 1e-9);
        assertEquals(0.0, engine.evaluate(perfect().ai(Double.NaN, true).build()).aiScore(), 1e-9);
    }

    @Test
    void configRejectsWeightsThatDoNotSumToOne() {
        assertThrows(IllegalArgumentException.class, () -> new Config(0.5, 0.5, 0.5, 0.5, 0.75, 0.45));
    }

    @Test
    void configRejectsNegativeWeightsAndBadThresholds() {
        assertThrows(IllegalArgumentException.class, () -> new Config(1.2, -0.2, 0, 0, 0.75, 0.45));
        assertThrows(IllegalArgumentException.class, () -> new Config(0.45, 0.25, 0.15, 0.15, 0.40, 0.60));
    }

    @Test
    void builderRejectsNonsenseGeoValues() {
        assertThrows(IllegalArgumentException.class, () -> Input.builder().geo(-1, 75));
        assertThrows(IllegalArgumentException.class, () -> Input.builder().geo(10, 0));
    }

    @Test
    void reasonsListCannotBeModifiedByCallers() {
        Result r = engine.evaluate(perfect().nearDuplicate(true).build());
        assertThrows(UnsupportedOperationException.class, () -> r.reasons().add("x"));
        assertFalse(r.reasons().isEmpty());
    }
}

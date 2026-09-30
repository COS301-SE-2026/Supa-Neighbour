package com.app.api.unit.vision;

import com.app.api.vision.VisionClient.TaskContext;
import com.app.api.vision.VisionProperties.Stub;
import com.app.api.vision.VisionProperties;
import com.app.api.vision.StubVisionClient;
import com.app.api.vision.VisionResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StubVisionClientTest {

    private static final TaskContext TASK = new TaskContext("Fix leaking tap", "Kitchen sink, left tap drips", "Home Repair");
    private static final byte[] IMG = {1, 2, 3};

    private final VisionProperties properties = new VisionProperties();
    private final StubVisionClient client = new StubVisionClient(properties);

    private void stubReturns(Stub.Outcome outcome) {
        properties.getStub().setOutcome(outcome);
    }

    @Test
    void defaultsToConfidentlyComplete() {
        VisionResult r = client.compare(TASK, IMG, IMG);

        assertTrue(r.available());
        assertTrue(r.taskLooksComplete());
        assertEquals(0.9, r.confidence(), 1e-9);
        assertFalse(r.labels().isEmpty());
    }

    @Test
    void incompleteOutcomeIsAvailableButSaysNotDone() {
        stubReturns(Stub.Outcome.INCOMPLETE);

        VisionResult r = client.compare(TASK, IMG, IMG);

        assertTrue(r.available());
        assertFalse(r.taskLooksComplete());
    }

    @Test
    void failureOutcomesAreUnavailableWithMatchingReason() {
        stubReturns(Stub.Outcome.CONTENT_FILTERED);
        assertEquals(VisionResult.Outcome.CONTENT_FILTERED, client.compare(TASK, IMG, IMG).outcome());

        stubReturns(Stub.Outcome.RATE_LIMITED);
        assertEquals(VisionResult.Outcome.RATE_LIMITED, client.compare(TASK, IMG, IMG).outcome());

        stubReturns(Stub.Outcome.ERROR);
        VisionResult r = client.compare(TASK, IMG, IMG);
        assertEquals(VisionResult.Outcome.ERROR, r.outcome());
        assertFalse(r.available());
        assertFalse(r.taskLooksComplete());
    }

    @Test
    void baselineReturnsLabelsAndNeverClaimsCompletion() {
        VisionResult r = client.analyzeBaseline(TASK, IMG);

        assertTrue(r.available());
        assertFalse(r.taskLooksComplete());
        assertFalse(r.labels().isEmpty());
    }

    @Test
    void baselineAlsoSimulatesFailures() {
        stubReturns(Stub.Outcome.RATE_LIMITED);
        assertFalse(client.analyzeBaseline(TASK, IMG).available());
    }

    @Test
    void badWiringFailsLoudlyLikeTheRealClientWill() {
        assertThrows(IllegalArgumentException.class, () -> client.compare(null, IMG, IMG));
        assertThrows(IllegalArgumentException.class, () -> client.compare(TASK, null, IMG));
        assertThrows(IllegalArgumentException.class, () -> client.compare(TASK, IMG, new byte[0]));
        assertThrows(IllegalArgumentException.class, () -> client.analyzeBaseline(TASK, null));
    }

    // ---------------------------------------------------------------- VisionResult itself

    @Test
    void unavailableRefusesTheOkOutcome() {
        assertThrows(IllegalArgumentException.class, () -> VisionResult.unavailable(VisionResult.Outcome.OK, "x"));
    }

    @Test
    void labelsAreNullSafeAndDefensivelyCopied() {
        assertTrue(new VisionResult(VisionResult.Outcome.OK, null, 0.5, "i", true, null).labels().isEmpty());

        List<String> mutable = new ArrayList<>(List.of("tap"));
        VisionResult r = VisionResult.ok(mutable, 0.8, "i", true);
        mutable.add("sink");

        assertEquals(List.of("tap"), r.labels());
        assertThrows(UnsupportedOperationException.class, () -> r.labels().add("x"));
    }
}
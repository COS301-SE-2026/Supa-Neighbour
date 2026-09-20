package com.app.api.vision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fake vision client: makes NO network call and looks at NO pixels. It exists so
 * VerificationService and the upload endpoint can be wired and tested end-to-end before the Azure
 * OpenAI resource exists.
 *
 * Only created when supaneighbour.vision.mode=stub - if the property is missing, no VisionClient
 * bean exists and the app fails at startup instead of silently faking AI results.
 * What it answers is set with supaneighbour.vision.stub.outcome, so you can exercise every branch
 * (complete, incomplete, content-filtered, rate-limited, error) without touching code.
 *
 * When AzureOpenAiVisionClient is written it gets mode=azure-openai; this class then simply stops
 * being created (delete it whenever you like).
 */
@Component
@ConditionalOnProperty(prefix = "supaneighbour.vision", name = "mode", havingValue = "stub")
public class StubVisionClient implements VisionClient {

    private static final Logger LOG = LoggerFactory.getLogger(StubVisionClient.class);

    private static final double STUB_CONFIDENCE = 0.90;

    private final VisionProperties properties;

    /**
     * Creates the stub client and logs a prominent warning so it is obvious in
     * the logs that AI results are simulated.
     *
     * @param properties the vision properties supplying the configured stub outcome
     */
    public StubVisionClient(VisionProperties properties) {
        this.properties = properties;
        LOG.warn("StubVisionClient is ACTIVE - AI verification results are FAKE. "
                + "Set supaneighbour.vision.mode=azure-openai for real analysis.");
    }

    /**
     * Returns a canned baseline analysis of the reference photo.
     *
     * <p>Honours the configured {@code stub.outcome}: failure outcomes produce
     * an unavailable {@link VisionResult}, otherwise an {@code OK} result with
     * a fixed label and confidence is returned.</p>
     *
     * @param task           the task context; must not be {@code null}
     * @param referenceImage the reference image bytes; must not be {@code null} or empty
     * @return a stub baseline result
     * @throws IllegalArgumentException if {@code task} is {@code null} or
     *                                  {@code referenceImage} is {@code null}/empty
     */
    @Override
    public VisionResult analyzeBaseline(TaskContext task, byte[] referenceImage) {
        requireInputs(task, referenceImage);
        VisionResult failure = simulatedFailure();
        if (failure != null) {
            return failure;
        }
        return VisionResult.ok(List.of("stub-scene"), STUB_CONFIDENCE,
                "Stub description of the reference photo - no real AI call was made.", false);
    }

    /**
     * Returns a canned comparison between the reference and completion photos.
     *
     * <p>Honours the configured {@code stub.outcome}: failure outcomes produce
     * an unavailable {@link VisionResult}. Otherwise the returned
     * {@code taskLooksComplete} flag is {@code true} only when the stub outcome
     * is {@link VisionProperties.Stub.Outcome#COMPLETE}.</p>
     *
     * @param task            the task context; must not be {@code null}
     * @param referenceImage  the reference image bytes; must not be {@code null} or empty
     * @param completionImage the completion image bytes; must not be {@code null} or empty
     * @return a stub comparison result
     * @throws IllegalArgumentException if {@code task} is {@code null} or either
     *                                  image is {@code null}/empty
     */
    @Override
    public VisionResult compare(TaskContext task, byte[] referenceImage, byte[] completionImage) {
        requireInputs(task, referenceImage);
        requireImage(completionImage, "completionImage");
        VisionResult failure = simulatedFailure();
        if (failure != null) {
            return failure;
        }

        boolean complete = properties.getStub().getOutcome() == VisionProperties.Stub.Outcome.COMPLETE;
        return VisionResult.ok(List.of("stub-scene"), STUB_CONFIDENCE,
                "Stub comparison - no real AI call was made.", complete);
    }

    /**
     * Maps the configured stub outcome onto an unavailable {@link VisionResult},
     * or returns {@code null} if the stub should answer normally.
     *
     * @return the simulated failure result, or {@code null} for non-failure outcomes
     */
    private VisionResult simulatedFailure() {
        switch (properties.getStub().getOutcome()) {
            case CONTENT_FILTERED:
                return VisionResult.unavailable(VisionResult.Outcome.CONTENT_FILTERED, "stub: simulated content filter");
            case RATE_LIMITED:
                return VisionResult.unavailable(VisionResult.Outcome.RATE_LIMITED, "stub: simulated HTTP 429");
            case ERROR:
                return VisionResult.unavailable(VisionResult.Outcome.ERROR, "stub: simulated error");
            default:
                return null;
        }
    }

    /**
     * Validates the task and reference-image arguments.
     *
     * @param task           the task context
     * @param referenceImage the reference image bytes
     * @throws IllegalArgumentException if {@code task} is {@code null} or
     *                                  {@code referenceImage} is {@code null}/empty
     */
    private static void requireInputs(TaskContext task, byte[] referenceImage) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
        requireImage(referenceImage, "referenceImage");
    }

    /**
     * Validates an image byte array.
     *
     * <p>Same rule as the real client: bad wiring (null / empty bytes) is a bug,
     * so fail loudly here too.</p>
     *
     * @param image the image bytes
     * @param name  the parameter name, used in the exception message
     * @throws IllegalArgumentException if {@code image} is {@code null} or empty
     */
    private static void requireImage(byte[] image, String name) {
        if (image == null || image.length == 0) {
            throw new IllegalArgumentException(name + " must not be null or empty");
        }
    }
}

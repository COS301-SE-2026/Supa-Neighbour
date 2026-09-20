package com.app.api.vision;
import java.util.List;

public record VisionResult(
    Outcome outcome,
    List<String> labels,
    double confidence,
    String insight,
    boolean taskLooksComplete,
    String detail
) {
    public enum Outcome {
        OK,
        CONTENT_FILTERED,
        RATE_LIMITED,
        INVALID_RESPONSE,
        ERROR
    }

    /**
     * Defensive-copy constructor: replaces a {@code null} {@code labels} list
     * with an empty immutable list, and otherwise stores an immutable copy.
     *
     */
    public VisionResult {
        labels = labels == null ? List.of() : List.copyOf(labels);
    }
    /**
     * Creates a successful result.
     *
     * @param labels            labels returned by the model; may be {@code null}
     * @param confidence        model confidence in {@code taskLooksComplete}, in {@code [0, 1]}
     * @param insight           human-readable model insight
     * @param taskLooksComplete the model's verdict on task completion
     * @return a result with {@link Outcome#OK} and no detail
     */
    public static VisionResult ok(List<String> labels, double confidence, String insight, boolean taskLooksComplete){
        return  new VisionResult(Outcome.OK, labels, confidence, insight, taskLooksComplete, null);
    }

    /**
     * @param detail short technical note for logs / debugging (NOT shown to users), e.g. "HTTP 404 DeploymentNotFound"
     */
    public static VisionResult unavailable(Outcome outcome, String detail){
        if(outcome == Outcome.OK){
            throw new IllegalArgumentException("unavailable() needs a failure outcome");
        }

        return new VisionResult(outcome, List.of(), 0.0, null, false, detail);
    }

    /** True only when the model gave a usable answer. */
    public boolean available() {
        return outcome == Outcome.OK;
    }
    
}

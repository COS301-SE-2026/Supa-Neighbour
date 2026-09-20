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

    public VisionResult {
        labels = labels == null ? List.of() : List.copyOf(labels);
    }

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

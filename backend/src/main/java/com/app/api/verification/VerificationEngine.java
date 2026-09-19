package com.app.api.verification;
 
import java.util.ArrayList;
import java.util.List;

public class VerificationEngine {
    public static final String CAPTURE_SOURCE_CAMERA = "CAMERA";
    public static final String REASON_IMAGE_REUSED = "IMAGE_REUSED";
    public static final String REASON_IMAGE_NEAR_DUPLICATE = "IMAGE_NEAR_DUPLICATE";
    public static final String REASON_MATCHES_REFERENCE = "MATCHES_REFERENCE_PHOTO";
    public static final String REASON_AI_UNAVAILABLE = "AI_UNAVAILABLE";
    public static final String REASON_AI_SAYS_INCOMPLETE = "AI_SAYS_INCOMPLETE";
    public static final String REASON_LOCATION_UNAVAILABLE = "LOCATION_UNAVAILABLE";
    public static final String REASON_LOCATION_MISMATCH = "LOCATION_MISMATCH";
    public static final String REASON_OUTSIDE_TASK_WINDOW = "OUTSIDE_TASK_WINDOW";
    public static final String REASON_NOT_LIVE_CAPTURE = "NOT_LIVE_CAPTURE";
    public static final String REASON_CAPTURE_TIME_INVALID = "CAPTURE_TIME_INVALID";
    public static final String REASON_NO_CAMERA_METADATA = "NO_CAMERA_METADATA";

    private static final double META_LIVE_CAPTURE = 0.45;
    private static final double META_TIME_VALID = 0.30;
    private static final double META_CAMERA_INFO = 0.15;
    private static final double META_EXIF_GPS = 0.15;

    public enum Status {VERIFIED, NEEDS_REVIEW, FAILED}

    public record Config(double weightAi, 
        double weightGeo, 
        double weightMeta, 
        double weightTime,
        double verifiedThreshold, 
        double needsReviewThreshold
    ){
        public Config {
            if(weightAi < 0 || weightGeo < 0 || weightMeta < 0 || weightTime < 0){
                throw new IllegalArgumentException("Weights must not be negative");
            }

            if(Math.abs(weightAi + weightGeo + weightMeta + weightTime - 1.0 ) > 1e-6){
                throw new IllegalArgumentException("Weights must sum to 1.0");
            }

            if(needsReviewThreshold < 0 || verifiedThreshold > 1 || needsReviewThreshold > verifiedThreshold){
                throw new IllegalArgumentException("Thresholds must satisfy 0 <= needsReview <= verified <= 1");
            }
        }

        public static Config defaults(){
                return new Config(0.45, 0.25, 0.15, 0.15, 0.75, 0.45);
        }
    }

    /**
     * All signals for one completion photo. Build it with Input.builder() - unset signals default to
     * "unavailable / false", which is the safe direction.
     *
     * aiConfidence means "how sure the model is of its taskLooksComplete verdict" (0..1), NOT
     * "probability the task is done" - the prompt sent to the model must define it that way.
     */
    public record Input(
        boolean aiAvailable, 
        double aiConfidence, 
        boolean aiTaskLooksComplete,
        boolean geoAvailable, 
        double distanceM,
        double geofenceRadiusM,
        String captureSource, 
        boolean captureTimeValid, 
        boolean hasCameraInfo, 
        boolean hasGps,
        boolean withinTaskWindow,
        boolean exactHashReused, 
        boolean nearDuplicate, 
        boolean matchesReference
    ){

        public static Builder builder(){
            return new Builder();
        }

        private static final class Builder{
            private boolean aiAvailable;
            private double aiConfidence;
            private boolean aiTaskLooksComplete;
            private boolean geoAvailable;
            private double distanceM;
            private double geofenceRadiusM;
            private String captureSource;
            private boolean captureTimeValid;
            private boolean hasCameraInfo;
            private boolean hasGps;
            private boolean withinTaskWindow;
            private boolean exactHashReused;
            private boolean nearDuplicate;
            private boolean matchesReference;

            public Builder ai(double confidence, boolean taskLooksComplete){
                this.aiAvailable = true;
                this.aiConfidence = confidence;
                this.aiTaskLooksComplete = taskLooksComplete;
                return this;
            }

            public Builder geo(double distanceM, double geofenceRadiusM){
                if(distanceM < 0 || Double.isNaN(distanceM)){
                    throw new IllegalArgumentException("distanceM must be >= 0");
                }

                if(geofenceRadiusM <= 0 || Double.isNaN(geofenceRadiusM)){
                    throw new IllegalArgumentException("geofenceRadiusM must be > 0");
                }

                this.geoAvailable = true;
                this.distanceM = distanceM;
                this.geofenceRadiusM = geofenceRadiusM;

                return this;
            }

            public Builder metadata(String captureSource, boolean captureTimeValid, boolean hasCameraInfo, boolean hasGps){
                this.captureSource = captureSource;
                this.captureTimeValid = captureTimeValid;
                this.hasCameraInfo = hasCameraInfo;
                this.hasGps = hasGps;
                return this;
            }

            public Builder withinTaskWindow(boolean v){
                this.withinTaskWindow = v;
                return this;
            }

            public Builder exactHashReused(boolean v){
                this.exactHashReused = v;
                return this;
            }

            public Builder nearDuplicate(boolean v){
                this.nearDuplicate = v;
                return this;
            }

            public Builder matchesReference(boolean v){
                this.matchesReference = v;
                return this;
            }

            public Input build(){
                return new Input(
                    aiAvailable, 
                    aiConfidence, 
                    aiTaskLooksComplete,
                    geoAvailable, 
                    distanceM, 
                    geofenceRadiusM,
                    captureSource, 
                    captureTimeValid, 
                    hasCameraInfo, 
                    hasGps,
                    withinTaskWindow, 
                    exactHashReused, 
                    nearDuplicate, 
                    matchesReference
                );
            }
        }
    }

    public record Result(
        Status status, 
        double score,
        double aiScore, 
        double geoScore, 
        double metaScore,
        double timeScore,
        List<String> reasons
    ){

    }

    private final Config config;

    public VerificationEngine(){
        this(Config.defaults());
    }

    public VerificationEngine(Config config){
        if(config == null){
            throw new IllegalArgumentException("config must not be null");
        }

        this.config = config;
    }

    public Result evaluate(Input in){
        List<String> reasons = new ArrayList<>();

        if(in.exactHashReused()){
            reasons.add(REASON_IMAGE_REUSED);
            return new Result(Status.FAILED, 0, 0, 0, 0, 0, List.copyOf(reasons));
        }

        boolean blocking = false;

        double aiScore = 0;
        if(!in.aiAvailable()){
            reasons.add(REASON_AI_UNAVAILABLE);
            blocking = true;
        }else{
            double c = clamp01(in.aiConfidence());
            aiScore = in.aiTaskLooksComplete() ? c : 1.0 - c;
            if(!in.aiTaskLooksComplete()){
                reasons.add(REASON_AI_SAYS_INCOMPLETE);
                blocking = true;
            }
        }

        double geoScore = 0;
        if(!in.geoAvailable){
            reasons.add(REASON_LOCATION_UNAVAILABLE);
        }else if(in.distanceM() <= in.geofenceRadiusM()){
            geoScore = 1.0;
        }else{
            geoScore = clamp01(1.0 - (in.distanceM() - in.geofenceRadiusM())/in.geofenceRadiusM());
            reasons.add(REASON_LOCATION_MISMATCH);
            blocking = true;
        }

        double metaScore = 0;
        if(CAPTURE_SOURCE_CAMERA.equalsIgnoreCase(in.captureSource())){
            metaScore += META_LIVE_CAPTURE;
        }else{
            reasons.add(REASON_NOT_LIVE_CAPTURE);
            blocking = true;
        }

        if(in.captureTimeValid()){
            metaScore += META_TIME_VALID;
        }else{
            reasons.add(REASON_CAPTURE_TIME_INVALID);
            blocking = true;
        }

        if(in.hasCameraInfo()){
            metaScore += META_CAMERA_INFO;
        }else{
            reasons.add(REASON_NO_CAMERA_METADATA);
        }

        if(in.hasGps()){
            metaScore += META_EXIF_GPS;
        }

        double timeScore = 0;
        if(in.withinTaskWindow()){
            timeScore = 1.0;
        }else{
            reasons.add(REASON_OUTSIDE_TASK_WINDOW);
            blocking = true;
        }

        if(in.nearDuplicate()){
            reasons.add(REASON_IMAGE_NEAR_DUPLICATE);
            blocking = true;
        }

        if (in.matchesReference()) {
            reasons.add(REASON_MATCHES_REFERENCE);
            blocking = true;
        }

        double score = round4(config.weightAi() * aiScore
                + config.weightGeo() * geoScore
                + config.weightMeta() * metaScore
                + config.weightTime() * timeScore);
 
        Status status;
        if (score >= config.verifiedThreshold()) {
            status = Status.VERIFIED;
        } else if (score >= config.needsReviewThreshold()) {
            status = Status.NEEDS_REVIEW;
        } else {
            status = Status.FAILED;
        }
        if (status == Status.VERIFIED && blocking) {
            status = Status.NEEDS_REVIEW;
        }
 
        return new Result(status, score, round4(aiScore), round4(geoScore),
                round4(metaScore), round4(timeScore), List.copyOf(reasons));
    }

    private static double clamp01(double v) {
        if (Double.isNaN(v)) return 0;
        return Math.max(0, Math.min(1, v));
    }
 
    private static double round4(double v) {
        return Math.round(v * 10_000.0) / 10_000.0;
    }
}
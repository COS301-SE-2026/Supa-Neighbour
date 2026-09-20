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

    /**
     * Weight and threshold configuration for the engine.
     *
     * @param weightAi           weight applied to the AI sub-score
     * @param weightGeo          weight applied to the geo sub-score
     * @param weightMeta         weight applied to the metadata sub-score
     * @param weightTime         weight applied to the time sub-score
     * @param verifiedThreshold  score at or above which the result is {@link Status#VERIFIED}
     * @param needsReviewThreshold score at or above which the result is {@link Status#NEEDS_REVIEW}
     */
    public record Config(double weightAi, 
        double weightGeo, 
        double weightMeta, 
        double weightTime,
        double verifiedThreshold, 
        double needsReviewThreshold
    ){
        /**
         * Validates the configuration.
         *
         * @throws IllegalArgumentException if any weight is negative, the
         *         weights do not sum to {@code 1.0} (within {@code 1e-6}),
         *         or the thresholds do not satisfy
         *         {@code 0 <= needsReview <= verified <= 1}
         */
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

         /**
         * Returns the default configuration used by {@link VerificationEngine#VerificationEngine()}.
         *
         * @return a configuration with weights {@code 0.45/0.25/0.15/0.15} and
         *         thresholds {@code 0.75}/{@code 0.45}
         */
        public static Config defaults(){
                return new Config(0.45, 0.25, 0.15, 0.15, 0.75, 0.45);
        }
    }

    /**
     * All signals for one completion photo.
     *
     * <p>Build it with {@link Input#builder()}; unset signals default to
     * "unavailable / false", which is the safe direction. Calling
     * {@link Builder#ai(double, boolean)} or {@link Builder#geo(double, double)}
     * implicitly marks the respective signal as available.</p>
     *
     * <p>{@code aiConfidence} means "how sure the model is of its
     * {@code taskLooksComplete} verdict" (0..1), <b>not</b> "probability the
     * task is done" — the prompt sent to the model must define it that way.</p>
     *
     * @param aiAvailable          whether an AI signal is present
     * @param aiConfidence         model confidence in {@code aiTaskLooksComplete}, in {@code [0, 1]}
     * @param aiTaskLooksComplete  the model's verdict
     * @param geoAvailable         whether a geo signal is present
     * @param distanceM            distance from client to task, in meters
     * @param geofenceRadiusM      allowed radius around the task, in meters
     * @param captureSource        capture source; see {@link #CAPTURE_SOURCE_CAMERA}
     * @param captureTimeValid     whether the capture time could be validated
     * @param hasCameraInfo        whether camera make/model is present
     * @param hasGps               whether EXIF GPS is present
     * @param withinTaskWindow     whether the capture falls inside the task's window
     * @param exactHashReused      whether the image exactly matches a prior submission
     * @param nearDuplicate        whether the image perceptually matches a prior submission
     * @param matchesReference     whether the image matches the task's reference photo
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

        /**
         * Creates a new {@link Builder} with all signals defaulted to
         * "unavailable / false".
         *
         * @return a fresh builder
         */
        public static Builder builder(){
            return new Builder();
        }

        /**
         * Fluent builder for {@link Input}. Not thread-safe.
         */
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

            /**
             * Marks the AI signal as available.
             *
             * @param confidence        model confidence in {@code taskLooksComplete}, in {@code [0, 1]}
             * @param taskLooksComplete the model's verdict
             * @return this builder
             */
            public Builder ai(double confidence, boolean taskLooksComplete){
                this.aiAvailable = true;
                this.aiConfidence = confidence;
                this.aiTaskLooksComplete = taskLooksComplete;
                return this;
            }

            /**
             * Marks the geo signal as available.
             *
             * @param distanceM       distance from client to task, in meters
             * @param geofenceRadiusM allowed radius around the task, in meters
             * @return this builder
             * @throws IllegalArgumentException if {@code distanceM} is negative
             *         or NaN, or {@code geofenceRadiusM} is non-positive or NaN
             */
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

            /**
             * Sets the metadata signals.
             *
             * @param captureSource    capture source; see {@link #CAPTURE_SOURCE_CAMERA}
             * @param captureTimeValid whether the capture time could be validated
             * @param hasCameraInfo    whether camera make/model is present
             * @param hasGps           whether EXIF GPS is present
             * @return this builder
             */
            public Builder metadata(String captureSource, boolean captureTimeValid, boolean hasCameraInfo, boolean hasGps){
                this.captureSource = captureSource;
                this.captureTimeValid = captureTimeValid;
                this.hasCameraInfo = hasCameraInfo;
                this.hasGps = hasGps;
                return this;
            }

            /**
             * Sets whether the capture falls inside the task's window.
             *
             * @param v the value
             * @return this builder
             */
            public Builder withinTaskWindow(boolean v){
                this.withinTaskWindow = v;
                return this;
            }

            /**
             * Sets whether the image exactly matches a prior submission.
             *
             * @param v the value
             * @return this builder
             */
            public Builder exactHashReused(boolean v){
                this.exactHashReused = v;
                return this;
            }

            /**
             * Sets whether the image perceptually matches a prior submission.
             *
             * @param v the value
             * @return this builder
             */
            public Builder nearDuplicate(boolean v){
                this.nearDuplicate = v;
                return this;
            }

            /**
             * Sets whether the image matches the task's reference photo.
             *
             * @param v the value
             * @return this builder
             */
            public Builder matchesReference(boolean v){
                this.matchesReference = v;
                return this;
            }

            /**
             * Builds an immutable {@link Input} from the current builder state.
             *
             * @return the constructed input
             */
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

    /**
     * Outcome of {@link #evaluate(Input)}.
     *
     * @param status    the overall verdict
     * @param score     the weighted total score, in {@code [0, 1]}
     * @param aiScore   the AI sub-score, in {@code [0, 1]}
     * @param geoScore  the geo sub-score, in {@code [0, 1]}
     * @param metaScore the metadata sub-score, in {@code [0, 1]}
     * @param timeScore the time sub-score, in {@code [0, 1]}
     * @param reasons   the reasons collected during evaluation, in the order
     *                  they were observed; never {@code null}
     */
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

     /**
     * Creates an engine with {@link Config#defaults()}.
     */
    public VerificationEngine(){
        this(Config.defaults());
    }

     /**
     * Creates an engine with the given configuration.
     *
     * @param config the configuration; must not be {@code null}
     * @throws IllegalArgumentException if {@code config} is {@code null}
     */
    public VerificationEngine(Config config){
        if(config == null){
            throw new IllegalArgumentException("config must not be null");
        }

        this.config = config;
    }

    /**
     * Evaluates a completion photo against its signals.
     *
     * <p>Exact hash reuse short-circuits to {@link Status#FAILED} with a
     * zeroed {@link Result}. Otherwise each sub-score is computed, reasons
     * are collected, and the weighted total is compared against the
     * configured thresholds. If the result would be {@link Status#VERIFIED}
     * but any blocking reason is present, the status is downgraded to
     * {@link Status#NEEDS_REVIEW}.</p>
     *
     * @param in the input signals; must not be {@code null}
     * @return the evaluation result
     */
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

    /**
     * Clamps a value into {@code [0, 1]}, treating NaN as {@code 0}.
     *
     * @param v the value
     * @return the clamped value
     */
    private static double clamp01(double v) {
        if (Double.isNaN(v)){
             return 0;
        }
        return Math.max(0, Math.min(1, v));
    }
 
    /**
     * Rounds a value to four decimal places.
     *
     * @param v the value
     * @return the rounded value
     */
    private static double round4(double v) {
        return Math.round(v * 10_000.0) / 10_000.0;
    }
}

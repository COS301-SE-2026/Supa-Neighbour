package com.app.api.verification;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
 
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "supaneighbour.verification")
public class VerificationProperties implements InitializingBean {

    public static class Weights {
        private double ai = 0.45;
        private double geo = 0.25;
        private double meta = 0.15;
        private double time = 0.15;
        /** @return the AI sub-score weight */
        public double getAi() { 
            return ai; 
        }
        /** @param ai the AI sub-score weight */
        public void setAi(double ai) {
             this.ai = ai; 
        }
         /** @return the geographic sub-score weight */
        public double getGeo() {
             return geo;
        }
        /** @param geo the geographic sub-score weight */
        public void setGeo(double geo) { 
            this.geo = geo; 
        }
        /** @return the metadata sub-score weight */
        public double getMeta() { 
            return meta; 
        }
        /** @param meta the metadata sub-score weight */
        public void setMeta(double meta) { 
            this.meta = meta; 
        }
         /** @return the time sub-score weight */
        public double getTime() { 
            return time; 
        }
        /** @param time the time sub-score weight */
        public void setTime(double time) { 
            this.time = time; 
        }
    }
 
    public static class Thresholds {
        private double verified = 0.75;
        private double needsReview = 0.45;
 
        /** @return the verified threshold */
        public double getVerified() { 
            return verified; 
        }
        /** @param verified the verified threshold */
        public void setVerified(double verified) { 
            this.verified = verified;
        }
        /** @return the needs-review threshold */
        public double getNeedsReview() { 
            return needsReview; 
        }
        /** @param needsReview the needs-review threshold */
        public void setNeedsReview(double needsReview) { 
            this.needsReview = needsReview; 
        }
    }


    public double geofenceRadiusM = 75;
    private double arrivalGeofenceRadiusM = 150;
    private int taskWindowGraceMinutes = 30;
    private final Weights weights = new Weights();
    private final Thresholds thresholds = new Thresholds();
    private long maxUploadBytes = 12_582_912L;
    private List<String> allowedMime = new ArrayList<>(List.of("image/jpeg", "image/png"));

    private String zone = ZoneId.systemDefault().getId();
    private int captureMaxAgeMinutes = 15;
    private int exifToleranceMinutes = 5;

    /**
     * Converts the current weight and threshold values into a
     * {@link VerificationEngine.Config}.
     *
     * @return an engine configuration built from this properties object
     * @throws IllegalArgumentException if the values violate the constraints
     *         enforced by {@link VerificationEngine.Config}
     */
    public VerificationEngine.Config toEngineConfig() {
        return new VerificationEngine.Config(
                weights.ai, weights.geo, weights.meta, weights.time,
                thresholds.verified, thresholds.needsReview);
    }

    /**
     * Resolves {@link #zone} into a {@link ZoneId}.
     *
     * @return the resolved zone
     * @throws java.time.DateTimeException if {@code zone} is not a valid zone ID
     * @throws NullPointerException        if {@code zone} is {@code null}
     */
    public ZoneId resolveZone(){
        return ZoneId.of(zone);
    }

    /**
     * Validates this configuration after Spring has bound all properties.
     *
     * <p>Checks that radii, upload limits, and durations are in range,
     * that {@link #allowedMime} is non-empty, that {@link #zone} resolves,
     * and that the weights and thresholds form a valid engine configuration.
     * Invalid values cause startup to fail immediately.</p>
     *
     * @throws IllegalStateException if any property is invalid
     */
    @Override 
    public void afterPropertiesSet(){
        if (geofenceRadiusM <= 0) {
            throw new IllegalStateException("supaneighbour.verification.geofence-radius-m must be > 0");
        }
        if (arrivalGeofenceRadiusM <= 0){
             throw new IllegalStateException("supaneighbour.verification.arrival-geofence-radius-m must be > 0");
        }
        if (taskWindowGraceMinutes < 0) {
            throw new IllegalStateException("supaneighbour.verification.task-window-grace-minutes must be >= 0");
        }
        if (maxUploadBytes <= 0){
             throw new IllegalStateException("supaneighbour.verification.max-upload-bytes must be > 0");
        }
        if (allowedMime == null || allowedMime.isEmpty()) {
            throw new IllegalStateException("supaneighbour.verification.allowed-mime must not be empty");
        }
        if (captureMaxAgeMinutes <= 0){
            throw new IllegalStateException("supaneighbour.verification.capture-max-age-minutes must be > 0");
        }
        if (exifToleranceMinutes < 0) {
            throw new IllegalStateException("supaneighbour.verification.exif-tolerance-minutes must be >= 0");
        }
        try {
            resolveZone();
        } catch (DateTimeException | NullPointerException e) {
            throw new IllegalStateException("supaneighbour.verification.zone is not a valid time zone id: " + zone, e);
        }
        try {
            toEngineConfig();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid supaneighbour.verification weights/thresholds: " + e.getMessage(), e);
        }
    }
}

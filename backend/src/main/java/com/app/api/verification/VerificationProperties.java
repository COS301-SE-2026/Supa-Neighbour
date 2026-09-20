package com.app.api.verification;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
 
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Component
@ConfigurationProperties(prefix = "supaneighbour.verification")
public class VerificationProperties implements InitializingBean {

    public static class Weights {
        private double ai = 0.45;
        private double geo = 0.25;
        private double meta = 0.15;
        private double time = 0.15;
 
        public double getAi() { return ai; }
        public void setAi(double ai) { this.ai = ai; }
        public double getGeo() { return geo; }
        public void setGeo(double geo) { this.geo = geo; }
        public double getMeta() { return meta; }
        public void setMeta(double meta) { this.meta = meta; }
        public double getTime() { return time; }
        public void setTime(double time) { this.time = time; }
    }
 
    public static class Thresholds {
        private double verified = 0.75;
        private double needsReview = 0.45;
 
        public double getVerified() { return verified; }
        public void setVerified(double verified) { this.verified = verified; }
        public double getNeedsReview() { return needsReview; }
        public void setNeedsReview(double needsReview) { this.needsReview = needsReview; }
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


     public VerificationEngine.Config toEngineConfig() {
        return new VerificationEngine.Config(
                weights.ai, weights.geo, weights.meta, weights.time,
                thresholds.verified, thresholds.needsReview);
    }

    public ZoneId resolveZone(){
        return ZoneId.of(zone);
    }

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

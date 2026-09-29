package com.app.api.trust;

import java.time.LocalDate;

/**
 * Response body for GET /api/helpers/{helperId}/trust-score.
 *
 * <p>Exposes every number the model used to arrive at the trust score, the scaled rating,
 * each of the six feature values, and each of the six learned weight values. Nothing is hidden. 
 * </p>
 * 
 */
public class TrustScoreResponseDTO {

    private final int helperId;
    private final double trustScore;
    private final double scaledScore;
    private final double completionRate;
    private final double ratingVolumeScore;
    private final double reportPenalty;
    private final double recencyScore;
    private final double zoneActivity;
    private final double daysActive;
    private final double weightCompletionRate;
    private final double weightRatingVolumeScore;
    private final double weightReportPenalty;
    private final double weightRecencyScore;
    private final double weightZoneActivity;
    private final double weightDaysActive;
    private final LocalDate lastUpdated;

    /**
     * Constructs a fully populated {@code TrustScoreResponseDTO}.
     *
     * @param helperId the helper identifier
     * @param trustScore raw model output in (0, 1)
     * @param scaledScore trust score scaled to (0, 5)
     * @param completionRate task completion rate feature value
     * @param ratingVolumeScore Wilson score feature value
     * @param reportPenalty report penalty feature value
     * @param recencyScore recency feature value
     * @param zoneActivity zone activity feature value
     * @param daysActive longevity feature value
     * @param weightCompletionRate learned weight for completionRate
     * @param weightRatingVolumeScore learned weight for ratingVolumeScore
     * @param weightReportPenalty learned weight for reportPenalty
     * @param weightRecencyScore learned weight for recencyScore
     * @param weightZoneActivity learned weight for zoneActivity
     * @param weightDaysActive learned weight for daysActive
     * @param lastUpdated date the score was last computed
     */
    public TrustScoreResponseDTO(int helperId, double trustScore,double scaledScore, double completionRate,
            double ratingVolumeScore, double reportPenalty, double recencyScore, double zoneActivity,
            double daysActive, double weightCompletionRate, double weightRatingVolumeScore, double weightReportPenalty,
            double weightRecencyScore, double weightZoneActivity, double weightDaysActive, LocalDate lastUpdated) {

        this.helperId = helperId;
        this.trustScore = trustScore;
        this.scaledScore = scaledScore;
        this.completionRate = completionRate;
        this.ratingVolumeScore = ratingVolumeScore;
        this.reportPenalty = reportPenalty;
        this.recencyScore = recencyScore;
        this.zoneActivity = zoneActivity;
        this.daysActive = daysActive;
        this.weightCompletionRate = weightCompletionRate;
        this.weightRatingVolumeScore = weightRatingVolumeScore;
        this.weightReportPenalty = weightReportPenalty;
        this.weightRecencyScore = weightRecencyScore;
        this.weightZoneActivity = weightZoneActivity;
        this.weightDaysActive = weightDaysActive;
        this.lastUpdated = lastUpdated;
    }

    /** @return the helper ID */
    public int getHelperId() { 
        return helperId;
    }

    /** @return raw trust score in (0, 1) */
    public double getTrustScore() { 
        return trustScore; 
    }

    /** @return trust score scaled to (0, 5) */
    public double getScaledScore() { 
        return scaledScore; 
    }

    /** @return completion rate feature value */
    public double getCompletionRate() { 
        return completionRate; 
    }

    /** @return Wilson score feature value */
    public double getRatingVolumeScore() { 
        return ratingVolumeScore; 
    }

    /** @return report penalty feature value */
    public double getReportPenalty() { 
        return reportPenalty; 
    }

    /** @return recency score feature value */
    public double getRecencyScore() { 
        return recencyScore; 
    }

    /** @return zone activity feature value */
    public double getZoneActivity() { 
        return zoneActivity; 
    }

    /** @return longevity feature value */
    public double getDaysActive() { 
        return daysActive; 
    }

    /** @return learned weight for completionRate */
    public double getWeightCompletionRate() { 
        return weightCompletionRate; 
    }

    /** @return learned weight for ratingVolumeScore */
    public double getWeightRatingVolumeScore() { 
        return weightRatingVolumeScore; 
    }

    /** @return learned weight for reportPenalty */
    public double getWeightReportPenalty() { 
        return weightReportPenalty;
    }

    /** @return learned weight for recencyScore */
    public double getWeightRecencyScore() { 
        return weightRecencyScore; 
    }

    /** @return learned weight for zoneActivity */
    public double getWeightZoneActivity() { 
        return weightZoneActivity; 
    }

    /** @return learned weight for daysActive */
    public double getWeightDaysActive() { 
        return weightDaysActive; 
    }

    /** @return the date the score was last computed */
    public LocalDate getLastUpdated() { 
        return lastUpdated; 
    }
}

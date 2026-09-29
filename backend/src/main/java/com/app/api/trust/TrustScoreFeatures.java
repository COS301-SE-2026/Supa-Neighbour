package com.app.api.trust;

/**
 * Immutable value object holding the six normalised input features
 * used by the Adaptive Trust Score Engine to evaluate a helper.
 */
public class TrustScoreFeatures {

    private final int helperId;
    private final double completionRate;
    private final double ratingVolumeScore;
    private final double reportPenalty;
    private final double recencyScore;
    private final double zoneActivity;
    private final double daysActive;


    /**
     * Constructs a fully populated {@code TrustScoreFeatures} instance.
     *
     * @param helperId  the id of the helper
     * @param completionRate proportion of accepted tasks that were completed
     * @param ratingVolumeScore Wilson score lower confidence bound derived from the helper's average rating 
     * and rating volume, statistically penalises helpers with few ratings
     * @param reportPenalty community safety signal - {@code 1.0} means zero reports in the last 90 days; 
     * {@code 0.0} means 5 or more reports
     * @param recencyScore proportion of this helper's recent completions relative to the most active helper in 
     * the last 30 days, measures current engagement
     * @param zoneActivity proportion of total completed tasks in this helper's zone that belong 
     * this helper, essentially measures neighbourhood contribution
     * @param daysActive days since the helper's first task, normalised against a 365-day ceiling, measures longevity
     */
    public TrustScoreFeatures(int helperId, double completionRate, double ratingVolumeScore,
            double reportPenalty, double recencyScore, double zoneActivity, double daysActive) {
        this.helperId = helperId;
        this.completionRate = completionRate;
        this.ratingVolumeScore = ratingVolumeScore;
        this.reportPenalty = reportPenalty;
        this.recencyScore = recencyScore;
        this.zoneActivity = zoneActivity;
        this.daysActive = daysActive;
    }

    /**
     * Returns the helper id these features belong to.
     *
     * @return the helper id
     */
    public int getHelperId() { 
        return helperId; 
    }

    /**
     * Returns the task completion rate, normalised to [0, 1].
     *
     * @return completion rate
     */
    public double getCompletionRate() { 
        return completionRate; 
    }

     /**
     * Returns the Wilson score lower confidence bound on the helper's average rating(normalised)
     *
     * @return rating volume score
     */
    public double getRatingVolumeScore() { 
        return ratingVolumeScore; 
    }

    /**
     * Returns the inverse report penalty, {@code 1.0} for a clean record, trending to {@code 0.0} 
     * as reports accumulate.
     *
     * @return report penalty score
     */
    public double getReportPenalty() { 
        return reportPenalty; 
    }

    /**
     * Returns the recency score, how active this helper has been in the last 30 days relative to
     * the most active helper.
     *
     * @return recency score
     */
    public double getRecencyScore() { 
        return recencyScore; 
    }

    /**
     * Returns the zone activity score, this helper's share of all completed tasks in their neighbourhood zone.
     *
     * @return zone activity score
     */
    public double getZoneActivity() { 
        return zoneActivity; 
    }

    /**
     * Returns the days-active score, how long this helper has been on the platform, 
     * normalised against a one-year ceiling.
     *
     * @return days active score
     */
    public double getDaysActive() { 
        return daysActive; 
    }


    /**
     * Returns the six features as a {@code double[]} in the order
     * expected by {@code TrustScoreModel}: completionRate,
     * ratingVolumeScore, reportPenalty, recencyScore, zoneActivity,
     * daysActive.
     *
     * @return feature vector as a primitive array
     */
    public double[] toArray() {
        return new double[]{
            completionRate,
            ratingVolumeScore,
            reportPenalty,
            recencyScore,
            zoneActivity,
            daysActive
        };
    }


    /**
     * Returns representation of the feature set, showing each value to three decimal places.
     *
     * @return formatted string representation
     */
    @Override
    public String toString() {
        return String.format(
            "TrustScoreFeatures{helperId=%d, completionRate=%.3f, "
            + "ratingVolumeScore=%.3f, reportPenalty=%.3f, "
            + "recencyScore=%.3f, zoneActivity=%.3f, daysActive=%.3f}",
            helperId, completionRate, ratingVolumeScore,
            reportPenalty, recencyScore, zoneActivity, daysActive);
    }
}

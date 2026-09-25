package com.app.api.trust;

import com.app.api.models.Helper;
import com.app.api.models.HelperAnalytics;
import com.app.api.repositories.HelperAnalyticsRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.repositories.ReportRepository;
import com.app.api.repositories.TaskInvoiceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Computes the six normalised input features required by the
 * Adaptive Trust Score Engine for a given helper.
 *
 * <p>Each feature is independently derived from raw platform data
 * (task history, analytics, and community reports) and normalised
 * to the range {@code [0.0, 1.0]} before being returned inside a
 * {@link TrustScoreFeatures} value object.</p>
 *
 * <p>This service is intentionally separated from model training and
 * inference, it is the data-access layer of the trust pipeline.
 * It is called by both the training phase (to build the training
 * matrix) and the inference phase (to score a live helper).</p>
 */
@Service
public class TrustScoreFeatureService {

    /** z-score for 95% confidence interval used in the wilson score. */
    private static final double Z_95 = 1.96;

    /** Number of days to look back when counting reports against a helper. */
    private static final int REPORT_WINDOW_DAYS = 90;

    /** Number of days to look back when measuring recent task activity. */
    private static final int RECENCY_WINDOW_DAYS = 30;

    /** Maximum days active before the longevity score is capped at 1.0. */
    private static final int MAX_DAYS_ACTIVE = 365;

    /** Number of reports at which the report penalty reaches 0.0. */
    private static final int MAX_REPORTS_BEFORE_ZERO = 5;

    /** Raw rating value used for wilson score normalisation. */
    private static final double MAX_RATING = 5.0;

    private final TaskInvoiceRepository taskInvoiceRepository;
    private final HelperAnalyticsRepository helperAnalyticsRepository;
    private final ReportRepository reportRepository;
    private final HelperRepository helperRepository;

    /**
     * Constructs a TrustScoreFeatureService with its required
     * repository dependencies.
     *
     * @param taskInvoiceRepository repository for task invoice data
     * @param helperAnalyticsRepository repository for helper analytics records
     * @param reportRepository repository for community reports
     * @param helperRepository repository for helper records
     */
    public TrustScoreFeatureService(
            TaskInvoiceRepository taskInvoiceRepository, HelperAnalyticsRepository helperAnalyticsRepository,
            ReportRepository reportRepository, HelperRepository helperRepository) {
        this.taskInvoiceRepository = taskInvoiceRepository;
        this.helperAnalyticsRepository = helperAnalyticsRepository;
        this.reportRepository = reportRepository;
        this.helperRepository = helperRepository;
    }

    /**
     * Computes all six features for the helper identified by helperId and returns them as a TrustScoreFeatures
     * value object.
     *
     * <p>Annotated with {@link Transactional} so the Hibernate session stays open
     * when accessing lazy associations like {@code helper.getUserid()}.</p>
     *
     * @param helperId the unique id of the helper to evaluate
     * @return a fully populated TrustScoreFeatures instance
     */
    @Transactional(readOnly = true)
    public TrustScoreFeatures computeFeatures(int helperId) {
        Helper helper = helperRepository.findById(helperId).orElse(null);

        if (helper == null || helper.getUserid() == null) {
            return zeroFeatures(helperId);
        }

        int userId = helper.getUserid().getUserid();

        LocalDate recencyCutoff = LocalDate.now().minusDays(RECENCY_WINDOW_DAYS);

        long totalCompletedAcrossAllHelpers = helperRepository.findAll().stream()
                .mapToLong(h -> taskInvoiceRepository.countCompletedByHelperId(h.getHelperid()))
                .sum();

        long maxRecentAcrossAllHelpers = helperRepository.findAll().stream()
                .mapToLong(h -> taskInvoiceRepository
                        .countCompletedByHelperIdSince(h.getHelperid(), recencyCutoff))
                .max()
                .orElse(1L);

        double completionRate    = computeCompletionRate(helperId);
        double ratingVolumeScore = computeRatingVolumeScore(userId);
        double reportPenalty     = computeReportPenalty(userId);
        double recencyScore      = computeRecencyScore(helperId, maxRecentAcrossAllHelpers, recencyCutoff);
        double zoneActivity      = computeZoneActivity(helperId, totalCompletedAcrossAllHelpers);
        double daysActive        = computeDaysActive(helperId);

        return new TrustScoreFeatures(
                helperId,
                completionRate,
                ratingVolumeScore,
                reportPenalty,
                recencyScore,
                zoneActivity,
                daysActive);
    }

    /**
     * Computes features for every helper currently registered and returns them as a list.
     *
     * <p>Pre-computes shared totals (zone total completions, max recent tasks)
     * once upfront so individual feature methods do not each call
     * {@code helperRepository.findAll()} - doing so inside a loop caused
     * N+1 query explosions that dropped the Azure DB connection.</p>
     *
     * @return list of TrustScoreFeatures, one per helper
     */
    @Transactional(readOnly = true)
    public List<TrustScoreFeatures> computeAllFeatures() {
        List<Helper> allHelpers = helperRepository.findAll();
        if (allHelpers.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        LocalDate recencyCutoff = LocalDate.now().minusDays(RECENCY_WINDOW_DAYS);

        long totalCompletedAcrossAllHelpers = allHelpers.stream()
                .mapToLong(h -> taskInvoiceRepository.countCompletedByHelperId(h.getHelperid()))
                .sum();

        long maxRecentAcrossAllHelpers = allHelpers.stream()
                .mapToLong(h -> taskInvoiceRepository
                        .countCompletedByHelperIdSince(h.getHelperid(), recencyCutoff))
                .max()
                .orElse(1L);

        return allHelpers.stream()
                .map(h -> computeFeaturesWithTotals(
                        h.getHelperid(),
                        h,
                        totalCompletedAcrossAllHelpers,
                        maxRecentAcrossAllHelpers,
                        recencyCutoff))
                .toList();
    }

   

    /**
     * Computes all six features for a single helper using pre-computed platform-wide totals.
     * This avoids repeated {@code findAll()} calls inside the bulk computation loop.
     *
     * @param helperId the helper id
     * @param helper the helper entity (already loaded)
     * @param totalCompletedAcrossAllHelpers sum of completed tasks across all helpers
     * @param maxRecentAcrossAllHelpers max recent completions by any single helper
     * @param recencyCutoff the date cutoff for recency window
     * @return a fully populated TrustScoreFeatures instance
     */
    private TrustScoreFeatures computeFeaturesWithTotals(int helperId, Helper helper,long totalCompletedAcrossAllHelpers, long maxRecentAcrossAllHelpers,LocalDate recencyCutoff) {

        if (helper.getUserid() == null) {
            return zeroFeatures(helperId);
        }

        int userId = helper.getUserid().getUserid();

        double completionRate = computeCompletionRate(helperId);
        double ratingVolumeScore = computeRatingVolumeScore(userId);
        double reportPenalty = computeReportPenalty(userId);
        double recencyScore = computeRecencyScore(helperId, maxRecentAcrossAllHelpers, recencyCutoff);
        double zoneActivity = computeZoneActivity(helperId, totalCompletedAcrossAllHelpers);
        double daysActive = computeDaysActive(helperId);

        return new TrustScoreFeatures(helperId, completionRate, ratingVolumeScore,
                reportPenalty, recencyScore, zoneActivity, daysActive);
    }

    /**
     * Computes the task completion rate for a helper.
     *
     * @param helperId the helper id
     * @return completion rate in [0, 1]
     */
    private double computeCompletionRate(int helperId) {
        long total = taskInvoiceRepository.countAllByHelperId(helperId);

        if (total == 0) {
            return 0.0;
        }

        long completed = taskInvoiceRepository.countCompletedByHelperId(helperId);

        return clamp((double) completed / total);
    }

    /**
     * Computes the Wilson score lower confidence bound for the helper's average rating.
     *
     * <p>A plain average rating is gameable, a helper with one perfect rating looks identical to a veteran with 200. 
     * The Wilson score penalises low sample sizes by computing the lower bound of the 95% confidence interval around 
     * the observed proportion, treating the normalised average rating as a success probability.
     * </p>
     *
     * <p>Formula:
     * <pre>
     *   p = averageRating / MAX_RATING
     *   n = total number of analytics records (used as volume proxy)
     *   z = 1.96 (95% confidence)
     *   lower = (p + z²/2n − z sqrt(p(1−p)/n + z²/4n²)) / (1 + z²/n)
     * </pre>
     * </p>
     *
     * @param userId the user id linked to the helper analytics record
     * @return Wilson score lower bound in [0, 1], or 0.0 if no record exists
     */
    private double computeRatingVolumeScore(int userId) {
        Optional<HelperAnalytics> analytics = helperAnalyticsRepository.findByUserId(userId);

        if (analytics.isEmpty()) {
            return 0.0;
        }

        float rawRating = analytics.get().getAverageRating();
        long n = helperAnalyticsRepository.count();

        if (n == 0) {
            return 0.0;
        }

        double p = rawRating / MAX_RATING;
        double z2 = Z_95 * Z_95;
        double numerator = p + z2 / (2 * n) - Z_95 * Math.sqrt((p * (1 - p) / n) + (z2 / (4 * n * n)));
        double denominator = 1 + z2 / n;

        return clamp(numerator / denominator);
    }

    /**
     * Computes the report penalty for a helper.
     *
     * <p>Formula: 1.0 − min(reportCount / MAX_REPORTS_BEFORE_ZERO, 1.0).
     * A helper with zero reports in the last 90 days scores 1.0.
     * A helper with five or more reports scores 0.0.
     * </p>
     *
     * @param userId the user id of the helper
     * @return report penalty in [0, 1], higher is better (fewer reports)
     */
    private double computeReportPenalty(int userId) {
        LocalDate cutoff = LocalDate.now().minusDays(REPORT_WINDOW_DAYS);

        Timestamp since  = Timestamp.valueOf(cutoff.atStartOfDay());

        long reports = reportRepository.countReportsAgainstUserSince(userId, since);

        return clamp(1.0 - Math.min((double) reports / MAX_REPORTS_BEFORE_ZERO, 1.0));
    }

    /**
     * Computes the recency score for a helper using a pre-computed max.
     *
     * @param helperId the helper id
     * @param maxRecentAcrossAllHelpers max completions in window by any helper
     * @param since the recency window start date
     * @return recency score in [0, 1]
     */
    private double computeRecencyScore(int helperId, long maxRecentAcrossAllHelpers, LocalDate since) {
        long recentTasks = taskInvoiceRepository
                .countCompletedByHelperIdSince(helperId, since);

        if (maxRecentAcrossAllHelpers == 0) {
            return 0.0;
        }
        return clamp((double) recentTasks / maxRecentAcrossAllHelpers);
    }

    /**
     * Computes the zone activity score using a pre-computed platform total.
     *
     * @param helperId                       the helper id
     * @param totalCompletedAcrossAllHelpers total completed tasks across all helpers
     * @return zone activity score in [0, 1]
     */
    private double computeZoneActivity(int helperId, long totalCompletedAcrossAllHelpers) {
        if (totalCompletedAcrossAllHelpers == 0) {
            return 0.0;
        }
        long helperCompleted = taskInvoiceRepository.countCompletedByHelperId(helperId);
        return clamp((double) helperCompleted / totalCompletedAcrossAllHelpers);
    }

    /**
     * Computes the days-active longevity score for a helper.
     *
     * <p>Counts the number of days between the helper's first recorded task and today, 
     * then normalises against a one-year ceiling. A helper who has been active for 6 months 
     * scores roughly 0.5; one year or more scores 1.0.
     * </p>
     *
     * @param helperId the helper id
     * @return longevity score in [0, 1], or 0.0 if no tasks exist
     */
    private double computeDaysActive(int helperId) {
        LocalDate earliest = taskInvoiceRepository
                .findEarliestStartDateByHelperId(helperId);

        if (earliest == null) {
            return 0.0;
        }

        long days = ChronoUnit.DAYS.between(earliest, LocalDate.now());

        return clamp((double) days / MAX_DAYS_ACTIVE);
    }

    /**
     * Returns a zero-valued feature set for a helper with no data.
     *
     * <p> reportPenalty is set to 1.0 rather than 0.0 because the absence of reports 
     * is not evidence of wrongdoing, it would be unfair for us to penalise a new helper for
     * having no report history.
     * </p>
     *
     * @param helperId the helper id
     * @return a TrustScoreFeatures with all features at zero except reportPenalty which is 1.0
     */
    private TrustScoreFeatures zeroFeatures(int helperId) {
        return new TrustScoreFeatures(helperId, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0);
    }

    /**
     * Clamps a value to the range [0.0, 1.0].
     *
     * @param value the value to clamp
     * @return the clamped value
     */
    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}

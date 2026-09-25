package com.app.api.trust;

import com.app.api.models.Helper;
import com.app.api.models.HelperAnalytics;
import com.app.api.repositories.HelperAnalyticsRepository;
import com.app.api.repositories.HelperRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates the full Adaptive Trust Score training pipeline.
 *
 * <h2>What it does</h2>
 * <ol>
 *   <li>Loads feature vectors for every helper via {@link TrustScoreFeatureService#computeAllFeatures()}.</li>
 *   <li>Labels each helper as trusted ({@code 1.0}) or not trusted ({@code 0.0}) using deterministic rules 
 *      derived from their features. Ambiguous helpers are excluded.</li>
 *   <li>Splits the labelled set 80/20 into training and test sets.</li>
 *   <li>Trains {@link TrustScoreModel} via gradient descent.</li>
 *   <li>Evaluates the trained model on the test set and logs precision, recall, and F1 score.</li>
 *   <li>Writes the predicted trust score back into {@code helper_analytics_table.average_rating} for every helper,
 *       so the leaderboard, profiles, and matching service pick it up automatically without any changes to those services.</li>
 * </ol>
 *
 */
@Component
public class TrustScoreTrainer {

    private static final Logger log = LoggerFactory.getLogger(TrustScoreTrainer.class);

    private static final double LEARNING_RATE = 0.01;
    private static final int EPOCHS = 100;
    private static final double TRAIN_SPLIT = 0.80;
    private static final double TRUSTED_COMPLETION_THRESHOLD = 0.60;
    private static final double TRUSTED_REPORT_THRESHOLD = 0.80;
    private static final double UNTRUSTED_COMPLETION_THRESHOLD = 0.40;
    private static final double UNTRUSTED_REPORT_THRESHOLD = 0.60;
    private static final double RATING_SCALE = 5.0;

    private final TrustScoreFeatureService featureService;
    private final TrustScoreModel model;
    private final HelperRepository helperRepository;
    private final HelperAnalyticsRepository helperAnalyticsRepository;
    private final ApplicationContext applicationContext;

    /**
     * Constructs the trainer with all required dependencies.
     *
     * @param featureService computes normalised feature vectors
     * @param model the logistic regression model to train
     * @param helperRepository used to load all registered helpers
     * @param helperAnalyticsRepository used to persist updated trust scores
     */
    public TrustScoreTrainer(TrustScoreFeatureService featureService, TrustScoreModel model,
            HelperRepository helperRepository, HelperAnalyticsRepository helperAnalyticsRepository,
            ApplicationContext applicationContext) {
        this.featureService = featureService;
        this.model = model;
        this.helperRepository = helperRepository;
        this.helperAnalyticsRepository = helperAnalyticsRepository;
        this.applicationContext = applicationContext;
    }

    /**
     * Runs the full training pipeline.
     *
     * <p>Annotated with {@link EventListener} rather than {@code @PostConstruct} so that all repository beans are
     * guaranteed to be ready before we attempt any db access.</p>
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        log.info("TrustScoreTrainer: running initial training pass on startup.");
        try {
            applicationContext.getBean(TrustScoreTrainer.class).runFullPipeline();
        } catch (Exception e) {
            log.error("TrustScoreTrainer: pipeline failed — {}", e.getMessage(), e);
        }
    }

    /**
     * Runs the full training pipeline every day at 02:00 AM.
     *
     * <p>The cron expression {@code "0 0 2 * * *"}. 
     * </p>
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void runScheduled() {
        log.info("TrustScoreTrainer: running scheduled daily retraining.");
        try {
            applicationContext.getBean(TrustScoreTrainer.class).runFullPipeline();
        } catch (Exception e) {
            log.error("TrustScoreTrainer: scheduled pipeline failed — {}", e.getMessage(), e);
        }
    }


    ////////////////////// { Pipeline } \\\\\\\\\\\\\\\\\\\\\\\
    /**
     * Executes all six steps of the trust score pipeline in order.
     *
     */
    @Transactional
    public void runFullPipeline() {
        List<TrustScoreFeatures> allFeatures = featureService.computeAllFeatures();

        if (allFeatures.isEmpty()) {
            log.warn("TrustScoreTrainer: no helpers found, skipping training.");
            return;
        }

        log.info("TrustScoreTrainer: loaded features for {} helpers.", allFeatures.size());

        List<TrainingSample> labelled = labelSamples(allFeatures);
        log.info("TrustScoreTrainer: {} helpers labelled ({} excluded as ambiguous).",
                labelled.size(), allFeatures.size() - labelled.size());

        if (labelled.size() < 5) {
            log.warn("TrustScoreTrainer: too few labelled samples ({}), "
                    + "skipping training, writing raw feature scores instead.", labelled.size());
            persistScores(allFeatures);
            return;
        }

        Collections.shuffle(labelled);
        int splitIndex = (int) (labelled.size() * TRAIN_SPLIT);
        List<TrainingSample> trainSet = labelled.subList(0, splitIndex);
        List<TrainingSample> testSet  = labelled.subList(splitIndex, labelled.size());

        log.info("TrustScoreTrainer: {} training samples, {} test samples.",
                trainSet.size(), testSet.size());

        model.train(trainSet, LEARNING_RATE, EPOCHS);

        if (!testSet.isEmpty()) {
            evaluate(testSet);
        }

        persistScores(allFeatures);
    }

    //////////////////// { labels } \\\\\\\\\\\\\\\\\\\\\\
    /**
     * Assigns binary labels to helpers based on their feature values.
     *
     * <p>A helper is labelled {@code 1.0} (trusted) if:
     * <ul>
     *   <li>completionRate &gt;= {@value TRUSTED_COMPLETION_THRESHOLD}</li>
     *   <li>reportPenalty >= {@value TRUSTED_REPORT_THRESHOLD}</li>
     *   <li>daysActive > 0 (has at least one completed task)</li>
     * </ul>
     *
     * A helper is labelled {@code 0.0} (not trusted) if:
     * <ul>
     *   <li>completionRate < {@value UNTRUSTED_COMPLETION_THRESHOLD}</li>
     *   <li>OR reportPenalty < {@value UNTRUSTED_REPORT_THRESHOLD}</li>
     * </ul>
     *
     * Helpers that fall between these thresholds are excluded, their label would be ambiguous 
     * and adding noise to a small dataset hurts us more than helping.
     * </p>
     *
     * @param allFeatures feature vectors for all helpers 
     * @return list of clearly-labelled {@link TrainingSample} objects
     */
    private List<TrainingSample> labelSamples(List<TrustScoreFeatures> allFeatures) {
        List<TrainingSample> labelled = new ArrayList<>();

        for (TrustScoreFeatures f : allFeatures) {
            boolean trusted = f.getCompletionRate() >= TRUSTED_COMPLETION_THRESHOLD
                           && f.getReportPenalty() >= TRUSTED_REPORT_THRESHOLD;

            boolean notTrusted = f.getCompletionRate() < UNTRUSTED_COMPLETION_THRESHOLD
                              || f.getReportPenalty() < UNTRUSTED_REPORT_THRESHOLD;

            if (trusted) {
                labelled.add(new TrainingSample(f, 1.0));
            } else if (notTrusted) {
                labelled.add(new TrainingSample(f, 0.0));
            }
        }

        return labelled;
    }

    /////////////////////////// {Evaluation } \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Evaluates the trained model on the held-out test set and logs precision, recall, and F1 score.
     *
     * <p>A prediction threshold of {@code 0.5} is used, helpers with a predicted score above 0.5 
     * are classified as trusted.
     * </p>
     *
     * @param testSet the held-out 20% of labelled samples
     */
    private void evaluate(List<TrainingSample> testSet) {
        int truePositives  = 0;
        int falsePositives = 0;
        int falseNegatives = 0;

        for (TrainingSample sample : testSet) {
            double predicted = model.predict(sample.getFeatures().toArray());
            boolean predictedTrusted = predicted >= 0.5;
            boolean actualTrusted    = sample.getLabel() >= 0.5;

            if (predictedTrusted && actualTrusted)  { truePositives++; }
            if (predictedTrusted && !actualTrusted)  { falsePositives++; }
            if (!predictedTrusted && actualTrusted)  { falseNegatives++; }
        }

        double precision = (truePositives + falsePositives) > 0
                ? (double) truePositives / (truePositives + falsePositives)
                : 0.0;

        double recall = (truePositives + falseNegatives) > 0
                ? (double) truePositives / (truePositives + falseNegatives)
                : 0.0;

        double f1 = (precision + recall) > 0
                ? 2 * precision * recall / (precision + recall)
                : 0.0;

        log.info("TrustScoreTrainer: evaluation on {} test samples, Precision={}, Recall={}, F1={}",
                testSet.size(),
                String.format("%.3f", precision),
                String.format("%.3f", recall),
                String.format("%.3f", f1));
    }

    ///////////////// { Score persistence } \\\\\\\\\\\\\\\\\\\\\

    /**
     * Writes the model's predicted trust score back into {@code helper_analytics_table.average_rating} for every helper.
     *
     * <p>The raw prediction is in {@code (0, 1)}, it is multiplied by {@value RATING_SCALE} to produce
     * a value in the {@code (0, 5)} range that the leaderboard, helper profile, and matching service already
     * read from {@code average_rating}.
     * </p>
     *
     * <p>If a helper has no existing {@code HelperAnalytics} row, a new one is created with 
     * a generated ID based on their helper ID. If one already exists, 
     * its {@code average_rating} is updated in place.
     * </p>
     *
     * @param allFeatures feature vectors for all helpers, used to get helper IDs for the DB lookup and to run inference
     */
    private void persistScores(List<TrustScoreFeatures> allFeatures) {
        log.info("TrustScoreTrainer: persisting trust scores for {} helpers.", allFeatures.size());

        for (TrustScoreFeatures features : allFeatures) {
            int helperId = features.getHelperId();

            double rawScore  = model.predict(features.toArray());
            float scaledScore = (float) (rawScore * RATING_SCALE);

            Helper helper = helperRepository.findById(helperId).orElse(null);
            if (helper == null || helper.getUserid() == null) {
                continue;
            }

            int userId = helper.getUserid().getUserid();

            Optional<HelperAnalytics> existing =
                    helperAnalyticsRepository.findByUserId(userId);

            if (existing.isPresent()) {
                HelperAnalytics record = existing.get();
                record.setAverageRating(scaledScore);
                helperAnalyticsRepository.save(record);
            } else {
                HelperAnalytics newRecord = new HelperAnalytics();
                newRecord.setHelperAnalyticsid("ha-" + helperId);
                newRecord.setUserid(helper.getUserid());
                newRecord.setAverageRating(scaledScore);
                helperAnalyticsRepository.save(newRecord);
            }

            log.debug("TrustScoreTrainer: helper {} — rawScore={}, scaledScore={}",
                    helperId,
                    String.format("%.4f", rawScore),
                    String.format("%.2f", scaledScore));
        }

        log.info("TrustScoreTrainer: trust scores persisted successfully.");
    }
}

package com.app.api.trust;

/**
 * Pairs a helper's computed feature vector with a training label.
 *
 * <p>Used exclusively by TrustScoreTrainer to build our labelled dataset 
 * that is passed to TrustScoreModel#train.
 * </p>
 *
 * <p>Helpers that do not clearly fall into either category are
 * excluded from the training set entirely to avoid noise. 
 * </p>
 */
public class TrainingSample {

    private final TrustScoreFeatures features;
    private final double label;

    /**
     * Constructs a training sample.
     *
     * @param features the six normalised feature values for this helper
     * @param label 1.0 for trusted, 0.0 for not trusted
     */
    public TrainingSample(TrustScoreFeatures features, double label) {
        this.features = features;
        this.label = label;
    }

    /**
     * Returns the feature vector for this sample.
     *
     * @return the TrustScoreFeatures value object
     */
    public TrustScoreFeatures getFeatures() {
        return features;
    }

    /**
     * Returns the training label for this sample.
     *
     * @return label
     */
    public double getLabel() {
        return label;
    }
}

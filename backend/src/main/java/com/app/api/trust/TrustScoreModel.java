package com.app.api.trust;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Logistic regression classifier that computes a dynamic trust score for every helper.
 *
 * <h2>How it works</h2>
 * <p>Given a feature vector {@code x} of length {@code k}, the model
 * computes:</p>
 * <pre>
 *  z = w[0]*x[0] + w[1]*x[1] + ... + w[k-1]*x[k-1] + bias
 *  score = sigmoid(z) = 1 / (1 + exp(-z))
 * </pre>
 * <p>The output {@code score} is a probability in {@code (0, 1)}.
 * Multiplied by 5.0 it maps directly onto the existing
 * {@code average_rating} scale used by the leaderboard and profiles.</p>
 *
 * <h2>Training</h2>
 * <p>Weights are updated via mini-batch gradient descent using binary
 * cross-entropy as the cost function. The update rule for each weight
 * {@code w[j]} over a batch of {@code m} samples is:</p>
 * <pre>
 *   w[j] -= learningRate * (1/m) * sum_i( (ŷ_i - y_i) * x_i[j] )
 * </pre>
 *
 * <h2>Thread safety</h2>
 * <p>A {@link ReentrantReadWriteLock} protects the weight array.
 * Any number of HTTP threads can call {@link #predict} concurrently
 * (read lock). When the scheduler retrains the model it acquires the
 * write lock, which blocks only until all in-flight predictions
 * complete, then swaps in the new weights atomically.</p>
 */
@Component
public class TrustScoreModel {

    private static final Logger log = LoggerFactory.getLogger(TrustScoreModel.class);

    public static final int FEATURE_COUNT = 6;

    /**
     * Domain-informed initial weights in feature order:
     *
     * <p>These values encode prior knowledge about which signals matter most for helper trustworthiness. 
     * Gradient descent refines them from this starting point during training.
     * </p>
     */
    private static final double[] INITIAL_WEIGHTS = {
        0.40, // completionRate    
        0.25, // ratingVolumeScore 
        0.20, // reportPenalty     
        0.08, // recencyScore     
        0.05, // zoneActivity     
        0.02  // daysActive       
    };

    private double[] weights;
    private double bias;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Constructs the model and loads domain-informed initial weights.
     */
    public TrustScoreModel() {
        this.weights = Arrays.copyOf(INITIAL_WEIGHTS, FEATURE_COUNT);
        this.bias = 0.0;
    }

    /**
     * Predicts the trust score for a helper given their feature vector.
     *
     * <p>Acquires a read lock so concurrent HTTP requests can call this simultaneously 
     * without blocking each other. Only a retraining event will cause a brief wait here.
     * </p>
     *
     * @param features a double[] of length FEATURE_COUNT in the order defined TrustScoreFeatures toArray()
     * @return a trust probability 
     * @throws IllegalArgumentException if the feature array length is wrong
     */
    public double predict(double[] features) {
        if (features.length != FEATURE_COUNT) {
            throw new IllegalArgumentException(
                "Expected " + FEATURE_COUNT + " features, got " + features.length);
        }

        lock.readLock().lock();

        try {
            return sigmoid(dotProduct(weights, features) + bias);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Trains the model on the provided labelled samples using gradient descent.
     *
     * <p>The method trains on a local copy of the weights, then acquires the write lock only
     * at the very end to swap in the new values.
     * This keeps the write-lock hold time as short as possible so in-flight prediction threads
     * are not kept waiting through the full training loop.
     * </p>
     *
     * @param samples labelled training samples 
     * @param learningRate step size for gradient descent set 0.01 as a safe default
     * @param epochs number of full passes over the training data set as 100 becoz its sufficient
     */
    public void train(List<TrainingSample> samples, double learningRate, int epochs) {
        if (samples == null || samples.isEmpty()) {
            log.warn("TrustScoreModel: no training samples provided — keeping current weights.");
            return;
        }

        lock.readLock().lock();
        double[] localWeights;
        double localBias;

        try {
            localWeights = Arrays.copyOf(weights, FEATURE_COUNT);
            localBias = bias;
        } finally {
            lock.readLock().unlock();
        }

        int m = samples.size();
        log.info("TrustScoreModel: starting training — {} samples, {} epochs, lr={}",
                m, epochs, learningRate);

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] weightGradients = new double[FEATURE_COUNT];
            double biasGradient = 0.0;

            for (TrainingSample sample : samples) {
                double[] x = sample.getFeatures().toArray();
                double y = sample.getLabel();
                double yHat = sigmoid(dotProduct(localWeights, x) + localBias);
                double error = yHat - y;

                for (int j = 0; j < FEATURE_COUNT; j++) {
                    weightGradients[j] += error * x[j];
                }
                biasGradient += error;
            }

            for (int j = 0; j < FEATURE_COUNT; j++) {
                localWeights[j] -= learningRate * (weightGradients[j] / m);
            }
            localBias -= learningRate * (biasGradient / m);

            if (epoch % 10 == 0) {
                double cost = computeCost(samples, localWeights, localBias);
                log.info("TrustScoreModel: epoch {} — cost={:.6f}", epoch, cost);
            }
        }

        lock.writeLock().lock();

        try {
            this.weights = localWeights;
            this.bias = localBias;
        } finally {
            lock.writeLock().unlock();
        }

        log.info("TrustScoreModel: training complete — new weights: {}", Arrays.toString(localWeights));
    }

    /**
     * Returns a copy of the current weight vector.
     *
     * @return a defensive copy of the weight array
     */
    public double[] getWeights() {
        lock.readLock().lock();

        try {
            return Arrays.copyOf(weights, FEATURE_COUNT);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns the current bias term.
     *
     * @return the bias value
     */
    public double getBias() {
        lock.readLock().lock();

        try {
            return bias;
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * The sigmoid activation function.
     * 
     * @param z the linear combination of weights and features plus bias
     * @return a probability 
     */
    private double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    /**
     * Computes the dot product of two equal-length arrays.
     *
     * @param a the weight vector
     * @param b the feature vector
     * @return the scalar dot product
     */
    private double dotProduct(double[] a, double[] b) {
        double sum = 0.0;

        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }

        return sum;
    }

    /**
     * Computes the binary cross-entropy cost over the full training set.
     *
     * @param samples the labelled training samples
     * @param localWeights the current candidate weight vector
     * @param localBias the current candidate bias
     * @return mean binary cross-entropy over all samples
     */
    private double computeCost(List<TrainingSample> samples, double[] localWeights, double localBias) {
        double total = 0.0;

        for (TrainingSample sample : samples) {
            double[] x = sample.getFeatures().toArray();
            double y = sample.getLabel();
            double yHat = sigmoid(dotProduct(localWeights, x) + localBias);
            double eps = 1e-15;
            total += -y * Math.log(yHat + eps) - (1 - y) * Math.log(1 - yHat + eps);
        }

        return total / samples.size();
    }
}

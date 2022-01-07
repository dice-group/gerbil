package org.aksw.gerbil.evaluate.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation computes the precision, recall and f1 scores for a
 * threshold with which the F1 is optimal.
 * 
 * @param <T>
 */
public class ConfidenceBasedEvaluator<T extends Model> implements Evaluator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfidenceBasedEvaluator.class);

    private static final Property DEFAULT_TRUTH_VALUE_PROP = ResourceFactory
            .createProperty("http://swc2017.aksw.org/hasTruthValue");

    protected static final double GS_VALUE_FOR_TRUE = 1.0;

    protected static final int PRECISION = 0;
    protected static final int RECALL = 1;
    protected static final int F1_SCORE = 2;
    protected static final int THRESHOLD = 3;

    protected static final String PRECISION_STR = "Precision";
    protected static final String RECALL_STR = "Recall";
    protected static final String F1_SCORE_STR = "F1-score";
    protected static final String THRESHOLD_STR = "Opt. Threshold";

    /**
     * ONLY USED FOR TESTING!!! Maps the threshold to the corresponding
     * precision/recall/f1-scores
     */
    private Map<Double, double[]> thresholdResultsMap;

    public ConfidenceBasedEvaluator() {
        thresholdResultsMap = new HashMap<Double, double[]>();
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        Model annoModel = annotatorResults.get(0).get(0);
        Model goldModel = goldStandard.get(0).get(0);

        Map<String, Double> annoMap = getMapFromModel(annoModel);
        Map<String, Double> goldMap = getMapFromModel(goldModel);

        // iterate over the statements in gold standard and collect the confidence
        // assigned to them together with the ground truth
        SortedMap<Double, ConfidenceStatistics> sortedResults = new TreeMap<>();
        Double confidence;
        ConfidenceStatistics confidenceStats;
        boolean expectedResult;
        int tp = 0;
        int fp = 0;
        int tn = 0;
        int fn = 0;
        for (Entry<String, Double> gsEntry : goldMap.entrySet()) {
            expectedResult = gsEntry.getValue() == GS_VALUE_FOR_TRUE ? true : false;
            // If there is an answer from the annotator
            if (annoMap.containsKey(gsEntry.getKey())) {
                confidence = annoMap.get(gsEntry.getKey());
                if (sortedResults.containsKey(confidence)) {
                    confidenceStats = sortedResults.get(confidence);
                } else {
                    confidenceStats = new ConfidenceStatistics();
                    sortedResults.put(confidence, confidenceStats);
                }
                // Check if the instance is classified as true in the gold standard
                if (expectedResult) {
                    confidenceStats.increaseTrueCount();
                    ++tp;
                } else {
                    confidenceStats.increaseFalseCount();
                    ++fp;
                }
            } else {
                // Check if the instance is classified as true in the gold standard
                if (expectedResult) {
                    // The annotator didn't give any result -> punish it by increasing FN
                    ++fn;
                } else {
                    // The annotator didn't give any result -> punish it by increasing FP
                    ++fp;
                }
            }
        }

        // At this point, the confidence statistics are sorted ascending. Hence, we
        // start with a threshold that classifies everything as true, i.e., all true
        // triples will be TP and all false triples will be FP. Missing statements are
        // already added to FP and FN, respectively.
        int tempCount;
        ConfidenceStatistics previousStats = new ConfidenceStatistics();
        double[] bestMeasures = new double[4];
        Iterator<Double> confidenceIter = sortedResults.keySet().iterator();
        Double threshold;
        while (confidenceIter.hasNext()) {
            threshold = confidenceIter.next();
            // Apply the changes of the last statistics object, i.e. true instances decrease
            // the tp count and increase the fn count. False instances decrease the fp count
            // and increase the tn count.
            tempCount = previousStats.getTrueCount();
            tp -= tempCount;
            fn += tempCount;
            tempCount = previousStats.getFalseCount();
            fp -= tempCount;
            tn += tempCount;

            // calculate current scores
            double[] curMeasures = calculateMeasures(tp, fn, fp);

            // if it is the first threshold considered or if f1 is better for the current
            // threshold
            if (bestMeasures == null || curMeasures[F1_SCORE] > bestMeasures[F1_SCORE]) {
                bestMeasures[PRECISION] = curMeasures[PRECISION];
                bestMeasures[RECALL] = curMeasures[RECALL];
                bestMeasures[F1_SCORE] = curMeasures[F1_SCORE];
                bestMeasures[THRESHOLD] = threshold;
            }
            thresholdResultsMap.put(threshold, curMeasures);

            previousStats = sortedResults.get(threshold);
        }

        if ((tp + fp + tn + fn) != goldMap.size()) {
            LOGGER.error(
                    "Ended up with a different sum for tp({}), fp({}), tn({}) and fn({}) than the number of instances in the gold standard({}).",
                    tp, fp, tn, fn, goldMap.size());
        }

        results.addResults(new DoubleEvaluationResult(PRECISION_STR, bestMeasures[PRECISION]),
                new DoubleEvaluationResult(RECALL_STR, bestMeasures[RECALL]),
                new DoubleEvaluationResult(F1_SCORE_STR, bestMeasures[F1_SCORE]),
                new DoubleEvaluationResult(THRESHOLD_STR, bestMeasures[THRESHOLD]));
    }

    /**
     * Assumes there only should be one triple per subject type for the TRUTH_VALUE
     * predicate. It returns a map between the model's subject and the confidence
     * value stmt_n -> confidence value
     * 
     * @param model
     * @return
     */
    private Map<String, Double> getMapFromModel(Model model) {
        Map<String, Double> map = new HashMap<String, Double>();
        StmtIterator iterator = model.listStatements(null, DEFAULT_TRUTH_VALUE_PROP, (RDFNode) null);
        while (iterator.hasNext()) {
            Statement curStmt = iterator.next();
            if (map.putIfAbsent(curStmt.getSubject().getURI(), curStmt.getDouble()) != null) {
                LOGGER.warn("Annotator or gold dataset have duplicate statements.");
            }
        }
        return map;
    }

    /**
     * TODO consider changing this to an util class ? it is repeated a lot in the
     * code
     * 
     * @param truePositives
     * @param falseNegatives
     * @param falsePositives
     * @return
     */
    private double[] calculateMeasures(int truePositives, int falseNegatives, int falsePositives) {
        double precision, recall, F1_score;
        if (truePositives == 0) {
            if ((falsePositives == 0) && (falseNegatives == 0)) {
                // If there haven't been something to find and nothing has been
                // found --> everything is great
                precision = 1.0;
                recall = 1.0;
                F1_score = 1.0;
            } else {
                // The annotator found no correct ones, but made some mistake
                // --> that is bad
                precision = 0.0;
                recall = 0.0;
                F1_score = 0.0;
            }
        } else {
            precision = (double) truePositives / (double) (truePositives + falsePositives);
            recall = (double) truePositives / (double) (truePositives + falseNegatives);
            F1_score = (2 * precision * recall) / (precision + recall);
        }
        return new double[] { precision, recall, F1_score };
    }

    /**
     * ONLY USED FOR TESTING! Should be used after evaluate call, since the map is
     * filled there
     * 
     * @return
     */
    protected Map<Double, double[]> getThresholdResultsMap() {
        return thresholdResultsMap;
    }

    /**
     * This simple structure represents statistics about a single confidence
     * threshold.
     * 
     * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
     *
     */
    public static class ConfidenceStatistics {
        /**
         * Instances that got this {@link #confidence} score by the annotator and a
         * marked as true in the gold standard.
         */
        private int trueCount;
        /**
         * Instances that got this {@link #confidence} score by the annotator and a
         * marked as false in the gold standard.
         */
        private int falseCount;

        public ConfidenceStatistics() {
            this(0, 0);
        }

        public ConfidenceStatistics(int trueCount, int falseCount) {
            this.trueCount = trueCount;
            this.falseCount = falseCount;
        }

        /**
         * @return the trueCount
         */
        public int getTrueCount() {
            return trueCount;
        }

        /**
         * @param trueCount the trueCount to set
         */
        public void setTrueCount(int trueCount) {
            this.trueCount = trueCount;
        }

        /**
         * @return the falseCount
         */
        public int getFalseCount() {
            return falseCount;
        }

        /**
         * @param falseCount the falseCount to set
         */
        public void setFalseCount(int falseCount) {
            this.falseCount = falseCount;
        }

        /**
         * Increases the internal count of true instance by 1.
         */
        public void increaseTrueCount() {
            ++this.trueCount;
        }

        /**
         * Increases the internal count of false instance by 1.
         */
        public void increaseFalseCount() {
            ++this.falseCount;
        }
    }
}

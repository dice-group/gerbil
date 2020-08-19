/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.evaluate.impl;

import java.util.List;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.matching.scored.ScoredEvaluationCounts;
import org.aksw.gerbil.matching.scored.ScoredEvaluationCountsArray;
import org.aksw.gerbil.matching.scored.ScoredMatchingsCounter;
import org.aksw.gerbil.matching.scored.ScoredMatchingsCounterImpl;
import org.aksw.gerbil.transfer.nif.Marking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfidenceBasedFMeasureCalculator<T extends Marking> implements Evaluator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfidenceBasedFMeasureCalculator.class);

    protected static final int PRECISION = 0;
    protected static final int RECALL = 1;
    protected static final int F1_SCORE = 2;

    private static boolean printDebugMsg = true;

    protected ScoredMatchingsCounter<T> matchingsCounter;

    public ConfidenceBasedFMeasureCalculator(MatchingsCounter<T> matchingsCounter) {
        super();
        this.matchingsCounter = new ScoredMatchingsCounterImpl<T>(matchingsCounter);
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results,String language) {
        ScoredEvaluationCountsArray counts = generateMatchingCounts(annotatorResults, goldStandard);
        if ((counts.truePositiveSums.length > 0) && (counts.falseNegativeSums.length > 0)
                && (counts.falsePositiveSums.length > 0)) {
            double threshold = calculateMicroFMeasure(counts, results);
            calculateMacroFMeasure(counts, results, threshold);
        }
    }

    protected ScoredEvaluationCountsArray generateMatchingCounts(List<List<T>> annotatorResults,
            List<List<T>> goldStandard) {
        ScoredEvaluationCounts counts[][] = new ScoredEvaluationCounts[annotatorResults.size()][];
        for (int i = 0; i < counts.length; ++i) {
            if (printDebugMsg && LOGGER.isDebugEnabled()) {
                LOGGER.debug("${pom_version} " + i + "|||||||||");
            }
            counts[i] = matchingsCounter.countMatchings(annotatorResults.get(i), goldStandard.get(i));
        }
        return ScoredEvaluationCountsArray.create(counts);
    }

    @SuppressWarnings("deprecation")
    protected double calculateMicroFMeasure(ScoredEvaluationCountsArray counts, EvaluationResultContainer results) {
        return calculateOptMicroFMeasure(counts, FMeasureCalculator.MICRO_PRECISION_NAME,
                FMeasureCalculator.MICRO_RECALL_NAME, FMeasureCalculator.MICRO_F1_SCORE_NAME,
                ConfidenceScoreEvaluatorDecorator.CONFIDENCE_SCORE_THRESHOLD_RESULT_NAME, results);
    }

    protected void calculateMicroFMeasure(ScoredEvaluationCountsArray counts, String precisionName, String recallName,
            String f1ScoreName, double threshold, EvaluationResultContainer results) {
        double measures[];
        int pos = 0;
        while ((pos < counts.scores.length) && (threshold > counts.scores[pos])) {
            ++pos;
        }
        if (pos < counts.scores.length) {
            measures = calculateMeasures(counts.truePositiveSums[pos], counts.falseNegativeSums[pos],
                    counts.falsePositiveSums[pos]);
        } else {
            if ((counts.truePositiveSums[0] + counts.falseNegativeSums[0] + counts.falsePositiveSums[0]) == 0) {
                measures = new double[] { 1, 1, 1 };
            } else {
                measures = new double[] { 0, 0, 0 };
            }
        }
        results.addResults(new DoubleEvaluationResult(precisionName, measures[PRECISION]));
        results.addResults(new DoubleEvaluationResult(recallName, measures[RECALL]));
        results.addResults(new DoubleEvaluationResult(f1ScoreName, measures[F1_SCORE]));
    }

    protected double calculateOptMicroFMeasure(ScoredEvaluationCountsArray counts, String precisionName,
            String recallName, String f1ScoreName, String confidenceScoreName, EvaluationResultContainer results) {
        double bestMeasures[] = calculateMeasures(counts.truePositiveSums[0], counts.falseNegativeSums[0],
                counts.falsePositiveSums[0]);
        int bestIndex = 0;
        double measures[];
        for (int i = 0; i < counts.scores.length; ++i) {
            measures = calculateMeasures(counts.truePositiveSums[i], counts.falseNegativeSums[i],
                    counts.falsePositiveSums[i]);
            if (bestMeasures[F1_SCORE] < measures[F1_SCORE]) {
                bestMeasures = measures;
                bestIndex = i;
            }
        }

        results.addResults(new DoubleEvaluationResult(precisionName, bestMeasures[PRECISION]));
        results.addResults(new DoubleEvaluationResult(recallName, bestMeasures[RECALL]));
        results.addResults(new DoubleEvaluationResult(f1ScoreName, bestMeasures[F1_SCORE]));
        results.addResults(new DoubleEvaluationResult(confidenceScoreName, counts.scores[bestIndex]));
        return counts.scores[bestIndex];
    }

    protected void calculateMacroFMeasure(ScoredEvaluationCountsArray counts, EvaluationResultContainer results,
            double confidencethreshold) {
        calculateMacroFMeasure(counts, FMeasureCalculator.MACRO_PRECISION_NAME, FMeasureCalculator.MACRO_RECALL_NAME,
                FMeasureCalculator.MACRO_F1_SCORE_NAME, confidencethreshold, results);
    }

    protected void calculateMacroFMeasure(ScoredEvaluationCountsArray counts, String precisionName, String recallName,
            String f1ScoreName, double confidencethreshold, EvaluationResultContainer results) {
        double avgs[];
        int pos = 0;
        while ((pos < counts.scores.length) && (confidencethreshold > counts.scores[pos])) {
            ++pos;
        }
        if (pos < counts.scores.length) {
            avgs = new double[3];
            double measures[];
            int numberOfDocuments = counts.truePositives[pos].length;
            for (int d = 0; d < numberOfDocuments; ++d) {
                measures = calculateMeasures(counts.truePositives[pos][d], counts.falseNegatives[pos][d],
                        counts.falsePositives[pos][d]);
                avgs[PRECISION] += measures[PRECISION];
                avgs[RECALL] += measures[RECALL];
                avgs[F1_SCORE] += measures[F1_SCORE];
            }
            avgs[PRECISION] /= numberOfDocuments;
            avgs[RECALL] /= numberOfDocuments;
            avgs[F1_SCORE] /= numberOfDocuments;
        } else {
            if ((counts.truePositiveSums[0] + counts.falseNegativeSums[0] + counts.falsePositiveSums[0]) == 0) {
                avgs = new double[] { 1, 1, 1 };
            } else {
                avgs = new double[] { 0, 0, 0 };
            }
        }
        results.addResults(new DoubleEvaluationResult(precisionName, avgs[PRECISION]));
        results.addResults(new DoubleEvaluationResult(recallName, avgs[RECALL]));
        results.addResults(new DoubleEvaluationResult(f1ScoreName, avgs[F1_SCORE]));
    }

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

    public static synchronized void setPrintDebugMsg(boolean flag) {
        printDebugMsg = flag;
    }
}

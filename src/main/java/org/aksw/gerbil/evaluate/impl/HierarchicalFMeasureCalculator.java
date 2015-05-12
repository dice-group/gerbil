package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.impl.HierarchicalMatchingsCounter;
import org.aksw.gerbil.matching.impl.MatchingsCounter;
import org.aksw.gerbil.transfer.nif.TypedMarking;

public class HierarchicalFMeasureCalculator<T extends TypedMarking> implements Evaluator<T> {

    protected HierarchicalMatchingsCounter<T> matchingsCounter;

    private static final int PRECISION_ID = 0;
    private static final int RECALL_ID = 1;

    public HierarchicalFMeasureCalculator(HierarchicalMatchingsCounter<T> matchingsCounter) {
        super();
        this.matchingsCounter = matchingsCounter;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results) {
        for (int i = 0; i < annotatorResults.size(); ++i) {
            matchingsCounter.countMatchings(annotatorResults.get(i), goldStandard.get(i));
        }
        List<List<int[]>> matchingCounts = matchingsCounter.getCounts();
        List<List<double[]>> measures = calculateMeasures(matchingCounts);

        results.addResults(calculateMicroFMeasure(measures));
        results.addResults(calculateMacroFMeasure(measures));
    }

    private List<List<double[]>> calculateMeasures(List<List<int[]>> matchingCounts) {
        List<List<double[]>> measures = new ArrayList<List<double[]>>(matchingCounts.size());
        List<double[]> localMeasures;
        double[] singleMeasures;
        for (List<int[]> counts : matchingCounts) {
            if (counts.size() > 0) {
                localMeasures = new ArrayList<double[]>(counts.size());
                for (int[] singleCounts : counts) {
                    singleMeasures = new double[2];
                    // If no matching was correct
                    if (singleCounts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] == 0) {
                        // check whether a matching was false
                        if ((singleCounts[MatchingsCounter.FALSE_POSITIVE_COUNT_ID] == 0)
                                && (singleCounts[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID] == 0)) {
                            singleMeasures[PRECISION_ID] = 1.0;
                            singleMeasures[RECALL_ID] = 1.0;
                        } else {
                            singleMeasures[PRECISION_ID] = 0.0;
                            singleMeasures[RECALL_ID] = 0.0;
                        }
                    } else {
                        singleMeasures[PRECISION_ID] = singleCounts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID]
                                / (singleCounts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] + singleCounts[MatchingsCounter.FALSE_POSITIVE_COUNT_ID]);
                        singleMeasures[RECALL_ID] = singleCounts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID]
                                / (singleCounts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] + singleCounts[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID]);
                    }
                    localMeasures.add(singleMeasures);
                }
                measures.add(localMeasures);
            }
        }
        return measures;
    }

    private EvaluationResult[] calculateMicroFMeasure(List<List<double[]>> measuresList) {
        double precision = 0, recall = 0;
        int count = 0;
        for (List<double[]> m : measuresList) {
            for (double[] measures : m) {
                precision += measures[PRECISION_ID];
                recall += measures[RECALL_ID];
                ++count;
            }
        }
        precision /= count;
        recall /= count;
        return new EvaluationResult[] { new DoubleEvaluationResult(FMeasureCalculator.MICRO_PRECISION_NAME, precision),
                new DoubleEvaluationResult(FMeasureCalculator.MICRO_RECALL_NAME, recall),
                new DoubleEvaluationResult(FMeasureCalculator.MICRO_F1_SCORE_NAME, calculateF1(precision, recall)) };
    }

    private EvaluationResult[] calculateMacroFMeasure(List<List<double[]>> measuresList) {
        double precision = 0, recall = 0, f1Score = 0, tmpPrecSum = 0, tmpRecSum = 0;
        for (List<double[]> m : measuresList) {
            tmpPrecSum = 0;
            tmpRecSum = 0;
            for (double[] measures : m) {
                tmpPrecSum += measures[PRECISION_ID];
                tmpRecSum += measures[RECALL_ID];
            }
            tmpPrecSum /= m.size();
            tmpRecSum /= m.size();
            precision += tmpPrecSum;
            recall += tmpRecSum;
            f1Score += calculateF1(tmpPrecSum, tmpRecSum);
        }
        precision /= measuresList.size();
        recall /= measuresList.size();
        f1Score /= measuresList.size();
        return new EvaluationResult[] { new DoubleEvaluationResult(FMeasureCalculator.MACRO_PRECISION_NAME, precision),
                new DoubleEvaluationResult(FMeasureCalculator.MACRO_RECALL_NAME, recall),
                new DoubleEvaluationResult(FMeasureCalculator.MACRO_F1_SCORE_NAME, f1Score) };
    }

    private double calculateF1(double precision, double recall) {
        if ((precision == 0) || (recall == 0)) {
            return 0;
        }
        return (2 * precision * recall) / (precision + recall);
    }
}

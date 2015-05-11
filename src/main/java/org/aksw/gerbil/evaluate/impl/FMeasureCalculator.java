package org.aksw.gerbil.evaluate.impl;

import java.util.List;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.impl.MatchingsCounter;
import org.aksw.gerbil.transfer.nif.Marking;

public class FMeasureCalculator<T extends Marking> implements Evaluator<T> {

    public static final String MACRO_F1_SCORE_NAME = "Macro F1 score";
    public static final String MACRO_PRECISION_NAME = "Macro Precision";
    public static final String MACRO_RECALL_NAME = "Macro Recall";
    public static final String MICRO_F1_SCORE_NAME = "Micro F1 score";
    public static final String MICRO_PRECISION_NAME = "Micro Precision";
    public static final String MICRO_RECALL_NAME = "Micro Recall";

    protected MatchingsCounter<T> matchingsCounter;

    public FMeasureCalculator(MatchingsCounter<T> matchingsCounter) {
        super();
        this.matchingsCounter = matchingsCounter;
    }

    @Override
    public EvaluationResult evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard) {
        for (int i = 0; i < annotatorResults.size(); ++i) {
            matchingsCounter.countMatchings(annotatorResults.get(i), goldStandard.get(i));
        }
        List<int[]> matchingCounts = matchingsCounter.getCounts();
        EvaluationResultContainer results = new EvaluationResultContainer();
        results.addResults(calculateMicroFMeasure(matchingCounts));
        results.addResults(calculateMacroFMeasure(matchingCounts));
        return results;
    }

    protected EvaluationResult[] calculateMicroFMeasure(List<int[]> matchingCounts) {
        return calculateMicroFMeasure(matchingCounts, MICRO_PRECISION_NAME, MICRO_RECALL_NAME, MICRO_F1_SCORE_NAME);
    }

    protected EvaluationResult[] calculateMicroFMeasure(List<int[]> matchingCounts, String precisionName,
            String recallName, String f1ScoreName) {
        int sums[] = new int[3];
        for (int[] counts : matchingCounts) {
            sums[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] += counts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID];
            sums[MatchingsCounter.FALSE_POSITIVE_COUNT_ID] += counts[MatchingsCounter.FALSE_POSITIVE_COUNT_ID];
            sums[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID] += counts[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID];
        }
        double measures[] = calculateMeasures(sums);
        return new EvaluationResult[] { new DoubleEvaluationResult(precisionName, measures[0]),
                new DoubleEvaluationResult(recallName, measures[1]),
                new DoubleEvaluationResult(f1ScoreName, measures[2]) };
    }

    protected EvaluationResult[] calculateMacroFMeasure(List<int[]> matchingCounts) {
        return calculateMacroFMeasure(matchingCounts, MACRO_PRECISION_NAME, MACRO_RECALL_NAME, MACRO_F1_SCORE_NAME);
    }

    protected EvaluationResult[] calculateMacroFMeasure(List<int[]> matchingCounts, String precisionName,
            String recallName, String f1ScoreName) {
        double avgs[] = new double[3];
        double measures[];
        for (int[] counts : matchingCounts) {
            measures = calculateMeasures(counts);
            avgs[0] += measures[0];
            avgs[1] += measures[1];
            avgs[2] += measures[2];
        }
        avgs[0] /= matchingCounts.size();
        avgs[1] /= matchingCounts.size();
        avgs[2] /= matchingCounts.size();
        return new EvaluationResult[] { new DoubleEvaluationResult(precisionName, avgs[0]),
                new DoubleEvaluationResult(recallName, avgs[1]), new DoubleEvaluationResult(f1ScoreName, avgs[2]) };
    }

    private double[] calculateMeasures(int[] counts) {
        double precision, recall, F1_score;
        if (counts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] == 0) {
            if ((counts[MatchingsCounter.FALSE_POSITIVE_COUNT_ID] == 0)
                    && (counts[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID] == 0)) {
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
            precision = (double) counts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID]
                    / (double) (counts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] + counts[MatchingsCounter.FALSE_POSITIVE_COUNT_ID]);
            recall = (double) counts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID]
                    / (double) (counts[MatchingsCounter.TRUE_POSITIVE_COUNT_ID] + counts[MatchingsCounter.FALSE_NEGATIVE_COUNT_ID]);
            F1_score = (2 * precision * recall) / (precision + recall);
        }
        return new double[] { precision, recall, F1_score };
    }
}

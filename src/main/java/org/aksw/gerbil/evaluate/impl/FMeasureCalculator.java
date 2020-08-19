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
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.EvaluationCounts;
import org.aksw.gerbil.matching.MatchingsCounter;
import org.aksw.gerbil.transfer.nif.Marking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FMeasureCalculator<T extends Marking> implements Evaluator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMeasureCalculator.class);

    public static final String MACRO_F1_SCORE_NAME = "Macro F1 score";
    public static final String MACRO_PRECISION_NAME = "Macro Precision";
    public static final String MACRO_RECALL_NAME = "Macro Recall";
    public static final String MICRO_F1_SCORE_NAME = "Micro F1 score";
    public static final String MICRO_PRECISION_NAME = "Micro Precision";
    public static final String MICRO_RECALL_NAME = "Micro Recall";
    public static final String BLEU_METRIC = "BLEU";
    public static final String METEOR_METRIC = "METEOR";

    protected MatchingsCounter<T> matchingsCounter;

    public FMeasureCalculator(MatchingsCounter<T> matchingsCounter) {
        super();
        this.matchingsCounter = matchingsCounter;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results, String language) {
        EvaluationCounts counts[] = generateMatchingCounts(annotatorResults, goldStandard);
        results.addResults(calculateMicroFMeasure(counts));
        results.addResults(calculateMacroFMeasure(counts));
    }

    protected EvaluationCounts[] generateMatchingCounts(List<List<T>> annotatorResults, List<List<T>> goldStandard) {
        EvaluationCounts counts[] = new EvaluationCounts[annotatorResults.size()];
        for (int i = 0; i < counts.length; ++i) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("doc " + i + "|||||||||");
            }
            counts[i] = matchingsCounter.countMatchings(annotatorResults.get(i), goldStandard.get(i));
        }
        return counts;
    }

    protected EvaluationResult[] calculateMicroFMeasure(EvaluationCounts counts[]) {
        return calculateMicroFMeasure(counts, MICRO_PRECISION_NAME, MICRO_RECALL_NAME, MICRO_F1_SCORE_NAME, BLEU_METRIC, METEOR_METRIC);
    }

    protected EvaluationResult[] calculateMicroFMeasure(EvaluationCounts counts[], String precisionName,
            String recallName, String f1ScoreName, String bleu, String meteor) {
        EvaluationCounts sums = new EvaluationCounts();
        for (int i = 0; i < counts.length; ++i) {
            sums.add(counts[i]);
        }
        double measures[] = calculateMeasures(sums);
        return new EvaluationResult[] { new DoubleEvaluationResult(precisionName, measures[0]),
                new DoubleEvaluationResult(recallName, measures[1]),
                new DoubleEvaluationResult(f1ScoreName, measures[2]),
                new DoubleEvaluationResult(bleu, measures[3]),
                new DoubleEvaluationResult(meteor, measures[4])};
    }

    protected EvaluationResult[] calculateMacroFMeasure(EvaluationCounts counts[]) {
        return calculateMacroFMeasure(counts, MACRO_PRECISION_NAME, MACRO_RECALL_NAME, MACRO_F1_SCORE_NAME, BLEU_METRIC, METEOR_METRIC);
    }

    protected EvaluationResult[] calculateMacroFMeasure(EvaluationCounts counts[], String precisionName,
            String recallName, String f1ScoreName, String bleu, String meteor) {
        double avgs[] = new double[3];
        double measures[];
        for (int i = 0; i < counts.length; ++i) {
            measures = calculateMeasures(counts[i]);
            avgs[0] += measures[0];
            avgs[1] += measures[1];
            avgs[2] += measures[2];
            avgs[3] += measures[3];
            avgs[4] += measures[4];

        }
        avgs[0] /= counts.length;
        avgs[1] /= counts.length;
        avgs[2] /= counts.length;
        avgs[3] /= counts.length;
        avgs[4] /= counts.length;
        return new EvaluationResult[] { new DoubleEvaluationResult(precisionName, avgs[0]),
                new DoubleEvaluationResult(recallName, avgs[1]), new DoubleEvaluationResult(f1ScoreName, avgs[2]) , new DoubleEvaluationResult(bleu, avgs[3]),
                new DoubleEvaluationResult(meteor, avgs[4])};
    }

    private double[] calculateMeasures(EvaluationCounts counts) {
        double precision, recall, F1_score;
        if (counts.truePositives == 0) {
            if ((counts.falsePositives == 0) && (counts.falseNegatives == 0)) {
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
            precision = (double) counts.truePositives / (double) (counts.truePositives + counts.falsePositives);
            recall = (double) counts.truePositives / (double) (counts.truePositives + counts.falseNegatives);
            F1_score = (2 * precision * recall) / (precision + recall);
        }
        return new double[] { precision, recall, F1_score };
    }
}

/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        EvaluationCounts counts[] = generateMatchingCounts(annotatorResults, goldStandard);
        results.addResults(calculateMicroFMeasure(counts));
        results.addResults(calculateMacroFMeasure(counts));
    }

    protected EvaluationCounts[] generateMatchingCounts(List<List<T>> annotatorResults, List<List<T>> goldStandard) {
        EvaluationCounts counts[] = new EvaluationCounts[annotatorResults.size()];
        for (int i = 0; i < counts.length; ++i) {
            counts[i] = matchingsCounter.countMatchings(annotatorResults.get(i), goldStandard.get(i));
        }
        return counts;
    }

    protected EvaluationResult[] calculateMicroFMeasure(EvaluationCounts counts[]) {
        return calculateMicroFMeasure(counts, MICRO_PRECISION_NAME, MICRO_RECALL_NAME, MICRO_F1_SCORE_NAME);
    }

    protected EvaluationResult[] calculateMicroFMeasure(EvaluationCounts counts[], String precisionName,
            String recallName, String f1ScoreName) {
        EvaluationCounts sums = new EvaluationCounts();
        for (int i = 0; i < counts.length; ++i) {
            sums.add(counts[i]);
        }
        double measures[] = calculateMeasures(sums);
        return new EvaluationResult[] { new DoubleEvaluationResult(precisionName, measures[0]),
                new DoubleEvaluationResult(recallName, measures[1]),
                new DoubleEvaluationResult(f1ScoreName, measures[2]) };
    }

    protected EvaluationResult[] calculateMacroFMeasure(EvaluationCounts counts[]) {
        return calculateMacroFMeasure(counts, MACRO_PRECISION_NAME, MACRO_RECALL_NAME, MACRO_F1_SCORE_NAME);
    }

    protected EvaluationResult[] calculateMacroFMeasure(EvaluationCounts counts[], String precisionName,
            String recallName, String f1ScoreName) {
        double avgs[] = new double[3];
        double measures[];
        for (int i = 0; i < counts.length; ++i) {
            measures = calculateMeasures(counts[i]);
            avgs[0] += measures[0];
            avgs[1] += measures[1];
            avgs[2] += measures[2];
        }
        avgs[0] /= counts.length;
        avgs[1] /= counts.length;
        avgs[2] /= counts.length;
        return new EvaluationResult[] { new DoubleEvaluationResult(precisionName, avgs[0]),
                new DoubleEvaluationResult(recallName, avgs[1]), new DoubleEvaluationResult(f1ScoreName, avgs[2]) };
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

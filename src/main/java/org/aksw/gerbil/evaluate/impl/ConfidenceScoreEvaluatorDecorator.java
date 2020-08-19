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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.aksw.gerbil.utils.filter.ConfidenceScoreBasedMarkingFilter;
import org.aksw.gerbil.utils.filter.MarkingFilter;

import com.carrotsearch.hppc.DoubleOpenHashSet;

@Deprecated
public class ConfidenceScoreEvaluatorDecorator<T extends Marking> extends AbstractEvaluatorDecorator<T> {

    public static final String CONFIDENCE_SCORE_THRESHOLD_RESULT_NAME = "confidence threshold";

    private String resultName;
    private Comparator<EvaluationResult> resultComparator;

    public ConfidenceScoreEvaluatorDecorator(Evaluator<T> evaluator, String resultName,
            Comparator<EvaluationResult> resultComparator) {
        super(evaluator);
        this.resultName = resultName;
        this.resultComparator = resultComparator;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results,String language) {
        // create a list of confidence scores
        double scores[] = getConfidenceScores(annotatorResults);
        Arrays.sort(scores);

        EvaluationResultContainer bestResult = evaluate(annotatorResults, goldStandard, results, 0, language);
        EvaluationResultContainer currentResult;
        int bestScoreId = -1;
        // go through the confidence scores
        for (int i = 0; i < scores.length; ++i) {
            // evaluate the result using the current confidence
            currentResult = evaluate(annotatorResults, goldStandard, results, scores[i], language);
            bestResult = getBetterResult(currentResult, bestResult);
            if (bestResult == currentResult) {
                bestScoreId = i;
            }
            // here, the current result could be added to a list of results for
            // further detailed analysis
        }
        // copy best results into result container
        copyResults(bestResult, results);
        // add the threshold result
        if (scores.length > 1) {
            results.addResult(new DoubleEvaluationResult(CONFIDENCE_SCORE_THRESHOLD_RESULT_NAME,
                    bestScoreId < 0 ? 0 : scores[bestScoreId]));
        }
    }

    protected double[] getConfidenceScores(List<List<T>> annotatorResults) {
        DoubleOpenHashSet scores = new DoubleOpenHashSet();
        boolean foundMarkingWithoutScore = false;
        for (List<T> results : annotatorResults) {
            for (Marking result : results) {
                if (result instanceof ScoredMarking) {
                    scores.add(((ScoredMarking) result).getConfidence());
                } else {
                    foundMarkingWithoutScore = true;
                }
            }
        }
        // If there where no markings
        if ((scores.size() == 0) && (!foundMarkingWithoutScore)) {
            return new double[] { 1.0 };
        }
        // If there is a marking without a score
        if (foundMarkingWithoutScore) {
            // insert a score higher than all other representing all markings
            // without a score
            double max = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < scores.allocated.length; i++) {
                if (scores.allocated[i]) {
                    if (scores.keys[i] > max) {
                        max = scores.keys[i];
                    }
                }
            }
            if (max < 1.0) {
                scores.add(1.0);
            } else {
                scores.add(max + 1.0);
            }
        }
        return scores.toArray();
    }

    protected EvaluationResultContainer evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results, double threshold,String language){
        EvaluationResultContainer currentResults = new EvaluationResultContainer(results);
        MarkingFilter<T> filter = new ConfidenceScoreBasedMarkingFilter<T>(threshold);
        this.evaluator.evaluate(filter.filterListOfLists(annotatorResults), goldStandard, currentResults,language);
        return currentResults;
    }

    protected EvaluationResultContainer getBetterResult(EvaluationResultContainer currentResult,
            EvaluationResultContainer bestResult) {
        if (currentResult == null) {
            return bestResult;
        } else if (bestResult == null) {
            return currentResult;
        }
        EvaluationResult currentImpResult = findImportantResult(currentResult);
        if (currentImpResult == null) {
            return bestResult;
        }
        EvaluationResult bestImpResult = findImportantResult(bestResult);
        if (bestImpResult == null) {
            return currentResult;
        }
        if (resultComparator.compare(currentImpResult, bestImpResult) > 0) {
            return currentResult;
        } else {
            return bestResult;
        }
    }

    protected EvaluationResult findImportantResult(EvaluationResultContainer container) {
        for (EvaluationResult result : container.getResults()) {
            if (resultName.equals(result.getName())) {
                return result;
            }
        }
        return null;
    }

    private void copyResults(EvaluationResultContainer bestResult, EvaluationResultContainer results) {
        Set<EvaluationResult> knownResults = new HashSet<EvaluationResult>(results.getResults());
        for (EvaluationResult result : bestResult.getResults()) {
            if (!knownResults.contains(result)) {
                results.addResult(result);
            }
        }
    }
}

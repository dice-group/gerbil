package org.aksw.gerbil.evaluate.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.ScoredMarking;
import org.aksw.gerbil.utils.filter.ConfidenceScoreBasedMarkingFilter;
import org.aksw.gerbil.utils.filter.MarkingFilter;

import com.carrotsearch.hppc.DoubleOpenHashSet;

public class ConfidenceScoreEvaluatorDecorator<T extends Marking> extends AbstractEvaluatorDecorator<T> {

	private String resultName;
	private Comparator<EvaluationResult> resultComparator;

	public ConfidenceScoreEvaluatorDecorator(Evaluator<T> evaluator, String resultName,
			Comparator<EvaluationResult> resultComparator) {
		super(evaluator);
		this.resultName = resultName;
		this.resultComparator = resultComparator;
	}

	@Override
	public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results) {
		// create a list of confidence scores
		double scores[] = getConfidenceScores(annotatorResults);
		Arrays.sort(scores);

		EvaluationResultContainer currentResult, bestResult = null;
		// go through the confidence scores
		for (int i = 0; i < scores.length; ++i) {
			// evaluate the result using the current confidence
			currentResult = evaluate(annotatorResults, goldStandard, results, scores[i]);
			bestResult = getBetterResult(currentResult, bestResult);
			// here, the current result could be added to a list of results for
			// further detailed analysis
		}
		// copy best results into result container
		copyResults(bestResult, results);
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
			EvaluationResultContainer results, double threshold) {
		EvaluationResultContainer currentResults = new EvaluationResultContainer(results);
		MarkingFilter<T> filter = new ConfidenceScoreBasedMarkingFilter<T>(threshold);
		this.evaluator.evaluate(filter.filterListOfLists(annotatorResults), goldStandard, currentResults);
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

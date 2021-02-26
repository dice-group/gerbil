package org.aksw.gerbil.evaluate.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.matching.EvaluationCounts;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
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

	private static final Property DEFAULT_TRUTH_VALUE_PROP = ResourceFactory.createProperty("http://swc2017.aksw.org/hasTruthValue");

	protected static final int PRECISION = 0;
	protected static final int RECALL = 1;
	protected static final int F1_SCORE = 2;
	protected static final int THRESHOLD = 3;

	protected static final String PRECISION_STR = "precision";
	protected static final String RECALL_STR = "recall";
	protected static final String F1_SCORE_STR = "f1-score";
	protected static final String THRESHOLD_STR = "threshold";
	
	/**
	 * Maps the threshold to the corresponding precision/recall/f1-scores
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

		// Start with a threshold that classifies all true triples part of TP
		// and all false triples as FP - take the minimum
		Iterator<Double> possibleT = getThresholds(annotatorResults.get(0)).iterator();
		double[] bestMeasures = new double[4];

		while (possibleT.hasNext()) {
			double threshold = possibleT.next();
			EvaluationCounts counts = new EvaluationCounts();
			
			// iterate over the statements in gold
			for (String curStmt : goldMap.keySet()) {
				Double actual = annoMap.get(curStmt);
				boolean expected = goldMap.get(curStmt) == 1.0 ? true : false;

				// check if statement is missing in the annotator and penalize counts
				if (actual == null) {
					if (expected) {
						counts.falseNegatives++;
					} else {
						counts.falsePositives++;
					}
				} else {
					// otherwise continue the count normally
					if (actual >= threshold && expected) {
						counts.truePositives++;
					} else if (actual < threshold && expected) {
						counts.falseNegatives++;
					} else if (actual >= threshold && !expected) {
						counts.falsePositives++;
					} else {
						// TN++
					}
				}
			}
				// calculate current scores
				double[] curMeasures = calculateMeasures(counts.truePositives, counts.falseNegatives,
						counts.falsePositives);

				// if it is the first threshold considered or if f1 is better for the current
				// threshold
				if (bestMeasures == null || curMeasures[F1_SCORE] > bestMeasures[F1_SCORE]) {
					bestMeasures[PRECISION] = curMeasures[PRECISION];
					bestMeasures[RECALL] = curMeasures[RECALL];
					bestMeasures[F1_SCORE] = curMeasures[F1_SCORE];
					bestMeasures[THRESHOLD] = threshold;
				}
				thresholdResultsMap.put(threshold, curMeasures);
			
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
	 * 
	 * @param annotatorResult
	 * @return the ascending set of confidence values obtained in the annotator
	 *         dataset
	 */
	private SortedSet<Double> getThresholds(List<T> annotatorResult) {
		SortedSet<Double> candidates = new TreeSet<Double>();
		NodeIterator nodeIter = annotatorResult.get(0).listObjectsOfProperty(DEFAULT_TRUTH_VALUE_PROP);
		nodeIter.forEachRemaining(k -> candidates.add(k.asLiteral().getDouble()));
		return candidates;
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
	 * Should be used after evaluate call, since the map is filled there
	 * @return
	 */
	public Map<Double, double[]> getThresholdResultsMap() {
		return thresholdResultsMap;
	}
}

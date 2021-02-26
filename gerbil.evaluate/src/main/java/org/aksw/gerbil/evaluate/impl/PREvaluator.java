package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.StringEvaluationResult;
import org.apache.jena.rdf.model.Model;

/**
 * Implements the precision-recall curve
 * 
 * @param <T>
 */
public class PREvaluator<T extends Model> extends AUCEvaluator<Model> {

	private static final String NAME = "PR Curve";

	public PREvaluator() {
		super();
	}

	public PREvaluator(String truthValueURI) {
		super(truthValueURI);
	}

	@Override
	public EvaluationResult[] compareModel(Model annotator, Model gold) {
		Double auc = null;
		ConfidenceBasedEvaluator<Model> calc = new ConfidenceBasedEvaluator<Model>();

		// create annotator results
		List<List<Model>> annotatorResults = new ArrayList<List<Model>>();
		List<Model> singleAnnotator = new ArrayList<Model>();
		singleAnnotator.add(annotator);
		annotatorResults.add(singleAnnotator);

		// create gold results
		List<List<Model>> goldResults = new ArrayList<List<Model>>();
		List<Model> singleGold = new ArrayList<Model>();
		singleGold.add(gold);
		goldResults.add(singleGold);

		// compute p/r/f1 for all thresholds
		EvaluationResultContainer results = new EvaluationResultContainer();
		calc.evaluate(annotatorResults, goldResults, results);
		Map<Double, double[]> map = calc.getThresholdResultsMap();

		TreeSet<Double> keys = new TreeSet<Double>(map.keySet());

		Curve curve = new Curve(new Point(0, 1), new Point(1, 0));
		if (keys.isEmpty()) {
			return new EvaluationResult[] { new DoubleEvaluationResult(AUC_NAME, 0),
					new StringEvaluationResult(NAME, curve.toString()) };
		}
		curve.addPoint(curve.first);
		keys.descendingSet().forEach(key -> {
			double[] curResults = map.get(key);
			curve.addPoint(curResults[ConfidenceBasedEvaluator.RECALL], curResults[ConfidenceBasedEvaluator.PRECISION]);
		});
		curve.finishCurve();
		auc = curve.calculateAUC();

		return new EvaluationResult[] { new DoubleEvaluationResult(AUC_NAME, auc),
				new StringEvaluationResult(NAME, curve.toString()) };
	}
}

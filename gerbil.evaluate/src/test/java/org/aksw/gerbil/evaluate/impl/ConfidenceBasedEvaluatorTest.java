package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConfidenceBasedEvaluatorTest {
	private static final int PRECISION = 0;
	private static final int RECALL = 1;
	private static final int F1_SCORE = 2;
	private static final int THRESHOLD = 3;

	private static final double THRESHOLD_1 = -0.6;
	private static final double THRESHOLD_2 = 0.8;
	private static final double THRESHOLD_3 = 0.9;

	private List<List<Model>> annotator;
	private List<List<Model>> gold;
	private double[] expectedBest;
	private Map<Double, double[]> expectedValuesThresholds;
	private double[] expectedThresholds;

	public ConfidenceBasedEvaluatorTest(List<List<Model>> annotator, List<List<Model>> gold, double[] expectedBest,
			Map<Double, double[]> expectedValuesThresholds, double[] expectedThresholds) {
		this.annotator = annotator;
		this.gold = gold;
		this.expectedBest = expectedBest;
		this.expectedValuesThresholds = expectedValuesThresholds;
		this.expectedThresholds = expectedThresholds;
	}

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testCases = new ArrayList<Object[]>();
		testCases.add(getFullModel());
		testCases.add(getPartialModel());
		testCases.add(getEmptyModel());
		return testCases;
	}

	@Test
	public void test() {
		ConfidenceBasedEvaluator<Model> eval = new ConfidenceBasedEvaluator<Model>();
		EvaluationResultContainer results = new EvaluationResultContainer();
		eval.evaluate(annotator, gold, results);

		// confirm best measures found
		Assert.assertEquals(expectedBest[PRECISION], results.getResults().get(PRECISION).getValue());
		Assert.assertEquals(expectedBest[RECALL], results.getResults().get(RECALL).getValue());
		Assert.assertEquals(expectedBest[F1_SCORE], results.getResults().get(F1_SCORE).getValue());
		Assert.assertEquals(expectedBest[THRESHOLD], results.getResults().get(THRESHOLD).getValue());

		// confirm values for each threshold
		Map<Double, double[]> thresholdsValues = eval.getThresholdResultsMap();

		for (int i = 0; i < expectedThresholds.length; i++) {
			Assert.assertTrue(Arrays.equals(thresholdsValues.get(expectedThresholds[i]),
					expectedValuesThresholds.get(expectedThresholds[i])));
		}
	}

	/**
	 * Annotator model has all the statements from gold
	 * 
	 * @return
	 */
	private static Object[] getFullModel() {
		Model annotator = ModelFactory.createDefaultModel();
		Model gold = ModelFactory.createDefaultModel();

		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");

		Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

		annotator.addLiteral(s1, p, THRESHOLD_1);
		annotator.addLiteral(s2, p, THRESHOLD_2);
		annotator.addLiteral(s3, p, THRESHOLD_3);

		gold.addLiteral(s1, p, 0.0);
		gold.addLiteral(s2, p, 1.0);
		gold.addLiteral(s3, p, 1.0);

		List<List<Model>> annotatorResults = wrapInList(annotator);
		List<List<Model>> goldResults = wrapInList(gold);

		double[] expectedBest = new double[4];
		expectedBest[PRECISION] = 1.0;
		expectedBest[RECALL] = 1.0;
		expectedBest[F1_SCORE] = 1.0;
		expectedBest[THRESHOLD] = THRESHOLD_2;

		Map<Double, double[]> expectedValues = new HashMap<Double, double[]>();
		expectedValues.put(THRESHOLD_1, new double[] { (double) 2 / 3, 1, (double) 4 / 5 });
		expectedValues.put(THRESHOLD_2, new double[] { 1, 1, 1 });
		expectedValues.put(THRESHOLD_3, new double[] { 1, (double) 1 / 2, (double) 2 / 3 });

		return new Object[] { annotatorResults, goldResults, expectedBest, expectedValues,
				new double[] { THRESHOLD_1, THRESHOLD_2, THRESHOLD_3 } };
	}

	/**
	 * Annotator model has no statements
	 * 
	 * @return
	 */
	private static Object[] getEmptyModel() {
		Model annotator = ModelFactory.createDefaultModel();
		Model gold = ModelFactory.createDefaultModel();

		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");

		Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

		gold.addLiteral(s1, p, 0.0);
		gold.addLiteral(s2, p, 1.0);
		gold.addLiteral(s3, p, 1.0);

		List<List<Model>> annotatorResults = wrapInList(annotator);
		List<List<Model>> goldResults = wrapInList(gold);

		double[] expectedBest = new double[4];
		expectedBest[PRECISION] = 0.0;
		expectedBest[RECALL] = 0.0;
		expectedBest[F1_SCORE] = 0.0;
		expectedBest[THRESHOLD] = 0.0;

		Map<Double, double[]> expectedValues = new HashMap<Double, double[]>();

		return new Object[] { annotatorResults, goldResults, expectedBest, expectedValues, new double[0] };
	}

	/**
	 * Annotator model has some statements from gold
	 * 
	 * @return
	 */
	private static Object[] getPartialModel() {
		Model annotator = ModelFactory.createDefaultModel();
		Model gold = ModelFactory.createDefaultModel();

		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");

		Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

		annotator.addLiteral(s1, p, THRESHOLD_1);
		annotator.addLiteral(s3, p, THRESHOLD_3);

		gold.addLiteral(s1, p, 0.0);
		gold.addLiteral(s2, p, 1.0);
		gold.addLiteral(s3, p, 1.0);

		List<List<Model>> annotatorResults = wrapInList(annotator);
		List<List<Model>> goldResults = wrapInList(gold);

		double[] expectedBest = new double[4];
		expectedBest[PRECISION] = 1.0;
		expectedBest[RECALL] = 0.5;
		expectedBest[F1_SCORE] = (double) 2 / 3;
		expectedBest[THRESHOLD] = THRESHOLD_3;

		Map<Double, double[]> expectedValues = new HashMap<Double, double[]>();
		expectedValues.put(THRESHOLD_1, new double[] { (double) 1 / 2, (double) 1 / 2, (double) 1 / 2 });
		expectedValues.put(THRESHOLD_3, new double[] { 1, (double) 1 / 2, (double) 2 / 3 });

		return new Object[] { annotatorResults, goldResults, expectedBest, expectedValues,
				new double[] { THRESHOLD_1, THRESHOLD_3 } };
	}

	private static List<List<Model>> wrapInList(Model model) {
		List<List<Model>> result = new ArrayList<List<Model>>();
		List<Model> single = new ArrayList<Model>();
		single.add(model);
		result.add(single);
		return result;
	}

}

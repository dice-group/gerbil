package org.aksw.gerbil.evaluate.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

public class ConfidenceBasedModelEvaluatorTest {

	private static final String[] PREDICATES = new String[] { "http://ont.thomsonreuters.com/mdaas/isDomiciledIn1",
			"http://ont.thomsonreuters.com/mdaas/isDomiciledIn2" };

	@Test
	public void compareModel() {
		EvaluationResultContainer results = new EvaluationResultContainer();
		Evaluator<Model> evaluator = new ConfidenceBasedModelComparator(PREDICATES, ModelComparator.F1_SCORE_NAME, false,
				new DoubleResultComparator(), "http://confidence.com");

		Model anno = ModelFactory.createDefaultModel();

		Model gold = ModelFactory.createDefaultModel();

		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");

		Property p = ResourceFactory.createProperty("http://confidence.com");
		Property p1 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn1");
		Property p2 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn2");

		gold.addLiteral(s1, p1, 0.0);
		anno.addLiteral(s1, p1, 0.0);
		anno.addLiteral(s1, p, 1.0);

		List<List<Model>> annoList = new ArrayList<List<Model>>();
		List<Model> annoSingleList = new ArrayList<Model>();
		annoSingleList.add(anno);
		annoList.add(annoSingleList);
		List<List<Model>> goldList = new ArrayList<List<Model>>();
		List<Model> goldSingleList = new ArrayList<Model>();
		goldList.add(goldSingleList);
		goldSingleList.add(gold);

		//////// FIRST all true check

		evaluator.evaluate(annoList, goldList, results);
		checkResults(results, new double[] { 1, 1, 1, 0 });

		//////// SECOND ALL FALSE check
		gold.removeAll();
		anno.removeAll();
		gold.addLiteral(s1, p1, 0.0);
		anno.addLiteral(s1, p1, 1.0);
		anno.addLiteral(s1, p, 0.8);
		results = new EvaluationResultContainer();
		evaluator.evaluate(annoList, goldList, results);
		checkResults(results, new double[] { 0, 0, 0, 0 });

		//////// THIRD 0.5 CONFIDENCE = 0.8 check
		gold.removeAll();
		anno.removeAll();
		gold.addLiteral(s1, p1, 0.0);
		gold.addLiteral(s2, p2, 0.0);
		anno.addLiteral(s1, p1, 0.0);
		anno.addLiteral(s2, p2, 1.0);
		anno.addLiteral(s1, p, 0.8);
		anno.addLiteral(s2, p, 0.7);
		results = new EvaluationResultContainer();
		evaluator.evaluate(annoList, goldList, results);
		 checkResults(results, new double[] { 2.0/3, 1, 0.5, 0.8 });

		//////// FOURTH all TRUE
		gold.removeAll();
		anno.removeAll();
		gold.addLiteral(s1, p1, 0.0);
		anno.addLiteral(s1, p1, 0.0);
		results = new EvaluationResultContainer();
		evaluator.evaluate(annoList, goldList, results);
		checkResults(results, new double[] { 1, 1, 1, 0 });

		//////// FITHT all FALSE
		gold.removeAll();
		anno.removeAll();
		gold.addLiteral(s1, p1, 0.0);
		anno.addLiteral(s1, p1, 1.0);
		results = new EvaluationResultContainer();
		evaluator.evaluate(annoList, goldList, results);
		checkResults(results, new double[] { 0, 0, 0, 0 });

		//////// THIRD 0.5 CONFIDENCE = 0.8 check
		gold.removeAll();
		anno.removeAll();
		gold.addLiteral(s1, p1, 0.0);
		gold.addLiteral(s2, p2, 0.0);
		anno.addLiteral(s1, p1, 0.0);
		anno.addLiteral(s2, p2, 1.0);
		results = new EvaluationResultContainer();
		evaluator.evaluate(annoList, goldList, results);
		checkResults(results, new double[] { 0.5, 0.5, 0.5, 0});

	}

	private void checkResults(EvaluationResultContainer results, double[] expected) {
		for (EvaluationResult result : results.getResults()) {
			@SuppressWarnings("unchecked")
			List<EvaluationResult> res = (List<EvaluationResult>) results.getValue();
			for (EvaluationResult doubleRes : res) {
				switch (doubleRes.getName()) {
				case ModelComparator.F1_SCORE_NAME:
					assertEquals(expected[0], doubleRes.getValue());
					break;
				case ModelComparator.PRECISION_NAME:
					assertEquals(expected[1], doubleRes.getValue());
					break;
				case ModelComparator.RECALL_NAME:
					assertEquals(expected[2], doubleRes.getValue());
					break;
				case ConfidenceScoreEvaluatorDecorator.CONFIDENCE_SCORE_THRESHOLD_RESULT_NAME:
					assertEquals(expected[3], doubleRes.getValue());
					break;
				default:
					System.out.println(result.getName() + " " + result.getValue());

				}

			}

		}
	}
}

package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aksw.gerbil.evaluate.EvaluationResult;
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
public class PREvaluatorTest {

	private Model annotator;
	private Model gold;
	private double expectedAUC;

	public PREvaluatorTest(Model annotator, Model gold, double expectedAUC) {
		this.annotator = annotator;
		this.gold = gold;
		this.expectedAUC = expectedAUC;
	}

	@Test
	public void test() {
		AUCEvaluator<Model> eval = new PREvaluator<Model>();
		EvaluationResult[] results = eval.compareModel(annotator, gold);

		Assert.assertEquals(expectedAUC, results[0].getValue());
	}

	@Parameters
	public static Collection<Object[]> data() {
		List<Object[]> testCases = new ArrayList<Object[]>();
		testCases.add(getFullModel());
		testCases.add(getPartialModel());
		testCases.add(getEmptyModel());
		return testCases;
	}

	private static Object[] getFullModel() {
		Model annotator = ModelFactory.createDefaultModel();
		Model gold = ModelFactory.createDefaultModel();

		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");

		Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

		annotator.addLiteral(s1, p, -0.6);
		annotator.addLiteral(s2, p, 0.8);
		annotator.addLiteral(s3, p, 0.9);

		gold.addLiteral(s1, p, 0.0);
		gold.addLiteral(s2, p, 1.0);
		gold.addLiteral(s3, p, 1.0);

		double expectedAUC = 1;

		return new Object[] { annotator, gold, expectedAUC };
	}

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

		double expectedAUC = 0.0;

		return new Object[] { annotator, gold, expectedAUC };
	}

	private static Object[] getPartialModel() {
		Model annotator = ModelFactory.createDefaultModel();
		Model gold = ModelFactory.createDefaultModel();

		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");

		Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

		annotator.addLiteral(s1, p, -0.6);
		annotator.addLiteral(s3, p, 0.9);

		gold.addLiteral(s1, p, 0.0);
		gold.addLiteral(s2, p, 1.0);
		gold.addLiteral(s3, p, 1.0);

		double expectedAUC = 0.625;

		return new Object[] { annotator, gold, expectedAUC };
	}

}

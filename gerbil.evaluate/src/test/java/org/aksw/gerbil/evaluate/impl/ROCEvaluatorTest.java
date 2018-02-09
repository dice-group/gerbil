package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.gerbil.evaluate.EvaluationResult;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.junit.Assert;
import org.junit.Test;

public class ROCEvaluatorTest {

	public static final double DELTA = 0.00001;

	@Test
	public void testNAN() {
		Model anno = ModelFactory.createDefaultModel();

		Model gold = ModelFactory.createDefaultModel();

		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");
		Resource s4 = ResourceFactory.createResource("http://test.com/stmt4");

		Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

		gold.addLiteral(s4, p, 1.0);
		gold.addLiteral(s3, p, 1.0);
		gold.addLiteral(s2, p, 0.0);
		gold.addLiteral(s1, p, 0.0);

		anno.addLiteral(s4, p, 1.0);
		anno.addLiteral(s2, p, 0.0);
		anno.addLiteral(s1, p, Double.NaN);
		anno.addLiteral(s3, p, Double.POSITIVE_INFINITY);

		ROCEvaluator<Model> eval = new ROCEvaluator<Model>();
		eval.setTruthValueURI(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);
		EvaluationResult result = eval.compareModel(anno, gold)[0];
		Assert.assertEquals(0.5, (double) result.getValue(), DELTA);
	}

	@Test
	public void testComparison() {
		Model anno = ModelFactory.createDefaultModel();

		Model gold = ModelFactory.createDefaultModel();

		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");
		Resource s4 = ResourceFactory.createResource("http://test.com/stmt4");

		Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

		gold.addLiteral(s4, p, 1.0);
		gold.addLiteral(s3, p, 1.0);
		gold.addLiteral(s2, p, 0.0);
		gold.addLiteral(s1, p, 0.0);

		anno.addLiteral(s4, p, 1.0);
		anno.addLiteral(s2, p, 0.8);
		anno.addLiteral(s1, p, 0.6);
		anno.addLiteral(s3, p, 0.0);

		ROCEvaluator<Model> eval = new ROCEvaluator<Model>();
		eval.setTruthValueURI(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);
		EvaluationResult result = eval.compareModel(anno, gold)[0];
		Assert.assertEquals(0.5, (double) result.getValue(), DELTA);
	}
	
    @Test
    public void testEqualRank() {
        Model anno = ModelFactory.createDefaultModel();

        Model gold = ModelFactory.createDefaultModel();

        Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
        Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
        Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");
        Resource s4 = ResourceFactory.createResource("http://test.com/stmt4");

        Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

        gold.addLiteral(s4, p, 1.0);
        gold.addLiteral(s3, p, 1.0);
        gold.addLiteral(s2, p, 0.0);
        gold.addLiteral(s1, p, 0.0);

        anno.addLiteral(s4, p, 1.0);
        anno.addLiteral(s3, p, 1.0);
        anno.addLiteral(s2, p, 1.0);
        anno.addLiteral(s1, p, 1.0);

        ROCEvaluator<Model> eval = new ROCEvaluator<Model>();
        eval.setTruthValueURI(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);
        EvaluationResult result = eval.compareModel(anno, gold)[0];
        Assert.assertEquals(0.5, (double) result.getValue(), DELTA);
    }
    
    @Test
    public void testEqualRank2() {
        Model anno = ModelFactory.createDefaultModel();

        Model gold = ModelFactory.createDefaultModel();

        Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
        Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
        Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");
        Resource s4 = ResourceFactory.createResource("http://test.com/stmt4");

        Property p = ResourceFactory.createProperty(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);

        gold.addLiteral(s4, p, 1.0);
        gold.addLiteral(s3, p, 1.0);
        gold.addLiteral(s2, p, 0.0);
        gold.addLiteral(s1, p, 0.0);

        anno.addLiteral(s4, p, 1.0);
        anno.addLiteral(s3, p, 0.5);
        anno.addLiteral(s2, p, 0.5);
        anno.addLiteral(s1, p, 0.0);

        ROCEvaluator<Model> eval = new ROCEvaluator<Model>();
        eval.setTruthValueURI(ROCEvaluator.DEFAULT_TRUTH_VALUE_URI);
        EvaluationResult result = eval.compareModel(anno, gold)[0];
        Assert.assertEquals(7.0/8.0, (double) result.getValue(), DELTA);
    }

	@Test
	public void curveTest() {
		ROCCurve curve = new ROCCurve(2, 2);
		curve.addUp();
		curve.addUp();
		curve.addRight();
		curve.addRight();
		curve.finishCurve();
		Assert.assertEquals(2, curve.points.size());
		Point a = curve.points.get(0);
		Point b = curve.points.get(1);
		Assert.assertEquals(0.0, a.x, DELTA);
		Assert.assertEquals(1.0, a.y, DELTA);
		Assert.assertEquals(1.0, b.x, DELTA);
		Assert.assertEquals(1.0, b.y, DELTA);

		double auc = curve.calculateAUC();
		Assert.assertEquals(1.0, auc, DELTA);

		curve = new ROCCurve(2, 2);
		curve.addRight();
		curve.addRight();
		curve.addUp();
		curve.addUp();
		curve.finishCurve();

		Assert.assertEquals(2, curve.points.size());
		a = curve.points.get(0);
		b = curve.points.get(1);
		Assert.assertEquals(1.0, a.x, DELTA);
		Assert.assertEquals(0.0, a.y, DELTA);
		Assert.assertEquals(1.0, b.x, DELTA);
		Assert.assertEquals(1.0, b.y, DELTA);

		auc = curve.calculateAUC();
		Assert.assertEquals(0.0, auc, DELTA);

		curve = new ROCCurve(2, 2);
		curve.addRight();
		curve.addUp();
		curve.addRight();
		curve.addUp();
		curve.finishCurve();

		Assert.assertEquals(4, curve.points.size());
		a = curve.points.get(0);
		b = curve.points.get(1);
		Point c = curve.points.get(2);
		Point d = curve.points.get(3);
		Assert.assertEquals(0.5, a.x, DELTA);
		Assert.assertEquals(0.0, a.y, DELTA);
		Assert.assertEquals(0.5, b.x, DELTA);
		Assert.assertEquals(0.5, b.y, DELTA);
		Assert.assertEquals(1.0, c.x, DELTA);
		Assert.assertEquals(0.5, c.y, DELTA);
		Assert.assertEquals(1.0, d.x, DELTA);
		Assert.assertEquals(1.0, d.y, DELTA);

		auc = curve.calculateAUC();
		Assert.assertEquals(0.25, auc, DELTA);

		curve = new ROCCurve(2, 2);
		curve.addUp();
		curve.addRight();
		curve.addUp();
		curve.addRight();
		curve.finishCurve();

		Assert.assertEquals(4, curve.points.size());
		a = curve.points.get(0);
		b = curve.points.get(1);
		c = curve.points.get(2);
		d = curve.points.get(3);
		Assert.assertEquals(0.0, a.x, DELTA);
		Assert.assertEquals(0.5, a.y, DELTA);
		Assert.assertEquals(0.5, b.x, DELTA);
		Assert.assertEquals(0.5, b.y, DELTA);
		Assert.assertEquals(0.5, c.x, DELTA);
		Assert.assertEquals(1.0, c.y, DELTA);
		Assert.assertEquals(1.0, d.x, DELTA);
		Assert.assertEquals(1.0, d.y, DELTA);

		auc = curve.calculateAUC();
		Assert.assertEquals(0.75, auc, DELTA);

		// Test curve with missing values
		curve = new ROCCurve(2, 2);
		curve.addUp();
		curve.addRight();
		curve.finishCurve();

		Assert.assertEquals(3, curve.points.size());
		a = curve.points.get(0);
		b = curve.points.get(1);
		c = curve.points.get(2);
		Assert.assertEquals(0.0, a.x, DELTA);
		Assert.assertEquals(0.5, a.y, DELTA);
		Assert.assertEquals(1.0, b.x, DELTA);
		Assert.assertEquals(0.5, b.y, DELTA);
		Assert.assertEquals(1.0, c.x, DELTA);
		Assert.assertEquals(1.0, c.y, DELTA);

		auc = curve.calculateAUC();
		Assert.assertEquals(0.5, auc, DELTA);

	}

	@Test
	public void sort() {
		Model anno = ModelFactory.createDefaultModel();
		Resource s = ResourceFactory.createResource("http://test.com/stmt1");
		Property p = ResourceFactory.createProperty("http://test.com/truthValue");

		anno.addLiteral(s, p, 1.0);

		s = ResourceFactory.createResource("http://test.com/stmt2");
		anno.addLiteral(s, p, 0.3);

		s = ResourceFactory.createResource("http://test.com/stmt3");
		anno.addLiteral(s, p, 0.8);

		s = ResourceFactory.createResource("http://test.com/stmt4");
		anno.addLiteral(s, p, 0.0);

		s = ResourceFactory.createResource("http://test.com/stmt5");
		anno.addLiteral(s, p, 0.9);

		List<Statement> stmts = anno.listStatements().toList();
		Collections.sort(stmts, new StatementComparator());
		List<String> orderedStmt = new ArrayList<String>();
		for (int i = 0; i < stmts.size() - 1; i++) {
			double higher = stmts.get(i).getDouble();
			double lower = stmts.get(i + 1).getDouble();
			Assert.assertTrue(higher >= lower);
			orderedStmt.add(stmts.get(i).getSubject().getURI());
		}
		orderedStmt.add(stmts.get(stmts.size() - 1).getSubject().getURI());
		List<String> correct = new ArrayList<String>();
		correct.add("http://test.com/stmt1");
		correct.add("http://test.com/stmt5");
		correct.add("http://test.com/stmt3");
		correct.add("http://test.com/stmt2");
		correct.add("http://test.com/stmt4");
		for (int i = 0; i < correct.size(); i++) {
			Assert.assertEquals(correct.get(i), orderedStmt.get(i));
		}
	}
}

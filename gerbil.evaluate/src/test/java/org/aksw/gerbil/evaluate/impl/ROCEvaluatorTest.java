package org.aksw.gerbil.evaluate.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import org.junit.Test;

public class ROCEvaluatorTest {

	
	@Test
	public void testComparison(){
		Model anno = ModelFactory.createDefaultModel();
		
		Model gold = ModelFactory.createDefaultModel();
		
		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		Resource s3 = ResourceFactory.createResource("http://test.com/stmt3");
		Resource s4 = ResourceFactory.createResource("http://test.com/stmt4");

		Property p = ResourceFactory.createProperty("http://gerbil-swc.org/truthValue");
		
		gold.addLiteral(s4, p, 1.0);
		gold.addLiteral(s3, p, 1.0);
		gold.addLiteral(s2, p, 0.0);
		gold.addLiteral(s1, p, 0.0);
		
		anno.addLiteral(s4, p, 1.0);
		anno.addLiteral(s2, p, 0.8);
		anno.addLiteral(s1, p, 0.6);
		anno.addLiteral(s3, p, 0.0);

		
		ROCEvaluator<Model> eval = new ROCEvaluator<Model>();
		EvaluationResult result = eval.compareModel(anno, gold)[0];
		assertTrue((double)result.getValue()==0.5);
		
	}
	
	
	@Test
	public void curveTest(){
		ROCCurve curve = new ROCCurve(2, 2);
		curve.addUp();
		curve.addUp();
		curve.addRight();
		curve.addRight();
		assertTrue(curve.points.size()==2);
		Point a = curve.points.get(0);
		Point b = curve.points.get(1);
		assertTrue(a.x==0.0);
		assertTrue(a.y==1.0);
		assertTrue(b.x==1.0);
		assertTrue(b.y==1.0);
	
		double auc=curve.calcualteAUC();
		assertTrue(auc==1.0);
		
		
		curve = new ROCCurve(2, 2);
		curve.addRight();
		curve.addRight();
		curve.addUp();
		curve.addUp();
		
		assertTrue(curve.points.size()==2);
		a = curve.points.get(0);
		b = curve.points.get(1);
		assertTrue(a.x==1.0);
		assertTrue(a.y==0.0);
		assertTrue(b.x==1.0);
		assertTrue(b.y==1.0);
	
		auc=curve.calcualteAUC();
		assertTrue(auc==0.0);
		
		curve = new ROCCurve(2, 2);
		curve.addRight();
		curve.addUp();
		curve.addRight();
		curve.addUp();
		
		assertTrue(curve.points.size()==4);
		a = curve.points.get(0);
		b = curve.points.get(1);
		Point c = curve.points.get(2);
		Point d = curve.points.get(3);
		assertTrue(a.x==0.5);
		assertTrue(a.y==0.0);
		assertTrue(b.x==0.5);
		assertTrue(b.y==0.5);
		assertTrue(c.x==1.0);
		assertTrue(c.y==0.5);
		assertTrue(d.x==1.0);
		assertTrue(d.y==1.0);
	
		auc=curve.calcualteAUC();
		//TODO!
		assertTrue(auc==0.25);
		
		curve = new ROCCurve(2, 2);
		curve.addUp();
		curve.addRight();
		curve.addUp();
		curve.addRight();
		
		assertTrue(curve.points.size()==4);
		a = curve.points.get(0);
		b = curve.points.get(1);
		c = curve.points.get(2);
		d = curve.points.get(3);
		assertTrue(a.x==0.0);
		assertTrue(a.y==0.5);
		assertTrue(b.x==0.5);
		assertTrue(b.y==0.5);
		assertTrue(c.x==0.5);
		assertTrue(c.y==1.0);
		assertTrue(d.x==1.0);
		assertTrue(d.y==1.0);
	
		auc=curve.calcualteAUC();
		//TODO!
		assertTrue(auc==0.75);
		
	}
	
	@Test
	public void sort(){
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
		for(int i=0;i<stmts.size()-1;i++){
			double higher = stmts.get(i).getDouble();
			double lower = stmts.get(i+1).getDouble();
			assertTrue(higher>=lower);
			orderedStmt.add(stmts.get(i).getSubject().getURI());
		}
		orderedStmt.add(stmts.get(stmts.size()-1).getSubject().getURI());
		List<String> correct = new ArrayList<String>();
		correct.add("http://test.com/stmt1");
		correct.add("http://test.com/stmt5");
		correct.add("http://test.com/stmt3");
		correct.add("http://test.com/stmt2");
		correct.add("http://test.com/stmt4");
		for(int i=0;i<correct.size();i++){
			assertEquals(correct.get(i), orderedStmt.get(i));
		}
	}
}

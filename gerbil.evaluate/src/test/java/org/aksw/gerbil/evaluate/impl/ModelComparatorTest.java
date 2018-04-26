package org.aksw.gerbil.evaluate.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluatorFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

public class ModelComparatorTest {

    private static final String[] PREDICATES = new String[] {"http://ont.thomsonreuters.com/mdaas/isDomiciledIn"};
	
//	@Test
	public void isomporphTest() {
		Model expected = ModelFactory.createDefaultModel();
		Model test = ModelFactory.createDefaultModel(); 

		Resource s = ResourceFactory.createResource("http://test.com/stmt1");
		Property p = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn");
		
		Literal l1 = ResourceFactory.createLangLiteral("United Kingdom", "en");
		Literal l2 = ResourceFactory.createStringLiteral("United Kingdom");
		
		expected.addLiteral(s, p, l1);
		test.addLiteral(s, p, l2);
		
		assertTrue(expected.isIsomorphicWith(test));
		
		expected.removeAll();
		test.removeAll();
		
		expected.addLiteral(s, p, l2);
		test.addLiteral(s, p, l1);
	}
	
	@Test
	public void cleansifyTest() {
		Model anno = ModelFactory.createDefaultModel();
		Model gold = ModelFactory.createDefaultModel();
		Property p1 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn");
		Property p2 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIns");
		
		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");
		
		gold.add(s1, p1, "YES");
		anno.add(s1, p1, "NO");
		anno.add(s1, p2, "NO");
		anno.add(s2, p1, "NO");
		
		Model cleaned = ModelComparator.cleansify(anno, gold);	
		
//		assertTrue(anno.size()==1);
		assertTrue("NO".equals(cleaned.listStatements().next().getObject().asLiteral().getString()));
		
	}
	
	@Test
	public void checkReduce(){
		Model anno = ModelFactory.createDefaultModel();
		Model expected = ModelFactory.createDefaultModel();
		Property p1 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn");
		Property p2 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn2");
		
		Resource s = ResourceFactory.createResource("http://test.com/stmt1");

		
		anno.addLiteral(s, p1, 0.0);
		expected.addLiteral(s, p1, 0.0);
		anno.addLiteral(s, p2, 0.0);
		
		Model test = ModelComparator.reduceModel(anno, PREDICATES);
		assertTrue(expected.isIsomorphicWith(test));
		
		ModelComparator<Model> evaluator = new ModelComparator<Model>(PREDICATES, false);
		EvaluationResult[] result = evaluator.compareModel(anno, expected);
		assertEquals(result[0].getValue(), 1.0);
		assertEquals(result[1].getValue(), 1.0);
		assertEquals(result[2].getValue(), 1.0);


	}
	
	@Test
	public void compareModel(){
		Model anno = ModelFactory.createDefaultModel();
		
		Model gold = ModelFactory.createDefaultModel();
		
		Resource s1 = ResourceFactory.createResource("http://test.com/stmt1");
		Resource s2 = ResourceFactory.createResource("http://test.com/stmt2");


		Property p = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn");

		Property p2 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn");

		
		gold.addLiteral(s2, p, 0.0);
		gold.addLiteral(s1, p, 0.0);
		
		anno.addLiteral(s2, p, 0.0);
		anno.addLiteral(s1, p, 0.0);
		
		ModelComparator<Model> evaluator = new ModelComparator<Model>(PREDICATES, false);
		EvaluationResult[] result = evaluator.compareModel(anno, gold);
		assertEquals(result[0].getValue(), 1.0);
		assertEquals(result[1].getValue(), 1.0);
		assertEquals(result[2].getValue(), 1.0);
		
		anno.removeAll();
		
		anno.addLiteral(s2, p2, 1.0);
		anno.addLiteral(s1, p2, 1.0);
		
		result = evaluator.compareModel(anno, gold);
		assertEquals(result[0].getValue(), 0.0);
		assertEquals(result[1].getValue(), 0.0);
		assertEquals(result[2].getValue(), 0.0);
		
		anno.addLiteral(s2, p, 0.0);
		anno.addLiteral(s1, p, 0.0);
		
		result = evaluator.compareModel(anno, gold);
		for(EvaluationResult res : result){
			switch(res.getName()){
			case ModelComparator.RECALL_NAME:
				assertEquals(res.getValue(), 1.0);
				break;
			case ModelComparator.PRECISION_NAME:
				assertEquals(res.getValue(), 0.5);
				break;
			case ModelComparator.F1_SCORE_NAME:
				assertEquals(res.getValue(), 2*0.5/1.5);
				break;
			}
		}

		anno.removeAll();
		anno.addLiteral(s2, p, 0.0);
		anno.addLiteral(s1, p, 0.0);
		
		gold.addLiteral(s2, p2, 1.0);
		gold.addLiteral(s1, p2, 1.0);
		
		result = evaluator.compareModel(anno, gold);
		for(EvaluationResult res : result){
			switch(res.getName()){
			case ModelComparator.RECALL_NAME:
				assertEquals(res.getValue(), 0.5);
				break;
			case ModelComparator.PRECISION_NAME:
				assertEquals(res.getValue(), 1.0);
				break;
			case ModelComparator.F1_SCORE_NAME:
				assertEquals(res.getValue(), 2*0.5/1.5);
				break;
			}
		}
		
		anno.removeAll();
		gold.removeAll();
		result = evaluator.compareModel(anno, gold);
		for(EvaluationResult res : result){
			switch(res.getName()){
			case ModelComparator.RECALL_NAME:
				assertEquals(res.getValue(), 1.0);
				break;
			case ModelComparator.PRECISION_NAME:
				assertEquals(res.getValue(), 1.0);
				break;
			case ModelComparator.F1_SCORE_NAME:
				assertEquals(res.getValue(), 1.0);
				break;
			}
		}
		
		gold.addLiteral(s1, p, 1.0);
		result = evaluator.compareModel(anno, gold);
		for(EvaluationResult res : result){
			switch(res.getName()){
			case ModelComparator.RECALL_NAME:
				assertEquals(res.getValue(), 0.0);
				break;
			case ModelComparator.PRECISION_NAME:
				assertEquals(res.getValue(), 1.0);
				break;
			case ModelComparator.F1_SCORE_NAME:
				assertEquals(res.getValue(), 0.0);
				break;
			}
		}
		
		
	}
}

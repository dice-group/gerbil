package org.aksw.gerbil.evaluate.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

public class ModelComparatorTest {

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
		ModelComparator cmp = new ModelComparator();
		Model anno = ModelFactory.createDefaultModel();
		Model expected = ModelFactory.createDefaultModel();
		Property p1 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn");
		Property p2 = ResourceFactory.createProperty("http://ont.thomsonreuters.com/mdaas/isDomiciledIn");
		
		Resource s = ResourceFactory.createResource("http://test.com/stmt1");

		
		anno.addLiteral(s, p1, 0.0);
		expected.addLiteral(s, p1, 0.0);
		anno.addLiteral(s, p2, 0.0);
		
		Model test = ModelComparator.reduceModel(anno);
		assertTrue(expected.isIsomorphicWith(test));
		
		ModelComparator<Model> evaluator = new ModelComparator<Model>();
		EvaluationResult[] result = evaluator.compareModel(anno, expected);
		assertEquals(result[0].getValue(), 1.0);
		assertEquals(result[1].getValue(), 1.0);
		assertEquals(result[2].getValue(), 1.0);


	}
	
	@Test
	public void compareModel(){
		ModelComparator cmp = new ModelComparator();

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
		
		ModelComparator<Model> evaluator = new ModelComparator<Model>();
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

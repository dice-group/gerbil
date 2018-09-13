package org.aksw.gerbil.evaluate.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.aksw.agdistis.util.ConfidenceBasedStatementComparator;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class  ConfidenceBasedModelComparator<T extends Model> extends ModelComparator<T> {

	public static final String CONFIDENCE_SCORE_THRESHOLD_RESULT_NAME = "confidence threshold";
	private Comparator<EvaluationResult> resultComparator;
	private String resultName;
	private Property confidenceProperty;

	
	public ConfidenceBasedModelComparator(String[] predicates, String resultName, boolean punishAdditionalAnnotatorStmts,
			Comparator<EvaluationResult> resultComparator, String confidenceURI) {
		super(predicates, punishAdditionalAnnotatorStmts);
		this.resultName=resultName;
		this.confidenceProperty = ResourceFactory.createProperty(confidenceURI);
		this.resultComparator=resultComparator;
		
	}
	
	
	public static Model cleansify(Model annotator, Model gold, Property confidence) {
		StmtIterator it = gold.listStatements();
		Model cleaned = ModelFactory.createDefaultModel();
		while(it.hasNext()) {
			Statement cur = it.next();
			cleaned.add(annotator.listStatements(cur.getSubject(), cur.getPredicate(), (RDFNode) null));
		}
		cleaned.add(annotator.listStatements(null , confidence, (RDFNode)null));
		return cleaned;
	}

	@Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
		
			Model system = reduceModel(annotatorResults.get(0).get(0), confidenceProperty);
        	Model gs = reduceModel(goldStandard.get(0).get(0), confidenceProperty);
        	if(!punishAdditionalAnnotatorStmts) {
        		system = cleansify(system, gs, confidenceProperty);
    		}
			
			List<ConfidenceBasedStatement> stmts = getStatements(system);
        	//sort system answers with confidence score from N -> 0
        	Collections.sort(stmts, new ConfidenceBasedStatementComparator());
        	Collections.reverse(stmts);
        	
        	//for each bulk, 
        	double bestConfidence=0;
        	EvaluationResultContainer bestResult=null;
        	long[] counts = new long[] {0,0,0};
        	Double confidence = null;
        	for(ConfidenceBasedStatement cbs : stmts) {
        		if(confidence !=null &&cbs.getConfidence()!=confidence) {    		
        			//for each gs not in it add fn 
        			counts[2]=gs.size()-counts[0];
        			//calculate
            		EvaluationResultContainer currentResult = measure(counts[0], counts[1], counts[2]);
        			//add to list and get best score
        			if(bestResult==null || isBetter(currentResult, bestResult) ) {
        				bestResult = currentResult;
        				bestConfidence=confidence;
        			}
            		confidence = cbs.getConfidence();

        		}
        		if(confidence==null) {
            		confidence = cbs.getConfidence();
        		}
        		//use previous tp, fp and add new ones
        		for(Statement st : cbs.getStatements()) {
        			if(st.getPredicate().equals(confidenceProperty)) {
        				//ignore any confidence properties
        				continue;
        			}
        			if(gs.contains(st)) {
        				counts[0]++;//tp
        			}
        			else {
        				counts[1]++;//fp
        			}
        		}

        	}
        	//last one 
			counts[2]=gs.size()-counts[0];

    		EvaluationResultContainer currentResult = measure(counts[0], counts[1], counts[2]);
			//add to list and get best score
			if(bestResult==null || isBetter(currentResult, bestResult) ) {
				bestResult = currentResult;
				bestConfidence=0;
			}
			for(EvaluationResult res : bestResult.getResults()) {
				results.addResult(res);
			}
			results.addResult(new DoubleEvaluationResult(CONFIDENCE_SCORE_THRESHOLD_RESULT_NAME, bestConfidence));
    }
	
	protected boolean isBetter(EvaluationResultContainer currentResult,
			EvaluationResultContainer bestResult) {
		if (currentResult == null) {
			return false;
		} else if (bestResult == null) {
			return true;
		}
		EvaluationResult currentImpResult = findImportantResult(currentResult);
		if (currentImpResult == null) {
			return false;
		}
		EvaluationResult bestImpResult = findImportantResult(bestResult);
		if (bestImpResult == null) {
			return true;
		}
		if (resultComparator.compare(currentImpResult, bestImpResult) > 0) {
			return true;
		} else {
			return false;
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

	private EvaluationResultContainer measure(long tp, long fp, long fn) {
		double prec = 1.0;
		if(tp!=0.0 || fp != 0.0)
			prec = tp*1.0/(tp+fp);
		double rec = 1.0;
		if(tp!=0.0 || fn!=0.0)
			rec =tp*1.0/(tp+fn);
		double f1 = 0.0;
		if(prec!=0.0 && rec!=0.0)
			f1 = 2*(prec*rec)/(prec+rec);
		EvaluationResult[] results =  new EvaluationResult[] { new DoubleEvaluationResult(PRECISION_NAME, prec),
                new DoubleEvaluationResult(RECALL_NAME, rec),
                new DoubleEvaluationResult(F1_SCORE_NAME, f1) };
		EvaluationResultContainer cnt = new EvaluationResultContainer();
		cnt.addResults(results);
		return cnt;
	}

	private List<ConfidenceBasedStatement> getStatements(Model m){
		
		List<ConfidenceBasedStatement> ret = new LinkedList<ConfidenceBasedStatement>();
		ResIterator entityIterator = m.listSubjects();
		while(entityIterator.hasNext()) {
			Resource entity = entityIterator.next();
			Set<Statement> smts = m.listStatements(entity, null, (RDFNode)null).toSet();
			ret.add(new ConfidenceBasedStatement(smts, confidenceProperty));
		}
		return ret;
	}



	
}

package org.aksw.gerbil.evaluate.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AUCEvaluator<T extends Model> implements Evaluator<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AUCEvaluator.class);
	
	public static final String AUC_NAME = "Area Under Curve";

	public static final String TRUTH_VALUE_URI_GERBIL_KEY = "org.aksw.gerbil.evaluator.roc.truthProperty";

	public static String DEFAULT_TRUTH_VALUE_URI = "http://swc2017.aksw.org/hasTruthValue";

	protected String truthValueURI = "http://swc2017.aksw.org//hasTruthValue";

	public AUCEvaluator() {
		super();
	}

	public AUCEvaluator(String truthValueURI) {
		this.truthValueURI = truthValueURI;
	}

	@Override
	public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
			EvaluationResultContainer results) {
		results.addResults(compareModel(annotatorResults.get(0).get(0), goldStandard.get(0).get(0)));
	}

	public abstract EvaluationResult[] compareModel(Model annotator, Model gold);
	
	protected List<DoubleBooleanPair> buildDoubleBooleanPairs(Model annotator, Model gold) {
		 Property truthValueProp = annotator.getProperty(truthValueURI);
	     Property truthValueGold = gold.getProperty(truthValueURI);

	     //remove all non number values (NAN, INFINITY, etc)
	     cleanTruthValues(annotator, truthValueProp);
	     List<Statement> statements = annotator.listStatements(null, truthValueProp, (RDFNode) null).toList();
	     List<DoubleBooleanPair> evalStatements = new ArrayList<>(statements.size()); 
	     for (Statement stmt : statements) {
	         // Get the same triple from the gold standard
	         Resource checkStmt = stmt.getSubject();
	         double predictedValue = stmt.getObject().asLiteral().getDouble();
	         StmtIterator stIt = gold.listStatements(checkStmt, truthValueGold, (RDFNode) null);
	         // if such a triple exists
	         if (stIt.hasNext()) {
	             Double truthValue = stIt.next().getDouble();
	             // We don't want to check whether it is equal to one or zero, so let's check
	             // whether it is larger than 0.5 ;-)
	             evalStatements.add(new DoubleBooleanPair(predictedValue, (truthValue > 0.5)));
	         } else {
	             // ignore it
	             LOGGER.info("The system answer contained the following unknown statement: {}", stmt.toString());
	         }
	     }
	     return evalStatements;
	}

	/**
	 * Method to clean the model from not number values, such as Infinity and NAN
	 * 
	 * @param annotator
	 * @param truthValueProp
	 */
	protected void cleanTruthValues(Model annotator, Property truthValueProp) {
		StmtIterator statements = annotator.listStatements(null, truthValueProp, (RDFNode) null);
		List<Statement> removeStmts = new LinkedList<Statement>();
		while (statements.hasNext()) {
			Statement stmt = statements.next();
			try {
				Double value = stmt.getLiteral().getDouble();
				if (value.isInfinite() || value.isNaN()) {
					// value cannot be processed so remove the statment
					removeStmts.add(stmt);
				}
			} catch (NumberFormatException e) {
				// not double so remove
				removeStmts.add(stmt);
			}
		}
		annotator.remove(removeStmts);
	}

	public String getTruthValueURI() {
		return truthValueURI;
	}

	public void setTruthValueURI(String truthValueURI) {
		this.truthValueURI = truthValueURI;
	}

	protected static class DoubleBooleanPair implements Comparable<DoubleBooleanPair> {
		public double predictedValue;
		public boolean goldFlag;

		public DoubleBooleanPair() {
		}

		public DoubleBooleanPair(double predictedValue, boolean goldFlag) {
			this.predictedValue = predictedValue;
			this.goldFlag = goldFlag;
		}

		@Override
		public int compareTo(DoubleBooleanPair o) {
			int compareResult = Double.compare(predictedValue, o.predictedValue);
			return (compareResult == 0) ? 0 : -compareResult;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append('(');
			builder.append(predictedValue);
			builder.append(',');
			builder.append(goldFlag ? '1' : '0');
			builder.append(')');
			return builder.toString();
		}
	}

}

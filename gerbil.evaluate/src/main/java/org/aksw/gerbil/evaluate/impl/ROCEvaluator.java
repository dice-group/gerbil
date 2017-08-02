/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.evaluate.impl;

import java.util.Collections;
import java.util.List;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.StringEvaluationResult;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ROCEvaluator<T extends Model> implements Evaluator<T> {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ROCEvaluator.class);

    public static final String AUC_NAME = "Area Under Curve";
    public static final String ROC_NAME = "ROC Curve";
    
    public static final String TRUTH_VALUE_PROPERTY_URI = "http://swc2017.aksw.org//hasTruthValue";


    public ROCEvaluator() {
        super();
   	
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        results.addResults(compareModel(annotatorResults.get(0).get(0), goldStandard.get(0).get(0)) );
    }


	public EvaluationResult[] compareModel(Model annotator, Model gold) {
		Double auc = null;
//		String rocJson = "";
		
		Property truthValueProp = annotator.getProperty(TRUTH_VALUE_PROPERTY_URI);
		Property truthValueGold = gold.getProperty(TRUTH_VALUE_PROPERTY_URI);
		

		int trueStmts = gold.listLiteralStatements(null, truthValueGold, 1.0).toList().size();
		int falseStmts = gold.listLiteralStatements(null, truthValueGold, 0.0).toList().size();
				
		List<Statement> sortedStatements = annotator.listStatements(null, truthValueProp, (RDFNode)null).toList();


		Collections.sort(sortedStatements, new StatementComparator());
		ROCCurve curve = new ROCCurve(trueStmts, falseStmts);

		int count=0;
		for(Statement stmt : sortedStatements){
			Resource checkStmt = stmt.getSubject(); 
			StmtIterator stIt = gold.listStatements(checkStmt, truthValueGold, (RDFNode)null);
			Double truthValue = stIt.next().getDouble();
			if(truthValue==1){
				curve.addUp();
			}
			else{
				curve.addRight();
			}
			count++;
		}
		
		for(int i=0; i<count-(trueStmts+falseStmts);i++){
			curve.addRight();
		}
		auc = curve.calcualteAUC();
		
		return new EvaluationResult[] { new DoubleEvaluationResult(AUC_NAME, auc), 
				new StringEvaluationResult(ROC_NAME, curve.toString())};
	}

}

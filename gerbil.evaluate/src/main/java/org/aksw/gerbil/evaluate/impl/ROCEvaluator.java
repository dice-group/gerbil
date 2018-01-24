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
import java.util.LinkedList;
import java.util.List;

import org.aksw.gerbil.config.GerbilConfiguration;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ROCEvaluator.class);

    public static final String AUC_NAME = "Area Under Curve";
    public static final String ROC_NAME = "ROC Curve";

    public static final String TRUTH_VALUE_URI_GERBIL_KEY = "org.aksw.gerbil.evaluator.roc.truthProperty";

    public static String DEFAULT_TRUTH_VALUE_URI = "http://swc2017.aksw.org/hasTruthValue";

    private String truthValueURI = "http://swc2017.aksw.org//hasTruthValue";

    public ROCEvaluator() {
        super();
        truthValueURI = GerbilConfiguration.getInstance().getString(TRUTH_VALUE_URI_GERBIL_KEY);
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        results.addResults(compareModel(annotatorResults.get(0).get(0), goldStandard.get(0).get(0)));
    }

    public EvaluationResult[] compareModel(Model annotator, Model gold) {
        Double auc = null;
        // String rocJson = "";

        Property truthValueProp = annotator.getProperty(truthValueURI);
        Property truthValueGold = gold.getProperty(truthValueURI);

        // Count the true and false statements (make sure we accept double and float values
        int trueStmts = gold.listLiteralStatements(null, truthValueGold, 1.0).toList().size() + gold.listLiteralStatements(null, truthValueGold, 1.0f).toList().size();
        int falseStmts = gold.listLiteralStatements(null, truthValueGold, 0.0).toList().size()+ gold.listLiteralStatements(null, truthValueGold, 0.0f).toList().size();

        //remove all non number values (NAN, INFINITY, etc)
        cleanTruthValues(annotator, truthValueProp);
        List<Statement> sortedStatements = annotator.listStatements(null, truthValueProp, (RDFNode) null).toList();

        Collections.sort(sortedStatements, new StatementComparator());
        ROCCurve curve = new ROCCurve(trueStmts, falseStmts);

        for (Statement stmt : sortedStatements) {
            // Get the same triple from the gold standard
            Resource checkStmt = stmt.getSubject();
            StmtIterator stIt = gold.listStatements(checkStmt, truthValueGold, (RDFNode) null);
            // if such a triple exists
            if (stIt.hasNext()) {
                Double truthValue = stIt.next().getDouble();
                // We don't want to check whether it is equal to one or zero, so let's check
                // whether it is larger than 0.5 ;-)
                if (truthValue > 0.5) {
                    curve.addUp();
                } else {
                    curve.addRight();
                }
            } else {
                // ignore it
                LOGGER.info("The system answer contained the following unknown statement: {}", stmt.toString());
            }
        }
        curve.finishCurve();
        auc = curve.calcualteAUC();

        return new EvaluationResult[] { new DoubleEvaluationResult(AUC_NAME, auc),
                new StringEvaluationResult(ROC_NAME, curve.toString()) };
    }

    /**
     * Method to clean the model from not number values, such as Infinity and NAN
     * @param annotator
     * @param truthValueProp 
     */
    private void cleanTruthValues(Model annotator, Property truthValueProp) {
		StmtIterator statements = annotator.listStatements(null, truthValueProp, (RDFNode)null);
		List<Statement> removeStmts = new LinkedList<Statement>();
		while(statements.hasNext()) {
			Statement stmt = statements.next();
			try{
				Double value = stmt.getLiteral().getDouble();
				if(value.isInfinite() || value.isNaN()) {
					//value cannot be processed so remove the statment
					removeStmts.add(stmt);
				}
			}catch(NumberFormatException e) {
				//not double so remove
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

}

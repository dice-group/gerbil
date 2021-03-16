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
import org.aksw.gerbil.evaluate.StringEvaluationResult;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

public class ROCEvaluator<T extends Model> extends AUCEvaluator<Model>{
    
    public static final String NAME = "ROC";

    public ROCEvaluator() {
        super();
    }

    public ROCEvaluator(String truthValueURI) {
    	super(truthValueURI);
    }
    
    @Override
    public EvaluationResult[] compareModel(Model annotator, Model gold) {
        Double auc = null;
       
	    Property truthValueGold = gold.getProperty(truthValueURI);
	     
        // Count the true and false statements (make sure we accept double and float values
	     int trueStmts = gold.listLiteralStatements(null, truthValueGold, 1.0).toList().size() + gold.listLiteralStatements(null, truthValueGold, 1.0f).toList().size();
	     int falseStmts = gold.listLiteralStatements(null, truthValueGold, 0.0).toList().size()+ gold.listLiteralStatements(null, truthValueGold, 0.0f).toList().size();
	     
        List<DoubleBooleanPair> evalStatements = buildDoubleBooleanPairs(annotator, gold);
        
        Collections.sort(evalStatements);
        
        StepCurve curve = new StepCurve(new Point(0,0), new Point(1,1));
        curve.defineStep(trueStmts, falseStmts);
        int trueResults = 0, falseResults = 0;
        for (int i = 0; i < evalStatements.size(); ++i) {
            DoubleBooleanPair current = evalStatements.get(i);
            if(current.goldFlag) {
                ++trueResults;
            } else {
                ++falseResults;
            }
            // If this is the last pair OR the next pair has a different predicted value
            if((i == (evalStatements.size() - 1)) || (evalStatements.get(i + 1).predictedValue != current.predictedValue)) {
                // check if there are only steps up
                if(falseResults == 0) {
                    for (int j = 0; j < trueResults; ++j) {
                        curve.addUp();
                    }
                } else if(trueResults == 0) {
                    for (int j = 0; j < falseResults; ++j) {
                        curve.addRight();
                    }
                } else {
                    curve.addDiagonally(trueResults, falseResults);
                }
                trueResults = 0;
                falseResults = 0;
            }
        }
        curve.finishCurve();
        auc = curve.calculateAUC();
        
        return new EvaluationResult[] { new DoubleEvaluationResult(NAME+"-"+AUC_NAME, auc),
                new StringEvaluationResult(NAME, curve.toString()) };
    }	
}

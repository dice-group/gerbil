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

import java.util.List;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelComparator<T extends Model> implements Evaluator<T> {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelComparator.class);

    public static final String F1_SCORE_NAME = "F1 score";
    public static final String PRECISION_NAME = "Precision";
    public static final String RECALL_NAME = "Recall";

    public ModelComparator() {
        super();
   	
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        results.addResults(compareModel(annotatorResults.get(0).get(0), goldStandard.get(0).get(0)) );
    }


	public EvaluationResult[] compareModel(Model annotator, Model gold) {
		long tp = annotator.intersection(gold).size();
		long fp = annotator.difference(gold).size();
		long fn = gold.difference(annotator).size();
		
		double prec = 1.0;
		if(tp!=0.0 || fp != 0.0)
			prec = tp*1.0/(tp+fp);
		double rec = 1.0;
		if(tp!=0.0 || fn!=0.0)
			rec =tp*1.0/(tp+fn);
		double f1 = 0.0;
		if(prec!=0.0 && rec!=0.0)
			f1 = 2*(prec*rec)/(prec+rec);
		return new EvaluationResult[] { new DoubleEvaluationResult(PRECISION_NAME, prec),
                new DoubleEvaluationResult(RECALL_NAME, rec),
                new DoubleEvaluationResult(F1_SCORE_NAME, f1) };
	}

}

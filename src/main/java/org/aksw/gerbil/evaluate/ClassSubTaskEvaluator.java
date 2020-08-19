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
package org.aksw.gerbil.evaluate;

import java.util.ArrayList;
import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.transfer.nif.Marking;

public class ClassSubTaskEvaluator<T extends Marking> extends SubTaskEvaluator<T> {
	
	private Class<? extends Marking> cleanClass = Marking.class;

    public ClassSubTaskEvaluator(ExperimentTaskConfiguration configuration, List<Evaluator<T>> evaluators, Class<? extends Marking> cleanClass) {
        super(configuration, evaluators);
        this.cleanClass=cleanClass;
    }

    public ClassSubTaskEvaluator(ExperimentTaskConfiguration configuration, Evaluator<T> evaluator,  Class<? extends Marking> cleanClass) {
        super(configuration, evaluator);
        this.cleanClass=cleanClass;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results, String language) {
        super.evaluate(cleanList(annotatorResults), cleanList(goldStandard), results, language);
    }
    
    private List<List<T>> cleanList(List<List<T>> results){
    	List<List<T>> cleanedResults = new ArrayList<List<T>>();
    	for(List<T> result : results) {
    		List<T> cleanedResult = new ArrayList<T>();
    		for(T marking : result) {
    			
    			if(cleanClass.isInstance(marking)) {
    				cleanedResult.add(marking);
    			}
    		}
    		cleanedResults.add(cleanedResult);
    	}
    	return cleanedResults;
    }

}

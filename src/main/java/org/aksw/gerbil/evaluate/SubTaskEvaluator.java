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

public class SubTaskEvaluator<T extends Marking> implements Evaluator<T> {

    private ExperimentTaskConfiguration configuration;
    private List<Evaluator<T>> evaluators;

    public SubTaskEvaluator(ExperimentTaskConfiguration configuration, List<Evaluator<T>> evaluators) {
        this.configuration = configuration;
        this.evaluators = evaluators;
    }

    public SubTaskEvaluator(ExperimentTaskConfiguration configuration, Evaluator<T> evaluator) {
        this.configuration = configuration;
        this.evaluators = new ArrayList<Evaluator<T>>();
        this.evaluators.add(evaluator);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results,String language) {
        SubTaskResult subTaskResults = new SubTaskResult(configuration);
        for (Evaluator<? extends Marking> e : evaluators) {
            ((Evaluator<T>) e).evaluate(annotatorResults, goldStandard, subTaskResults, language);
            if (subTaskResults.getResults().size() > 0) {
                results.addResult(subTaskResults);
            }
        }
    }

}

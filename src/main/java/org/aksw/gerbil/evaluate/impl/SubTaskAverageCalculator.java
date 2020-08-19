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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.SubTaskEvaluator;
import org.aksw.gerbil.transfer.nif.Marking;

import com.carrotsearch.hppc.DoubleArrayList;

public class SubTaskAverageCalculator<T extends Marking> implements Evaluator<T> {

    private List<SubTaskEvaluator<T>> evaluators;

    public SubTaskAverageCalculator(List<SubTaskEvaluator<T>> evaluators) {
        this.evaluators = evaluators;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results,String language) {
        EvaluationResultContainer subTaskResults = new EvaluationResultContainer();
        for (SubTaskEvaluator<T> evaluator : evaluators) {
            evaluator.evaluate(annotatorResults, goldStandard, subTaskResults, language);
        }
        addSubTaskResults(subTaskResults, results);
        addAverages(subTaskResults, results);
    }

    protected void addSubTaskResults(EvaluationResultContainer subTaskResults, EvaluationResultContainer results) {
        for (EvaluationResult result : subTaskResults.getResults()) {
            results.addResult(result);
        }
    }

    protected void addAverages(EvaluationResultContainer subTaskResults, EvaluationResultContainer results) {
        Map<String, DoubleArrayList> mapping = createNameValueMapping(subTaskResults.getResults());
        DoubleArrayList values;
        int subTaskCount = subTaskResults.getResults().size();
        double sum;
        for (String name : mapping.keySet()) {
            values = mapping.get(name);
            if (values.elementsCount == subTaskCount) {
                sum = 0;
                for (int i = 0; i < values.elementsCount; ++i) {
                    sum += values.buffer[i];
                }
                results.addResult(new DoubleEvaluationResult(name, sum / subTaskCount));
            }
        }
    }

    private Map<String, DoubleArrayList> createNameValueMapping(List<EvaluationResult> results) {
        Map<String, DoubleArrayList> mapping = new HashMap<String, DoubleArrayList>();
        for (EvaluationResult result : results) {
            addToMapping(mapping, result);
        }
        return mapping;
    }

    private void addToMapping(Map<String, DoubleArrayList> mapping, EvaluationResult result) {
        if (result instanceof EvaluationResultContainer) {
            for (EvaluationResult r : ((EvaluationResultContainer) result).getResults()) {
                addToMapping(mapping, r);
            }
        } else if (result instanceof DoubleEvaluationResult) {
            DoubleArrayList values;
            if (mapping.containsKey(result.getName())) {
                values = mapping.get(result.getName());
            } else {
                values = new DoubleArrayList();
                mapping.put(result.getName(), values);
            }
            values.add(((DoubleEvaluationResult) result).getValueAsDouble());
        }
    }

}

/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results) {
        SubTaskResult subTaskResults = new SubTaskResult(configuration);
        for (Evaluator<? extends Marking> e : evaluators) {
            ((Evaluator<T>) e).evaluate(annotatorResults, goldStandard, subTaskResults);
            if (subTaskResults.getResults().size() > 0) {
                results.addResult(subTaskResults);
            }
        }
    }

}

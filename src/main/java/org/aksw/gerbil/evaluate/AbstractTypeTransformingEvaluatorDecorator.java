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

import org.aksw.gerbil.transfer.nif.Marking;

public abstract class AbstractTypeTransformingEvaluatorDecorator<U extends Marking, V extends Marking>
        implements TypeTransformingEvaluatorDecorator<U, V> {

    protected Evaluator<V> evaluator;

    public AbstractTypeTransformingEvaluatorDecorator(Evaluator<V> evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Evaluator<V> getDecorated() {
        return evaluator;
    }

    @Override
    public void evaluate(List<List<U>> annotatorResults, List<List<U>> goldStandard,
            EvaluationResultContainer results,String language) {
        evaluator.evaluate(changeListType(annotatorResults), changeListType(goldStandard), results, language);
    }

    protected List<List<V>> changeListType(List<List<U>> markings) {
        List<List<V>> newMarkings = new ArrayList<List<V>>(markings.size());
        for (List<U> markingsList : markings) {
            newMarkings.add(changeType(markingsList));
        }
        return newMarkings;
    }

    /**
     * The method that performs the internal transformation.
     * 
     * @param markings
     *            the original list of markings lists
     * @return the transformed list
     */
    protected abstract List<V> changeType(List<U> markings);

}

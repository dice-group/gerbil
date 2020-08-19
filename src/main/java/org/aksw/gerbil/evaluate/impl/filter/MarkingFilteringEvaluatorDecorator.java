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
package org.aksw.gerbil.evaluate.impl.filter;

import java.util.List;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.utils.filter.MarkingFilter;

public class MarkingFilteringEvaluatorDecorator<T extends Marking> extends AbstractEvaluatorDecorator<T> {

    protected MarkingFilter<T> filter;

    public MarkingFilteringEvaluatorDecorator(MarkingFilter<T> filter, Evaluator<T> evaluator) {
        super(evaluator);
        this.filter = filter;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results,String language) {
        evaluator.evaluate(filter.filterListOfLists(annotatorResults), filter.filterListOfLists(goldStandard), results, language);
    }

}

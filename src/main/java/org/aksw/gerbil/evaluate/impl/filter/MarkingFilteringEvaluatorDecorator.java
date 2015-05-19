package org.aksw.gerbil.evaluate.impl.filter;

import java.util.List;

import org.aksw.gerbil.evaluate.AbstractEvaluatorDecorator;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.transfer.nif.Marking;

public class MarkingFilteringEvaluatorDecorator<T extends Marking> extends AbstractEvaluatorDecorator<T> {

    protected MarkingFilter<T> filter;

    public MarkingFilteringEvaluatorDecorator(MarkingFilter<T> filter, Evaluator<T> evaluator) {
        super(evaluator);
        this.filter = filter;
    }

    @Override
    public void evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard, EvaluationResultContainer results) {
        evaluator.evaluate(filter.filterListOfLists(annotatorResults), filter.filterListOfLists(goldStandard), results);
    }

}

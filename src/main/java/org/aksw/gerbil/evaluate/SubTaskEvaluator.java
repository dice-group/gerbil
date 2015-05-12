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

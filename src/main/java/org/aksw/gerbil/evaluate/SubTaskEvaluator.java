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
    public EvaluationResult evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard) {
        SubTaskResult evalResults = new SubTaskResult(configuration);
        EvaluationResult evalResult;
        for (Evaluator<? extends Marking> e : evaluators) {
            evalResult = ((Evaluator<T>) e).evaluate(annotatorResults, goldStandard);
            if (evalResult != null) {
                evalResults.addResult(evalResult);
            }
        }
        return evalResults;
    }

}

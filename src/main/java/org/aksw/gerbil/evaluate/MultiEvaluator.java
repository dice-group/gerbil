package org.aksw.gerbil.evaluate;

import java.util.Arrays;
import java.util.List;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;

/**
 * A simple class that wraps multiple {@link Evaluator} instances that are
 * executed linearly in the order in which they have been given.
 * 
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 * @param <T> An implementation of the {@link Marking} interface that the given
 *            {@link Evaluator} can process.
 */
public class MultiEvaluator<T extends Marking> implements Evaluator<T> {

    protected List<Evaluator<T>> evaluators;

    /**
     * Constructor.
     * 
     * @param evaluators the {@link Evaluator} instances that should be executed one
     *                   after the other during the evaluation.
     */
    public MultiEvaluator(@SuppressWarnings("unchecked") Evaluator<T>... evaluators) {
        super();
        this.evaluators = Arrays.asList(evaluators);
    }

    /**
     * Constructor.
     * 
     * @param evaluators the {@link Evaluator} instances that should be executed one
     *                   after the other during the evaluation.
     */
    public MultiEvaluator(List<Evaluator<T>> evaluators) {
        super();
        this.evaluators = evaluators;
    }

    @Override
    public void evaluate(List<Document> instances, List<List<T>> annotatorResults, List<List<T>> goldStandard,
            EvaluationResultContainer results) {
        for (Evaluator<T> evaluator : evaluators) {
            evaluator.evaluate(instances, annotatorResults, goldStandard, results);
        }
    }

}

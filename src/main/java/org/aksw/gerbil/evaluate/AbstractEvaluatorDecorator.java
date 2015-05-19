package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.transfer.nif.Marking;

public abstract class AbstractEvaluatorDecorator<T extends Marking> implements EvaluatorDecorator<T> {

    protected Evaluator<T> evaluator;

    public AbstractEvaluatorDecorator(Evaluator<T> evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public Evaluator<T> getDecorated() {
        return evaluator;
    }

}

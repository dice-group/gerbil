package org.aksw.gerbil.evaluate;

import org.aksw.gerbil.transfer.nif.Marking;

public interface EvaluatorDecorator<T extends Marking> extends Evaluator<T> {

    public Evaluator<T> getDecorated();
}

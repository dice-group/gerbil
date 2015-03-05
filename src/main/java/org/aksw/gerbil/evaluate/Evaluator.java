package org.aksw.gerbil.evaluate;

import java.util.List;

import org.aksw.gerbil.transfer.nif.Marking;

public interface Evaluator<T extends Marking> {

    public EvaluationResult evaluate(List<List<T>> annotatorResults, List<List<T>> goldStandard);
}

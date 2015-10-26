package org.aksw.gerbil.evaluate.impl;

import java.util.Comparator;

import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResult;

public class DoubleResultComparator implements Comparator<EvaluationResult> {

    @Override
    public int compare(EvaluationResult result1, EvaluationResult result2) {
        return Double.compare(((DoubleEvaluationResult) result1).getValueAsDouble(),
                ((DoubleEvaluationResult) result2).getValueAsDouble());
    }

}

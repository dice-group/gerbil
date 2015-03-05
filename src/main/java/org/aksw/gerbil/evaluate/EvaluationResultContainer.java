package org.aksw.gerbil.evaluate;

import java.util.ArrayList;
import java.util.List;

public class EvaluationResultContainer implements EvaluationResult {

    private List<EvaluationResult> results = new ArrayList<EvaluationResult>();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object getValue() {
        return results;
    }

    public void addResult(EvaluationResult result) {
        results.add(result);
    }

    public void addResults(EvaluationResult... results) {
        for (int i = 0; i < results.length; i++) {
            this.results.add(results[i]);
        }
    }

    public List<EvaluationResult> getResults() {
        return results;
    }

}

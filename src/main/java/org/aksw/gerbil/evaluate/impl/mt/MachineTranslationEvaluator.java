package org.aksw.gerbil.evaluate.impl.mt;

import java.util.List;

import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;

public class MachineTranslationEvaluator implements Evaluator<SimpleFileRef> {

    @Override
    public void evaluate(List<List<SimpleFileRef>> annotatorResults, List<List<SimpleFileRef>> goldStandard,
            EvaluationResultContainer results) {
        // We assume that both lists have only one element!!!
        // We assume that each sub list has exactly one element!!!

        SimpleFileRef expected = goldStandard.get(0).get(0);
        SimpleFileRef hypothesis = annotatorResults.get(0).get(0);
        
        // expected.getFileRef() --> gives path to file with the expected translation
        
        // hypothesis.getFileRef() --> gives path to file with the uploaded translation
        
        // start python script and gather results
        
        // Add results to the results container, e.g.,
        results.addResult(new DoubleEvaluationResult("meteor", 0.0));
    }

}

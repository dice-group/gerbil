package org.knowgraphs;

import java.util.List;

import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.ScoredMarking;

public class MyFancyEvaluator implements Evaluator<MeaningSpan> {

    /**
     * The method to implement your evaluation.
     * 
     * @param annotatorResults The result of the benchmarked system as list of
     *                         annotations per document. So the first list contains
     *                         a single list for each document. The documents
     *                         already have the same order as the documents of the
     *                         gold standard, i.e., document 0 of this list refers
     *                         to the same document as document 0 in the gold
     *                         standard list.
     * @param goldStandard     The gold standard annotations as list of annotations
     *                         per document. So the first list contains a single
     *                         list for each document.
     * @param results          A container to which you should add your evaluation
     *                         results
     */
    @Override
    public void evaluate(List<List<MeaningSpan>> annotatorResults, List<List<MeaningSpan>> goldStandard,
            EvaluationResultContainer results) {
        // TODO Here, you may want to implement your fancy evaluation :)

        // Some systems may provide you with confidence scores. In that case, you will
        // receive ScoredMarking instances.
        if (annotatorResults.get(0).get(0) instanceof ScoredMarking) {
            double confidenceScore = ((ScoredMarking) annotatorResults.get(0).get(0)).getConfidence();
        }

        // You can choose between different evaluation results.
        // We have classes for simple results:
        // results.addResult(new IntEvaluationResult("My fancy int result", 42));
        // results.addResult(new DoubleEvaluationResult("My fancy double result",
        // 3.14));
        // You can also write your own EvaluationResult class if you need one :)
    }

}

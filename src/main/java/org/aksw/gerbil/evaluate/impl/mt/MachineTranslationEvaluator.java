package org.aksw.gerbil.evaluate.impl.mt;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;

public class MachineTranslationEvaluator implements Evaluator<SimpleFileRef> {

    @Override
    public void evaluate(List<List<SimpleFileRef>> annotatorResults, List<List<SimpleFileRef>> goldStandard,
            EvaluationResultContainer results){
        // We assume that both lists have only one element!!!
        // We assume that each sub list has exactly one element!!!

        SimpleFileRef expected = goldStandard.get(0).get(0);
        SimpleFileRef hypothesis = annotatorResults.get(0).get(0);
        
         File ref = expected.getFileRef(); // gives path to file with the expected translation
        
         File hypo = hypothesis.getFileRef(); // gives path to file with the uploaded translation
        
        // start python script and gather results
        try {
            Process p = Runtime.getRuntime().exec("python3 src/main/java/org/aksw/gerbil/dataset/impl/mt/pyt/eval.py -R" +
                    ref+ "-H " + hypo + " -nr 1 -m bleu,meteor,chrf++,ter");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line + "\n");
            }
        }catch (Exception e){

        }
        // Add results to the results container, e.g.,
        results.addResult(new DoubleEvaluationResult("METEOR", 0.0));
        results.addResult(new DoubleEvaluationResult("BLEU", 0.0));
    }

}

package org.aksw.gerbil.evaluate.impl.mt;

import java.io.*;
import java.util.List;

import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
            Process p = Runtime.getRuntime().exec("python3 src/main/java/org/aksw/gerbil/dataset/impl/mt/python/eval.py -R" +
                    ref+ "-H " + hypo + " -nr 1 -m bleu,meteor,chrf++,ter");
        BufferedReader breader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        while ((line = breader.readLine()) != null) {
            System.out.println(line + "\n");
        }
        JSONParser jsonParser = new JSONParser();
        double bleu, nltk, meteor, chrF, ter;
            Object obj = jsonParser.parse(new FileReader("result.json"));
            JSONObject jsonObject = (JSONObject) obj;
            bleu = (Double) jsonObject.get("BLEU");
            // Add results to the results container
            results.addResult(new DoubleEvaluationResult("BLEU", bleu));

            nltk = (Double) jsonObject.get("BLEU NLTK");
            results.addResult(new DoubleEvaluationResult("BLEU_NLTK", nltk));

            meteor = (Double) jsonObject.get("METEOR");
            results.addResult(new DoubleEvaluationResult("METEOR", meteor));

            chrF = (Double) jsonObject.get("chrF++");
            results.addResult(new DoubleEvaluationResult("chrF++", chrF));

            ter = (Double) jsonObject.get("TER");
            results.addResult(new DoubleEvaluationResult("TER", ter));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

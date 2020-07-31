package org.aksw.gerbil.evaluate.impl.NLG;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.dataset.InitializableDataset;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.utils.ClosePermitionGranter;
import org.apache.commons.io.IOUtils;
import org.apache.jena.base.Sys;

import java.io.*;
import java.util.List;

public class NLGEvaluator implements Evaluator<SimpleFileRef> {

    @Override
    public void evaluate(List<List<SimpleFileRef>> annotatorResults, List<List<SimpleFileRef>> goldStandard,
                         EvaluationResultContainer results) {

        // We assume that both lists have only one element!!!
        // We assume that each sub list has exactly one element!!!
       // File[] directory = new File[0];
      //  f(directory);
        SimpleFileRef expected = goldStandard.get(0).get(0);
        SimpleFileRef hypothesis = annotatorResults.get(0).get(0);
        File ref = expected.getFileRef(); // gives path to file with the expected translation
        File hypo = hypothesis.getFileRef(); // gives path to file with the uploaded translation

        // start python script and gather results
        try {
            ReaderThread reader = new ReaderThread();
            Thread readerThread = new Thread(reader);

            Process p = Runtime.getRuntime()
                    .exec("python3 src/main/java/org/aksw/gerbil/python/mt/eval.py -R " + ref + " -H "
                            + hypo + " -nr 8 -m bleu,meteor,chrf++,ter");
            System.out.println("python3 src/main/java/org/aksw/gerbil/python/mt/eval.py -R " + ref + " -H "
                    + hypo + " -nr 8 -m bleu,meteor,chrf++,ter");

            reader.setInput(p.getInputStream());
            readerThread.start();

            // Wait for the python process to terminate
            int exitValue = p.waitFor();
            // stop the reader thread
            reader.setTerminate(true);
            // Wait for the reader thread to terminate
            readerThread.join();

            // The script encountered an issue
            if (exitValue != 0) {
                // Try to get the error message of the script
                IOUtils.copy(p.getErrorStream(), System.err);
                throw new IllegalStateException("Python script aborted with an error.");
            }

            String scriptResult = reader.getBuffer().toString();
            System.out.println("Data:" + scriptResult + "\n");

            int jsonStart = scriptResult.indexOf('{');
            if(jsonStart < 0) {
                throw new IllegalStateException("The script result does not seem to contain a JSON object!");
            }
            scriptResult = scriptResult.substring(jsonStart);

            double bleu, nltk, meteor, chrF, ter;
            JsonObject jsonObject = new JsonParser().parse(scriptResult).getAsJsonObject();
            bleu = jsonObject.get("BLEU").getAsDouble();
            results.addResult(new DoubleEvaluationResult("BLEU", bleu));

            nltk = jsonObject.get("BLEU NLTK").getAsDouble();
            results.addResult(new DoubleEvaluationResult("BLEU NLTK", nltk));

            meteor = jsonObject.get("METEOR").getAsDouble();
            results.addResult(new DoubleEvaluationResult("METEOR", meteor));

            chrF = jsonObject.get("chrF++").getAsDouble();
            results.addResult(new DoubleEvaluationResult("chrF++", chrF));

            ter = jsonObject.get("TER").getAsDouble();
            results.addResult(new DoubleEvaluationResult("TER", ter));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
/*public File[] f(File[] d){
        File[] list = d;
        return list;
}
 */
    public static final class ReaderThread implements Runnable {

        private StringBuilder buffer = new StringBuilder();
        private Reader input;
        private boolean terminate = false;

        @Override
        public void run() {
            try {
                char cBuffer[] = new char[256];
                int length;
                while (!terminate) {
                    while ((input != null) && (input.ready())) {
                        length = input.read(cBuffer);
                        buffer.append(cBuffer, 0, length);
                    }
                    // sleep for a short moment before checking the stream again
                    Thread.sleep(50);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setInput(InputStream input) {
            this.input = new BufferedReader(new InputStreamReader(input));
        }

        public void setTerminate(boolean terminate) {
            this.terminate = terminate;
        }

        public StringBuilder getBuffer() {
            return buffer;
        }


    }
}

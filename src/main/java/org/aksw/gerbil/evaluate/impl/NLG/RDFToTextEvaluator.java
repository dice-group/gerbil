package org.aksw.gerbil.evaluate.impl.NLG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RDFToTextEvaluator implements Evaluator<SimpleFileRef> {

    @Override
    public void evaluate(List<List<SimpleFileRef>> annotatorResults, List<List<SimpleFileRef>> goldStandard,
                         EvaluationResultContainer results, String language) {

        // We assume that both lists have only one element!!!
        // We assume that each sub list has exactly one element!!!
        SimpleFileRef expected = goldStandard.get(0).get(0);
        SimpleFileRef hypothesis = annotatorResults.get(0).get(0);
        File ref = expected.getFileRef();// gives path to file with the expected translation
        String[] pathnames = ref.list();
        String reference = "";
        for (String pathname : pathnames) {
            reference = pathname;
        }

        String datasetName = ref.getName();
        System.out.println(datasetName);
        int numberOfReferences = ref.list((dir, name) -> name.matches("reference[0-9]+")).length;
        File hypo = hypothesis.getFileRef(); // gives path to file with the uploaded translation
        // start python script and gather results
        try {
            String command;
            ReaderThread reader = new ReaderThread();
            Thread readerThread = new Thread(reader);
            if (datasetName.equals("English") && numberOfReferences > 1) {
                command = new StringBuilder().append("python3 src/main/java/org/aksw/gerbil/python/mt/eval.py -R ")
                        .append(ref.getPath()).append("/reference -H ").append(hypo.getPath()).append(" -nr ")
                        .append(numberOfReferences).append(" -m bleu,meteor,chrf++,ter").toString();
                System.out.println(numberOfReferences);
                System.out.println(command);
            }
            else if(datasetName.equals("English") && numberOfReferences == 1){
                command = new StringBuilder().append("python3 src/main/java/org/aksw/gerbil/python/mt/eval.py -R ")
                        .append(ref.getPath()).append("/"+reference).append(" -H ").append(hypo.getPath()).append(" -nr 1 ")
                        .append(" -m bleu,meteor,chrf++,ter").toString();
                System.out.println(numberOfReferences);
                System.out.println(reference);
                System.out.println(command);
            }
            else if (datasetName.equals("Russian") && numberOfReferences == 1){
                command = new StringBuilder().append("python3 src/main/java/org/aksw/gerbil/python/mt/eval.py -R ")
                        .append(ref.getPath()).append("/"+reference).append(" -H ").append(hypo.getPath()).append(" -lng ru -nr 1 ")
                        .append(" -m bleu,meteor,chrf++,ter").toString();
                System.out.println(numberOfReferences);
                System.out.println(reference);
                System.out.println(command);

            }
            else {
                command = new StringBuilder().append("python3 src/main/java/org/aksw/gerbil/python/mt/eval.py -R ")
                        .append(ref.getPath()).append("/reference -H ").append(hypo.getPath()).append(" -lng ru -nr ")
                        .append(numberOfReferences).append(" -m bleu,meteor,chrf++,ter").toString();
                System.out.println(numberOfReferences);
                System.out.println(command);

            }

            Process p = Runtime.getRuntime().exec(command);

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
            if (jsonStart < 0) {
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

    /*
     * public File[] f(File[] d){ File[] list = d; return list; }
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

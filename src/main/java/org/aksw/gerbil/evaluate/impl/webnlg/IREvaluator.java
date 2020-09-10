package org.aksw.gerbil.evaluate.impl.webnlg;
import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.evaluate.DoubleEvaluationResult;
import org.aksw.gerbil.evaluate.EvaluationResultContainer;
import org.aksw.gerbil.evaluate.Evaluator;
import org.aksw.gerbil.evaluate.SubTaskResult;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.List;

public class IREvaluator implements Evaluator<SimpleFileRef> {
    // Configuration of the experiment
    private ExperimentTaskConfiguration configuration;

    public IREvaluator(ExperimentTaskConfiguration configuration) {
        this.configuration = configuration;
    }
    @Override
    public void evaluate(List<List<SimpleFileRef>> annotatorResults, List<List<SimpleFileRef>> goldStandard,
                         EvaluationResultContainer results, String language) {
        // We assume that both lists have only one element!!!
        // We assume that each sub list has exactly one element!!!

        SimpleFileRef expected = goldStandard.get(0).get(0);
        SimpleFileRef candidate = annotatorResults.get(0).get(0);

        File ref = expected.getFileRef(); // gives path to file with the expected translation

        File candidateTriple = candidate.getFileRef(); // gives path to file with the uploaded translation

        // start python script and gather results
        try {
            ReaderThread reader = new ReaderThread();
            Thread readerThread = new Thread(reader);
            Process p = Runtime.getRuntime()
                    .exec("python3 src/main/java/org/aksw/gerbil/python/webnlg/Evaluation_script_json.py "+ ref +" "+
                            candidateTriple + " result.json");
            reader.setInput(p.getInputStream());
            System.out.println("python3 src/main/java/org/aksw/gerbil/python/webnlg/Evaluation_script_json.py "+ ref +" "+
                    candidateTriple + " result.json");
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

            double precision,recall,f1;
            JSONParser jsonParser = new JSONParser();
            Object object;
            object = jsonParser.parse(new FileReader("result.json"));
            JSONObject jsonObject = (JSONObject) object;
            JSONObject total_scores = (JSONObject) jsonObject.get("Total_scores");

            //Exact
            JSONObject exact = (JSONObject) total_scores.get("Exact");
            precision = (double) exact.get("Precision");
            results.addResult(new DoubleEvaluationResult("Precision", precision));
            recall = (double) exact.get("Recall");
            results.addResult(new DoubleEvaluationResult("Recall", recall));
            f1 = (double) exact.get("F1");
            results.addResult(new DoubleEvaluationResult("F1", f1));

            //Ent_type
            EvaluationResultContainer subTaskResultsEntType = new SubTaskResult(
                    new ExperimentTaskConfiguration(configuration.getAnnotatorConfig(), configuration.getDatasetConfig(),
                            ExperimentType.Ent_Type, language));
            JSONObject type = (JSONObject) total_scores.get("Ent_type");
            precision = (double) type.get("Precision");
            subTaskResultsEntType.addResult(new DoubleEvaluationResult("Precision", precision));
            recall = (double) type.get("Recall");
            subTaskResultsEntType.addResult(new DoubleEvaluationResult("Recall", recall));
            f1 = (double) type.get("F1");
            subTaskResultsEntType.addResult(new DoubleEvaluationResult("F1", f1));
            results.addResult(subTaskResultsEntType);

            //Partial
            EvaluationResultContainer subTaskResultsPartial = new SubTaskResult(
                    new ExperimentTaskConfiguration(configuration.getAnnotatorConfig(), configuration.getDatasetConfig(),
                            ExperimentType.Partial, language));
            JSONObject partial = (JSONObject) total_scores.get("Partial");
            precision = (double) partial.get("Precision");
            subTaskResultsPartial.addResult(new DoubleEvaluationResult("Precision", precision));
            recall = (double) partial.get("Recall");
            subTaskResultsPartial.addResult(new DoubleEvaluationResult("Recall", recall));
            f1 = (double) partial.get("F1");
            subTaskResultsPartial.addResult(new DoubleEvaluationResult("F1", f1));
            results.addResult(subTaskResultsPartial);

            //Strict
            EvaluationResultContainer subTaskResultsStrict = new SubTaskResult(
                    new ExperimentTaskConfiguration(configuration.getAnnotatorConfig(), configuration.getDatasetConfig(),
                            ExperimentType.Strict, language));
            JSONObject strict = (JSONObject) total_scores.get("Strict");
            System.out.println("Strict: " + strict);
            precision = (double) strict.get("Precision");
            subTaskResultsStrict.addResult(new DoubleEvaluationResult("Precision", precision));
            recall = (double) strict.get("Recall");
            subTaskResultsStrict.addResult(new DoubleEvaluationResult("Recall", recall));
            f1 = (double) strict.get("F1");
            subTaskResultsStrict.addResult(new DoubleEvaluationResult("F1", f1));
            results.addResult(subTaskResultsStrict);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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

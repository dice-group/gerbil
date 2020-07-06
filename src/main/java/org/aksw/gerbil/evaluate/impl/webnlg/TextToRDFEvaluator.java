package org.aksw.gerbil.evaluate.impl.webnlg;

import org.apache.commons.io.IOUtils;
import java.io.*;

public class TextToRDFEvaluator{


    public static void main(String[] args) {
        // start python script and gather results
        try {
            org.aksw.gerbil.evaluate.impl.mt.MachineTranslationEvaluator.ReaderThread reader = new org.aksw.gerbil.evaluate.impl.mt.MachineTranslationEvaluator.ReaderThread();
            Thread readerThread = new Thread(reader);

            Process p = Runtime.getRuntime()
                    .exec("python3 src/main/java/org/aksw/gerbil/python/webnlg/Evaluation_script_json.py gerbil_data/datasets/webnlgData/Refs.xml " +
                            "gerbil_data/datasets/webnlgData/Cands2.xml result.json");
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
            System.out.println(scriptResult + "\n");
            int jsonStart = scriptResult.indexOf('{');
            if(jsonStart < 0) {
                throw new IllegalStateException("The script result does not seem to contain a JSON object!");
            }
            scriptResult = scriptResult.substring(jsonStart);
/*
            double entType,partial,strict,exact,scorePerTag;
            JsonObject jsonObject = new JsonParser().parse(scriptResult).getAsJsonObject();
            entType = jsonObject.get("Ent_type").getAsDouble();
            System.out.println("Ent_type:" + entType + "\n");

            partial = jsonObject.get("Partial").getAsDouble();
            System.out.println("Partial:" + partial + "\n");

            strict = jsonObject.get("Strict").getAsDouble();
            System.out.println("Data:" + strict + "\n");

            exact = jsonObject.get("Exact").getAsDouble();
            System.out.println("Exact:" + exact + "\n");

 */
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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


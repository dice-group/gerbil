/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.tools;

import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.data.Tag;
import it.unipi.di.acube.batframework.problems.C2WDataset;
import it.unipi.di.acube.batframework.problems.D2WDataset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.aksw.gerbil.web.config.DatasetsConfig;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
/**
 * FIXME update this tool.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class DatasetAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetAnalyzer.class);

    public static void main(String[] args) {
        List<DatasetConfiguration> datasetConfigs = DatasetsConfig.datasets().getConfigurations();
        PrintStream output = null;
        try {
            output = new PrintStream("datasetAnalyzation.log");
            DatasetAnalyzer analyzer = new DatasetAnalyzer(output);
            for (DatasetConfiguration config : datasetConfigs) {
                try {
                    analyzer.analyzeDataset(config);
                    SingletonWikipediaApi.getInstance().flush();
                } catch (GerbilException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    private PrintStream output;

    public DatasetAnalyzer(PrintStream output) {
        this.output = output;
    }

    public void analyzeDataset(DatasetConfiguration config) throws GerbilException {
        analyzeAsD2W(config);
        analyzeAsC2W(config);
    }

    private void analyzeAsC2W(DatasetConfiguration config) throws GerbilException {
        D2WDataset dataset = (D2WDataset) config.getDataset(ExperimentType.D2KB);
        if (dataset == null) {
            return;
        }
        output.print("D2W dataset: " + config.getName());
        output.print(" size=" + dataset.getSize());
        List<HashSet<Annotation>> goldStandard = dataset.getD2WGoldStandardList();
        double annotationsSum = 0;
        for (HashSet<Annotation> annotations : goldStandard) {
            annotationsSum += annotations.size();
        }
        // analyze texts
        int tokensSum = 0;
        for (String text : dataset.getTextInstanceList()) {
            tokensSum += countTokensInText(text);
        }
        output.print(" Annotations=" + annotationsSum);
        output.print(" Annotations/doc=" + (annotationsSum / dataset.getSize()));
        output.print(" tokens=" + tokensSum);
        output.print(" tokens/doc=" + ((double) tokensSum / (double) dataset.getSize()));
        output.println(" Annotations/tokens=" + ((double) annotationsSum / (double) tokensSum));
    }

    private int countTokensInText(String text) {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer(new StringReader(text));
        int tokens = 0;
        try {
            while (tokenizer.incrementToken()) {
                ++tokens;
            }
        } catch (IOException e) {
            LOGGER.error("Error while tokenizing text. Returning.", e);
        }
        return tokens;
    }

    private void analyzeAsD2W(DatasetConfiguration config) throws GerbilException {
        C2WDataset dataset = (C2WDataset) config.getDataset(ExperimentType.C2KB);
        if (dataset == null) {
            return;
        }
        output.print("C2W dataset: " + config.getName());
        output.print(" size=" + dataset.getSize());
        List<HashSet<Tag>> goldStandard = dataset.getC2WGoldStandardList();
        double annotationsSum = 0;
        for (HashSet<Tag> annotations : goldStandard) {
            annotationsSum += annotations.size();
        }
        // analyze texts
        int tokensSum = 0;
        for (String text : dataset.getTextInstanceList()) {
            tokensSum += countTokensInText(text);
        }
        output.print(" Tags=" + annotationsSum);
        output.print(" Tags/doc=" + (annotationsSum / dataset.getSize()));
        output.print(" tokens=" + tokensSum);
        output.print(" tokens/doc=" + ((double) tokensSum / (double) dataset.getSize()));
        output.println(" Tags/tokens=" + ((double) annotationsSum / (double) tokensSum));
    }
}

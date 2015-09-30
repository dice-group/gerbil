/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.tools;

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.data.Tag;
import it.acubelab.batframework.problems.C2WDataset;
import it.acubelab.batframework.problems.D2WDataset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;

import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.utils.DatasetMapping;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetAnalyzer.class);

    public static void main(String[] args) {
        List<DatasetConfiguration> datasetConfigs = DatasetMapping.getDatasetConfigurations();
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

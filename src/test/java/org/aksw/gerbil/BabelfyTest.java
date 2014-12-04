/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
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
package org.aksw.gerbil;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig.AIDACoNLLChunk;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.junit.Ignore;

@Ignore
public class BabelfyTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        // ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()), new KnownNIFFileDatasetConfig(
        // SingletonWikipediaApi.getInstance(),
        // NIFDatasets.N3_REUTERS_128), ExperimentType.D2KB,
        // Matching.STRONG_ANNOTATION_MATCH) };
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(
                        new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()), new AIDACoNLLDatasetConfig(
                                AIDACoNLLChunk.TEST_A, SingletonWikipediaApi.getInstance()), ExperimentType.A2KB,
                        Matching.WEAK_ANNOTATION_MATCH), new ExperimentTaskConfiguration(
                        new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()), new AIDACoNLLDatasetConfig(
                                AIDACoNLLChunk.TEST_B, SingletonWikipediaApi.getInstance()), ExperimentType.A2KB,
                        Matching.WEAK_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleLoggingDAO4Debugging(), taskConfigs,
                "BABELFY_TEST");
        experimenter.run();
    }
}

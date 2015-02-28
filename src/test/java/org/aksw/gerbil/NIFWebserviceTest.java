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

import it.acubelab.batframework.data.Annotation;
import it.acubelab.batframework.problems.A2WDataset;
import it.acubelab.batframework.problems.A2WSystem;
import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.aksw.gerbil.annotators.NIFWebserviceAnnotatorConfiguration;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.bat.annotator.ErrorCountingAnnotatorDecorator;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.aksw.simba.topicmodeling.concurrent.overseers.simple.SimpleOverseer;
import org.junit.Ignore;

@Ignore
public class NIFWebserviceTest {

    private static final String ANNOTATOR_URL = "http://localhost:8080/gerbil-spotWrapNifWS4Test/spotlight";
    private static final String ANNOTATOR_NAME = "gerbil-spotWrapNifWS4Test";

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException,
            GerbilException {
//         runDirectComprarisonD2KB();
//         runDirectComprarisonA2KB();
//         runCompleteExperimentD2KB();
        runCompleteExperimentA2KB();
    }

    public static void runCompleteExperimentA2KB() {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(
                        new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME, false,
                                wikiAPI, dbpApi, ExperimentType.A2KB),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.A2KB,
                        Matching.WEAK_ANNOTATION_MATCH),
                // compare this with the real Spotlight annotator
                new ExperimentTaskConfiguration(
                        new SpotlightAnnotatorConfig(wikiAPI, dbpApi),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.A2KB, Matching.WEAK_ANNOTATION_MATCH)
        };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleOverseer(), new SimpleLoggingDAO4Debugging(), taskConfigs,
                "SPOTLIGHT_NIF_TEST");
        experimenter.run();
    }

    public static void runCompleteExperimentD2KB() {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(
                        new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME, false,
                                wikiAPI, dbpApi, ExperimentType.D2KB),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.D2KB,
                        Matching.STRONG_ANNOTATION_MATCH),
                // compare this with the real Spotlight annotator
                new ExperimentTaskConfiguration(
                        new SpotlightAnnotatorConfig(wikiAPI, dbpApi),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.D2KB, Matching.STRONG_ANNOTATION_MATCH)
        };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleOverseer(), new SimpleLoggingDAO4Debugging(), taskConfigs,
                "SPOTLIGHT_NIF_TEST");
        experimenter.run();
    }

    public static void runDirectComprarisonA2KB() throws GerbilException {
        runDirectComprarison(ExperimentType.A2KB);
    }

    public static void runDirectComprarisonD2KB() throws GerbilException {
        runDirectComprarison(ExperimentType.D2KB);
    }

    public static void runDirectComprarison(ExperimentType experimentType) throws GerbilException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();

        A2WDataset dataset = (A2WDataset) (new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(),
                NIFDatasets.KORE50))
                .getDataset(experimentType);
        A2WSystem spotlight = (A2WSystem) ErrorCountingAnnotatorDecorator
                .createDecorator((new SpotlightAnnotatorConfig(wikiAPI, dbpApi))
                        .getAnnotator(experimentType));
        A2WSystem nifWS = (A2WSystem) ErrorCountingAnnotatorDecorator
                .createDecorator((new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME, false,
                        wikiAPI, dbpApi, experimentType)).getAnnotator(experimentType));

        HashSet<Annotation> spotlightResult = null, nifWSResult = null;
        boolean foundError = false;
        for (String document : dataset.getTextInstanceList()) {
            spotlightResult = spotlight.solveA2W(document);
            nifWSResult = nifWS.solveA2W(document);

            foundError = (spotlightResult.size() != nifWSResult.size());
            if (!foundError) {
                for (Annotation annotation : spotlightResult) {
                    if (!nifWSResult.contains(annotation)) {
                        foundError = true;
                        break;
                    }
                }
            }
            if (foundError) {
                break;
            }
        }

        if (foundError) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("spotlightResult=[");
            boolean first = true;
            for (Annotation annotation : spotlightResult) {
                if (first) {
                    first = false;
                } else {
                    errorMsg.append(',');
                }
                errorMsg.append('(');
                errorMsg.append(annotation.getPosition());
                errorMsg.append(',');
                errorMsg.append(annotation.getLength());
                errorMsg.append(',');
                errorMsg.append(annotation.getConcept());
                errorMsg.append(')');
            }
            errorMsg.append("]\nnifWSResult    =[");
            first = true;
            for (Annotation annotation : nifWSResult) {
                if (first) {
                    first = false;
                } else {
                    errorMsg.append(',');
                }
                errorMsg.append('(');
                errorMsg.append(annotation.getPosition());
                errorMsg.append(',');
                errorMsg.append(annotation.getLength());
                errorMsg.append(',');
                errorMsg.append(annotation.getConcept());
                errorMsg.append(')');
            }
            errorMsg.append(']');
            System.out.println(errorMsg.toString());
        }
    }
}

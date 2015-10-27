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
package org.aksw.gerbil;


import it.unipi.di.acube.batframework.data.Annotation;
import it.unipi.di.acube.batframework.problems.A2WDataset;
import it.unipi.di.acube.batframework.problems.A2WSystem;
import it.unipi.di.acube.batframework.systemPlugins.DBPediaApi;
import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

@Ignore
public class NIFWebserviceTest {

    private static final String ANNOTATOR_URL = "http://localhost:8080/gerbil-spotWrapNifWS4Test/spotlight";
    private static final String ANNOTATOR_NAME = "gerbil-spotWrapNifWS4Test";

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException,
            GerbilException {
        // runDirectComprarisonD2KB();
        // runDirectComprarisonA2KB();
        // runCompleteExperimentD2KB();
        runCompleteExperimentA2KB();
    }

    public static void runCompleteExperimentA2KB() {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME,
                        false, wikiAPI, dbpApi, ExperimentType.A2KB), new KnownNIFFileDatasetConfig(
                        SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50), ExperimentType.A2KB,
                        Matching.WEAK_ANNOTATION_MATCH),
                // compare this with the real Spotlight annotator
                new ExperimentTaskConfiguration(new SpotlightAnnotatorConfig(wikiAPI, dbpApi),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.A2KB, Matching.WEAK_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleOverseer(), new SimpleLoggingDAO4Debugging(),
                taskConfigs, "SPOTLIGHT_NIF_TEST");
        experimenter.run();
    }

    public static void runCompleteExperimentD2KB() {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME,
                        false, wikiAPI, dbpApi, ExperimentType.D2KB), new KnownNIFFileDatasetConfig(
                        SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50), ExperimentType.D2KB,
                        Matching.STRONG_ANNOTATION_MATCH),
                // compare this with the real Spotlight annotator
                new ExperimentTaskConfiguration(new SpotlightAnnotatorConfig(wikiAPI, dbpApi),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.D2KB, Matching.STRONG_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleOverseer(), new SimpleLoggingDAO4Debugging(),
                taskConfigs, "SPOTLIGHT_NIF_TEST");
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
                NIFDatasets.KORE50)).getDataset(experimentType);
        A2WSystem spotlight = (A2WSystem) ErrorCountingAnnotatorDecorator.createDecorator(
                (new SpotlightAnnotatorConfig(wikiAPI, dbpApi)).getAnnotator(experimentType), dataset.getSize());
        A2WSystem nifWS = (A2WSystem) ErrorCountingAnnotatorDecorator.createDecorator(
                (new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME, false, wikiAPI, dbpApi,
                        experimentType)).getAnnotator(experimentType), dataset.getSize());

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

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
import org.junit.Ignore;

@Ignore
public class NIFWebserviceTest {

    private static final String ANNOTATOR_URL = "http://localhost:8080/gerbil-spotWrapNifWS4Test/spotlight";
    private static final String ANNOTATOR_NAME = "gerbil-spotWrapNifWS4Test";

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException,
            GerbilException {
        // runDirectComprarisonD2W();
        // runDirectComprarisonA2W();
        // runCompleteExperimentD2W();
        runCompleteExperimentA2W();
    }

    public static void runCompleteExperimentA2W() {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(
                        new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME, false,
                                wikiAPI, dbpApi, ExperimentType.A2W),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.A2W,
                        Matching.WEAK_ANNOTATION_MATCH),
                // compare this with the real Spotlight annotator
                new ExperimentTaskConfiguration(
                        new SpotlightAnnotatorConfig(wikiAPI, dbpApi),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.A2W, Matching.WEAK_ANNOTATION_MATCH)
        };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleLoggingDAO4Debugging(), taskConfigs,
                "SPOTLIGHT_NIF_TEST");
        experimenter.run();
    }

    public static void runCompleteExperimentD2W() {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(
                        new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME, false,
                                wikiAPI, dbpApi, ExperimentType.D2W),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.D2W,
                        Matching.STRONG_ANNOTATION_MATCH),
                // compare this with the real Spotlight annotator
                new ExperimentTaskConfiguration(
                        new SpotlightAnnotatorConfig(wikiAPI, dbpApi),
                        new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), NIFDatasets.KORE50),
                        ExperimentType.D2W, Matching.STRONG_ANNOTATION_MATCH)
        };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleLoggingDAO4Debugging(), taskConfigs,
                "SPOTLIGHT_NIF_TEST");
        experimenter.run();
    }

    public static void runDirectComprarisonA2W() throws GerbilException {
        runDirectComprarison(ExperimentType.A2W);
    }

    public static void runDirectComprarisonD2W() throws GerbilException {
        runDirectComprarison(ExperimentType.D2W);
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

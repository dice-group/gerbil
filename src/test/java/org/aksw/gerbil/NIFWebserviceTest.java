package org.aksw.gerbil;

import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.annotators.NIFWebserviceAnnotatorConfiguration;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;

public class NIFWebserviceTest {

    private static final String ANNOTATOR_URL = "http://localhost:8080/gerbil-spotWrapNifWS4Test/spotlight";
    private static final String ANNOTATOR_NAME = "gerbil-spotWrapNifWS4Test";

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        DBPediaApi dbpApi = new DBPediaApi();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] {
                new ExperimentTaskConfiguration(
                        new NIFWebserviceAnnotatorConfiguration(ANNOTATOR_URL, ANNOTATOR_NAME, false,
                                ExperimentType.D2W),
                        new KnownNIFFileDatasetConfig(NIFDatasets.KORE50), ExperimentType.D2W,
                        Matching.STRONG_ANNOTATION_MATCH),
                // compare this with the real Spotlight annotator
                new ExperimentTaskConfiguration(
                        new SpotlightAnnotatorConfig(wikiAPI, dbpApi),
                        new KnownNIFFileDatasetConfig(NIFDatasets.KORE50),
                        ExperimentType.D2W, Matching.STRONG_ANNOTATION_MATCH)
        };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleLoggingDAO4Debugging(), taskConfigs,
                "SPOTLIGHT_NIF_TEST");
        experimenter.run();
    }
}

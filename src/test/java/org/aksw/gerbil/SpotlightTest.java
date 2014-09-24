package org.aksw.gerbil;

import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;

public class SpotlightTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        WikipediaApiInterface wikiAPI = new WikipediaApiInterface("wiki-id-title.cache", "wiki-id-id.cache");
        DBPediaApi dbpApi = new DBPediaApi();
        // ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new SpotlightAnnotatorConfig(wikiAPI, dbpApi), new IITBDatasetConfig(wikiAPI),
        // ExperimentType.D2W, Matching.STRONG_ANNOTATION_MATCH) };
        // ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
        // ExperimentTaskConfiguration(
        // new SpotlightAnnotatorConfig(wikiAPI, dbpApi), new AIDACoNLLDatasetConfig(
        // AIDACoNLLChunk.COMPLETE,
        // wikiAPI), ExperimentType.D2W, Matching.STRONG_ANNOTATION_MATCH) };
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new
                ExperimentTaskConfiguration(
                        new SpotlightAnnotatorConfig(wikiAPI, dbpApi), new AQUAINTDatasetConfiguration(
                                wikiAPI), ExperimentType.D2W, Matching.STRONG_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleLoggingDAO4Debugging(), taskConfigs,
                "SPOTLIGHT_TEST");
        experimenter.run();
    }
}

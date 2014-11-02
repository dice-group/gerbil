package org.aksw.gerbil;

import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfigs;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfigs.NIFDatasets;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.junit.Ignore;

@Ignore
public class BabelfyTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new ExperimentTaskConfiguration(
                new BabelfyAnnotatorConfig(), KnownNIFFileDatasetConfigs.getDatasetConfig(
                        SingletonWikipediaApi.getInstance(), new DBPediaApi(), NIFDatasets.KORE50), ExperimentType.D2W,
                Matching.STRONG_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleLoggingDAO4Debugging(), taskConfigs,
                "BABELFY_TEST");
        experimenter.run();
    }
}

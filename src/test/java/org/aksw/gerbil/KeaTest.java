package org.aksw.gerbil;

import it.acubelab.batframework.systemPlugins.DBPediaApi;
import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.annotators.KeaAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.junit.Ignore;

@Ignore
public class KeaTest {

    public static void main(String[] args) throws FileNotFoundException,
            IOException, ClassNotFoundException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new ExperimentTaskConfiguration(
                new KeaAnnotatorConfig(SingletonWikipediaApi.getInstance(), new DBPediaApi()),
                new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), new DBPediaApi(), NIFDatasets.KORE50),
                ExperimentType.Sa2W, Matching.STRONG_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI,
                new SimpleLoggingDAO4Debugging(), taskConfigs, "AGDISTIS_TEST");
        experimenter.run();
    }
}

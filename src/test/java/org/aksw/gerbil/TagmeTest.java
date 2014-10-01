package org.aksw.gerbil;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.aksw.gerbil.annotators.TagMeAnnotatorConfig;
import org.aksw.gerbil.database.SimpleLoggingDAO4Debugging;
import org.aksw.gerbil.datasets.IITBDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.aksw.gerbil.utils.SingletonWikipediaApi;
import org.junit.Ignore;

@Ignore
public class TagmeTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        WikipediaApiInterface wikiAPI = SingletonWikipediaApi.getInstance();
        ExperimentTaskConfiguration taskConfigs[] = new ExperimentTaskConfiguration[] { new ExperimentTaskConfiguration(
                new TagMeAnnotatorConfig(), new IITBDatasetConfig(wikiAPI), ExperimentType.D2W,
                Matching.STRONG_ANNOTATION_MATCH) };
        Experimenter experimenter = new Experimenter(wikiAPI, new SimpleLoggingDAO4Debugging(), taskConfigs,
                "TAGME_TEST");
        experimenter.run();
    }
}

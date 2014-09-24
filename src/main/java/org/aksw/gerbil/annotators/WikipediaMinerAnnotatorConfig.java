package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.WikipediaMinerAnnotator;

import org.aksw.gerbil.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class WikipediaMinerAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private static final String WIKI_MINER_CONFIG_FILE_PROPERTY_NAME = "org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig.ConfigFile";

    public WikipediaMinerAnnotatorConfig() {
        super("WikipediaMiner", true, new ExperimentType[] { ExperimentType.D2W });
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new WikipediaMinerAnnotator(GerbilConfiguration.getInstance().getString(WIKI_MINER_CONFIG_FILE_PROPERTY_NAME));
    }

}

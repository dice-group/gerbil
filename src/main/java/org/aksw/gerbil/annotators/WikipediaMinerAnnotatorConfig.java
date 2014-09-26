package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.WikipediaMinerAnnotator;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

public class WikipediaMinerAnnotatorConfig extends AbstractAnnotatorConfiguration {
    
    public static final String ANNOTATOR_NAME = "Wikipedia Miner";

    private static final String WIKI_MINER_CONFIG_FILE_PROPERTY_NAME = "org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig.ConfigFile";

    public WikipediaMinerAnnotatorConfig() {
        super(ANNOTATOR_NAME, true, new ExperimentType[] { ExperimentType.D2W });
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new WikipediaMinerAnnotator(GerbilConfiguration.getInstance().getString(WIKI_MINER_CONFIG_FILE_PROPERTY_NAME));
    }

}

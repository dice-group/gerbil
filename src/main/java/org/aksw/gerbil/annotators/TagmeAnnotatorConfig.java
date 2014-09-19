package org.aksw.gerbil.annotators;

import it.acubelab.batframework.problems.TopicSystem;
import it.acubelab.batframework.systemPlugins.TagmeAnnotator;

import org.aksw.gerbil.GerbilProperties;
import org.aksw.gerbil.datatypes.ExperimentType;

public class TagmeAnnotatorConfig extends AbstractAnnotatorConfiguration {

    private static final String TAGME_CONFIG_FILE_PROPERTY_NAME = "org.aksw.gerbil.annotators.TagmeAnnotatorConfig.ConfigFile";

    public TagmeAnnotatorConfig(String annotatorName, boolean couldBeCached, ExperimentType[] applicableForExperiment) {
        super("Tagme", true, new ExperimentType[] { ExperimentType.D2W });
    }

    @Override
    protected TopicSystem loadAnnotator() throws Exception {
        return new TagmeAnnotator(GerbilProperties.getPropertyValue(TAGME_CONFIG_FILE_PROPERTY_NAME));
    }

}

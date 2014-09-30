package org.aksw.gerbil.utils;

import it.acubelab.batframework.systemPlugins.DBPediaApi;

import org.aksw.gerbil.annotators.AgdistisAnnotatorConfig;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.annotators.NIFWebserviceAnnotatorConfiguration;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.annotators.TagMeAnnotatorConfig;
import org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig;
import org.aksw.gerbil.datatypes.ExperimentType;

public class Name2AnnotatorMapping {

    // public static final String ANNOTATOR_NAMES[] = new String[] {
    // BabelfyAnnotatorConfig.ANNOTATOR_NAME,
    // NIFWebserviceAnnotatorConfiguration.ANNOTATOR_NAME,
    // SpotlightAnnotatorConfig.ANNOTATOR_NAME,
    // TagMeAnnotatorConfig.ANNOTATOR_NAME,
    // WikipediaMinerAnnotatorConfig.ANNOTATOR_NAME };

    public static AnnotatorConfiguration getAnnotatorConfig(String name) {
        switch (name) {
        case BabelfyAnnotatorConfig.ANNOTATOR_NAME:
            return new BabelfyAnnotatorConfig();
        case NIFWebserviceAnnotatorConfiguration.ANNOTATOR_NAME:
            return new NIFWebserviceAnnotatorConfiguration(null, name, false, SingletonWikipediaApi.getInstance(),
                    new DBPediaApi(), ExperimentType.Sa2W);
        case SpotlightAnnotatorConfig.ANNOTATOR_NAME:
            return new SpotlightAnnotatorConfig(
                    SingletonWikipediaApi.getInstance(), new DBPediaApi());
        case TagMeAnnotatorConfig.ANNOTATOR_NAME:
            return new TagMeAnnotatorConfig();
        case WikipediaMinerAnnotatorConfig.ANNOTATOR_NAME:
            return new WikipediaMinerAnnotatorConfig();
        case AgdistisAnnotatorConfig.ANNOTATOR_NAME:
            return new AgdistisAnnotatorConfig(
                    SingletonWikipediaApi.getInstance());
        default:
            return null;
        }
    }
}

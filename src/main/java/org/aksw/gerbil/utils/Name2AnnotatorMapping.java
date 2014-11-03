package org.aksw.gerbil.utils;

import it.acubelab.batframework.systemPlugins.DBPediaApi;

import org.aksw.gerbil.annotators.AgdistisAnnotatorConfig;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.annotators.FOXAnnotatorConfig;
import org.aksw.gerbil.annotators.NERDAnnotatorConfig;
import org.aksw.gerbil.annotators.NIFWebserviceAnnotatorConfiguration;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.annotators.TagMeAnnotatorConfig;
import org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig;
import org.aksw.gerbil.bat.annotator.FOXAnnotator;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Name2AnnotatorMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(Name2AnnotatorMapping.class);

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
            // case NIFWebserviceAnnotatorConfiguration.ANNOTATOR_NAME:
            // return new NIFWebserviceAnnotatorConfiguration(null, name, false,
            // SingletonWikipediaApi.getInstance(),
            // new DBPediaApi(), ExperimentType.Sa2W);
        case SpotlightAnnotatorConfig.ANNOTATOR_NAME:
            return new SpotlightAnnotatorConfig(
                    SingletonWikipediaApi.getInstance(), new DBPediaApi());
        case TagMeAnnotatorConfig.ANNOTATOR_NAME:
            return new TagMeAnnotatorConfig();
        case WATAnnotatorConfig.ANNOTATOR_NAME:
            return new WATAnnotatorConfig();
        case WikipediaMinerAnnotatorConfig.ANNOTATOR_NAME:
            return new WikipediaMinerAnnotatorConfig();
        case AgdistisAnnotatorConfig.ANNOTATOR_NAME:
            return new AgdistisAnnotatorConfig(SingletonWikipediaApi.getInstance());
        case NERDAnnotatorConfig.ANNOTATOR_NAME:
            return new NERDAnnotatorConfig();
        case FOXAnnotator.NAME:
            return new FOXAnnotatorConfig(SingletonWikipediaApi.getInstance());
        }
        if (name.startsWith("NIFWS_")) {
            // This describes a NIF based web service
            // The name should have the form "NIFWS_name(uri)"
            int pos = name.indexOf('(');
            if (pos < 0) {
                LOGGER.error("Couldn't parse the definition of this NIF based web service \"" + name
                        + "\". Returning null.");
                return null;
            }
            String uri = name.substring(pos + 1, name.length() - 1);
            // remove "NIFWS_" from the name
            name = name.substring(6, pos);
            return new NIFWebserviceAnnotatorConfiguration(uri, name, false, SingletonWikipediaApi.getInstance(),
                    new DBPediaApi(), ExperimentType.Sa2W);
        }
        LOGGER.error("Got an unknown annotator name\"" + name + "\". Returning null.");
        return null;
    }
}

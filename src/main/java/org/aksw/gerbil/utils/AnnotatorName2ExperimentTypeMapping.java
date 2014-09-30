package org.aksw.gerbil.utils;

import java.util.HashMap;
import java.util.Map;

import org.aksw.gerbil.annotators.AgdistisAnnotatorConfig;
import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.annotators.NIFWebserviceAnnotatorConfiguration;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.annotators.TagMeAnnotatorConfig;
import org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig;
import org.aksw.gerbil.datatypes.ExperimentType;

public class AnnotatorName2ExperimentTypeMapping {

    private final static Map<String, ExperimentType> mapping = new HashMap<String, ExperimentType>();

    static {
        mapping.put(BabelfyAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
        mapping.put(NIFWebserviceAnnotatorConfiguration.ANNOTATOR_NAME, ExperimentType.Sa2W);
        mapping.put(SpotlightAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
        mapping.put(TagMeAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
        mapping.put(WikipediaMinerAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
        mapping.put(AgdistisAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
    }

    public ExperimentType getExperimentType(String name) {
        if (mapping.containsKey(name)) {
            return mapping.get(name);
        } else {
            return null;
        }
    }
}

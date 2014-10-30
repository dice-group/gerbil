package org.aksw.gerbil.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.annotators.AgdistisAnnotatorConfig;
import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.annotators.TagMeAnnotatorConfig;
import org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig;
import org.aksw.gerbil.datatypes.ExperimentType;

public class AnnotatorName2ExperimentTypeMapping {

    private static AnnotatorName2ExperimentTypeMapping instance = null;

    private synchronized static AnnotatorName2ExperimentTypeMapping getInstance() {
        if (instance == null) {
            Map<String, ExperimentType> mapping = new HashMap<String, ExperimentType>();
            mapping.put(BabelfyAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
            // mapping.put(NIFWebserviceAnnotatorConfiguration.ANNOTATOR_NAME, ExperimentType.Sa2W);
            mapping.put(SpotlightAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
            mapping.put(TagMeAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
            mapping.put(WikipediaMinerAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
            mapping.put(AgdistisAnnotatorConfig.ANNOTATOR_NAME, ExperimentType.Sa2W);
            instance = new AnnotatorName2ExperimentTypeMapping(mapping);
        }
        return instance;
    }

    public static Set<String> getAnnotatorsForExperimentType(ExperimentType type) {
        AnnotatorName2ExperimentTypeMapping anno2ExpType = getInstance();
        ExperimentType annotatorType;
        Set<String> names = new HashSet<String>();
        for (String annotatorName : anno2ExpType.mapping.keySet()) {
            annotatorType = anno2ExpType.mapping.get(annotatorName);
            if (annotatorType.equalsOrContainsType(type)) {
                names.add(annotatorName);
            }
        }
        return names;
    }

    private final Map<String, ExperimentType> mapping;

    private AnnotatorName2ExperimentTypeMapping(Map<String, ExperimentType> mapping) {
        this.mapping = mapping;
    }

    public static ExperimentType getExperimentType(String name) {
        return getInstance().getExperimentTypeOfAnnotator(name);
    }

    public ExperimentType getExperimentTypeOfAnnotator(String name) {
        if (mapping.containsKey(name)) {
            return mapping.get(name);
        } else {
            return null;
        }
    }
}

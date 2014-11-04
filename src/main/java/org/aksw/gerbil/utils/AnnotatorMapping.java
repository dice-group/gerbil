package org.aksw.gerbil.utils;

import it.acubelab.batframework.systemPlugins.DBPediaApi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.annotators.AgdistisAnnotatorConfig;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.annotators.FOXAnnotatorConfig;
import org.aksw.gerbil.annotators.KeaAnnotatorConfig;
import org.aksw.gerbil.annotators.NERDAnnotatorConfig;
import org.aksw.gerbil.annotators.NIFWebserviceAnnotatorConfiguration;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.annotators.TagMeAnnotatorConfig;
import org.aksw.gerbil.annotators.WATAnnotatorConfig;
import org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig;
import org.aksw.gerbil.bat.annotator.FOXAnnotator;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatorMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatorMapping.class);

    private static AnnotatorMapping instance = null;

    private synchronized static AnnotatorMapping getInstance() {
        if (instance == null) {
            Map<String, AnnotatorConfiguration> mapping = new HashMap<String, AnnotatorConfiguration>();
            mapping.put(BabelfyAnnotatorConfig.ANNOTATOR_NAME,
                    new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()));
            mapping.put(SpotlightAnnotatorConfig.ANNOTATOR_NAME, new SpotlightAnnotatorConfig(
                    SingletonWikipediaApi.getInstance(), new DBPediaApi()));
            mapping.put(TagMeAnnotatorConfig.ANNOTATOR_NAME, new TagMeAnnotatorConfig());
            mapping.put(WATAnnotatorConfig.ANNOTATOR_NAME, new WATAnnotatorConfig());
            mapping.put(WikipediaMinerAnnotatorConfig.ANNOTATOR_NAME, new WikipediaMinerAnnotatorConfig());
            mapping.put(AgdistisAnnotatorConfig.ANNOTATOR_NAME,
                    new AgdistisAnnotatorConfig(SingletonWikipediaApi.getInstance()));
            mapping.put(NERDAnnotatorConfig.ANNOTATOR_NAME,
                    new NERDAnnotatorConfig(SingletonWikipediaApi.getInstance()));
            mapping.put(FOXAnnotator.NAME, new FOXAnnotatorConfig(SingletonWikipediaApi.getInstance()));
            mapping.put(KeaAnnotatorConfig.ANNOTATOR_NAME, new KeaAnnotatorConfig(SingletonWikipediaApi.getInstance(),
                    new DBPediaApi()));

            instance = new AnnotatorMapping(mapping);
        }
        return instance;
    }

    public static AnnotatorConfiguration getAnnotatorConfig(String name) {
        AnnotatorMapping annotators = getInstance();
        if (annotators.mapping.containsKey(name)) {
            return annotators.mapping.get(name);
        } else {
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

    public static Set<String> getAnnotatorsForExperimentType(ExperimentType type) {
        AnnotatorMapping annotators = getInstance();
        Set<String> names = new HashSet<String>();
        for (String datasetName : annotators.mapping.keySet()) {
            if (annotators.mapping.get(datasetName).isApplicableForExperiment(type)) {
                names.add(datasetName);
            }
        }
        return names;
    }

    private final Map<String, AnnotatorConfiguration> mapping;

    private AnnotatorMapping(Map<String, AnnotatorConfiguration> mapping) {
        this.mapping = mapping;
    }
}

/**
 * The MIT License
 * Copyright (c) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.utils;

import it.acubelab.batframework.systemPlugins.DBPediaApi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.annotators.AgdistisAnnotatorConfig;
import org.aksw.gerbil.annotators.AnnotatorConfiguration;
import org.aksw.gerbil.annotators.BabelfyAnnotatorConfig;
import org.aksw.gerbil.annotators.DexterAnnotatorConfig;
import org.aksw.gerbil.annotators.EntityclassifierEUConfig;
import org.aksw.gerbil.annotators.KeaAnnotatorConfig;
import org.aksw.gerbil.annotators.NERDAnnotatorConfig;
import org.aksw.gerbil.annotators.NIFWebserviceAnnotatorConfiguration;
import org.aksw.gerbil.annotators.SpotlightAnnotatorConfig;
import org.aksw.gerbil.annotators.TagMeAnnotatorConfig;
import org.aksw.gerbil.annotators.WATAnnotatorConfig;
import org.aksw.gerbil.annotators.WikipediaMinerAnnotatorConfig;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a very ugly workaround performing the mapping from annotator
 * names to {@link AnnotatorConfiguration} objects and from an
 * {@link ExperimentType} to a list of {@link AnnotatorConfiguration}s that are
 * usable for this {@link ExperimentType}.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
public class AnnotatorMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatorMapping.class);

    private static final String NIF_WS_SUFFIX = "(NIF WS)";

    private static AnnotatorMapping instance = null;

    private synchronized static AnnotatorMapping getInstance() {
        if (instance == null) {
            Map<String, AnnotatorConfiguration> mapping = new HashMap<String, AnnotatorConfiguration>();
            mapping.put(BabelfyAnnotatorConfig.ANNOTATOR_NAME,
                    new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()));
            mapping.put(DexterAnnotatorConfig.ANNOTATOR_NAME,
                    new DexterAnnotatorConfig(SingletonWikipediaApi.getInstance(), new DBPediaApi()));
            mapping.put(SpotlightAnnotatorConfig.ANNOTATOR_NAME,
                    new SpotlightAnnotatorConfig(SingletonWikipediaApi.getInstance(), new DBPediaApi()));
            mapping.put(TagMeAnnotatorConfig.ANNOTATOR_NAME, new TagMeAnnotatorConfig());
            mapping.put(WATAnnotatorConfig.ANNOTATOR_NAME, new WATAnnotatorConfig());
            mapping.put(WikipediaMinerAnnotatorConfig.ANNOTATOR_NAME, new WikipediaMinerAnnotatorConfig());
            mapping.put(AgdistisAnnotatorConfig.ANNOTATOR_NAME,
                    new AgdistisAnnotatorConfig(SingletonWikipediaApi.getInstance()));
            mapping.put(NERDAnnotatorConfig.ANNOTATOR_NAME,
                    new NERDAnnotatorConfig(SingletonWikipediaApi.getInstance()));
            mapping.put(EntityclassifierEUConfig.ANNOTATOR_NAME,
                    new EntityclassifierEUConfig(SingletonWikipediaApi.getInstance(), new DBPediaApi()));
            // mapping.put(FOXAnnotator.NAME, new
            // FOXAnnotatorConfig(SingletonWikipediaApi.getInstance()));
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
                name = name.substring(6, pos) + NIF_WS_SUFFIX;
                return new NIFWebserviceAnnotatorConfiguration(uri, name, false, SingletonWikipediaApi.getInstance(),
                        new DBPediaApi(), ExperimentType.Sa2KB);
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

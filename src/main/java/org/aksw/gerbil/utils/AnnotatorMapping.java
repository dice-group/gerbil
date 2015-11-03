/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.utils;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;

/**
 * This class is a very ugly workaround performing the mapping from annotator
 * names to {@link AnnotatorConfiguration} objects and from an
 * {@link ExperimentType} to a list of {@link AnnotatorConfiguration}s that are
 * usable for this {@link ExperimentType}.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 */
@Deprecated
public class AnnotatorMapping {

//     private static final Logger LOGGER =
//     LoggerFactory.getLogger(AnnotatorMapping.class);
    //
    // private static final String NIF_WS_SUFFIX = " (NIF WS)";
    //
    // private static AnnotatorMapping instance = null;
    //
    // private synchronized static AnnotatorMapping getInstance() {
    // if (instance == null) {
    // Map<String, AnnotatorConfiguration> mapping = new HashMap<String,
    // AnnotatorConfiguration>();
    // // mapping.put(BabelfyAnnotatorConfig.ANNOTATOR_NAME,
    // // new BabelfyAnnotatorConfig(SingletonWikipediaApi.getInstance()));
    // mapping.put(DexterAnnotatorConfig.ANNOTATOR_NAME, new
    // DexterAnnotatorConfig());
    // mapping.put(SpotlightAnnotator.ANNOTATOR_NAME, new
    // SpotlightAnnotatorConfig());
    // // mapping.put(TagMeAnnotatorConfig.ANNOTATOR_NAME, new
    // // TagMeAnnotatorConfig());
    // // mapping.put(WATAnnotatorConfig.ANNOTATOR_NAME, new
    // // WATAnnotatorConfig());
    // // mapping.put(WikipediaMinerAnnotatorConfig.ANNOTATOR_NAME, new
    // // WikipediaMinerAnnotatorConfig());
    // mapping.put(
    // AgdistisAnnotatorConfig.ANNOTATOR_NAME,
    // new AgdistisAnnotatorConfig(SingletonWikipediaApi.getInstance()));
    // // mapping.put(NERDAnnotatorConfig.ANNOTATOR_NAME,
    // // new NERDAnnotatorConfig(SingletonWikipediaApi.getInstance()));
    // // mapping.put(FOXAnnotator.NAME, new
    // // FOXAnnotatorConfig(SingletonWikipediaApi.getInstance()));
    // mapping.put(FOXAnnotator.NAME, new FOXAnnotatorConfig());
    // mapping.put(KeaAnnotatorConfig.ANNOTATOR_NAME, new KeaAnnotatorConfig());
    //
    // instance = new AnnotatorMapping(mapping);
    // }
    // return instance;
    // }
    //
//    public static AnnotatorConfiguration getAnnotatorConfig(String name) {
//        AnnotatorMapping annotators = getInstance();
//        if (annotators.mapping.containsKey(name)) {
//            return annotators.mapping.get(name);
//        } else {
//            if (name.startsWith("NIFWS_")) {
//                // This describes a NIF based web service
//                // The name should have the form "NIFWS_name(uri)"
//                int pos = name.indexOf('(');
//                if (pos < 0) {
//                    LOGGER.error("Couldn't parse the definition of this NIF based web service \"" + name
//                            + "\". Returning null.");
//                    return null;
//                }
//                String uri = name.substring(pos + 1, name.length() - 1);
//                // remove "NIFWS_" from the name
//                name = name.substring(6, pos) + NIF_WS_SUFFIX;
//                return new NIFWebserviceAnnotatorConfiguration(uri, name, false, ExperimentType.OKE_Task1,
//                        ExperimentType.OKE_Task2);
//            }
//            LOGGER.error("Got an unknown annotator name\"" + name + "\". Returning null.");
//            return null;
//        }
//    }
    //
    // public static Set<String> getAnnotatorsForExperimentType(ExperimentType
    // type) {
    // AnnotatorMapping annotators = getInstance();
    // Set<String> names = new HashSet<String>();
    // for (String datasetName : annotators.mapping.keySet()) {
    // if (annotators.mapping.get(datasetName).isApplicableForExperiment(type))
    // {
    // names.add(datasetName);
    // }
    // }
    // return names;
    // }
    //
    // private final Map<String, AnnotatorConfiguration> mapping;
    //
    // private AnnotatorMapping(Map<String, AnnotatorConfiguration> mapping) {
    // this.mapping = mapping;
    // }
}

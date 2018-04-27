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
package org.aksw.gerbil.web.config;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotator.Annotator;
import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.annotator.InstanceListBasedConfigurationImpl;
import org.aksw.gerbil.annotator.RDFWebServiceConfiguration;
import org.aksw.gerbil.annotator.impl.sw.RDFWebServiceSystem;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.dataset.impl.sw.RDFFileDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AdapterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterManager.class);

    private static final String NIF_WS_PREFIX = "NIFWS_";
    private static final String NIF_WS_SUFFIX = " (WS)";
    private static final String AF_PREFIX = "AF_";
    private static final String UPLOADED_AF_SUFFIX = " (uploaded)";
    private static final String UPLOADED_FILES_PATH_PROPERTY_KEY = "org.aksw.gerbil.UploadPath";
    private static final String UPLOADED_DATASET_SUFFIX = " (uploaded)";
    private static final String UPLOADED_DATASET_PREFIX = "NIFDS_";

	private static final String RDF_PREFIX = "RDFDS_";

	private static final String RDF_WS_PREFIX = "RDFWS_";

    @Autowired
    @Qualifier("annotators")
    private AdapterList<AnnotatorConfiguration> annotators;

    @Autowired
    @Qualifier("datasets")
    private AdapterList<DatasetConfiguration> datasets;

    @Autowired
    private EntityCheckerManager entityCheckerManager;

    @Autowired
    private SameAsRetriever globalRetriever;

    public Set<String> getAnnotatorNamesForExperiment(ExperimentType type) {
        return annotators.getAdapterNamesForExperiment(type);
    }

    public Set<String> getDatasetNamesForExperiment(ExperimentType type) {
        return datasets.getAdapterNamesForExperiment(type);
    }

    public AnnotatorConfiguration getAnnotatorConfig(String name, ExperimentType type) {
        List<AnnotatorConfiguration> configs = annotators.getAdaptersForName(name);
        if (configs != null) {
            for (AnnotatorConfiguration config : configs) {
                if (config.isApplicableForExperiment(type)) {
                    return config;
                }
            }
            LOGGER.error("Couldn't find an annotator with the name \"" + name
                    + "\" that is applicable for the experiment \"" + type + "\". Returning null.");
        } else {
            if (name.startsWith(NIF_WS_PREFIX)) {
                // This describes a NIF based web service
                // The name should have the form NIF_WS_PREFIX + "name(uri)"
                int pos = name.lastIndexOf('(');
                if (pos < 0) {
                    LOGGER.error("Couldn't parse the definition of this NIF based web service \"" + name
                            + "\". Returning null.");
                    return null;
                }
                //String uri = name.substring(pos + 1, name.length() - 1);
                // remove "NIFWS_" from the name
                name = name.substring(NIF_WS_PREFIX.length(), pos) + NIF_WS_SUFFIX;
                try {
                	throw new Exception("Currently not supported");
                } catch (Exception e) {
                    LOGGER.error(
                            "Exception while trying to create an annotator configuration for a NIF based webservice. Returning null.",
                            e);
                    return null;
                }
                // return new NIFWebserviceAnnotatorConfiguration(uri, name,
                // false, type);
            }
            if(name.startsWith(RDF_WS_PREFIX)){
            	int pos = name.lastIndexOf('(');
                if (pos < 0) {
                    LOGGER.error("Couldn't parse the definition of this NIF based web service \"" + name
                            + "\". Returning null.");
                    return null;
                }
                //String uri = name.substring(pos + 1, name.length() - 1);
                // remove "NIFWS_" from the name
                String uri = name.substring(pos + 1, name.length() - 1);
                name = name.substring(RDF_WS_PREFIX.length(), pos) + NIF_WS_SUFFIX;
                return new RDFWebServiceConfiguration(name, false, type, uri);
            }
            if (name.startsWith(AF_PREFIX)) {
                String uploadedFilesPath = GerbilConfiguration.getInstance()
                        .getString(UPLOADED_FILES_PATH_PROPERTY_KEY);
                if (uploadedFilesPath == null) {
                    LOGGER.error(
                            "Couldn't process uploaded file request, because the upload path is not set (\"{}\"). Returning null.",
                            UPLOADED_FILES_PATH_PROPERTY_KEY);
                    return null;
                }
                // "name (email)(file)"
                String email = name.substring(name.indexOf('(')+1, name.indexOf(')'));
                String fileName = uploadedFilesPath+File.separator+name.substring(name.lastIndexOf('(')+1, name.lastIndexOf(')'));
                // remove "AF_" from the name
                name = name.substring(AF_PREFIX.length(), name.indexOf('('));
                File2SystemEntry fileMapping = new File2SystemEntry(fileName, name, email,-1);
                name += "("+email+")";
                try {
                	
                    AnnotatorConfiguration system =  new InstanceListBasedConfigurationImpl(name, false,
                    		new RDFFileDatasetConfig(name, fileName, false, type, null, null),
                            type);
                    system.setFileMapping(fileMapping);
                    return system;
                } catch (Exception e) {
                    LOGGER.error(
                            "Exception while trying to create an annotator configuration for a uploaded SWC answer file. Returning null.",
                            e);
                    return null;
                }
            }
            LOGGER.error(",Got an unknown annotator name \"" + name + "\". Returning null.");
        }
        return null;
    }

    public DatasetConfiguration getDatasetConfig(String name, ExperimentType type) {
        List<DatasetConfiguration> configs = datasets.getAdaptersForName(name);
        if (configs != null) {
            for (DatasetConfiguration config : configs) {
                if (config.isApplicableForExperiment(type)) {
                    return config;
                }
            }
        } else {
            if (name.startsWith(UPLOADED_DATASET_PREFIX) || name.startsWith(RDF_PREFIX)) {
                String uploadedFilesPath = GerbilConfiguration.getInstance()
                        .getString(UPLOADED_FILES_PATH_PROPERTY_KEY);
                if (uploadedFilesPath == null) {
                    LOGGER.error(
                            "Couldn't process uploaded file request, because the upload path is not set (\"{}\"). Returning null.",
                            UPLOADED_FILES_PATH_PROPERTY_KEY);
                    return null;
                }
                // This describes a NIF based web service
                // The name should have the form "NIFDS_name(uri)"
                int brackets[] = getLastBracketsContent(name, name.length() - 1);
                if (brackets == null) {
                    LOGGER.error("Couldn't parse the uploaded NIF file name from \"" + name + "\". Returning null.");
                    return null;
                }
                String uri = uploadedFilesPath + name.substring(brackets[0] + 1, brackets[1]);
                // remove dataset prefix from the name
                int len = RDF_PREFIX.length();
                if(name.startsWith(UPLOADED_DATASET_PREFIX)) {
                	len = UPLOADED_DATASET_PREFIX.length();
                }
                name = name.substring(len, brackets[0]) + UPLOADED_DATASET_SUFFIX;
                return new RDFFileDatasetConfig(name, uri, false, type, entityCheckerManager, globalRetriever);
            }
            if (name.startsWith(AF_PREFIX)) {
                // This describes a QA answer file
                // The name should have the form NIF_WS_PREFIX +
                // "name(file)(type)(dataset)"
                int brackets[] = getLastBracketsContent(name, name.length() - 1);
                if (brackets == null) {
                    LOGGER.error(
                            "Couldn't parse the definition of this QA answer file \"" + name + "\". Returning null.");
                    return null;
                }
                String datasetName = name.substring(brackets[0] + 1, brackets[1]);
                return getDatasetConfig(datasetName, type);
            }

        }
        LOGGER.error("Got an unknown annotator name\"" + name + "\". Returning null.");
        return null;
    }

    public AdapterList<AnnotatorConfiguration> getAnnotators() {
        return annotators;
    }

    public void setAnnotators(AdapterList<AnnotatorConfiguration> annotators) {
        this.annotators = annotators;
    }

    public AdapterList<DatasetConfiguration> getDatasets() {
        return datasets;
    }

    public void setDatasets(AdapterList<DatasetConfiguration> datasets) {
        this.datasets = datasets;
    }

    /**
     * Returns the positions of the last bracket pair searching backwards from
     * the given position or null if no pair could be found
     * 
     * @return
     */
    protected static int[] getLastBracketsContent(String input, int pos) {
        int endPos = input.lastIndexOf(')', pos);
        if (endPos < 0) {
            return null;
        }
        int startPos = input.lastIndexOf('(', endPos);
        if (startPos < 0) {
            return null;
        }
        return new int[] { startPos, endPos };
    }

public EntityCheckerManager getEntityCheckerManager() {
        return entityCheckerManager;
    }

    public void setEntityCheckerManager(EntityCheckerManager entityCheckerManager) {
        this.entityCheckerManager = entityCheckerManager;
    }

    public SameAsRetriever getGlobalRetriever() {
        return globalRetriever;
    }

    public void setGlobalRetriever(SameAsRetriever globalRetriever) {
        this.globalRetriever = globalRetriever;
    }
}

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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotator.AnnotatorConfiguration;
import org.aksw.gerbil.annotator.AnnotatorConfigurationImpl;
import org.aksw.gerbil.annotator.impl.instance.InstanceListBasedAnnotator;
import org.aksw.gerbil.annotator.impl.nif.NIFBasedAnnotatorWebservice;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.data.SimpleFileRef;
import org.aksw.gerbil.dataset.DatasetConfiguration;
import org.aksw.gerbil.dataset.InstanceListBasedDataset;
import org.aksw.gerbil.dataset.check.EntityCheckerManager;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.semantic.sameas.SameAsRetriever;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AdapterManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterManager.class);

    private static final String NIF_WS_PREFIX = "NIFWS_";
    private static final String NIF_WS_SUFFIX = " (NIF WS)";
    private static final String UPLOADED_FILES_PATH_PROPERTY_KEY = "org.aksw.gerbil.UploadPath";
    private static final String UPLOADED_DATASET_SUFFIX = " (uploaded)";
    private static final String UPLOADED_DATASET_PREFIX = "NIFDS_";
    private static final String HYPOTHESIS_PREFIX = "HF_";
    private static final String UPLOADED_HYPOTHESIS_SUFFIX = " (uploaded)";
    private static final String CANDIDATE_PREFIX = "CF_";
    private static final String UPLOADED_CANDIDATE_SUFFIX = " (uploaded)";

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

    public AnnotatorConfiguration getAnnotatorConfig(String name, ExperimentType type, String language) {
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
                String uri = name.substring(pos + 1, name.length() - 1);
                // remove "NIFWS_" from the name
                name = name.substring(NIF_WS_PREFIX.length(), pos) + NIF_WS_SUFFIX;
                try {
                    return new AnnotatorConfigurationImpl(name, false,
                            NIFBasedAnnotatorWebservice.class.getConstructor(String.class), new Object[] { uri }, type);
                } catch (Exception e) {
                    LOGGER.error(
                            "Exception while trying to create an annotator configuration for a NIF based webservice. Returning null.",
                            e);
                    return null;
                }
                // return new NIFWebserviceAnnotatorConfiguration(uri, name,
                // false, type);
            }
            if (name.startsWith(HYPOTHESIS_PREFIX)) {
                String uploadedFilesPath = GerbilConfiguration.getInstance()
                        .getString(UPLOADED_FILES_PATH_PROPERTY_KEY);
                if (uploadedFilesPath == null) {
                    LOGGER.error(
                            "Couldn't process uploaded file request, because the upload path is not set (\"{}\"). Returning null.",
                            UPLOADED_FILES_PATH_PROPERTY_KEY);
                    return null;
                }
                // This describes a hypothesis file
                // "name(file)(type)(dataset)"
                int brackets[] = getLastBracketsContent(name, name.length() - 1);
                if (brackets == null) {
                    LOGGER.error(
                            "Couldn't parse the definition of this hypothesis file \"" + name + "\". Returning null.");
                    return null;
                }
//                String datasetName = name.substring(brackets[0] + 1, brackets[1]);

                String fileName = name.substring(brackets[0] + 1, brackets[1]);
                File file = new File(uploadedFilesPath + fileName);
                name = name.substring(HYPOTHESIS_PREFIX.length(), brackets[0]) + UPLOADED_HYPOTHESIS_SUFFIX;
                try {
                    return new AnnotatorConfigurationImpl(name, false,
                            InstanceListBasedAnnotator.class.getConstructor(String.class, List.class),
                            new Object[] { name,
                                    Arrays.asList(new DocumentImpl(fileName, file.getAbsoluteFile().toURI().toString(),
                                            Arrays.asList((Marking) new SimpleFileRef(file)))) },
                            type);
                } catch (Exception e) {
                    LOGGER.error(
                            "Error while trying to create annotator configuration \"" + name + "\". Returning null.",
                            e);
                    return null;
                }
            }
            if (name.startsWith(CANDIDATE_PREFIX)) {
                String uploadedFilesPath = GerbilConfiguration.getInstance()
                        .getString(UPLOADED_FILES_PATH_PROPERTY_KEY);
                if (uploadedFilesPath == null) {
                    LOGGER.error(
                            "Couldn't process uploaded file request, because the upload path is not set (\"{}\"). Returning null.",
                            UPLOADED_FILES_PATH_PROPERTY_KEY);
                    return null;
                }
                // This describes a hypothesis file
                // "name(file)(type)(dataset)"
                int brackets[] = getLastBracketsContent(name, name.length() - 1);
                if (brackets == null) {
                    LOGGER.error(
                            "Couldn't parse the definition of this candidate file \"" + name + "\". Returning null.");
                    return null;
                }
//                String datasetName = name.substring(brackets[0] + 1, brackets[1]);

                String fileName = name.substring(brackets[0] + 1, brackets[1]);
                File file = new File(uploadedFilesPath + fileName);
                name = name.substring(CANDIDATE_PREFIX.length(), brackets[0]) + UPLOADED_CANDIDATE_SUFFIX;
                try {
                    return new AnnotatorConfigurationImpl(name, false,
                            InstanceListBasedAnnotator.class.getConstructor(String.class, List.class),
                            new Object[] { name,
                                    Arrays.asList(new DocumentImpl(fileName, file.getAbsoluteFile().toURI().toString(),
                                            Arrays.asList((Marking) new SimpleFileRef(file)))) },
                            type);
                } catch (Exception e) {
                    LOGGER.error(
                            "Error while trying to create annotator configuration \"" + name + "\". Returning null.",
                            e);
                    return null;
                }
            }
            LOGGER.error("Got an unknown annotator name \"" + name + "\". Returning null.");
        }
        return null;
    }

    public DatasetConfiguration getDatasetConfig(String name, ExperimentType type, String language) {
        List<DatasetConfiguration> configs = datasets.getAdaptersForName(name);
        if (configs != null) {
            for (DatasetConfiguration config : configs) {
                if (config.isApplicableForExperiment(type)) {
                    return config;
                }
            }
        } else {
            if (name.startsWith(UPLOADED_DATASET_PREFIX)) {
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
                int pos = name.lastIndexOf('(');
                if (pos < 0) {
                    LOGGER.error("Couldn't parse the definition of this NIF based web service \"" + name
                            + "\". Returning null.");
                    return null;
                }
                String fileName = uploadedFilesPath + name.substring(pos + 1, name.length() - 1);
                File file = new File(fileName);
                // remove dataset prefix from the name
                name = name.substring(UPLOADED_DATASET_PREFIX.length(), pos) + UPLOADED_DATASET_SUFFIX;
                // return new NIFFileDatasetConfig(name, uri, false, type, entityCheckerManager,
                // globalRetriever);
                return new InstanceListBasedDataset(name, Arrays.asList(new DocumentImpl(fileName,
                        file.getAbsoluteFile().toURI().toString(), Arrays.asList((Marking) new SimpleFileRef(file)))),
                        type, language);
            }
            LOGGER.error("Got an unknown annotator name\"" + name + "\". Returning null.");
            return null;
        }
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

    public EntityCheckerManager getEntityCheckerManager() {
        return entityCheckerManager;
    }

    public void setEntityCheckerManager(EntityCheckerManager entityCheckerManager) {
        this.entityCheckerManager = entityCheckerManager;
    }

    /**
     * Returns the positions of the last bracket pair searching backwards from the
     * given position or null if no pair could be found
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

    public SameAsRetriever getGlobalRetriever() {
        return globalRetriever;
    }

    public void setGlobalRetriever(SameAsRetriever globalRetriever) {
        this.globalRetriever = globalRetriever;
    }

}

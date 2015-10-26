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


import it.unipi.di.acube.batframework.utils.WikipediaApiInterface;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datasets.*;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datasets.datahub.DatahubNIFLoader;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class is a very ugly workaround performing the mapping from dataset names to {@link DatasetConfiguration}
 * objects and from an {@link ExperimentType} to a list of {@link DatasetConfiguration}s that are usable for this
 * {@link ExperimentType}.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * @author Giuseppe Rizzo <giuse.rizzo@gmail.com>
 */
public class DatasetMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetMapping.class);

    private static final String UPLOADED_FILES_PATH_PROPERTY_KEY = "org.aksw.gerbil.UploadPath";
    private static final String UPLOADED_DATASET_SUFFIX = "(uploaded)";

    private static DatasetMapping instance = null;

    private synchronized static DatasetMapping getInstance() {
        if (instance == null) {
            Map<String, DatasetConfiguration> nameDatasetMapping = new HashMap<String, DatasetConfiguration>();
            WikipediaApiInterface wikiApi = SingletonWikipediaApi.getInstance();

            nameDatasetMapping.put(ACE2004DatasetConfig.DATASET_NAME, new ACE2004DatasetConfig(wikiApi));
            nameDatasetMapping.put(AQUAINTDatasetConfiguration.DATASET_NAME, new AQUAINTDatasetConfiguration(wikiApi));
            nameDatasetMapping.put(IITBDatasetConfig.DATASET_NAME, new IITBDatasetConfig(wikiApi));
            nameDatasetMapping.put(MeijDatasetConfig.DATASET_NAME, new MeijDatasetConfig());
            nameDatasetMapping.put(MSNBCDatasetConfig.DATASET_NAME, new MSNBCDatasetConfig(wikiApi));

            nameDatasetMapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Training", new AIDACoNLLDatasetConfig(
                    AIDACoNLLDatasetConfig.AIDACoNLLChunk.TRAINING, wikiApi));
            nameDatasetMapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Test A", new AIDACoNLLDatasetConfig(
                    AIDACoNLLDatasetConfig.AIDACoNLLChunk.TEST_A, wikiApi));
            nameDatasetMapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Test B", new AIDACoNLLDatasetConfig(
                    AIDACoNLLDatasetConfig.AIDACoNLLChunk.TEST_B, wikiApi));
            nameDatasetMapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Complete", new AIDACoNLLDatasetConfig(
                    AIDACoNLLDatasetConfig.AIDACoNLLChunk.COMPLETE, wikiApi));

            nameDatasetMapping.put(Microposts2014Config.DATASET_NAME_START + "-Train", new Microposts2014Config(
                    Microposts2014Config.Microposts2014Chunk.TRAIN, wikiApi));
            nameDatasetMapping.put(Microposts2014Config.DATASET_NAME_START + "-Test", new Microposts2014Config(
                    Microposts2014Config.Microposts2014Chunk.TEST, wikiApi));

            // Got through the known NIF datasets
            NIFDatasets nifDatasets[] = NIFDatasets.values();
            for (int i = 0; i < nifDatasets.length; ++i) {
                nameDatasetMapping.put(nifDatasets[i].getDatasetName(), new KnownNIFFileDatasetConfig(wikiApi,
                        nifDatasets[i]));
            }

            // load Datahub data
            DatahubNIFLoader datahub = new DatahubNIFLoader();
            Map<String, String> datasets = datahub.getDataSets();
            for (String datasetName : datasets.keySet()) {
                nameDatasetMapping.put(datasetName,
                        new DatahubNIFConfig(wikiApi, datasetName, datasets.get(datasetName), true));
            }

            instance = new DatasetMapping(nameDatasetMapping);
        }
        return instance;
    }

    public static Set<String> getDatasetsForExperimentType(ExperimentType type) {
        DatasetMapping datasets = getInstance();
        Set<String> names = new HashSet<String>();
        for (String datasetName : datasets.mapping.keySet()) {
            if (datasets.mapping.get(datasetName).isApplicableForExperiment(type)) {
                names.add(datasetName);
            }
        }
        return names;
    }

    public static DatasetConfiguration getDatasetConfig(String name) {
        DatasetMapping datasets = getInstance();
        if (datasets.mapping.containsKey(name)) {
            return datasets.mapping.get(name);
        } else {
            if (name.startsWith("NIFDS_")) {
                String uploadedFilesPath = GerbilConfiguration.getInstance()
                        .getString(UPLOADED_FILES_PATH_PROPERTY_KEY);
                if (uploadedFilesPath == null) {
                    LOGGER.error("Couldn't process uploaded file request, because the upload path is not set (\"{}\"",
                            UPLOADED_FILES_PATH_PROPERTY_KEY);
                }
                // This describes a NIF based web service
                // The name should have the form "NIFDS_name(uri)"
                int pos = name.indexOf('(');
                if (pos < 0) {
                    LOGGER.error("Couldn't parse the definition of this NIF based web service \"" + name
                            + "\". Returning null.");
                    return null;
                }
                String uri = uploadedFilesPath + name.substring(pos + 1, name.length() - 1);
                // remove "NIFDS_" from the name
                name = name.substring(6, pos) + UPLOADED_DATASET_SUFFIX;
                LOGGER.error("name={}, uri={}", name, uri);
                return new NIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), name, uri,
                        false, ExperimentType.Sa2KB);
            }
            LOGGER.error("Got an unknown annotator name\"" + name + "\". Returning null.");
            return null;
        }
    }

    public static List<DatasetConfiguration> getDatasetConfigurations() {
        return getInstance().getDatasetConfigs();
    }

    private final Map<String, DatasetConfiguration> mapping;

    private DatasetMapping(Map<String, DatasetConfiguration> mapping) {
        this.mapping = mapping;
    }

    protected List<DatasetConfiguration> getDatasetConfigs() {
        return new ArrayList<DatasetConfiguration>(this.mapping.values());
    }
}

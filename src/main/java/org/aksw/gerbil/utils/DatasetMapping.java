package org.aksw.gerbil.utils;

import it.acubelab.batframework.utils.WikipediaApiInterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.datasets.ACE2004DatasetConfig;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig;
import org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration;
import org.aksw.gerbil.datasets.DatahubNIFConfig;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.IITBDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datasets.MSNBCDatasetConfig;
import org.aksw.gerbil.datasets.MeijDatasetConfig;
import org.aksw.gerbil.datasets.Microposts2014Config;
import org.aksw.gerbil.datasets.datahub.DatahubNIFLoader;
import org.aksw.gerbil.datatypes.ExperimentType;

/**
 * ...
 * 
 * @authors .....
 *          Giuseppe Rizzo <giuse.rizzo@gmail.com>
 */
public class DatasetMapping {

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
            return null;
        }
    }

    private final Map<String, DatasetConfiguration> mapping;

    private DatasetMapping(Map<String, DatasetConfiguration> mapping) {
        this.mapping = mapping;
    }
}

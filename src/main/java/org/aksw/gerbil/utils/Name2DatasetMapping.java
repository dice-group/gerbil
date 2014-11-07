package org.aksw.gerbil.utils;

import java.util.Map.Entry;

import org.aksw.gerbil.datasets.ACE2004DatasetConfig;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig.AIDACoNLLChunk;
import org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration;
import org.aksw.gerbil.datasets.CMNSDatasetConfig;
import org.aksw.gerbil.datasets.DatahubNIFConfig;
import org.aksw.gerbil.datasets.DatasetConfiguration;
import org.aksw.gerbil.datasets.IITBDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datasets.MSNBCDatasetConfig;
import org.aksw.gerbil.datasets.datahub.DatahubNIFLoader;

@Deprecated
public class Name2DatasetMapping {

    public static DatasetConfiguration getDatasetConfig(String name) {

        switch (name) {
        case ACE2004DatasetConfig.DATASET_NAME:
            return new ACE2004DatasetConfig(SingletonWikipediaApi.getInstance());
        case AQUAINTDatasetConfiguration.DATASET_NAME:
            return new AQUAINTDatasetConfiguration(SingletonWikipediaApi.getInstance());
        case IITBDatasetConfig.DATASET_NAME:
            return new IITBDatasetConfig(SingletonWikipediaApi.getInstance());
        case CMNSDatasetConfig.DATASET_NAME:
            return new CMNSDatasetConfig();
        case MSNBCDatasetConfig.DATASET_NAME:
            return new MSNBCDatasetConfig(SingletonWikipediaApi.getInstance());
        }

        if (name.startsWith(AIDACoNLLDatasetConfig.DATASET_NAME_START)) {
            AIDACoNLLChunk chunk = null;
            switch (name) {
            case AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Training": {
                chunk = AIDACoNLLChunk.TRAINING;
                break;
            }
            case AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Test A": {
                chunk = AIDACoNLLChunk.TEST_A;
                break;
            }
            case AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Test B": {
                chunk = AIDACoNLLChunk.TEST_B;
                break;
            }
            case AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Complete": {
                chunk = AIDACoNLLChunk.COMPLETE;
                break;
            }
            default:
                return null;
            }
            return new AIDACoNLLDatasetConfig(chunk, SingletonWikipediaApi.getInstance());
        }

        // Got through the known NIF datasets
        NIFDatasets nifDatasets[] = NIFDatasets.values();
        for (int i = 0; i < nifDatasets.length; ++i) {
            if (nifDatasets[i].getDatasetName().equals(name)) {
                return new KnownNIFFileDatasetConfig(SingletonWikipediaApi.getInstance(), nifDatasets[i]);
            }
        }

        DatahubNIFLoader dh = new DatahubNIFLoader();
        for (Entry<String, String> d : dh.getDataSets().entrySet()) {
            if (d.getKey().equals(name)) {
                return new DatahubNIFConfig(SingletonWikipediaApi.getInstance(), d.getKey(), d.getValue(), true);
            }
        }

        return null;
    }
}

package org.aksw.gerbil.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.datasets.ACE2004DatasetConfig;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig;
import org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration;
import org.aksw.gerbil.datasets.CMNSDatasetConfig;
import org.aksw.gerbil.datasets.IITBDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datasets.MSNBCDatasetConfig;
import org.aksw.gerbil.datasets.datahub.DatahubNIFLoader;
import org.aksw.gerbil.datatypes.ExperimentType;

@Deprecated
public class DatasetName2ExperimentTypeMapping {

    private static DatasetName2ExperimentTypeMapping instance = null;

    private synchronized static DatasetName2ExperimentTypeMapping getInstance() {
        if (instance == null) {
            Map<String, ExperimentType> mapping = new HashMap<String, ExperimentType>();

            mapping.put(ACE2004DatasetConfig.DATASET_NAME, ExperimentType.Sa2W);
            mapping.put(AQUAINTDatasetConfiguration.DATASET_NAME, ExperimentType.Sa2W);
            mapping.put(IITBDatasetConfig.DATASET_NAME, ExperimentType.Sa2W);
            mapping.put(CMNSDatasetConfig.DATASET_NAME, ExperimentType.Rc2W);
            mapping.put(MSNBCDatasetConfig.DATASET_NAME, ExperimentType.Sa2W);

            mapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Training", ExperimentType.Sa2W);
            mapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Test A", ExperimentType.Sa2W);
            mapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Test B", ExperimentType.Sa2W);
            mapping.put(AIDACoNLLDatasetConfig.DATASET_NAME_START + "-Complete", ExperimentType.Sa2W);

            // Got through the known NIF datasets
            NIFDatasets nifDatasets[] = NIFDatasets.values();
            for (int i = 0; i < nifDatasets.length; ++i) {
                mapping.put(nifDatasets[i].getDatasetName(), ExperimentType.Sa2W);
            }

            // put Datahub data in it too
            DatahubNIFLoader datahub = new DatahubNIFLoader();
            for (String s : datahub.getDataSets().keySet()) {
                mapping.put(s, ExperimentType.Sa2W);
            }

            instance = new DatasetName2ExperimentTypeMapping(mapping);
        }
        return instance;
    }

    public static Set<String> getDatasetsForExperimentType(ExperimentType type) {
        DatasetName2ExperimentTypeMapping data2ExpType = getInstance();
        ExperimentType annotatorType;
        Set<String> names = new HashSet<String>();
        for (String annotatorName : data2ExpType.mapping.keySet()) {
            annotatorType = data2ExpType.mapping.get(annotatorName);
            if (annotatorType.equalsOrContainsType(type)) {
                names.add(annotatorName);
            }
        }
        return names;
    }

    private final Map<String, ExperimentType> mapping;

    private DatasetName2ExperimentTypeMapping(Map<String, ExperimentType> mapping) {
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

package org.aksw.gerbil.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aksw.gerbil.datasets.ACE2004DatasetConfig;
import org.aksw.gerbil.datasets.AIDACoNLLDatasetConfig;
import org.aksw.gerbil.datasets.AQUAINTDatasetConfiguration;
import org.aksw.gerbil.datasets.CMNSDatasetConfig;
import org.aksw.gerbil.datasets.IITBDatasetConfig;
import org.aksw.gerbil.datasets.KnownNIFFileDatasetConfig.NIFDatasets;
import org.aksw.gerbil.datasets.MSNBCDatasetConfig;
import org.aksw.gerbil.datatypes.ExperimentType;

public class DatasetName2ExperimentTypeMapping {

	private final static Map<String, ExperimentType> mapping = new HashMap<String, ExperimentType>();

	static {
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
	}

	public ExperimentType getExperimentType(String name) {
		if (mapping.containsKey(name)) {
			return mapping.get(name);
		} else {
			return null;
		}
	}

	public static Set<String> getDatasetsForExperimentType(ExperimentType type) {

		return null;
	}
}

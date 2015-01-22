package org.aksw.gerbil.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Very ugly way to manage the mapping of datasets to their metadata objects.
 * 
 * FIXME: This should be part of the dataset objects or their metadata/configuration classes.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * 
 */
public class DatasetMetaDataMapping {

    private static DatasetMetaDataMapping instance;

    public static DatasetMetaDataMapping getInstance() {
        if (instance == null) {
            Map<String, DatasetMetaData> mapping = new HashMap<String, DatasetMetaData>();
            // mapping.put(key, value);

            instance = new DatasetMetaDataMapping(mapping);
        }
        return instance;
    }

    private Map<String, DatasetMetaData> mapping;

    public DatasetMetaDataMapping(Map<String, DatasetMetaData> mapping) {
        super();
        this.mapping = mapping;
    }

    public DatasetMetaData getMetaData(String datasetName) {
        if (mapping.containsKey(datasetName)) {
            return mapping.get(datasetName);
        } else {
            return null;
        }
    }
}

package org.aksw.gerbil.utils;

import java.util.Comparator;

import org.aksw.gerbil.datatypes.ExperimentTaskConfiguration;

/**
 * A Comparator that sorts the {@link ExperimentTaskConfiguration} ascending
 * regarding the dataset name and if the dataset names are equal, ascending
 * regarding the annotator name.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class ExpTaskConfigComparator implements Comparator<ExperimentTaskConfiguration> {

    @Override
    public int compare(ExperimentTaskConfiguration config1, ExperimentTaskConfiguration config2) {
        int diff = config1.datasetConfig.getName().compareTo(config2.datasetConfig.getName());
        if (diff == 0) {
            return config1.annotatorConfig.getName().compareTo(config2.annotatorConfig.getName());
        }
        return diff;
    }

}

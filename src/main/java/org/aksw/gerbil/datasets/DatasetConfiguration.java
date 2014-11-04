package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;

import org.aksw.gerbil.datatypes.AdapterConfiguration;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public interface DatasetConfiguration extends AdapterConfiguration {

    /**
     * Returns the dataset implementing the interface needed for the given
     * {@link ExperimentType} or null if the dataset can not be used for this
     * experiment.
     * 
     * @param experimentType
     *            The type of experiment the user wants to run with this dataset
     * @return the dataset or null if the dataset is not applicable to the given
     *         experiment type
     * @throws GerbilException
     *             throws an exception if an error occurs during the dataset
     *             generation
     */
    public TopicDataset getDataset(ExperimentType experimentType) throws GerbilException;
}

package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;

import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public interface DatasetConfiguration {

    /**
     * Getter of the datasets name.
     * 
     * @return The name of the dataset.
     */
    public String getDatasetName();

    /**
     * Returns true if we are allowed to cache the results of this dataset.
     * 
     * @return true if the results already could be cached inside the database. Otherwise false is returned.
     */
    public boolean couldBeCached();

    /**
     * Returns the dataset implementing the interface needed for the given {@link ExperimentType} or null if the dataset
     * can not be used for this experiment.
     * 
     * @param experimentType
     *            The type of experiment the user wants to run with this dataset
     * @return the dataset or null if the dataset is not applicable to the given experiment type
     * @throws GerbilException
     *             throws an exception if an error occurs during the dataset generation
     */
    public TopicDataset getDataset(ExperimentType experimentType) throws GerbilException;
}

package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public abstract class AbstractDatasetConfiguration implements DatasetConfiguration {

    private String datasetName;
    private boolean couldBeCached;
    private ExperimentType applicableForExperiments[];

    public AbstractDatasetConfiguration(String datasetName, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        this.datasetName = datasetName;
        this.couldBeCached = couldBeCached;
        applicableForExperiments = applicableForExperiment;
    }

    @Override
    public String getDatasetName() {
        return datasetName;
    }

    @Override
    public boolean couldBeCached() {
        return couldBeCached;
    }

    @Override
    public TopicDataset getDataset(ExperimentType experimentType) throws GerbilException {
        for (int i = 0; i < applicableForExperiments.length; ++i) {
            if (applicableForExperiments[i].equalsOrContainsType(experimentType)) {
                try {
                    return loadDataset();
                } catch (Exception e) {
                    throw new GerbilException(e, ErrorTypes.DATASET_LOADING_ERROR);
                }
            }
        }
        return null;
    }

    protected abstract TopicDataset loadDataset() throws Exception;

}

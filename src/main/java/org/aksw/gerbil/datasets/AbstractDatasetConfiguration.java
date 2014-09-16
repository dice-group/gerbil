package org.aksw.gerbil.datasets;

import it.acubelab.batframework.problems.TopicDataset;

import java.util.HashSet;
import java.util.Set;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.exceptions.GerbilException;

public abstract class AbstractDatasetConfiguration implements DatasetConfiguration {

    private String datasetName;
    private boolean couldBeCached;
    private Set<ExperimentType> applicableForExperiments;

    public AbstractDatasetConfiguration(String datasetName, boolean couldBeCached,
            ExperimentType... applicableForExperiment) {
        this.datasetName = datasetName;
        this.couldBeCached = couldBeCached;
        applicableForExperiments = new HashSet<ExperimentType>();
        for (int i = 0; i < applicableForExperiment.length; ++i) {
            applicableForExperiments.add(applicableForExperiment[i]);
        }
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
        if (applicableForExperiments.contains(experimentType)) {
            try {
                return loadDataset();
            } catch (Exception e) {
                throw new GerbilException(e, ErrorTypes.DATASET_LOADING_ERROR);
            }
        } else {
            return null;
        }
    }

    protected abstract TopicDataset loadDataset() throws Exception;

}

package org.aksw.gerbil.database;

import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentResult;

public interface ExperimentDAO {

    public void setResultDurability(long milliseconds);

    public void storeExperimentTaskResult(String annotatorName, String datasetName, String experimentType,
            String metric, double result);

    public boolean connectExistingTaskWithExperiment(String annotatorName, String datasetName, String experimentType,
            String metric, String experimentId);

    public List<ExperimentResult> getResultsOfExperiment(String experimentId);
}

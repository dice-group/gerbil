package org.aksw.gerbil.database;

import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggingDAO4Debugging extends AbstractExperimentDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoggingDAO4Debugging.class);

    private int nextTaskId = 0;

    @Override
    public List<ExperimentTaskResult> getResultsOfExperiment(String experimentId) {
        return null;
    }

    @Override
    public int createTask(String annotatorName, String datasetName, String experimentType, String matching,
            String experimentId) {
        int taskId = nextTaskId;
        ++nextTaskId;
        LOGGER.info("creating task " + taskId + "annotatorName=\"" + annotatorName + "\", datasetName=\"" + datasetName
                + "\", experimentType=\"" + experimentType.toString() + "\", String matching=\"" + matching
                + "\", experimentId)");
        return taskId;
    }

    @Override
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskResult result) {
        LOGGER.info("Setting result of task " + experimentTaskId + " to " + result.toString());
    }

    @Override
    protected int getCachedExperimentTaskId(String annotatorName, String datasetName, String experimentType,
            String matching) {
        return AbstractExperimentDAO.EXPERIMENT_TASK_NOT_CACHED;
    }

    @Override
    protected void connectExistingTaskWithExperiment(int experimentTaskId, String experimentId) {
        LOGGER.info("Task " + experimentTaskId + " belongs to experiment " + experimentId);
    }

}

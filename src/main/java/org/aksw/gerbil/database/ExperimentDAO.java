package org.aksw.gerbil.database;

import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;

public interface ExperimentDAO {

    /**
     * Sets the durability of a experiment task result.
     * 
     * @param milliseconds
     */
    public void setResultDurability(long milliseconds);

    /**
     * Returns the results of the experiment tasks that are connected to the experiment with the given experiment id.
     * 
     * @param experimentId
     *            if of the experiment
     * @return a list of experiment task results that are connected to the experiment
     */
    public List<ExperimentTaskResult> getResultsOfExperiment(String experimentId);

    /**
     * This method is called with the description of an experiment task and an experiment id. The method checks whether
     * there is already such an experiment task inside the database. If such a task exists and if it is not to old
     * regarding the durability of experiment task results, the experiment id is connected to the already existing task
     * and -1 is returned. Otherwise,
     * 
     * <b>NOTE:</b> this method MUST be synchronized since it should only be called by a single thread at once.
     * 
     * @param annotatorName
     *            the name with which the annotator can be identified
     * @param datasetName
     *            the name of the dataset
     * @param experimentType
     *            the name of the experiment type
     * @param metric
     *            the name of the metric used
     * @param experimentId
     *            the id of the experiment
     * @return -1 if there is already an experiment task with the given preferences or the id of the newly created
     *         experiment task.
     */
    public int connectCachedResultOrCreateTask(String annotatorName, String datasetName, String experimentType,
            String metric, String experimentId);

    /**
     * Creates a new experiment task with the given preferences and connects it to the experiment with the given
     * experiment id.
     * 
     * @param annotatorName
     *            the name with which the annotator can be identified
     * @param datasetName
     *            the name of the dataset
     * @param experimentType
     *            the name of the experiment type
     * @param metric
     *            the name of the metric used
     * @param experimentId
     *            the id of the experiment
     * @return the id of the newly created experiment task.
     */
    public int createTask(String annotatorName, String datasetName, String experimentType,
            String metric, String experimentId);

    /**
     * This method updates the result of the already existing experiment task, identified by the given id. Additionally
     * it should update the timestamp of the task.
     * 
     * @param experimentTaskId
     *            the id of the experiment task to which the result has been calculated
     * @param result
     *            the result of this experiment task
     */
    public void setExperimentTaskResult(int experimentTaskId, double result);
}

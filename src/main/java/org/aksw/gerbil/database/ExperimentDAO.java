package org.aksw.gerbil.database;

import java.util.List;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;

/**
 * This interface defines the methods a class has to implement for making the results of the framework persistent.
 * 
 * @author m.roeder
 * 
 */
public interface ExperimentDAO {

    /**
     * Sentinel value indicating that the database already contains a result for an experiment task which can be reused
     * by an experiment.
     */
    public static final int CACHED_EXPERIMENT_TASK_CAN_BE_USED = -1;

    /**
     * Sentinel value stored as state in the database indicating that a task has been finished.
     */
    public static final int TASK_FINISHED = 0;

    /**
     * Sentinel value stored as state in the database indicating that a task has been started but there weren't stored
     * any results until now.
     */
    public static final int TASK_STARTED_BUT_NOT_FINISHED_YET = -1;

    /**
     * Sentinel value returned if the task with the given id couldn't be found.
     */
    public static final int TASK_NOT_FOUND = -2;

    /**
     * Sets the durability of a experiment task result.
     * 
     * @param milliseconds
     */
    public void setResultDurability(long milliseconds);

    /**
     * Returns the results of the experiment tasks that are connected to the experiment with the given experiment id.
     * Note that before retrieving the results of an experiment its state should be checked.
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
     * and {@link #CACHED_EXPERIMENT_TASK_CAN_BE_USED}={@value #CACHED_EXPERIMENT_TASK_CAN_BE_USED} is returned.
     * Otherwise, a new experiment task is created, set to unfinished by setting its state to
     * {@link #TASK_STARTED_BUT_NOT_FINISHED_YET}, connected to the given experiment and the id of the newly created
     * experiment task is returned.
     * 
     * <b>NOTE:</b> this method MUST be synchronized since it should only be called by a single thread at once.
     * 
     * @param annotatorName
     *            the name with which the annotator can be identified
     * @param datasetName
     *            the name of the dataset
     * @param experimentType
     *            the name of the experiment type
     * @param matching
     *            the name of the matching used
     * @param experimentId
     *            the id of the experiment
     * @return {@link #CACHED_EXPERIMENT_TASK_CAN_BE_USED}={@value #CACHED_EXPERIMENT_TASK_CAN_BE_USED} if there is
     *         already an experiment task with the given preferences or the id of the newly created
     *         experiment task.
     */
    public int connectCachedResultOrCreateTask(String annotatorName, String datasetName, String experimentType,
            String matching, String experimentId);

    /**
     * Creates a new experiment task with the given preferences, set to unfinished by setting its state to
     * {@link #TASK_STARTED_BUT_NOT_FINISHED_YET} and connects it to the experiment with the given experiment id.
     * 
     * @param annotatorName
     *            the name with which the annotator can be identified
     * @param datasetName
     *            the name of the dataset
     * @param experimentType
     *            the name of the experiment type
     * @param matching
     *            the name of the matching used
     * @param experimentId
     *            the id of the experiment
     * @return the id of the newly created experiment task.
     */
    public int createTask(String annotatorName, String datasetName, String experimentType,
            String matching, String experimentId);

    /**
     * This method updates the result of the already existing experiment task, identified by the given id. Additionally
     * it should update the timestamp of the task and set its state to {@link #TASK_FINISHED}={@value #TASK_FINISHED}.
     * 
     * @param experimentTaskId
     *            the id of the experiment task to which the result has been calculated
     * @param result
     *            the result of this experiment task
     */
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskResult result);

    /**
     * Sets the state of the already existing experiment task, identified by the given id.
     * 
     * @param experimentTaskId
     *            the id of the experiment task for which the state should be set
     * @param state
     *            the state of this experiment task
     */
    public void setExperimentState(int experimentTaskId, int state);

    /**
     * Returns the state of the existing experiment task, identified by the given id.
     * 
     * @param experimentTaskId
     *            the id of the experiment task for which the state should be retrieved
     * @return the state of the experiment task or {@link #TASK_NOT_FOUND}={@value #TASK_NOT_FOUND} if such a task
     *         couldn't be found.
     */
    public int getExperimentState(int experimentTaskId);
}

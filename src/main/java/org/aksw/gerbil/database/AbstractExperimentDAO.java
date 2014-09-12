package org.aksw.gerbil.database;

/**
 * Abstract class implementing the general behavior of an {@link ExperimentDAO}. Note that it is strongly recommended to
 * extend this class instead of implementing the {@link ExperimentDAO} class directly since this class already takes
 * care of the synchronization problem of the
 * {@link ExperimentDAO#connectCachedResultOrCreateTask(String, String, String, String, String)} method.
 * 
 * @author m.roeder
 * 
 */
public abstract class AbstractExperimentDAO implements ExperimentDAO {

    /**
     * Sentinel value used to indicate that an experiment task with the given preferences couldn't be found.
     */
    protected static final int EXPERIMENT_TASK_NOT_CACHED = -1;

    protected long resultDurability;

    public AbstractExperimentDAO() {
    }

    public AbstractExperimentDAO(long resultDurability) {
        setResultDurability(resultDurability);
    }

    @Override
    public void setResultDurability(long resultDurability) {
        this.resultDurability = resultDurability;
    }

    public long getResultDurability() {
        return resultDurability;
    }

    @Override
    public synchronized int connectCachedResultOrCreateTask(String annotatorName, String datasetName,
            String experimentType, String metric, String experimentId) {
        int experimentTaskId = getCachedExperimentTaskId(annotatorName, datasetName, experimentType, metric);
        if (experimentTaskId == EXPERIMENT_TASK_NOT_CACHED) {
            experimentTaskId = createTask(annotatorName, datasetName, experimentType, metric, experimentId);
        } else {
            connectExistingTaskWithExperiment(experimentTaskId, experimentId);
        }
        return experimentTaskId;
    }

    /**
     * The method checks whether there exists an experiment task with the given preferences inside the database. If such
     * a task exists and if it is not to old
     * regarding the durability of experiment task results, its experiment task id is returned. Otherwise
     * {@link #EXPERIMENT_TASK_NOT_CACHED} is returned.
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
     * @return The id of the experiment task or {@value #EXPERIMENT_TASK_NOT_CACHED} if such an experiment task couldn't
     *         be found.
     */
    protected abstract int getCachedExperimentTaskId(String annotatorName, String datasetName, String experimentType,
            String metric);

    /**
     * This method connects an already existing experiment task with an experiment.
     * 
     * @param experimentTaskId
     *            the id of the experiment task
     * @param experimentId
     *            the id of the experiment
     */
    protected abstract void connectExistingTaskWithExperiment(int experimentTaskId, String experimentId);

}

/**
 * This file is part of General Entity Annotator Benchmark.
 *
 * General Entity Annotator Benchmark is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * General Entity Annotator Benchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with General Entity Annotator Benchmark.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.gerbil.database;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Abstract class implementing the general behavior of an {@link ExperimentDAO}.
 * Note that it is strongly recommended to extend this class instead of
 * implementing the {@link ExperimentDAO} class directly since this class
 * already takes care of the synchronization problem of the
 * {@link ExperimentDAO#connectCachedResultOrCreateTask(String, String, String, String)} method.
 * 
 * @author m.roeder
 * 
 */
public abstract class AbstractExperimentDAO implements ExperimentDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentDAOImpl.class);

    /**
     * Sentinel value used to indicate that an experiment task with the given
     * preferences couldn't be found.
     */
    protected static final int EXPERIMENT_TASK_NOT_CACHED = -1;

    protected long resultDurability;
    protected boolean initialized = false;

    public AbstractExperimentDAO() {
    }

    public AbstractExperimentDAO(long resultDurability) {
        setResultDurability(resultDurability);
    }

    /**
     * The {@link AbstractExperimentDAO} class makes sure that an instance is
     * initialized only once even if this method is called multiple times.
     */
    @Override
    public void initialize() {
        if (!initialized) {
            /*
             * We only have to set back the status of experiments that were
             * running while the server has been stopped.
             */
            setRunningExperimentsToError();
        }
    }

    /**
     * Searches the database for experiment tasks that have been started but not
     * ended yet (their status equals {@link #TASK_STARTED_BUT_NOT_FINISHED_YET} ) and set their status to
     * {@link ErrorTypes#SERVER_STOPPED_WHILE_PROCESSING}. This method should
     * only be called directly after the initialization of the database. It
     * makes sure that "old" experiment tasks which have been started but never
     * finished are set to an error state and can't be used inside the caching
     * mechanism.
     */
    protected abstract void setRunningExperimentsToError();

    @Override
    public void setResultDurability(long resultDurability) {
        this.resultDurability = resultDurability;
    }

    public long getResultDurability() {
        return resultDurability;
    }
    
    @Override
    public synchronized int connectCachedResultOrCreateTask(String annotatorName, String datasetName,
            String experimentType, String experimentId) {
        int experimentTaskId = EXPERIMENT_TASK_NOT_CACHED;
        if (resultDurability > 0) {
            experimentTaskId = getCachedExperimentTaskId(annotatorName, datasetName, experimentType);
        } else {
            LOGGER.warn("The durability of results is <= 0. I won't be able to cache results.");
        }
        if (experimentTaskId == EXPERIMENT_TASK_NOT_CACHED) {
            return createTask(annotatorName, datasetName, experimentType, experimentId);
        } else {
            LOGGER.debug("Could reuse cached task (id={}).", experimentTaskId);
            connectExistingTaskWithExperiment(experimentTaskId, experimentId);
            return CACHED_EXPERIMENT_TASK_CAN_BE_USED;
        }
    }

    /**
     * The method checks whether there exists an experiment task with the given
     * preferences inside the database. If such a task exists, if it is not to
     * old regarding the durability of experiment task results and if its state
     * is not an error code, its experiment task id is returned. Otherwise {@link #EXPERIMENT_TASK_NOT_CACHED} is
     * returned.
     * 
     * <b>NOTE:</b> this method MUST be synchronized since it should only be
     * called by a single thread at once.
     * 
     * @param annotatorName
     *            the name with which the annotator can be identified
     * @param datasetName
     *            the name of the dataset
     * @param experimentType
     *            the name of the experiment type
     * @return The id of the experiment task or {@value #EXPERIMENT_TASK_NOT_CACHED} if such an experiment task
     *         couldn't be found.
     */
    protected abstract int getCachedExperimentTaskId(String annotatorName, String datasetName, String experimentType);

    /**
     * This method connects an already existing experiment task with an
     * experiment.
     * 
     * @param experimentTaskId
     *            the id of the experiment task
     * @param experimentId
     *            the id of the experiment
     */
    protected abstract void connectExistingTaskWithExperiment(int experimentTaskId, String experimentId);

    /**
     * This method returns a list of annotator dataset combinations for the given experimentType and matching that exist
     * inside the database. The first element of every array contains the name of an annotator and the second element is
     * the name of a dataset.
     * 
     * @param experimentType
     *            the name of the experiment type

     * @return a list of annotator dataset combinations
     */
    @Deprecated
    protected abstract List<String[]> getAnnotatorDatasetCombinations(String experimentType);

}

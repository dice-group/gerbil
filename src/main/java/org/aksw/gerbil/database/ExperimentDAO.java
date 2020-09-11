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

import java.io.Closeable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.datatypes.ChallengeDescr;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.ExperimentType;

/**
 * This interface defines the methods a class has to implement for making the
 * results of the framework persistent.
 * 
 * @author m.roeder
 * 
 */
public interface ExperimentDAO extends Closeable {

    /**
     * Sentinel value indicating that the database already contains a result for
     * an experiment task which can be reused by an experiment.
     */
    public static final int CACHED_EXPERIMENT_TASK_CAN_BE_USED = -1;

    /**
     * Sentinel value stored as state in the database indicating that a task has
     * been finished.
     */
    public static final int TASK_FINISHED = 0;

    /**
     * Sentinel value stored as state in the database indicating that a task has
     * been started but there weren't stored any results until now.
     */
    public static final int TASK_STARTED_BUT_NOT_FINISHED_YET = -1;

    public void insertResultNames(String[] names);
    public List<String> getResultNames();
        /**
         * Sentinel value returned if the task with the given id couldn't be found.
         */
    public static final int TASK_NOT_FOUND = -2;

    /**
     * Initializes the database. Searches the database for experiment tasks that
     * have been started but not ended yet (their status equals
     * {@link #TASK_STARTED_BUT_NOT_FINISHED_YET} ) and set their status to
     * {@link ErrorTypes#SERVER_STOPPED_WHILE_PROCESSING}. This method should
     * only be called directly after the initialization of the database. It
     * makes sure that "old" experiment tasks which have been started but never
     * finished are set to an error state and can't be used inside the caching
     * mechanism.
     */
    public void initialize();

    /**
     * Sets the durability of a experiment task result.
     * 
     * @param milliseconds
     */
    public void setResultDurability(long milliseconds);

    /**
     * Returns the results of the experiment tasks that are connected to the
     * experiment with the given experiment id. Note that before retrieving the
     * results of an experiment its state should be checked.
     * 
     * @param experimentId
     *            if of the experiment
     * @return a list of experiment task results that are connected to the
     *         experiment
     */
    public List<ExperimentTaskStatus> getResultsOfExperiment(String experimentId);

    /**
     * Returns the result of the experiment task with the given ID or null if
     * this task does not exist.
     * 
     * @param experimentTaskId
     *            the id of the experiment task
     * @return the experiment task or null if this task does not exist.
     */
    public ExperimentTaskStatus getResultOfExperimentTask(int experimentTaskId);

    /**
     * This method is called with the description of an experiment task and an
     * experiment id. The method checks whether there is already such an
     * experiment task inside the database that does not have an error code as
     * state. If such a task exists and if it is not to old regarding the
     * durability of experiment task results, the experiment id is connected to
     * the already existing task and {@link #CACHED_EXPERIMENT_TASK_CAN_BE_USED}
     * = {@value #CACHED_EXPERIMENT_TASK_CAN_BE_USED} is returned. Otherwise, a
     * new experiment task is created, set to unfinished by setting its state to
     * {@link #TASK_STARTED_BUT_NOT_FINISHED_YET}, connected to the given
     * experiment and the id of the newly created experiment task is returned.
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
     * @param experimentId
     *            the id of the experiment
     * @return {@link #CACHED_EXPERIMENT_TASK_CAN_BE_USED}=
     *         {@value #CACHED_EXPERIMENT_TASK_CAN_BE_USED} if there is already
     *         an experiment task with the given preferences or the id of the
     *         newly created experiment task.
     */
    int connectCachedResultOrCreateTask(String annotatorName, String datasetName, String experimentType,
			String experimentId);

    /**
     * Creates a new experiment task with the given preferences, sets its GERBIL
     * version value using the current version, sets the task to unfinished by
     * setting its state to {@link #TASK_STARTED_BUT_NOT_FINISHED_YET} and
     * connects it to the experiment with the given experiment id.
     * 
     * @param annotatorName
     *            the name with which the annotator can be identified
     * @param datasetName
     *            the name of the dataset
     * @param experimentType
     *            the name of the experiment type

     * @param experimentId
     *            the id of the experiment
     * @return the id of the newly created experiment task.
     */
    int createTask(String annotatorName, String datasetName, String experimentType,
			String experimentId);

    /**
     * This method updates the result of the already existing experiment task,
     * identified by the given id. Additionally it should update the timestamp
     * of the task and set its state to the state countained inside the given
     * result object.
     * 
     * @param experimentTaskId
     *            the id of the experiment task to which the result has been
     *            calculated
     * @param result
     *            the result of this experiment task
     */
    void setExperimentTaskResult(int experimentTaskId, ExperimentTaskStatus result);

    /**
     * Sets the state of the already existing experiment task, identified by the
     * given id.
     * 
     * @param experimentTaskId
     *            the id of the experiment task for which the state should be
     *            set
     * @param state
     *            the state of this experiment task
     */
    public void setExperimentState(int experimentTaskId, int state);

    /**
     * Returns the state of the existing experiment task, identified by the
     * given id.
     * 
     * @param experimentTaskId
     *            the id of the experiment task for which the state should be
     *            retrieved
     * @return the state of the experiment task or {@link #TASK_NOT_FOUND}=
     *         {@value #TASK_NOT_FOUND} if such a task couldn't be found.
     */
    public int getExperimentState(int experimentTaskId);

    /**
     * Returns the highest experiment ID that is known by the system or null if
     * there are no experiments.
     * 
     * @return the highest experiment ID or null if there is no experiment
     */
    public String getHighestExperimentId();

    /**
     * Returns the latest results for experiments with the given experiment type
     * and matching type. Note that the experiment tasks of which the results
     * are returned should be finished.
     * 
     * @param experimentType
     *            the name of the experiment type
     * @param annotatorNames
     *            the names of annotators for which the data should be collected
     * @param datasetNames
     *            the names of datasets for which the data should be collected
     * @return a list of the latest results available in the database.
     */
    public List<ExperimentTaskStatus> getLatestResultsOfExperiments(String experimentType,
            String annotatorNames[], String datasetNames[]);
    
    /**
     * Returns a list of all running experiment tasks.
     * 
     * @return a list of all running experiment tasks.
     */
    public List<ExperimentTaskStatus> getAllRunningExperimentTasks();
    
    /**
     * Counts the number of running experiments before the provided taskId
     * @param lastTaskId - provided task Id
     * @return number of running tasks before provided id
     */
	Integer countPrecedingRunningTasks(int lastTaskId);

    public List<ExperimentTaskStatus> getBestResults(String name, String dataset);

    public List<ExperimentTaskStatus> getBestResults(String name, String dataset, String resultName, Timestamp start, Timestamp end);

    public List<ChallengeDescr> getAllChallenges();

    public void addChallenge(ChallengeDescr challenge);
    public boolean isChallengeInDB(ChallengeDescr challenge);

    public Set<String> getAnnotators();

    public void setFile2SystemMapping(int experimentTaskId, String fileName, String systemName, String email);
    public void setFile2SystemMapping(int experimentTaskId, File2SystemEntry entry);


    public List<File2SystemEntry> getFile2SystemByID(int experimentTaskId);

    public String getTaskId(int id);

    public List<String> getAllMetricsForExperimentType(ExperimentType experimentType);

    public int insertSubmission(String teamName, String email, String task, String zipFileName);
}

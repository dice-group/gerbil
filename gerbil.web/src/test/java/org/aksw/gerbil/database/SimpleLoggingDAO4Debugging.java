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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.datatypes.ChallengeDescr;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggingDAO4Debugging extends AbstractExperimentDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoggingDAO4Debugging.class);

    private int nextTaskId = 0;

    @Override
    public List<ExperimentTaskStatus> getResultsOfExperiment(String experimentId) {
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
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskStatus result) {
        setExperimentTaskResult(experimentTaskId, result, false);
    }

    protected void setExperimentTaskResult(int experimentTaskId, ExperimentTaskStatus result, boolean isSubTask) {
        if (isSubTask) {
            LOGGER.info("Setting result of " + result.type.name() + " sub task of " + experimentTaskId + " to "
                    + result.toString());
        } else {
            LOGGER.info("Setting result of task " + experimentTaskId + " to " + result.toString());
        }
        Map<String, TaskResult> results = result.getResultsMap();
        for(Entry<String, TaskResult> e : results.entrySet()) {
            LOGGER.info(e.getKey() + " = " + shrinkToMaxLength(e.getValue()));
        }
        if (result.hasSubTasks()) {
            for (ExperimentTaskStatus subTask : result.subTasks) {
                setExperimentTaskResult(experimentTaskId, subTask, true);
            }
        }
    }
    
    protected static String shrinkToMaxLength(TaskResult result) {
        String resultString = result.toString();
        if(resultString.length() > 100) {
            resultString = resultString.substring(0, 100) + "...";
        }
        return resultString;
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

    @Override
    public void setExperimentState(int experimentTaskId, int state) {
        LOGGER.info("State of Task " + experimentTaskId + " was set to " + state);
    }

    @Override
    public int getExperimentState(int experimentTaskId) {
        return 0;
    }

    @Override
    public String getHighestExperimentId() {
        return null;
    }

    @Override
    public void setRunningExperimentsToError() {
        LOGGER.info("Setting the state of all running tasks to an error code.");
    }

    @Override
    protected List<String[]> getAnnotatorDatasetCombinations(String experimentType, String matching) {
        return new ArrayList<String[]>(0);
    }

    @Override
    protected ExperimentTaskStatus getLatestExperimentTaskResult(String experimentType, String matching,
            String annotatorName, String datasetName) {
        return null;
    }

    @Override
    public List<ExperimentTaskStatus> getAllRunningExperimentTasks() {
        return new ArrayList<ExperimentTaskStatus>(0);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public List<ExperimentTaskStatus> getLatestResultsOfExperiments(String experimentType, String matching,
            String[] annotatorNames, String[] datasetNames) {
        return null;
    }

    @Override
    public ExperimentTaskStatus getResultOfExperimentTask(int experimentTaskId) {
        return null;
    }

    @Override
    public List<ExperimentTaskStatus> getBestResults(String name, String dataset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getAnnotators() {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public void setFile2SystemMapping(int experimentTaskId, String fileName, String systemName, String email) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFile2SystemMapping(int experimentTaskId, File2SystemEntry entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<File2SystemEntry> getFile2SystemByID(int experimentTaskId) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public String getTaskId(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ExperimentTaskStatus> getBestResults(String name, String dataset, Timestamp start, Timestamp end) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ChallengeDescr> getAllChallenges() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addChallenge(ChallengeDescr challenge) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isChallengeInDB(ChallengeDescr challenge) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer countExperiments() {
        return 0;
    }

}

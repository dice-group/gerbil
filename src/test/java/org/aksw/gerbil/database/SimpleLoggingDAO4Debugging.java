/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2014 Agile Knowledge Engineering and Semantic Web (AKSW) (usbeck@informatik.uni-leipzig.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aksw.gerbil.database;

import java.util.ArrayList;
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
    protected ExperimentTaskResult getLatestExperimentTaskResult(String experimentType, String matching,
            String annotatorName, String datasetName) {
        return null;
    }

    @Override
    public List<ExperimentTaskResult> getAllRunningExperimentTasks() {
        return new ArrayList<ExperimentTaskResult>(0);
    }

}

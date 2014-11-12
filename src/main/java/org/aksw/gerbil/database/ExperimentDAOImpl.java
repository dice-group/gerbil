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

import java.util.List;

import javax.sql.DataSource;

import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * SQL database based implementation of the {@link AbstractExperimentDAO} class.
 * 
 * @author b.eickmann
 * @author m.roeder
 * 
 */
public class ExperimentDAOImpl extends AbstractExperimentDAO {

    private final static String INSERT_TASK = "INSERT INTO ExperimentTasks (annotatorName, datasetName, experimentType, matching, state, lastChanged) VALUES (:annotatorName, :datasetName, :experimentType, :matching, :state, :lastChanged)";
    private final static String SET_TASK_STATE = "UPDATE ExperimentTasks SET state=:state, lastChanged=:lastChanged WHERE id=:id";
    private final static String SET_EXPERIMENT_TASK_RESULT = "UPDATE ExperimentTasks SET microF1=:microF1 , microPrecision=:microPrecision, microRecall=:microRecall, macroF1=:macroF1, macroPrecision=:macroPrecision, macroRecall=:macroRecall, errorCount=:errorCount, lastChanged=:lastChanged WHERE id=:id";
    private final static String CONNECT_TASK_EXPERIMENT = "INSERT INTO Experiments (id, taskId) VALUES(:id, :taskId)";
    private final static String GET_TASK_STATE = "SELECT state FROM ExperimentTasks WHERE id=:id";
    private final static String GET_EXPERIMENT_RESULTS = "SELECT annotatorName, datasetName, experimentType, matching, microF1, microPrecision, microRecall, macroF1, macroPrecision, macroRecall, state, errorCount, lastChanged FROM ExperimentTasks t, Experiments e WHERE e.id=:id AND e.taskId=t.id";
    private final static String GET_CACHED_TASK = "SELECT id FROM ExperimentTasks WHERE annotatorName=:annotatorName AND datasetName=:datasetName AND experimentType=:experimentType AND matching=:matching AND lastChanged>:lastChanged AND state>:errorState ORDER BY lastChanged DESC LIMIT 1";
    private final static String GET_HIGHEST_EXPERIMENT_ID = "SELECT id FROM Experiments ORDER BY id DESC LIMIT 1";
    private final static String SET_UNFINISHED_TASK_STATE = "UPDATE ExperimentTasks SET state=:state, lastChanged=:lastChanged WHERE state=:unfinishedState";
    @Deprecated
    private final static String GET_LATEST_EXPERIMENT_TASKS = "SELECT DISTINCT annotatorName, datasetName FROM ExperimentTasks WHERE experimentType=:experimentType AND matching=:matching";
    @Deprecated
    private final static String GET_LATEST_EXPERIMENT_TASK_RESULT = "SELECT annotatorName, datasetName, experimentType, matching, microF1, microPrecision, microRecall, macroF1, macroPrecision, macroRecall, state, errorCount, lastChanged FROM ExperimentTasks WHERE annotatorName=:annotatorName AND datasetName=:datasetName AND experimentType=:experimentType AND matching=:matching AND state<>:unfinishedState ORDER BY lastChanged DESC LIMIT 1";
    private final static String GET_LATEST_EXPERIMENT_TASK_RESULTS = "SELECT annotatorName, datasetName, experimentType, matching, microF1, microPrecision, microRecall, macroF1, macroPrecision, macroRecall, state, errorCount, lastChanged FROM ExperimentTasks , (SELECT datasetName, annotatorName, MAX(lastChanged) AS lastChanged FROM ExperimentTasks WHERE experimentType=:experimentType AND matching=:matching AND state<>:unfinishedState GROUP BY datasetName, annotatorName) pairs WHERE annotatorName=pairs.annotatorName AND datasetName=pairs.datasetName AND experimentType=:experimentType AND matching=:matching AND lastChanged=pairs.lastChanged";
    private final static String GET_RUNNING_EXPERIMENT_TASKS = "SELECT annotatorName, datasetName, experimentType, matching, microF1, microPrecision, microRecall, macroF1, macroPrecision, macroRecall, state, errorCount, lastChanged FROM ExperimentTasks WHERE state=:unfinishedState";

    private final NamedParameterJdbcTemplate template;

    public ExperimentDAOImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public ExperimentDAOImpl(DataSource dataSource, long resultDurability) {
        super(resultDurability);
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<ExperimentTaskResult> getResultsOfExperiment(String experimentId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentId);
        List<ExperimentTaskResult> result = this.template.query(GET_EXPERIMENT_RESULTS, parameters,
                new ExperimentTaskResultRowMapper());
        return result;
    }

    @Override
    public int createTask(String annotatorName, String datasetName, String experimentType, String matching,
            String experimentId) {
        MapSqlParameterSource params = createTaskParameters(annotatorName, datasetName, experimentType, matching);
        params.addValue("state", ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET);
        java.util.Date today = new java.util.Date();
        params.addValue("lastChanged", new java.sql.Timestamp(today.getTime()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.template.update(INSERT_TASK, params, keyHolder);
        Integer generatedKey = (Integer) keyHolder.getKey();
        connectToExperiment(experimentId, generatedKey);
        return generatedKey;
    }

    private void connectToExperiment(String experimentId, Integer taskId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentId);
        parameters.addValue("taskId", taskId);
        this.template.update(CONNECT_TASK_EXPERIMENT, parameters);
    }

    private MapSqlParameterSource createTaskParameters(String annotatorName, String datasetName, String experimentType,
            String matching) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("annotatorName", annotatorName);
        parameters.addValue("datasetName", datasetName);
        parameters.addValue("experimentType", experimentType);
        parameters.addValue("matching", matching);
        return parameters;
    }

    @Override
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskResult result) {
        // Note that we have to set the state first if we want to override the
        // automatic timestamp with the one from the
        // result object
        setExperimentState(experimentTaskId, result.state);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        parameters.addValue("microF1", result.getMicroF1Measure());
        parameters.addValue("microPrecision", result.getMicroPrecision());
        parameters.addValue("microRecall", result.getMicroRecall());
        parameters.addValue("macroF1", result.getMacroF1Measure());
        parameters.addValue("macroPrecision", result.getMacroPrecision());
        parameters.addValue("macroRecall", result.getMacroRecall());
        parameters.addValue("errorCount", result.getErrorCount());
        parameters.addValue("lastChanged", new java.sql.Timestamp(result.timestamp));

        this.template.update(SET_EXPERIMENT_TASK_RESULT, parameters);
    }

    @Override
    public void setExperimentState(int experimentTaskId, int state) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        parameters.addValue("state", state);
        java.util.Date today = new java.util.Date();
        parameters.addValue("lastChanged", new java.sql.Timestamp(today.getTime()));
        this.template.update(SET_TASK_STATE, parameters);
    }

    @Override
    public int getExperimentState(int experimentTaskId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        List<Integer> result = this.template.query(GET_TASK_STATE, parameters, new IntegerRowMapper());
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return TASK_NOT_FOUND;
        }
    }

    @Override
    protected int getCachedExperimentTaskId(String annotatorName, String datasetName, String experimentType,
            String matching) {
        MapSqlParameterSource params = createTaskParameters(annotatorName, datasetName, experimentType, matching);
        java.util.Date today = new java.util.Date();
        params.addValue("lastChanged", new java.sql.Timestamp(today.getTime() - this.resultDurability));
        params.addValue("errorState", ErrorTypes.HIGHEST_ERROR_CODE);
        List<Integer> result = this.template.query(GET_CACHED_TASK, params, new IntegerRowMapper());
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return EXPERIMENT_TASK_NOT_CACHED;
        }
    }

    @Override
    protected void connectExistingTaskWithExperiment(int experimentTaskId, String experimentId) {
        connectToExperiment(experimentId, experimentTaskId);
    }

    @Override
    public String getHighestExperimentId() {
        List<String> result = this.template.query(GET_HIGHEST_EXPERIMENT_ID, new StringRowMapper());
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    protected void setRunningExperimentsToError() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        parameters.addValue("state", ErrorTypes.SERVER_STOPPED_WHILE_PROCESSING.getErrorCode());
        java.util.Date today = new java.util.Date();
        parameters.addValue("lastChanged", new java.sql.Timestamp(today.getTime()));
        this.template.update(SET_UNFINISHED_TASK_STATE, parameters);
    }
    
    @Deprecated
    @Override
    protected List<String[]> getAnnotatorDatasetCombinations(String experimentType, String matching) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("experimentType", experimentType);
        params.addValue("matching", matching);
        return this.template.query(GET_LATEST_EXPERIMENT_TASKS, params, new StringArrayRowMapper(new int[] { 1, 2 }));
    }
    
    @Deprecated
    @Override
    protected ExperimentTaskResult getLatestExperimentTaskResult(String experimentType, String matching,
            String annotatorName, String datasetName) {
        MapSqlParameterSource params = createTaskParameters(annotatorName, datasetName, experimentType, matching);
        params.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        List<ExperimentTaskResult> result = this.template.query(GET_LATEST_EXPERIMENT_TASK_RESULT, params,
                new ExperimentTaskResultRowMapper());
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<ExperimentTaskResult> getAllRunningExperimentTasks() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        return this.template.query(GET_RUNNING_EXPERIMENT_TASKS, params, new ExperimentTaskResultRowMapper());
    }

    @Override
    public List<ExperimentTaskResult> getLatestResultsOfExperiments(String experimentType, String matching) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("experimentType", experimentType);
        parameters.addValue("matching", matching);
        parameters.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        return this.template.query(GET_LATEST_EXPERIMENT_TASK_RESULTS, parameters,
                new ExperimentTaskResultRowMapper());
    }
}

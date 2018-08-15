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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentDAOImpl.class);

    private final static String INSERT_TASK = "INSERT INTO ExperimentTasks (annotatorName, datasetName, experimentType, matching, state, lastChanged, version) VALUES (:annotatorName, :datasetName, :experimentType, :matching, :state, :lastChanged, :version )";
    private final static String SET_TASK_STATE = "UPDATE ExperimentTasks SET state=:state, lastChanged=:lastChanged WHERE id=:id";
    private final static String SET_EXPERIMENT_TASK_RESULT = "UPDATE ExperimentTasks SET lastChanged=:lastChanged WHERE id=:id";
    private final static String CONNECT_TASK_EXPERIMENT = "INSERT INTO Experiments (id, taskId) VALUES(:id, :taskId)";
    private final static String GET_TASK_STATE = "SELECT state FROM ExperimentTasks WHERE id=:id";
    private final static String GET_EXPERIMENT_RESULTS = "SELECT annotatorName, datasetName, experimentType, matching, state, lastChanged, version, taskId FROM ExperimentTasks t, Experiments e WHERE e.id=:id AND e.taskId=t.id";
    private final static String GET_EXPERIMENT_TASK_RESULT = "SELECT annotatorName, datasetName, experimentType, matching,  state, lastChanged, version, id FROM ExperimentTasks t WHERE id=:id";
    private final static String GET_CACHED_TASK = "SELECT id FROM ExperimentTasks WHERE annotatorName=:annotatorName AND datasetName=:datasetName AND experimentType=:experimentType AND matching=:matching AND lastChanged>:lastChanged AND state>:errorState ORDER BY lastChanged DESC LIMIT 1";
    private final static String GET_HIGHEST_EXPERIMENT_ID = "SELECT id FROM Experiments ORDER BY id DESC LIMIT 1";
    private final static String SET_UNFINISHED_TASK_STATE = "UPDATE ExperimentTasks SET state=:state, lastChanged=:lastChanged WHERE state=:unfinishedState";
    @Deprecated
    private final static String GET_LATEST_EXPERIMENT_TASKS = "SELECT DISTINCT annotatorName, datasetName FROM ExperimentTasks WHERE experimentType=:experimentType AND matching=:matching";
    @Deprecated
    private final static String GET_LATEST_EXPERIMENT_TASK_RESULT = "SELECT annotatorName, datasetName, experimentType, matching, microF1, microPrecision, microRecall, macroF1, macroPrecision, macroRecall, state, errorCount, lastChanged FROM ExperimentTasks WHERE annotatorName=:annotatorName AND datasetName=:datasetName AND experimentType=:experimentType AND matching=:matching AND state<>:unfinishedState ORDER BY lastChanged DESC LIMIT 1";
    private final static String GET_LATEST_EXPERIMENT_TASK_RESULTS = "SELECT tasks.annotatorName, tasks.datasetName, tasks.experimentType, tasks.matching, tasks.state, tasks.lastChanged, tasks.id FROM ExperimentTasks tasks, (SELECT datasetName, annotatorName, MAX(lastChanged) AS lastChanged FROM ExperimentTasks WHERE experimentType=:experimentType AND matching=:matching AND state<>:unfinishedState AND annotatorName IN (:annotatorNames) AND datasetName IN (:datasetNames) GROUP BY datasetName, annotatorName) pairs WHERE tasks.annotatorName=pairs.annotatorName AND tasks.datasetName=pairs.datasetName AND tasks.experimentType=:experimentType AND tasks.matching=:matching AND tasks.lastChanged=pairs.lastChanged";
    private final static String GET_RUNNING_EXPERIMENT_TASKS = "SELECT annotatorName, datasetName, experimentType, matching,  state, lastChanged, version FROM ExperimentTasks WHERE state=:unfinishedState";
    private final static String SHUTDOWN = "SHUTDOWN";

    private final static String GET_ADDITIONAL_RESULTS = "SELECT resultId, value FROM ExperimentResultsDouble WHERE taskId=:taskId UNION SELECT resultId, value FROM ExperimentResultsInt WHERE taskId=:taskId";
    private final static String INSERT_RESULT_DOUBLE = "INSERT INTO ExperimentResultsDouble(taskId, resultId, value) VALUES (:taskId, :resultId, :value)";
    private final static String INSERT_RESULT_INT = "INSERT INTO ExperimentResultsInt(taskId, resultId, value) VALUES (:taskId, :resultId, :value)";
    private final static String GET_SUB_TASK_RESULTS = "SELECT annotatorName, datasetName, experimentType, matching, state, lastChanged, version, subTaskId FROM ExperimentTasks t, ExperimentTasks_SubTasks s WHERE s.taskId=:taskId AND s.subTaskId=t.id";
    private final static String INSERT_SUB_TASK_RELATION = "INSERT INTO ExperimentTasks_SubTasks(taskId, subTaskId) VALUES (:taskId, :subTaskId)";


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
      
        for (ExperimentTaskResult e : result) {
            addAdditionalResults(e);
            addSubTasks(e);
        }
        return result;
    }


    @Override
    public int createTask(String annotatorName, String datasetName, String experimentType, String matching,
            String experimentId) {
        MapSqlParameterSource params = createTaskParameters(annotatorName, datasetName, experimentType, matching);
        params.addValue("state", ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET);
        java.util.Date today = new java.util.Date();
        params.addValue("lastChanged", new java.sql.Timestamp(today.getTime()));
        params.addValue("version", GerbilConfiguration.getGerbilVersion());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.template.update(INSERT_TASK, params, keyHolder);
        Integer generatedKey = (Integer) keyHolder.getKey();
        if (experimentId != null) {
            connectToExperiment(experimentId, generatedKey);
        }
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
        parameters.addValue("lastChanged", new java.sql.Timestamp(result.timestamp));

        this.template.update(SET_EXPERIMENT_TASK_RESULT, parameters);
        if (result.hasAdditionalResults()) {
            for (int i = 0; i < result.additionalResults.allocated.length; ++i) {
                if ((result.additionalResults.allocated[i]) && (result.additionalResults.keys[i] >= 0)) {
                    addAdditionalResult(experimentTaskId, result.additionalResults.keys[i], result.additionalResults.values[i]);          
               }
            }
        }
        if (result.hasSubTasks()) {
            for (ExperimentTaskResult subTask : result.getSubTasks()) {
                insertSubTask(subTask, experimentTaskId);
            }
        }
    }

    protected void addAdditionalResult(int taskId, int resultId, Object value) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", taskId);
        parameters.addValue("resultId", resultId);
        
        if(value instanceof Integer){
        	parameters.addValue("value", (int)value);
        	this.template.update(INSERT_RESULT_INT, parameters);
        } else {
        	parameters.addValue("value", (double)value);
        	this.template.update(INSERT_RESULT_DOUBLE, parameters);
        }
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
        List<ExperimentTaskResult> results = this.template.query(GET_LATEST_EXPERIMENT_TASK_RESULTS, parameters,
                new ExperimentTaskResultRowMapper());

        for (ExperimentTaskResult result : results) {
            addAdditionalResults(result);
        }
        return results;
    }

    @Override
    public List<ExperimentTaskResult> getLatestResultsOfExperiments(String experimentType, String matching,
            String annotatorNames[], String datasetNames[]) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("experimentType", experimentType);
        parameters.addValue("matching", matching);
        parameters.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        parameters.addValue("annotatorNames", Arrays.asList(annotatorNames));
        parameters.addValue("datasetNames", Arrays.asList(datasetNames));
        List<ExperimentTaskResult> results = this.template.query(GET_LATEST_EXPERIMENT_TASK_RESULTS, parameters,
                new ExperimentTaskResultRowMapper());

        for (ExperimentTaskResult result : results) {
            addAdditionalResults(result);
        }
        return results;
    }

    protected void addAdditionalResults(ExperimentTaskResult result) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", result.idInDb);

        List<IntDoublePair> addResults = this.template.query(GET_ADDITIONAL_RESULTS, parameters,
                new IntDoublePairRowMapper());

        for (IntDoublePair a : addResults) {
            result.addAdditionalResult(a.first, a.second);
        }
    }

    protected void insertSubTask(ExperimentTaskResult subTask, int experimentTaskId) {
        subTask.idInDb = createTask(subTask.annotator, subTask.dataset, subTask.type.name(), subTask.matching.name(),
                null);
        setExperimentTaskResult(subTask.idInDb, subTask);
        addSubTaskRelation(experimentTaskId, subTask.idInDb);
    }

    protected void addSubTaskRelation(int taskId, int subTaskId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", taskId);
        parameters.addValue("subTaskId", subTaskId);
        this.template.update(INSERT_SUB_TASK_RELATION, parameters);
    }

    protected void addSubTasks(ExperimentTaskResult expTask) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", expTask.idInDb);
        List<ExperimentTaskResult> subTasks = this.template.query(GET_SUB_TASK_RESULTS, parameters,
                new ExperimentTaskResultRowMapper());
        expTask.setSubTasks(subTasks);
        for (ExperimentTaskResult subTask : subTasks) {
            subTask.gerbilVersion = expTask.gerbilVersion;
            addAdditionalResults(subTask);
        }
    }

    @Override
    public ExperimentTaskResult getResultOfExperimentTask(int experimentTaskId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        List<ExperimentTaskResult> results = this.template.query(GET_EXPERIMENT_TASK_RESULT, parameters,
                new ExperimentTaskResultRowMapper());
        if (results.size() == 0) {
            return null;
        }
        ExperimentTaskResult result = results.get(0);
        addAdditionalResults(result);
        addSubTasks(result);
        return result;
    }

    @Override
    public void close() throws IOException {
        this.template.execute(SHUTDOWN, new PreparedStatementCallback<Object>() {
            @Override
            public Object doInPreparedStatement(PreparedStatement arg0) throws SQLException, DataAccessException {
                // nothing to do
                return null;
            }
        });
    }
}

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
import java.util.Map;

import javax.sql.DataSource;

import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ErrorTypes;
import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.TaskResult;
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

    private final static String INSERT_TASK = "INSERT INTO ExperimentTasks (systemName, datasetName, experimentType, matching, state, lastChanged, version) VALUES (:annotatorName, :datasetName, :experimentType, :matching, :state, :lastChanged, :version)";
    private final static String SET_TASK_STATE = "UPDATE ExperimentTasks SET state=:state, lastChanged=:lastChanged WHERE id=:id";
    private final static String SET_EXPERIMENT_TASK_RESULT = "UPDATE ExperimentTasks SET lastChanged=:lastChanged WHERE id=:id";
    private final static String CONNECT_TASK_EXPERIMENT = "INSERT INTO Experiments (id, taskId) VALUES(:id, :taskId)";
    private final static String GET_TASK_STATE = "SELECT state FROM ExperimentTasks WHERE id=:id";
    private final static String GET_EXPERIMENT_RESULTS = "SELECT systemName, datasetName, experimentType, matching, state, version, lastChanged, taskId FROM ExperimentTasks t, Experiments e WHERE e.id=:id AND e.taskId=t.id ORDER BY t.id";
    private final static String GET_CACHED_TASK = "SELECT id FROM ExperimentTasks WHERE systemName=:annotatorName AND datasetName=:datasetName AND experimentType=:experimentType AND matching=:matching AND lastChanged>:lastChanged AND state>:errorState ORDER BY lastChanged DESC LIMIT 1";
    private final static String GET_HIGHEST_EXPERIMENT_ID = "SELECT id FROM Experiments ORDER BY id DESC LIMIT 1";
    private final static String SET_UNFINISHED_TASK_STATE = "UPDATE ExperimentTasks SET state=:state, lastChanged=:lastChanged WHERE state=:unfinishedState";
    @Deprecated
    private final static String GET_LATEST_EXPERIMENT_TASKS = "SELECT DISTINCT annotatorName, datasetName FROM ExperimentTasks WHERE experimentType=:experimentType AND matching=:matching";
    private final static String GET_LATEST_EXPERIMENT_TASK_RESULTS = "SELECT tasks.systemName, tasks.datasetName, tasks.experimentType, tasks.matching, tasks.state, tasks.version, tasks.lastChanged, tasks.id FROM ExperimentTasks tasks, (SELECT datasetName, systemName, MAX(lastChanged) AS lastChanged FROM ExperimentTasks WHERE experimentType=:experimentType AND matching=:matching AND state<>:unfinishedState AND systemName IN (:annotatorNames) AND datasetName IN (:datasetNames) GROUP BY datasetName, systemName) pairs WHERE tasks.systemName=pairs.systemName AND tasks.datasetName=pairs.datasetName AND tasks.experimentType=:experimentType AND tasks.matching=:matching AND tasks.lastChanged=pairs.lastChanged";
    private final static String GET_RUNNING_EXPERIMENT_TASKS = "SELECT systemName, datasetName, experimentType, matching, state, version, lastChanged FROM ExperimentTasks WHERE state=:unfinishedState";
    private final static String SHUTDOWN = "SHUTDOWN";

    private final static String GET_TASK_RESULTS_DOUBLE = "SELECT rn.name, 'DOUBLE' AS Type, dr.resvalue FROM ExperimentTasks_DoubleResults dr, ResultNames rn WHERE dr.resultId = rn.id and dr.taskId = :taskId";
    private final static String GET_TASK_RESULTS_INTEGER = "SELECT rn.name, 'INT' AS Type, ir.resvalue FROM ExperimentTasks_IntResults ir, ResultNames rn WHERE ir.resultId = rn.id and ir.taskId = :taskId";
    private final static String INSERT_DOUBLE_RESULT = "INSERT INTO ExperimentTasks_DoubleResults(taskId, resultId, resvalue) select :taskId, id, :value from ResultNames where name = :resName";
    private final static String INSERT_INT_RESULT = "INSERT INTO ExperimentTasks_IntResults(taskId, resultId, resvalue) select :taskId, id, :value from ResultNames where name = :resName";
    private final static String GET_SUB_TASK_RESULTS = "SELECT systemName, datasetName, experimentType, matching, state, version, lastChanged, subTaskId FROM ExperimentTasks t, ExperimentTasks_SubTasks s WHERE s.taskId=:taskId AND s.subTaskId=t.id";
    private final static String INSERT_SUB_TASK_RELATION = "INSERT INTO ExperimentTasks_SubTasks(taskId, subTaskId) VALUES (:taskId, :subTaskId)";
    
    private final static String GET_RUNNING_EXP_COUNT = "SELECT count(*) FROM ExperimentTasks where state = :unfinishedState AND id < :lastTaskId";
    
    public static final String[] RES_NAME_ARR = {"Micro F1 score", "Micro Precision", "Micro Recall", "Macro F1 score"
    		, "Macro Precision", "Macro Recall"};
    public static final String ERROR_COUNT_NAME = "Error Count";
    
    private final NamedParameterJdbcTemplate template;

    public ExperimentDAOImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    public ExperimentDAOImpl(DataSource dataSource, long resultDurability) {
        super(resultDurability);
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }
    
    @Override
    public List<ExperimentTaskStatus> getResultsOfExperiment(String experimentId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentId);
        List<ExperimentTaskStatus> result = this.template.query(GET_EXPERIMENT_RESULTS, parameters,
                new ExperimentTaskRowMapper());
        for (ExperimentTaskStatus e : result) {
        	addAllResults(e);
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
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskStatus result) {
        // Note that we have to set the state first if we want to override the
        // automatic timestamp with the one from the
        // result object
        setExperimentState(experimentTaskId, result.state);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        parameters.addValue("lastChanged", new java.sql.Timestamp(result.timestamp));

        this.template.update(SET_EXPERIMENT_TASK_RESULT, parameters);
        Map<String, TaskResult> resMap = result.getResultsMap();
        if (resMap.size()>0) {
        	TaskResult tempResult;
        	String tempType;
           for(String resName : resMap.keySet()) {
        	   tempResult = resMap.get(resName);
        	   tempType = tempResult.getResType();
        	   if(DOUBLE_RESULT_TYPE.equalsIgnoreCase(tempType)) {
        		   addDoubleResult(experimentTaskId, resName, Double.parseDouble(tempResult.getResValue().toString()));
        	   } else if(INT_RESULT_TYPE.equalsIgnoreCase(tempType)) {
        		   addIntResult(experimentTaskId, resName, Integer.parseInt(tempResult.getResValue().toString()));
        	   } else {
        	       LOGGER.error("Got a result (\"{}\") with an unknown result type (\"{}\"). It will be ignored.",
        	               resName, tempType);
        	   }
           }
        }
        if (result.hasSubTasks()) {
            for (ExperimentTaskStatus subTask : result.getSubTasks()) {
                insertSubTask(subTask, experimentTaskId);
            }
        }
    }
    
    protected void addDoubleResult(int taskId, String resName, double value) throws DataAccessException {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", taskId);
        parameters.addValue("resName", resName);
        parameters.addValue("value", value);
        this.template.update(INSERT_DOUBLE_RESULT, parameters);
    }
    
    protected void addIntResult(int taskId, String resName, int value) throws DataAccessException {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", taskId);
        parameters.addValue("resName", resName);
        parameters.addValue("value", value);
        this.template.update(INSERT_INT_RESULT, parameters);
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
    
    @Override
    public List<ExperimentTaskStatus> getAllRunningExperimentTasks() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        return this.template.query(GET_RUNNING_EXPERIMENT_TASKS, params, new ExperimentTaskRowMapper());
    }
    
    @Override
    public List<ExperimentTaskStatus> getLatestResultsOfExperiments(String experimentType, String matching,
            String annotatorNames[], String datasetNames[]) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("experimentType", experimentType);
        parameters.addValue("matching", matching);
        parameters.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        parameters.addValue("annotatorNames", Arrays.asList(annotatorNames));
        parameters.addValue("datasetNames", Arrays.asList(datasetNames));
        List<ExperimentTaskStatus> results = this.template.query(GET_LATEST_EXPERIMENT_TASK_RESULTS, parameters,
                new ExperimentTaskRowMapper());

        for (ExperimentTaskStatus result : results) {
        	addAllResults(result);
        }
        return results;
    }
    
    protected void addAllResults(ExperimentTaskStatus result) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", result.idInDb);
        //Result is being set inside the mapper
        TaskResultsRowMapper resultMapper = new TaskResultsRowMapper(result);
        this.template.query(GET_TASK_RESULTS_DOUBLE, parameters, resultMapper);
        this.template.query(GET_TASK_RESULTS_INTEGER, parameters, resultMapper);
        setDefaultResultMap(result);
        // this.template.query(GET_TASK_RESULTS_BLOB, parameters, resultMapper);
    }
    
    public void setDefaultResultMap(ExperimentTaskStatus result) {
    	Map<String, TaskResult> resMap = result.getResultsMap();
    	TaskResult tempRes;
    	for(String dResName: RES_NAME_ARR) {
    		//Add a default double entry
    		if(resMap.get(dResName)==null) {
    		    LOGGER.info("Got an experiment task ({}) without the expected \"{}\" result. Setting it to 0.", result, dResName);
    			tempRes = new TaskResult(0d, DOUBLE_RESULT_TYPE);
    			resMap.put(dResName, tempRes);
    		}
    	}
    	
    	if(resMap.get(ERROR_COUNT_NAME)==null) {
            LOGGER.info("Got an experiment task ({}) without the expected \"" + ERROR_COUNT_NAME + "\" result. Setting it to 0.", result);
    		tempRes = new TaskResult(0, INT_RESULT_TYPE);
        	resMap.put(ERROR_COUNT_NAME, tempRes);
    	}
    }
    
    protected void insertSubTask(ExperimentTaskStatus subTask, int experimentTaskId) {
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
    
    protected void addSubTasks(ExperimentTaskStatus expTask) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", expTask.idInDb);
        List<ExperimentTaskStatus> subTasks = this.template.query(GET_SUB_TASK_RESULTS, parameters,
                new ExperimentTaskRowMapper());
        expTask.setSubTasks(subTasks);
        for (ExperimentTaskStatus subTask : subTasks) {
            subTask.gerbilVersion = expTask.gerbilVersion;
            addAllResults(subTask);
        }
    }

    @Override
    public ExperimentTaskStatus getResultOfExperimentTask(int experimentTaskId) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        List<ExperimentTaskStatus> results = this.template.query(GET_EXPERIMENT_RESULTS, parameters,
                new ExperimentTaskRowMapper());
        if (results.size() == 0) {
            return null;
        }
        ExperimentTaskStatus result = results.get(0);
        addAllResults(result);
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
    @Override
    public Integer countPrecedingRunningTasks(int lastTaskId) {
    	MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("lastTaskId", lastTaskId);
        parameters.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
    	List<Integer> result = this.template.query(GET_RUNNING_EXP_COUNT, parameters, new IntegerRowMapper());
        return result.get(0);
    }
}

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
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;

import org.aksw.gerbil.annotator.File2SystemEntry;
import org.aksw.gerbil.config.GerbilConfiguration;
import org.aksw.gerbil.datatypes.ChallengeDescr;
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

    private final static String INSERT_RESULT_NAME = "INSERT INTO ResultNames (name) VALUES (:name)";
    private final static String GET_RESULT_NAMES = "SELECT name FROM ResultNames";

    private final static String INSERT_TASK = "INSERT INTO ExperimentTasks (systemName, datasetName, experimentType, matching, state, lastChanged, version) VALUES (:annotatorName, :datasetName, :experimentType, :matching, :state, :lastChanged, :version)";
    private final static String SET_TASK_STATE = "UPDATE ExperimentTasks SET state=:state, lastChanged=:lastChanged WHERE id=:id";
    private final static String SET_EXPERIMENT_TASK_RESULT = "UPDATE ExperimentTasks SET publish=:publish, lastChanged=:lastChanged WHERE id=:id";
    private final static String CONNECT_TASK_EXPERIMENT = "INSERT INTO Experiments (id, taskId) VALUES(:id, :taskId)";
    private final static String GET_EXPERIMENT_TASKID = "SELECT id FROM Experiments WHERE taskId=:taskId";

    private final static String GET_TASK_STATE = "SELECT state FROM ExperimentTasks WHERE id=:id";
    private final static String GET_EXPERIMENT_RESULTS = "SELECT systemName, datasetName, experimentType, matching,  state, version, lastChanged, taskId FROM ExperimentTasks t, Experiments e WHERE e.id=:id AND e.taskId=t.id";
    private final static String GET_EXPERIMENT_TASK_RESULT = "SELECT systemName, datasetName, experimentType, matching, state, version, lastChanged, id FROM ExperimentTasks t WHERE id=:id";
    private final static String GET_CACHED_TASK = "SELECT id FROM ExperimentTasks WHERE systemName=:annotatorName AND datasetName=:datasetName AND experimentType=:experimentType AND matching=:matching AND lastChanged>:lastChanged AND state>:errorState ORDER BY lastChanged DESC LIMIT 1";
    private final static String GET_HIGHEST_EXPERIMENT_ID = "SELECT id FROM Experiments ORDER BY id DESC LIMIT 1";
    private final static String SET_UNFINISHED_TASK_STATE = "UPDATE ExperimentTasks SET state=:state, lastChanged=:lastChanged WHERE state=:unfinishedState";

    @Deprecated
    private final static String GET_LATEST_EXPERIMENT_TASKS = "SELECT DISTINCT systemName, datasetName FROM ExperimentTasks WHERE experimentType=:experimentType AND matching=:matching";
    @Deprecated
    private final static String GET_LATEST_EXPERIMENT_TASK_RESULT = "SELECT systemName, datasetName, experimentType, matching, state, version, lastChanged FROM ExperimentTasks WHERE systemName=:annotatorName AND datasetName=:datasetName AND experimentType=:experimentType AND matching=:matching AND state<>:unfinishedState ORDER BY lastChanged DESC LIMIT 1";
    private final static String GET_LATEST_EXPERIMENT_TASK_RESULTS = "SELECT systemName, datasetName, experimentType, matching, state, version, lastChanged FROM ExperimentTasks , (SELECT datasetName, systemName, MAX(lastChanged) AS lastChanged FROM ExperimentTasks WHERE experimentType=:experimentType AND matching=:matching AND state<>:unfinishedState GROUP BY datasetName, systemName) pairs WHERE systemName=pairs.systemName AND datasetName=pairs.datasetName AND experimentType=:experimentType AND matching=:matching AND lastChanged=pairs.lastChanged";
    private final static String GET_RUNNING_EXPERIMENT_TASKS = "SELECT systemName, datasetName, experimentType, matching, state, version, lastChanged FROM ExperimentTasks WHERE state=:unfinishedState";
    private final static String SHUTDOWN = "SHUTDOWN";

    private final static String GET_TASK_RESULTS_DOUBLE = "SELECT rn.name, 'DOUBLE' AS Type, dr.resvalue FROM ExperimentTasks_DoubleResults dr, ResultNames rn WHERE dr.resultId = rn.id and dr.taskId = :taskId";
    private final static String GET_TASK_RESULTS_INTEGER = "SELECT rn.name, 'INT' AS Type, ir.resvalue FROM ExperimentTasks_IntResults ir, ResultNames rn WHERE ir.resultId = rn.id and ir.taskId = :taskId";
    private final static String INSERT_DOUBLE_RESULT = "INSERT INTO ExperimentTasks_DoubleResults(taskId, resultId, resvalue) select :taskId, id, :value from ResultNames where name = :resName";
    private final static String INSERT_INT_RESULT = "INSERT INTO ExperimentTasks_IntResults(taskId, resultId, resvalue) select :taskId, id, :value from ResultNames where name = :resName";

    private final static String GET_TASK_RESULTS_BLOB = "SELECT rn.name, 'BLOB' AS Type, ir.resvalue FROM ExperimentTasks_BlobResults ir, ResultNames rn WHERE ir.resultId = rn.id and ir.taskId = :taskId";
    private final static String INSERT_BLOB_RESULT = "INSERT INTO ExperimentTasks_BlobResults(taskId, resultId, resvalue) select :taskId, id, :value from ResultNames where name = :resName";

    private final static String GET_SUB_TASK_RESULTS = "SELECT systemName, datasetName, experimentType, matching, state, version, lastChanged, subTaskId FROM ExperimentTasks t, ExperimentTasks_SubTasks s WHERE s.taskId=:taskId AND s.subTaskId=t.id";
    private final static String INSERT_SUB_TASK_RELATION = "INSERT INTO ExperimentTasks_SubTasks(taskId, subTaskId) VALUES (:taskId, :subTaskId)";

    private static final String GET_BEST_EXPERIMENT_TASK_RESULTS = "select inExp.systemName, outExp.datasetName, outExp.experimentType, outExp.matching, outExp.state, outExp.version, outExp.lastChanged, outExp.id, inExp.result from ExperimentTasks outExp, (SELECT exp.systemName, max(addi.resvalue) AS result FROM ExperimentTasks exp join ExperimentTasks_DoubleResults addi ON exp.Id=addi.taskId WHERE exp.publish='true' AND (addi.resultId=0 OR addi.resultId=3) and addi.resvalue <> sqrt(-1) and exp.experimentType = :experimentType and exp.datasetName = :dataset GROUP by exp.systemName) as inExp, ExperimentTasks_DoubleResults outAddi WHERE outExp.publish='true' AND (outAddi.resultId=0 OR outAddi.resultId=3) and outExp.Id=outAddi.taskId and outExp.systemName = inExp.systemName and inExp.result = outAddi.resvalue and outExp.state = 0 and outExp.datasetName = :dataset ORDER BY inExp.result DESC;";

    private static final String GET_ALL_ANNOTATORS = "SELECT DISTINCT systemName FROM ExperimentTasks";

    private static final String GET_BEST_EXPERIMENT_DATE_TASK_RESULTS = "select inExp.systemName, outExp.datasetName, outExp.experimentType, outExp.matching, outExp.state, outExp.version, outExp.lastChanged, outExp.id, inExp.result from ExperimentTasks outExp, (SELECT exp.systemName, max(addi.resvalue) AS result FROM ExperimentTasks exp join ExperimentTasks_DoubleResults addi ON exp.Id=addi.taskId WHERE exp.publish='true' AND (addi.resultId=0 OR addi.resultId=3) and addi.resvalue <> sqrt(-1) and exp.experimentType = :experimentType and exp.datasetName = :dataset and exp.lastChanged <= :before and exp.lastChanged >= :after GROUP by exp.systemName) as inExp, ExperimentTasks_DoubleResults outAddi WHERE outExp.publish='true' AND (outAddi.resultId=0 OR outAddi.resultId=3) and outExp.Id=outAddi.taskId and outExp.systemName = inExp.systemName and inExp.result = outAddi.resvalue and outExp.state = 0 and outExp.datasetName = :dataset ORDER BY inExp.result DESC;";

    private static final String SET_FILE2SYSTEM_MAPPING = "INSERT INTO File2System (id, file, system, email) VALUES(:id, :file, :system, :email)";
    private static final String GET_FILE2SYSTEM_MAPPING = "SELECT id, file, system, email FROM File2System WHERE id=:id";

    private static final String GET_CHALLENGE_DESCRIPTIONS = "SELECT startDate, endDate, name FROM ChallengeDescriptions";
    private final static String INSERT_CHALLENGE_DESCRIPTIONS = "INSERT INTO ChallengeDescriptions(startDate, endDate, name) VALUES (:startDate, :endDate, :name)";
    private static final String GET_CHALLENGE_DESCRIPTION = "SELECT startDate, endDate, name FROM ChallengeDescriptions WHERE startDate=:startDate and endDate=:endDate and name=:name";
    
    private final static String GET_EXP_COUNT = "SELECT count(*) FROM ExperimentTasks";

    private final NamedParameterJdbcTemplate template;
    /**
     * The result names that are already in the database.
     */
    private Set<String> resultNames;

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
                new ExperimentTaskStatusRowMapper());

        for (ExperimentTaskStatus e : result) {
            addAllResults(e);
            addSubTasks(e);
        }
        return result;
    }

    protected void addAllResults(ExperimentTaskStatus result) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", result.idInDb);
        // Result is being set inside the mapper
        TaskResultsRowMapper resultMapper = new TaskResultsRowMapper(result);
        this.template.query(GET_TASK_RESULTS_DOUBLE, parameters, resultMapper);
        this.template.query(GET_TASK_RESULTS_INTEGER, parameters, resultMapper);
        this.template.query(GET_TASK_RESULTS_BLOB, parameters, resultMapper);
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
    public void initialize() {
        super.initialize();
        // Get known result names
        resultNames = new HashSet<String>(this.template.query(GET_RESULT_NAMES, new StringRowMapper()));
        LOGGER.info("ExperimentDAO initialized.");
    }

    /**
     * Checks the given result name and inserts it in case it is not known.
     * 
     * @param name the result name that should be checked
     */
    private synchronized void checkResultName(String name) {
        if (!resultNames.contains(name)) {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("name", name);
            int rows = this.template.update(INSERT_RESULT_NAME, parameters);
            if (rows == 0) {
                LOGGER.warn("Insertion of {} did not change anything.", name);
            } else {
                resultNames.add(name);
            }
        }
    }

    @Override
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskStatus result) {
        // Note that we have to set the state first if we want to override the
        // automatic timestamp with the one from the
        // result object
        setExperimentState(experimentTaskId, result.state);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        parameters.addValue("publish", result.isPublish());
        parameters.addValue("lastChanged", new java.sql.Timestamp(result.timestamp));

        this.template.update(SET_EXPERIMENT_TASK_RESULT, parameters);
        Map<String, TaskResult> resMap = result.getResultsMap();
        if (resMap.size() > 0) {
            for (String resName : resMap.keySet()) {
                insertResults(experimentTaskId, resName, resMap.get(resName));
            }
        }
        if (result.hasSubTasks()) {
            for (ExperimentTaskStatus subTask : result.getSubTasks()) {
                insertSubTask(subTask, experimentTaskId);
            }
        }
    }

    protected void insertResults(int taskId, String resName, TaskResult taskResult) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", taskId);
        parameters.addValue("resName", resName);
        checkResultName(resName);
        if (taskResult.getResType().equalsIgnoreCase("BLOB")) {
            try {
                parameters.addValue("value", new SerialBlob(taskResult.getResValue().toString().getBytes()));
            } catch (SQLException e) {
                LOGGER.error("Could not store ROC Blob into database", e);
            }
        } else {
            parameters.addValue("value", taskResult.getResValue());
        }
        String queryStr = getTypeQuery(taskResult);
        if (queryStr != null)
            this.template.update(queryStr, parameters);
    }

    protected String getTypeQuery(TaskResult taskRes) {
        String type = taskRes.getResType();
        String queryStr = null;
        if (type.equalsIgnoreCase("DOUBLE")) {
            queryStr = INSERT_DOUBLE_RESULT;
        } else if (type.equalsIgnoreCase("INT")) {
            queryStr = INSERT_INT_RESULT;
        } else if (type.equalsIgnoreCase("BLOB")) {
            queryStr = INSERT_BLOB_RESULT;
        }
        return queryStr;
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

    @Override
    public List<ExperimentTaskStatus> getAllRunningExperimentTasks() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        return this.template.query(GET_RUNNING_EXPERIMENT_TASKS, params, new ExperimentTaskStatusRowMapper());
    }

    @Override
    public Set<String> getAnnotators() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        return new HashSet<String>(this.template.queryForList(GET_ALL_ANNOTATORS, params, String.class));
    }

    @Override
    public String getTaskId(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("taskId", id);
        return this.template.query(GET_EXPERIMENT_TASKID, params, new StringRowMapper()).get(0);
    }

    @Override
    public List<ExperimentTaskStatus> getLatestResultsOfExperiments(String experimentType, String matching) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("experimentType", experimentType);
        parameters.addValue("matching", matching);
        parameters.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        List<ExperimentTaskStatus> results = this.template.query(GET_LATEST_EXPERIMENT_TASK_RESULTS, parameters,
                new ExperimentTaskStatusRowMapper());

        for (ExperimentTaskStatus result : results) {
            addAllResults(result);
        }
        return results;
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
                new ExperimentTaskStatusRowMapper());

        for (ExperimentTaskStatus result : results) {
            addAllResults(result);
        }
        return results;
    }

    protected void insertSubTask(ExperimentTaskStatus subTask, int experimentTaskId) {
        subTask.idInDb = createTask(subTask.annotator, subTask.dataset, subTask.type.name(), subTask.matching.name(),
                "" + experimentTaskId);
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
                new ExperimentTaskStatusRowMapper());
        expTask.setSubTasks(subTasks);
        for (ExperimentTaskStatus subTask : subTasks) {
            addAllResults(subTask);
        }
    }

    @Override
    public ExperimentTaskStatus getResultOfExperimentTask(int experimentTaskId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        List<ExperimentTaskStatus> results = this.template.query(GET_EXPERIMENT_TASK_RESULT, parameters,
                new ExperimentTaskStatusRowMapper());
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
    public List<ExperimentTaskStatus> getBestResults(String experimentType, String dataset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("experimentType", experimentType);
        parameters.addValue("dataset", dataset);

        List<ExperimentTaskStatus> result = this.template.query(GET_BEST_EXPERIMENT_TASK_RESULTS, parameters,
                new ExperimentTaskStatusRowMapper());
        for (ExperimentTaskStatus res : result) {
            addAllResults(res);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    @Override
    public List<ExperimentTaskStatus> getBestResults(String experimentType, String dataset, Timestamp start,
            Timestamp end) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("experimentType", experimentType);
        parameters.addValue("dataset", dataset);
        parameters.addValue("after", start);
        parameters.addValue("before", end);

        List<ExperimentTaskStatus> result = this.template.query(GET_BEST_EXPERIMENT_DATE_TASK_RESULTS, parameters,
                new ExperimentTaskStatusRowMapper());
        for (ExperimentTaskStatus res : result) {
            addAllResults(res);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    public boolean isChallengeInDB(ChallengeDescr challenge) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("startDate", challenge.getStartDate());
        parameters.addValue("endDate", challenge.getEndDate());
        parameters.addValue("name", challenge.getName());
        List<ChallengeDescr> result = this.template.query(GET_CHALLENGE_DESCRIPTION, parameters,
                new ChallengeDescrRowMapper());
        return !result.isEmpty();
    }

    public List<ChallengeDescr> getAllChallenges() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        List<ChallengeDescr> result = this.template.query(GET_CHALLENGE_DESCRIPTIONS, parameters,
                new ChallengeDescrRowMapper());
        return result;
    }

    public void addChallenge(ChallengeDescr challenge) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("startDate", challenge.getStartDate());
        parameters.addValue("endDate", challenge.getEndDate());
        parameters.addValue("name", challenge.getName());
        this.template.update(INSERT_CHALLENGE_DESCRIPTIONS, parameters);
    }

    @Override
    public void setFile2SystemMapping(int experimentTaskId, String fileName, String systemName, String email) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        parameters.addValue("file", fileName);
        parameters.addValue("system", systemName);
        parameters.addValue("email", email);

        this.template.update(SET_FILE2SYSTEM_MAPPING, parameters);
    }

    @Override
    public void setFile2SystemMapping(int experimentTaskId, File2SystemEntry entry) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        parameters.addValue("file", entry.getFileName());
        parameters.addValue("system", entry.getSystemName());
        parameters.addValue("email", entry.getEmail());

        this.template.update(SET_FILE2SYSTEM_MAPPING, parameters);
    }

    @Override
    public List<File2SystemEntry> getFile2SystemByID(int experimentTaskId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", experimentTaskId);
        List<File2SystemEntry> entry = this.template.query(GET_FILE2SYSTEM_MAPPING, parameters,
                new File2SystemEntryRowMapper());
        return entry;
    }

    @Deprecated
    @Override
    protected List<String[]> getAnnotatorDatasetCombinations(String experimentType, String matching) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("experimentType", experimentType);
        params.addValue("matching", matching);
        return this.template.query(GET_LATEST_EXPERIMENT_TASKS, params,
                new StringArrayRowMapper(new int[] { 1, 2, 3 }));
    }

    @Deprecated
    @Override
    protected ExperimentTaskStatus getLatestExperimentTaskResult(String experimentType, String matching,
            String annotatorName, String datasetName) {
        MapSqlParameterSource params = createTaskParameters(annotatorName, datasetName, experimentType, matching);
        params.addValue("unfinishedState", TASK_STARTED_BUT_NOT_FINISHED_YET);
        List<ExperimentTaskStatus> result = this.template.query(GET_LATEST_EXPERIMENT_TASK_RESULT, params,
                new ExperimentTaskStatusRowMapper());
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer countExperiments() {
        List<Integer> result = this.template.query(GET_EXP_COUNT, new IntegerRowMapper());
        return result.get(0);
    }
}

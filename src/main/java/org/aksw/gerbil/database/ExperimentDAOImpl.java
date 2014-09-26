package org.aksw.gerbil.database;

import java.util.List;

import javax.sql.DataSource;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * 
 * @author b.eickmann
 * 
 */
public class ExperimentDAOImpl extends AbstractExperimentDAO {

  
    private final static String INSERT_TASK = "INSERT INTO ExperimentTasks (annotatorName, datasetName, experimentType, matching) VALUES (:annotatorName, :datasetName, :experimentType, :matching)";
    private final static String CONNECT_TASK_EXPERIMENT = "INSERT INTO Experiments (id, taskId) VALUES(:id, :taskId)";

    private final NamedParameterJdbcTemplate template;

    public ExperimentDAOImpl(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<ExperimentTaskResult> getResultsOfExperiment(String experimentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int createTask(String annotatorName, String datasetName, String experimentType, String matching,
            String experimentId) {
        SqlParameterSource params = createTaskParameters(annotatorName, datasetName, experimentType, matching,
                ExperimentDAO.TASK_STARTED_BUT_NOT_FINISHED_YET);
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
            String matching, int state) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("annotatorName", annotatorName);
        parameters.addValue("datasetName", datasetName);
        parameters.addValue("experimentType", experimentType);
        parameters.addValue("matching", matching);
        parameters.addValue("state", state);
        return parameters;
    }

    @Override
    public void setExperimentTaskResult(int experimentTaskId, ExperimentTaskResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setExperimentState(int experimentTaskId, int state) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getExperimentState(int experimentTaskId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected int getCachedExperimentTaskId(String annotatorName, String datasetName, String experimentType,
            String matching) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void connectExistingTaskWithExperiment(int experimentTaskId, String experimentId) {
        // TODO Auto-generated method stub

    }

}

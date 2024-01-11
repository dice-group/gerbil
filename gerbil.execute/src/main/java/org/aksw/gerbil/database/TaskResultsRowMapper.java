package org.aksw.gerbil.database;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.TaskResult;
import org.aksw.gerbil.evaluate.impl.AUCEvaluator;
import org.aksw.gerbil.evaluate.impl.PREvaluator;
import org.aksw.gerbil.evaluate.impl.ROCEvaluator;
import org.springframework.jdbc.core.RowMapper;

/**
 * Creates {@link TaskResultsRowMapper} instances from a given
 * {@link ResultSet}. Note that the following mapping is expected (column index
 * - value):
 * 
 * <ul>
 * <li>1 - result name</li>
 * <li>2 - result type</li>
 * <li>3 - result value</li>
 * </ul>
 * 
 * @author nikitsrivastava
 * 
 */
public class TaskResultsRowMapper implements RowMapper<TaskResult> {
	private ExperimentTaskStatus relatedTask;
	public TaskResultsRowMapper(ExperimentTaskStatus relatedTask) {
		this.relatedTask = relatedTask;
	}
	@Override
	public TaskResult mapRow(ResultSet resultSet, int rowId) throws SQLException {
		String resultName = resultSet.getString(1);
		String resultType = resultSet.getString(2);
		Object resultValue = null;
		if(resultType.equalsIgnoreCase("BLOB") && (resultName.equalsIgnoreCase(ROCEvaluator.NAME) || resultName.equalsIgnoreCase(PREvaluator.NAME))) {
			Blob rocBlob = resultSet.getBlob(3);
			byte[] bdata = rocBlob.getBytes(1, (int) rocBlob.length());
			resultValue = new String(bdata);
		}
		else {
			resultValue = resultSet.getObject(3);
		}
		
		TaskResult result = new TaskResult(resultValue,resultType);
		relatedTask.getResultsMap().put(resultName, result);
		return result;
	}

}

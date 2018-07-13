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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.aksw.gerbil.datatypes.ExperimentTaskStatus;
import org.aksw.gerbil.datatypes.TaskResult;
import org.springframework.jdbc.core.RowMapper;

/**
 * Creates {@link ExperimentTaskResult} instances from a given
 * {@link ResultSet}. Note that the following mapping is expected (column index
 * - value):
 * 
 * <ul>
 * <li>1 - annotator name</li>
 * <li>2 - dataset name</li>
 * <li>3 - experiment type</li>
 * <li>4 - matching</li>
 * <li>5 - micro F1 measure</li>
 * <li>6 - micro precision</li>
 * <li>7 - micro recall</li>
 * <li>8 - macro F1 measure</li>
 * <li>9 - macro precision</li>
 * <li>10 - macro recall</li>
 * <li>11 - state</li>
 * <li>12 - error count</li>
 * <li>13 - timestamp</li>
 * <li>14 - id inside the database (optional)</li>
 * </ul>
 * 
 * @author m.roeder
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
		Object resultValue = resultSet.getObject(3);
		TaskResult result = new TaskResult(resultValue,resultType);
		relatedTask.getResultsMap().put(resultName, result);
		return result;
	}

}

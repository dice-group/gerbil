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

import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.springframework.jdbc.core.RowMapper;

/**
 * Creates {@link ExperimentTaskResult} instances from a given {@link ResultSet}. Note that the following mapping is
 * expected (column index - value):
 * 
 * <ul>
 * <li>1 - annotator name</li>
 * <li>2 - dataset name</li>
 * <li>3 - experiment type</li>
 * <li>4 - matching</li>
 * <li>5 - state</li>
 * <li>6 - error count</li>
 * <li>7 - timestamp</li>
 * <li>8 - id inside the database (optional)</li>
 * </ul>
 * 
 * @author m.roeder
 * 
 */
public class LeaderBoardResultRowMapper implements RowMapper<ExperimentTaskResult> {

    @Override
    public ExperimentTaskResult mapRow(ResultSet resultSet, int rowId) throws SQLException {
        int idInDatabase = -1;
        try {
            idInDatabase = resultSet.getInt(10);
        } catch (Exception e) {
            // nothing to do
        }
        double[] results = new double[6];
        results[ExperimentTaskResult.MACRO_F1_MEASURE_INDEX] = resultSet.getDouble(5);
        results[ExperimentTaskResult.MICRO_F1_MEASURE_INDEX] = resultSet.getDouble(6);
        return new ExperimentTaskResult(resultSet.getString(2), resultSet.getString(1), resultSet.getString(3),
                ExperimentType.valueOf(resultSet.getString(4)), null,
                results,
                resultSet.getInt(7), resultSet.getInt(8), resultSet.getTimestamp(9).getTime(), idInDatabase);
    }

}
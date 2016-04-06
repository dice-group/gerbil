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
import org.aksw.gerbil.matching.Matching;
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
public class ExperimentTaskResultRowMapper implements RowMapper<ExperimentTaskResult> {

    @Override
    public ExperimentTaskResult mapRow(ResultSet resultSet, int rowId) throws SQLException {
        int idInDatabase = -1;
        try {
            idInDatabase = resultSet.getInt(14);
        } catch (Exception e) {
            // nothing to do
        }
        return new ExperimentTaskResult(resultSet.getString(1), resultSet.getString(2),
                ExperimentType.valueOf(resultSet.getString(3)), Matching.valueOf(resultSet.getString(4)),
                new double[] { resultSet.getDouble(5), resultSet.getDouble(6), resultSet.getDouble(7),
                        resultSet.getDouble(8), resultSet.getDouble(9), resultSet.getDouble(10) },
                resultSet.getInt(11), resultSet.getInt(12), resultSet.getTimestamp(13).getTime(), idInDatabase);
    }

}

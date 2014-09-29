package org.aksw.gerbil.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.aksw.gerbil.datatypes.ExperimentTaskResult;
import org.aksw.gerbil.datatypes.ExperimentType;
import org.aksw.gerbil.matching.Matching;
import org.springframework.jdbc.core.RowMapper;

public class ExperimentTaskResultRowMapper implements RowMapper<ExperimentTaskResult> {

    @Override
    public ExperimentTaskResult mapRow(ResultSet resultSet, int rowId) throws SQLException {
        return new ExperimentTaskResult(resultSet.getString(1), resultSet.getString(2),
                ExperimentType.valueOf(resultSet.getString(3)), Matching.valueOf(resultSet.getString(4)),
                new double[] { resultSet.getDouble(5), resultSet.getDouble(6), resultSet.getDouble(7),
                        resultSet.getDouble(8), resultSet.getDouble(9), resultSet.getDouble(10) },
                resultSet.getInt(11), resultSet.getInt(12), resultSet.getTimestamp(13).getTime());
    }

}

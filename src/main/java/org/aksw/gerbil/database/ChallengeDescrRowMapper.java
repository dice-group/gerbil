package org.aksw.gerbil.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.aksw.gerbil.datatypes.ChallengeDescr;
import org.springframework.jdbc.core.RowMapper;

public class ChallengeDescrRowMapper implements RowMapper<ChallengeDescr> {

    @Override
    public ChallengeDescr mapRow(ResultSet resultSet, int rowId) throws SQLException {
        return new ChallengeDescr(resultSet.getTimestamp(1), resultSet.getTimestamp(2), resultSet.getString(3));
    }

}

package org.aksw.gerbil.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StringArrayRowMapper implements RowMapper<String[]> {

    private int columns[];

    public StringArrayRowMapper(int[] columns) {
        this.columns = columns;
    }

    @Override
    public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
        String result[] = new String[columns.length];
        for (int i = 0; i < columns.length; ++i) {
            result[i] = rs.getString(columns[i]);
        }
        return result;
    }

}

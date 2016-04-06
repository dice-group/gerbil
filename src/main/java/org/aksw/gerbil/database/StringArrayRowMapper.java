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

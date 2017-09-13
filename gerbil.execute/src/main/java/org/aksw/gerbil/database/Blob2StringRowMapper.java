package org.aksw.gerbil.database;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class Blob2StringRowMapper implements RowMapper<String>{

	@Override
	public String mapRow(ResultSet arg0, int arg1) throws SQLException {
		Blob rocBlob = arg0.getBlob(1);
		byte[] bdata;
		try {
			bdata = rocBlob.getBytes(1, (int) rocBlob.length());
			return new String(bdata);
		} catch (SQLException e) {
//			LOGGER.warn("Could not get ROC Curve for experiment task.", e);
		}
		return null;
	}

}

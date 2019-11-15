package org.unidata.mdm.core.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.unidata.mdm.core.po.security.BaseTokenPO;
import org.unidata.mdm.core.po.security.TokenPO;

public class TokenRowMapper implements RowMapper<BaseTokenPO> {

	@Override
	public BaseTokenPO mapRow(ResultSet rs, int rowNum) throws SQLException {
		TokenPO result = new TokenPO();
		result.setId(rs.getInt(TokenPO.Fields.ID));
		result.setToken(rs.getString(TokenPO.Fields.TOKEN));
		result.setCreatedAt(rs.getTimestamp(TokenPO.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(TokenPO.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(TokenPO.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(TokenPO.Fields.UPDATED_BY));
		return result;
	}

}

package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.unidata.mdm.backend.service.security.po.BaseTokenPO;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.security.po.TokenPO;

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

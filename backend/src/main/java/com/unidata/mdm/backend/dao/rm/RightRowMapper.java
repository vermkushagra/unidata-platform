package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.security.po.RightPO;

/**
 * Row mapper for the RightPO class.
 * @author ilya.bykov
 */
public class RightRowMapper implements RowMapper<RightPO> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public RightPO mapRow(ResultSet rs, int rowNum) throws SQLException {
		RightPO result = new RightPO();
		result.setId(rs.getInt(RightPO.Fields.ID));
		result.setName(rs.getString(RightPO.Fields.NAME));
		result.setDescription(rs.getString(RightPO.Fields.DESCRIPTION));
		result.setCreatedAt(rs.getTimestamp(RightPO.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(RightPO.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(RightPO.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(RightPO.Fields.UPDATED_BY));
		return result;
	}

}

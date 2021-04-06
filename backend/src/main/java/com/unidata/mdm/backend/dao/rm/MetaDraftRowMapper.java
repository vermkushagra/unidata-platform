package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.po.MetaDraftPO;

public class MetaDraftRowMapper implements RowMapper<MetaDraftPO> {

	@Override
	public MetaDraftPO mapRow(ResultSet rs, int rowNum) throws SQLException {
		MetaDraftPO result = new MetaDraftPO();
		result.setActive(rs.getBoolean(MetaDraftPO.Field.ACTIVE));
		result.setCreatedAt(rs.getDate(MetaDraftPO.Field.CREATED_AT));
		result.setCreatedBy(rs.getString(MetaDraftPO.Field.CREATED_BY));
		result.setId(rs.getLong(MetaDraftPO.Field.ID));
		result.setName(rs.getString(MetaDraftPO.Field.NAME));
		result.setStorageId(rs.getString(MetaDraftPO.Field.STORAGE_ID));
		result.setType(MetaDraftPO.Type.valueOf(rs.getString(MetaDraftPO.Field.TYPE)));
		result.setUpdatedAt(rs.getDate(MetaDraftPO.Field.UPDATED_AT));
		result.setUpdatedBy(rs.getString(MetaDraftPO.Field.UPDATED_BY));
		result.setValue(rs.getBytes(MetaDraftPO.Field.VALUE));
		result.setVersion(rs.getLong(MetaDraftPO.Field.VERSION));
		return result;
	}

}

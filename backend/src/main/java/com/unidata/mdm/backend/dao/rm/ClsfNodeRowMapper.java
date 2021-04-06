package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;

/**
 * The Class ClsfNodeRowMapper.
 */
public class ClsfNodeRowMapper implements RowMapper<ClsfNodePO>{


	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public ClsfNodePO mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClsfNodePO result = new ClsfNodePO();
		result.setCode(rs.getString(ClsfNodePO.FieldColumns.CODE.name()));
		result.setCreatedAt(rs.getDate(ClsfNodePO.FieldColumns.CREATED_AT.name()));
		result.setCreatedBy(rs.getString(ClsfNodePO.FieldColumns.CREATED_BY.name()));
		result.setDescription(rs.getString(ClsfNodePO.FieldColumns.DESCRIPTION.name()));
		result.setId(rs.getInt(ClsfNodePO.FieldColumns.ID.name()));
		result.setName(rs.getString(ClsfNodePO.FieldColumns.NAME.name()));
		result.setNodeId(rs.getString(ClsfNodePO.FieldColumns.NODE_ID.name()));
		result.setParentId(rs.getString(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name()));
		result.setUpdatedAt(rs.getDate(ClsfNodePO.FieldColumns.UPDATED_AT.name()));
		result.setUpdatedBy(rs.getString(ClsfNodePO.FieldColumns.UPDATED_BY.name()));
		return result;
	}

}

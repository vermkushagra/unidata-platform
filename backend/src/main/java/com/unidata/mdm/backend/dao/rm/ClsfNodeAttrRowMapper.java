package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;

/**
 * The Class ClsfNodeAttrRowMapper.
 */
public class ClsfNodeAttrRowMapper implements RowMapper<ClsfNodeAttrPO> {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
	 */
	@Override
	public ClsfNodeAttrPO mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClsfNodeAttrPO result = new ClsfNodeAttrPO();
		result.setAttrName(rs.getString(ClsfNodeAttrPO.FieldColumns.ATTR_NAME.name()));
		result.setCreatedAt(rs.getDate(ClsfNodeAttrPO.FieldColumns.CREATED_AT.name()));
		result.setCreatedBy(rs.getString(ClsfNodeAttrPO.FieldColumns.CREATED_BY.name()));
		result.setDataType(rs.getString(ClsfNodeAttrPO.FieldColumns.DATA_TYPE.name()));
		result.setDefaultValue(rs.getString(ClsfNodeAttrPO.FieldColumns.DEFAULT_VALUE.name()));
		result.setDescription(rs.getString(ClsfNodeAttrPO.FieldColumns.DESCRIPTION.name()));
		result.setDisplayName(rs.getString(ClsfNodeAttrPO.FieldColumns.DISPLAY_NAME.name()));
		result.setHidden(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_HIDDEN.name()));
		result.setId(rs.getInt(ClsfNodeAttrPO.FieldColumns.ID.name()));
		result.setNodeId(rs.getInt(ClsfNodeAttrPO.FieldColumns.CLSF_NODE_ID.name()));
		result.setNullable(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_NULLABLE.name()));
		result.setReadOnly(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_READ_ONLY.name()));
		result.setSearchable(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_SEARCHABLE.name()));
		result.setUnique(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_UNIQUE.name()));
		result.setUpdatedAt(rs.getDate(ClsfNodeAttrPO.FieldColumns.UPDATED_AT.name()));
		result.setUpdatedBy(rs.getString(ClsfNodeAttrPO.FieldColumns.UPDATED_BY.name()));
		return result;
	}

}

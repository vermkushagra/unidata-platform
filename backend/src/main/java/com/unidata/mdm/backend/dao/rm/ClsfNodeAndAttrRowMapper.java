package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;

/**
 * The Class ClsfNodeAndAttrRowMapper.
 */
public class ClsfNodeAndAttrRowMapper implements ResultSetExtractor<List<ClsfNodePO>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.
	 * ResultSet)
	 */
	@Override
	public List<ClsfNodePO> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<Integer, ClsfNodePO> map = new HashMap<>();
		while (rs.next()) {
			ClsfNodePO node = new ClsfNodePO();
			node.setCode(rs.getString(ClsfNodePO.FieldColumns.CODE.name()));
			node.setCreatedAt(rs.getDate(ClsfNodePO.FieldColumns.CREATED_AT.name()));
			node.setCreatedBy(rs.getString(ClsfNodePO.FieldColumns.CREATED_BY.name()));
			node.setDescription(rs.getString(ClsfNodePO.FieldColumns.DESCRIPTION.name()));
			node.setId(rs.getInt(ClsfNodePO.FieldColumns.ID.name()));
			node.setName(rs.getString(ClsfNodePO.FieldColumns.NAME.name()));
			node.setNodeId(rs.getString(ClsfNodePO.FieldColumns.NODE_ID.name()));
			node.setParentId(rs.getString(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name()));
			node.setUpdatedAt(rs.getDate(ClsfNodePO.FieldColumns.UPDATED_AT.name()));
			node.setUpdatedBy(rs.getString(ClsfNodePO.FieldColumns.UPDATED_BY.name()));
			if (map.containsKey(node.getId())) {
				node = map.get(node.getId());
			} else {
				map.put(node.getId(), node);
			}
			ClsfNodeAttrPO nodeA = new ClsfNodeAttrPO();
			nodeA.setAttrName(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.ATTR_NAME.name()));
			nodeA.setCreatedAt(rs.getDate("_" + ClsfNodeAttrPO.FieldColumns.CREATED_AT.name()));
			nodeA.setCreatedBy(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.CREATED_BY.name()));
			nodeA.setDataType(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.DATA_TYPE.name()));
			nodeA.setDefaultValue(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.DEFAULT_VALUE.name()));
			nodeA.setDescription(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.DESCRIPTION.name()));
			nodeA.setDisplayName(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.DISPLAY_NAME.name()));
			nodeA.setHidden(rs.getBoolean("_" + ClsfNodeAttrPO.FieldColumns.IS_HIDDEN.name()));
			nodeA.setId(rs.getInt("_" + ClsfNodeAttrPO.FieldColumns.ID.name()));
			nodeA.setNodeId(rs.getInt("_" + ClsfNodeAttrPO.FieldColumns.CLSF_NODE_ID.name()));
			nodeA.setNullable(rs.getBoolean("_" + ClsfNodeAttrPO.FieldColumns.IS_NULLABLE.name()));
			nodeA.setReadOnly(rs.getBoolean("_" + ClsfNodeAttrPO.FieldColumns.IS_READ_ONLY.name()));
			nodeA.setSearchable(rs.getBoolean("_" + ClsfNodeAttrPO.FieldColumns.IS_SEARCHABLE.name()));
			nodeA.setUnique(rs.getBoolean("_" + ClsfNodeAttrPO.FieldColumns.IS_UNIQUE.name()));
			nodeA.setUpdatedAt(rs.getDate("_" + ClsfNodeAttrPO.FieldColumns.UPDATED_AT.name()));
			nodeA.setUpdatedBy(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.UPDATED_BY.name()));
			if (!StringUtils.isEmpty(nodeA.getAttrName())) {
				node.getNodeAttrs().add(nodeA);
			}
		}
		return map.values().stream().collect(Collectors.toList());
	}

}

/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.dao.rm;

import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
		result.setCustomProperties(rs.getString(ClsfNodePO.FieldColumns.CUSTOM_PROPS.name()));
		result.setClsfName(rs.getString(ClsfNodePO.FieldColumns.CLSF_NAME.name()));
		return result;
	}

}

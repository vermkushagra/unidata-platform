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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;

public class SystemElementRowMapper  implements RowMapper<SystemElementPO> {
/**
 * 	id, 
		element_type, 
		element_name, 
		element_folder, 
		element_description, 
		element_content,
		element_class, 
		created_at, 
		created_by, 
		updated_at, 
		updated_by
 */
	@Override
	public SystemElementPO mapRow(ResultSet rs, int rowNum) throws SQLException {
		SystemElementPO result = new SystemElementPO();
		result.setId(rs.getInt("id"));
		result.setType(ElementTypePO.valueOf(rs.getString("element_type")));
		result.setName(rs.getString("element_name"));
		result.setFolder(rs.getString("element_folder"));
		result.setDescription(rs.getString("element_description"));
		result.setContent(rs.getBytes("element_content"));
		result.setClassName(rs.getString("element_class"));
		result.setCreatedAt(rs.getTimestamp("created_at"));
		result.setCreatedBy(rs.getString("created_by"));
		result.setUpdatedAt(rs.getDate("updated_at"));
		result.setUpdatedBy(rs.getString("updated_by"));
		return result;
	}

}

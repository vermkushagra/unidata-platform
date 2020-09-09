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
//		result.setStorageId(rs.getString(MetaDraftPO.Field.STORAGE_ID));
		result.setType(MetaDraftPO.Type.valueOf(rs.getString(MetaDraftPO.Field.TYPE)));
		result.setUpdatedAt(rs.getDate(MetaDraftPO.Field.UPDATED_AT));
		result.setUpdatedBy(rs.getString(MetaDraftPO.Field.UPDATED_BY));
		result.setValue(rs.getBytes(MetaDraftPO.Field.VALUE));
		result.setVersion(rs.getLong(MetaDraftPO.Field.VERSION));
		return result;
	}

}

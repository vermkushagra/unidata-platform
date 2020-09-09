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

import com.unidata.mdm.backend.service.classifier.po.ClsfPO;

/**
 * The Class ClsfRowMapper.
 */
public class ClsfRowMapper implements RowMapper<ClsfPO> {

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public ClsfPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ClsfPO result = new ClsfPO();
        result.setCodePattern(rs.getString(ClsfPO.FieldColumns.CODE_PATTERN.name()));
        result.setCreatedAt(rs.getDate(ClsfPO.FieldColumns.CREATED_AT.name()));
        result.setCreatedBy(rs.getString(ClsfPO.FieldColumns.CREATED_BY.name()));
        result.setDescription(rs.getString(ClsfPO.FieldColumns.DESCRIPTION.name()));
        result.setDisplayName(rs.getString(ClsfPO.FieldColumns.DISPLAY_NAME.name()));
        result.setName(rs.getString(ClsfPO.FieldColumns.NAME.name()));
        result.setUpdatedAt(rs.getDate(ClsfPO.FieldColumns.UPDATED_AT.name()));
        result.setUpdatedBy(rs.getString(ClsfPO.FieldColumns.UPDATED_BY.name()));
        result.setValidateCodeByLevel(rs.getBoolean(ClsfPO.FieldColumns.VALIDATE_CODE_BY_LEVEL.name()));
        return result;
    }

}

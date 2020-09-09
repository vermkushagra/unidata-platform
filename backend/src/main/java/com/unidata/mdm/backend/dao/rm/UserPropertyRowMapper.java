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

/**
 * Date: 05.07.2016
 */

package com.unidata.mdm.backend.dao.rm;

import com.unidata.mdm.backend.service.security.po.UserPropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyPO.FieldColumns;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class UserPropertyRowMapper implements RowMapper<UserPropertyPO> {
    @Override
    public UserPropertyPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserPropertyPO result = new UserPropertyPO();

        result.setId(rs.getLong(FieldColumns.ID.name()));
        result.setName(rs.getString(FieldColumns.NAME.name()));
        result.setDisplayName(rs.getString(FieldColumns.DISPLAY_NAME.name()));

        return result;
    }
}

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

import com.unidata.mdm.backend.po.MetaStoragePO;

/**
 * @author Mikhail Mikhailov
 */
public class MetaStorageRowMapper implements RowMapper<MetaStoragePO> {
    @Override
    public MetaStoragePO mapRow(ResultSet rs, int rowNum) throws SQLException {

        MetaStoragePO storage = new MetaStoragePO();
        storage.setId(rs.getString(MetaStoragePO.FIELD_ID));
        storage.setName(rs.getString(MetaStoragePO.FIELD_NAME));
        storage.setCreateDate(rs.getTimestamp(MetaStoragePO.FIELD_CREATE_DATE));
        storage.setUpdateDate(rs.getTimestamp(MetaStoragePO.FIELD_UPDATE_DATE));
        storage.setCreatedBy(rs.getString(MetaStoragePO.FIELD_CREATED_BY));
        storage.setUpdatedBy(rs.getString(MetaStoragePO.FIELD_UPDATED_BY));

        return storage;
    }
}

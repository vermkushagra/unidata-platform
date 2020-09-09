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
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.po.LargeObjectPO;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class LargeObjectAbstractRowMapper {

    /**
     * Maps common part for large objects.
     * @param po the object
     * @param rs the {@link ResultSet}
     * @param rowNum row number
     * @throws SQLException
     */
    public void mapRow(LargeObjectPO po, ResultSet rs, int rowNum) throws SQLException {

        po.setId(rs.getString(LargeObjectPO.FIELD_ID));
        po.setCreateDate(rs.getDate(LargeObjectPO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(LargeObjectPO.FIELD_CREATED_BY));
        po.setUpdateDate(rs.getDate(LargeObjectPO.FIELD_UPDATE_DATE));
        po.setUpdatedBy(rs.getString(LargeObjectPO.FIELD_UPDATED_BY));
        po.setEtalonId(rs.getString(LargeObjectPO.FIELD_ETALON_ID));
        po.setOriginId(rs.getString(LargeObjectPO.FIELD_ORIGIN_ID));
        po.setEventId(rs.getString(LargeObjectPO.FIELD_EVENT_ID));
        po.setField(rs.getString(LargeObjectPO.FIELD_FIELD));
        po.setFileName(rs.getString(LargeObjectPO.FIELD_FILE_NAME));
        po.setMimeType(rs.getString(LargeObjectPO.FIELD_MIME_TYPE));
        po.setData(rs.getBinaryStream(LargeObjectPO.FIELD_DATA));
        po.setSize(rs.getLong(LargeObjectPO.FIELD_SIZE));
        po.setState(ApprovalState.valueOf(rs.getString(LargeObjectPO.FIELD_STATUS)));
    }
}

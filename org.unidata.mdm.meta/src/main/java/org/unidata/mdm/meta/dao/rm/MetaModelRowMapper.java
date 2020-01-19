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

package org.unidata.mdm.meta.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.unidata.mdm.meta.po.MetaModelPO;
import org.unidata.mdm.meta.type.ModelType;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
public class MetaModelRowMapper implements RowMapper<MetaModelPO> {
    @Override
    public MetaModelPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        MetaModelPO model = new MetaModelPO();
        model.setId(rs.getString(MetaModelPO.FIELD_ID));
        model.setStorageId(rs.getString(MetaModelPO.FIELD_STORAGE_ID));
        model.setType(ModelType.valueOf(rs.getString(MetaModelPO.FIELD_TYPE)));
        model.setData(rs.getString(MetaModelPO.FIELD_DATA));
        model.setVersion(rs.getInt(MetaModelPO.FIELD_VERSION));
        model.setCreateDate(rs.getTimestamp(MetaModelPO.FIELD_CREATE_DATE));
        model.setUpdateDate(rs.getTimestamp(MetaModelPO.FIELD_UPDATE_DATE));
        model.setCreatedBy(rs.getString(MetaModelPO.FIELD_CREATED_BY));
        model.setUpdatedBy(rs.getString(MetaModelPO.FIELD_UPDATED_BY));

        return model;
    }
}

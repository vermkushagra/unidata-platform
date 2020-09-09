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
import java.util.Map;
import java.util.function.BiFunction;

import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.util.collections.Maps;
import org.springframework.jdbc.core.RowMapper;

/**
 * The Class ClsfNodeAttrRowMapper.
 */
public class ClsfNodeAttrRowMapper implements RowMapper<ClsfNodeAttrPO> {

    private static final Map<String, BiFunction<ResultSet, ClsfNodeAttrPO, ClsfNodeAttrPO>> ATTR_OBJECT_ENRICHERS = Maps.of(
            "SIMPLE", ClsfDao.ATTR_OBJECT_ENRICHER_MAPPING.get("SIMPLE").apply(""),
            "ARRAY", ClsfDao.ATTR_OBJECT_ENRICHER_MAPPING.get("ARRAY").apply("")
    );

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public ClsfNodeAttrPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        final String attrType = rs.getString(ClsfNodeAttrPO.FieldColumns.ATTR_TYPE.name());
        final ClsfNodeAttrPO result = ClsfDao.ATTR_OBJECT_SUPPLIER_MAPPING.get(attrType).get();
        result.setAttrName(rs.getString(ClsfNodeAttrPO.FieldColumns.ATTR_NAME.name()));
        result.setCreatedAt(rs.getDate(ClsfNodeAttrPO.FieldColumns.CREATED_AT.name()));
        result.setCreatedBy(rs.getString(ClsfNodeAttrPO.FieldColumns.CREATED_BY.name()));
        result.setDataType(rs.getString(ClsfNodeAttrPO.FieldColumns.DATA_TYPE.name()));
        result.setLookupEntityType(rs.getString(ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_TYPE.name()));
        result.setLookupEntityCodeAttributeType(rs.getString(ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_DATA_TYPE.name()));
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
        result.setOrder(rs.getInt(ClsfNodeAttrPO.FieldColumns.ORDER.name()));
        result.setCustomProperties(rs.getString(ClsfNodeAttrPO.FieldColumns.CUSTOM_PROPS.name()));
        return ATTR_OBJECT_ENRICHERS.get(attrType).apply(rs, result);
    }

}

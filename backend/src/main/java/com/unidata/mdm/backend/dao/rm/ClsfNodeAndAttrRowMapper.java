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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeArrayAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeSimpleAttrPO;
import com.unidata.mdm.backend.util.collections.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;

/**
 * The Class ClsfNodeAndAttrRowMapper.
 */
public class ClsfNodeAndAttrRowMapper implements ResultSetExtractor<List<ClsfNodePO>> {

    private static final Map<String, BiFunction<ResultSet, ClsfNodeAttrPO, ClsfNodeAttrPO>> ATTR_OBJECT_ENRICHERS = Maps.of(
            "SIMPLE", ClsfDao.ATTR_OBJECT_ENRICHER_MAPPING.get("SIMPLE").apply("_"),
            "ARRAY", ClsfDao.ATTR_OBJECT_ENRICHER_MAPPING.get("ARRAY").apply("_")
    );

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.
     * ResultSet)
     */
    @Override
    public List<ClsfNodePO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Integer, ClsfNodePO> map = new HashMap<>();
        while (rs.next()) {
            final int nodeId = rs.getInt(ClsfNodePO.FieldColumns.ID.name());
            final ClsfNodePO node = map.computeIfAbsent(nodeId, (id) -> generateNode(rs));
            final String attrType = rs.getString("_" + ClsfNodeAttrPO.FieldColumns.ATTR_TYPE.name());
            if(StringUtils.isEmpty(attrType)) {
               continue;
            }
            final ClsfNodeAttrPO nodeA = ClsfDao.ATTR_OBJECT_SUPPLIER_MAPPING.get(attrType).get();
            nodeA.setAttrName(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.ATTR_NAME.name()));
            nodeA.setCreatedAt(rs.getDate("_" + ClsfNodeAttrPO.FieldColumns.CREATED_AT.name()));
            nodeA.setCreatedBy(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.CREATED_BY.name()));
            nodeA.setDataType(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.DATA_TYPE.name()));
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
            nodeA.setLookupEntityType(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_TYPE.name()));
            nodeA.setLookupEntityCodeAttributeType(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_DATA_TYPE.name()));
            nodeA.setOrder(rs.getInt("_" + ClsfNodeAttrPO.FieldColumns.ORDER.name()));
            nodeA.setCustomProperties(rs.getString("_" + ClsfNodeAttrPO.FieldColumns.CUSTOM_PROPS.name()));
            addAttrToNode(node, ATTR_OBJECT_ENRICHERS.get(attrType).apply(rs, nodeA));
        }
        return new ArrayList<>(map.values());
    }

    private ClsfNodePO generateNode(final ResultSet rs) {
        final ClsfNodePO node = new ClsfNodePO();
        try {
            node.setId(rs.getInt(ClsfNodePO.FieldColumns.ID.name()));
            node.setClsfName(rs.getString(ClsfNodePO.FieldColumns.CLSF_NAME.name()));
            node.setClsfName(rs.getString(ClsfNodePO.FieldColumns.CLSF_NAME.name()));
            node.setCode(rs.getString(ClsfNodePO.FieldColumns.CODE.name()));
            node.setCreatedAt(rs.getDate(ClsfNodePO.FieldColumns.CREATED_AT.name()));
            node.setCreatedBy(rs.getString(ClsfNodePO.FieldColumns.CREATED_BY.name()));
            node.setDescription(rs.getString(ClsfNodePO.FieldColumns.DESCRIPTION.name()));
            node.setName(rs.getString(ClsfNodePO.FieldColumns.NAME.name()));
            node.setNodeId(rs.getString(ClsfNodePO.FieldColumns.NODE_ID.name()));
            node.setParentId(rs.getString(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name()));
            node.setUpdatedAt(rs.getDate(ClsfNodePO.FieldColumns.UPDATED_AT.name()));
            node.setUpdatedBy(rs.getString(ClsfNodePO.FieldColumns.UPDATED_BY.name()));
            node.setCustomProperties(rs.getString(ClsfNodePO.FieldColumns.CUSTOM_PROPS.name()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return node;
    }

    private void addAttrToNode(final ClsfNodePO node, ClsfNodeAttrPO nodeAttr) {
        if (nodeAttr instanceof ClsfNodeArrayAttrPO) {
            node.getNodeArrayAttrs().add((ClsfNodeArrayAttrPO) nodeAttr);
        } else {
            node.getNodeSimpleAttrs().add((ClsfNodeSimpleAttrPO) nodeAttr);
        }
    }
}

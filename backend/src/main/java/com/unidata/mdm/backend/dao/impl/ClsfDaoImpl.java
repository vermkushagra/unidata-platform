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

package com.unidata.mdm.backend.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.configuration.BeanNameConstants;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.dao.rm.ClsfNodeAndAttrRowMapper;
import com.unidata.mdm.backend.dao.rm.ClsfNodeAttrRowMapper;
import com.unidata.mdm.backend.dao.rm.ClsfNodeRowMapper;
import com.unidata.mdm.backend.dao.rm.ClsfRowMapper;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeSimpleAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.collections.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


/**
 * The Class ClsfDaoImpl.
 */
@Repository
public class ClsfDaoImpl extends AbstractDaoImpl implements ClsfDao {

    /**
     * The clsf row mapper.
     */
    private static final ClsfRowMapper CLSF_ROW_MAPPER = new ClsfRowMapper();

    /**
     * The clsf node row mapper.
     */
    private static final ClsfNodeRowMapper CLSF_NODE_ROW_MAPPER = new ClsfNodeRowMapper();

    /**
     * The clsf node attr row mapper.
     */
    private static final ClsfNodeAttrRowMapper CLSF_NODE_ATTR_ROW_MAPPER = new ClsfNodeAttrRowMapper();

    /**
     * The clsf node and attr row mapper.
     */
    private static final ClsfNodeAndAttrRowMapper CLSF_NODE_AND_ATTR_ROW_MAPPER = new ClsfNodeAndAttrRowMapper();

    private static final String[] NODE_ID_COLUMNS = { "id" };

    /** The insert clsf. */
    @Autowired
    @Qualifier(BeanNameConstants.DEFAULT_OBJECT_MAPPER_BEAN_NAME)
    protected ObjectMapper objectMapper;

    /**
     * The insert clsf.
     */
    private String INSERT_CLSF;

    /**
     * The insert clsf node.
     */
    private String INSERT_CLSF_NODE;

    /**
     * The get all clsf nodes.
     */
    private String SELECT_ALL_CLSF_NODES;

    /**
     * The get all clsf.
     */
    private final String SELECT_ALL_CLSF;

    /**
     * The update clsf.
     */
    private final String UPDATE_CLSF_BY_NAME;

    /**
     * The update clsf node by node id.
     */
    private final String UPDATE_CLSF_NODE_BY_NODE_ID;

    /**
     * The delete clsf by name.
     */
    private final String DELETE_CLSF_BY_NAME;

    /**
     * The delete clsf node by node id.
     */
    private final String DELETE_CLSF_NODE_BY_NODE_ID;

    /**
     * The select clsf by name.
     */
    private final String SELECT_CLSF_BY_NAME;

    /**
     * The select all classifier node attrs.
     */
    private final String SELECT_ALL_CLASSIFIER_NODE_ATTRS;

    /** Gets the node and children by node id */
    private final String SELECT_CLSF_NODE_BY_NODE_ID_AND_CHILDREN;

    private final String selectClsfRootNode;

    /**
     * The Constant SELECT_CLSF_NODE_BY_CODE.
     */
    private String SELECT_CLSF_NODE_BY_CODE;

    /**
     * The insert clsf node attrs.
     */
    private final String insertClsfNodeAttrs;

    /**
     * The insert clsf node attrs.
     */
    private final String insertClsfNodeAttrsForDifirentNodes;

    /**
     * The delete clsf node attrs.
     */
    private final String deleteClsfNodeAttrs;

    /**
     * The select node id by path.
     */
    private final String selectNodeIdByPath;

    private final String deleteNodesByClsfId;

    private final String deleteOriginsClassifiersWhereNodesNotExists;

    private final String deleteEtalonsClassifiersWhereNoClassifiers;

    private final String selectClsfNodeByCodeAndNameAndParentId;

    private final String selectClsfNodeByCodeAndNameAndParentIdExceptNode;

    private final String findAllClassifiers;

    private final String selectOwnAttributesOnlySQL;

    private final String removeCodeAttrsValues;

    private final String selectNodesWithLookupAttributes;

    private final String containsCodeAttrsValue;

    private final String removeCodeAttrsWithLookupsLinks;

    private final String selectAttrsForOverrideCheck;

    /**
     * Instantiates a new clsf dao impl.
     *
     * @param dataSource the data source
     * @param sql the sql
     */
    @Autowired
    public ClsfDaoImpl(
            @Qualifier("unidataDataSource") final DataSource dataSource,
            @Qualifier("classifiers-sql") final Properties sql
    ) {
        super(dataSource);
        INSERT_CLSF = sql.getProperty("INSERT_CLSF");
        INSERT_CLSF_NODE = sql.getProperty("INSERT_CLSF_NODE");
        SELECT_ALL_CLSF_NODES = sql.getProperty("SELECT_ALL_CLSF_NODES");
        SELECT_ALL_CLSF = sql.getProperty("SELECT_ALL_CLSF");
        UPDATE_CLSF_BY_NAME = sql.getProperty("UPDATE_CLSF_BY_NAME");
        UPDATE_CLSF_NODE_BY_NODE_ID = sql.getProperty("UPDATE_CLSF_NODE_BY_NODE_ID");
        DELETE_CLSF_BY_NAME = sql.getProperty("DELETE_CLSF_BY_NAME");
        DELETE_CLSF_NODE_BY_NODE_ID = sql.getProperty("DELETE_CLSF_NODE_BY_NODE_ID");
        SELECT_CLSF_BY_NAME = sql.getProperty("SELECT_CLSF_BY_NAME");
        SELECT_ALL_CLASSIFIER_NODE_ATTRS = sql.getProperty("SELECT_ALL_CLASSIFIER_NODE_ATTRS");
        SELECT_CLSF_NODE_BY_NODE_ID_AND_CHILDREN = sql.getProperty("SELECT_CLSF_NODE_BY_NODE_ID_AND_CHILDREN");
        selectClsfRootNode = sql.getProperty("SELECT_CLSF_ROOT_NODE");
        SELECT_CLSF_NODE_BY_CODE = sql.getProperty("SELECT_CLSF_NODE_BY_CODE");
        insertClsfNodeAttrs = sql.getProperty("INSERT_CLSF_NODE_ATTRS");
        insertClsfNodeAttrsForDifirentNodes = sql.getProperty("INSERT_CLSF_NODE_ATTRS_FOR_DIFFERENT_NODES");
        deleteClsfNodeAttrs = sql.getProperty("DELETE_CLSF_NODE_ATTRS");
        selectNodeIdByPath = sql.getProperty("SELECT_NODE_ID_BY_PATH");
        deleteNodesByClsfId = sql.getProperty("DELETE_NODES_BY_CLSF_ID");
        deleteOriginsClassifiersWhereNodesNotExists =
                sql.getProperty("DELETE_ORIGINS_CLASSIFIERS_WHERE_NODES_NOT_EXISTS");
        deleteEtalonsClassifiersWhereNoClassifiers =
                sql.getProperty("DELETE_ETALONS_CLASSIFIERS_WHERE_NO_CLASSIFIERS");
        selectClsfNodeByCodeAndNameAndParentId =
                sql.getProperty("SELECT_CLSF_NODE_BY_CODE_AND_NAME_AND_PARENT_ID");
        selectClsfNodeByCodeAndNameAndParentIdExceptNode =
                sql.getProperty("SELECT_CLSF_NODE_BY_CODE_AND_NAME_AND_PARENT_ID_EXCEPT_NODE");
        findAllClassifiers = sql.getProperty("SELECT_ALL_CLASSIFIERS");
        selectOwnAttributesOnlySQL = sql.getProperty("selectOwnAttributesOnlySQL");
        removeCodeAttrsValues = sql.getProperty("REMOVE_CODE_ATTRS_VALUES");
        selectNodesWithLookupAttributes = sql.getProperty("SELECT_NODES_WITH_LOOKUP_ATTRIBUTES");
        containsCodeAttrsValue = sql.getProperty("CONTAINS_CODE_ATTRS_VALUE");
        removeCodeAttrsWithLookupsLinks = sql.getProperty("REMOVE_CODE_ATTRS_WITH_LOOKUPS_LINKS");
        selectAttrsForOverrideCheck = sql.getProperty("SELECT_ATTRS_FOR_OVERRIDE_CHECK");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#create(com.unidata.mdm.backend.
     * service.classifier.po.ClsfPO)
     */
    @Override
    public void create(ClsfPO toSave) {
        if (toSave == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfPO.FieldColumns.CODE_PATTERN.name().toLowerCase(), toSave.getCodePattern());
        params.put(ClsfPO.FieldColumns.CREATED_AT.name().toLowerCase(), toSave.getCreatedAt());
        params.put(ClsfPO.FieldColumns.CREATED_BY.name().toLowerCase(), toSave.getCreatedBy());
        params.put(ClsfPO.FieldColumns.DESCRIPTION.name().toLowerCase(), toSave.getDescription());
        params.put(ClsfPO.FieldColumns.DISPLAY_NAME.name().toLowerCase(), toSave.getDisplayName());
        params.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), toSave.getName());
        params.put(ClsfPO.FieldColumns.UPDATED_AT.name().toLowerCase(), toSave.getUpdatedAt());
        params.put(ClsfPO.FieldColumns.UPDATED_BY.name().toLowerCase(), toSave.getUpdatedBy());
        params.put(ClsfPO.FieldColumns.VALIDATE_CODE_BY_LEVEL.name().toLowerCase(), toSave.isValidateCodeByLevel());
        namedJdbcTemplate.update(INSERT_CLSF, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int create(final String clsfName, final ClsfNodePO toSave) {

        if (toSave == null) {
            return -1;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.NAME.name().toLowerCase(), toSave.getName());
        params.put(ClsfNodePO.FieldColumns.CODE.name().toLowerCase(), toSave.getCode());
        params.put(ClsfNodePO.FieldColumns.CREATED_AT.name().toLowerCase(), toSave.getCreatedAt());
        params.put(ClsfNodePO.FieldColumns.CREATED_BY.name().toLowerCase(), toSave.getCreatedBy());
        params.put(ClsfNodePO.FieldColumns.DESCRIPTION.name().toLowerCase(), toSave.getDescription());
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), toSave.getNodeId());
        params.put(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name().toLowerCase(), toSave.getParentId());
        params.put(ClsfNodePO.FieldColumns.UPDATED_AT.name().toLowerCase(), toSave.getUpdatedAt());
        params.put(ClsfNodePO.FieldColumns.UPDATED_BY.name().toLowerCase(), toSave.getUpdatedBy());
        params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);
        params.put(ClsfNodePO.FieldColumns.CUSTOM_PROPS.name().toLowerCase(), toSave.getCustomProperties());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbcTemplate.update(INSERT_CLSF_NODE, new MapSqlParameterSource(params), keyHolder, NODE_ID_COLUMNS);

        List<ClsfNodeAttrPO> attrs = new ArrayList<>(toSave.getNodeSimpleAttrs());
        attrs.addAll(toSave.getNodeArrayAttrs());
        insertNodeAttrs(attrs, toSave.getNodeId(), clsfName);

        return (int) keyHolder.getKey();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getAllClassifiers()
     */
    @Override
    public List<ClsfPO> getAllClassifiers() {
        return namedJdbcTemplate.query(SELECT_ALL_CLSF, CLSF_ROW_MAPPER);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getAllNodes(java.lang.String)
     */
    @Override
    public List<ClsfNodePO> getAllNodes(String clsfName) {
        Map<String, Object> params = new HashMap<>();
        params.put("CLSF_NAME", clsfName);
        return namedJdbcTemplate.query(SELECT_ALL_CLSF_NODES, params, CLSF_NODE_AND_ATTR_ROW_MAPPER);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#update(com.unidata.mdm.backend.
     * service.classifier.po.ClsfPO)
     */
    @Override
    public void update(ClsfPO toUpdate) {
        if (toUpdate == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfPO.FieldColumns.CODE_PATTERN.name().toLowerCase(), toUpdate.getCodePattern());
        params.put(ClsfPO.FieldColumns.CREATED_AT.name().toLowerCase(), toUpdate.getCreatedAt());
        params.put(ClsfPO.FieldColumns.CREATED_BY.name().toLowerCase(), toUpdate.getCreatedBy());
        params.put(ClsfPO.FieldColumns.DESCRIPTION.name().toLowerCase(), toUpdate.getDescription());
        params.put(ClsfPO.FieldColumns.DISPLAY_NAME.name().toLowerCase(), toUpdate.getDisplayName());
        params.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), toUpdate.getName());
        params.put(ClsfPO.FieldColumns.UPDATED_AT.name().toLowerCase(), toUpdate.getUpdatedAt());
        params.put(ClsfPO.FieldColumns.UPDATED_BY.name().toLowerCase(), toUpdate.getUpdatedBy());
        namedJdbcTemplate.update(UPDATE_CLSF_BY_NAME, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final String clsfName, final ClsfNodePO toUpdate) {

        if (toUpdate == null) {
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.NAME.name().toLowerCase(), toUpdate.getName());
        params.put(ClsfNodePO.FieldColumns.CODE.name().toLowerCase(), toUpdate.getCode());
        params.put(ClsfNodePO.FieldColumns.CREATED_AT.name().toLowerCase(), toUpdate.getCreatedAt());
        params.put(ClsfNodePO.FieldColumns.CREATED_BY.name().toLowerCase(), toUpdate.getCreatedBy());
        params.put(ClsfNodePO.FieldColumns.DESCRIPTION.name().toLowerCase(), toUpdate.getDescription());
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), toUpdate.getNodeId());
        params.put(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name().toLowerCase(), toUpdate.getParentId());
        params.put(ClsfNodePO.FieldColumns.UPDATED_AT.name().toLowerCase(), toUpdate.getUpdatedAt());
        params.put(ClsfNodePO.FieldColumns.UPDATED_BY.name().toLowerCase(), toUpdate.getUpdatedBy());
        params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);
        params.put(ClsfNodePO.FieldColumns.CUSTOM_PROPS.name().toLowerCase(), toUpdate.getCustomProperties());

        namedJdbcTemplate.update(UPDATE_CLSF_NODE_BY_NODE_ID, params);

        final List<ClsfNodeAttrPO> attrs = new ArrayList<>(toUpdate.getNodeSimpleAttrs());
        attrs.addAll(toUpdate.getNodeArrayAttrs());

        removeNodeAttr(toUpdate.getNodeId(), clsfName);
        insertNodeAttrs(attrs, toUpdate.getNodeId(), clsfName);
    }
    /**
     * Removes the node attr.
     *
     * @param nodeId the node id
     * @param clsfName the clsf name
     */
    private void removeNodeAttr(String nodeId, final String clsfName) {
        jdbcTemplate.update(deleteClsfNodeAttrs, clsfName, nodeId);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#remove(java.lang.String)
     */
    @Override
    public void remove(String clsfName) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), clsfName);
        namedJdbcTemplate.update(DELETE_CLSF_BY_NAME, params);

    }

    @Override
    public void remove(final String clsfName, final String nodeId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), nodeId);
        params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);
        namedJdbcTemplate.update(DELETE_CLSF_NODE_BY_NODE_ID, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<ClsfNodePO, List<ClsfNodePO>> getNodeAndChildrenById(final String clsfName, String id) {

        List<ClsfNodePO> result = jdbcTemplate.query(
                SELECT_CLSF_NODE_BY_NODE_ID_AND_CHILDREN,
                CLSF_NODE_ROW_MAPPER,
                clsfName,
                id,
                clsfName,
                id
        );

        ClsfNodePO node = null;
        List<ClsfNodePO> children = new ArrayList<>(result.size());
        for (ClsfNodePO po : result) {
            if (po.getNodeId().equals(id)) {
                node = po;
            } else {
                children.add(po);
            }
        }

        return node == null ? null : new ImmutablePair<>(node, children);
    }

    @Override
    public ClsfNodePO getRootNode(String clsfName) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);
        List<ClsfNodePO> result = namedJdbcTemplate.query(selectClsfRootNode, params, CLSF_NODE_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    @Override
    public void create(final String clsfName, final List<ClsfNodePO> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object>[] toInsert = new Map[nodes.size()];
        for (int i = 0; i < toInsert.length; i++) {
            ClsfNodePO toSave = nodes.get(i);
            Map<String, Object> params = new HashMap<>();
            params.put(ClsfNodePO.FieldColumns.NAME.name().toLowerCase(), toSave.getName());
            params.put(ClsfNodePO.FieldColumns.CODE.name().toLowerCase(), toSave.getCode());
            params.put(ClsfNodePO.FieldColumns.CREATED_AT.name().toLowerCase(), toSave.getCreatedAt());
            params.put(ClsfNodePO.FieldColumns.CREATED_BY.name().toLowerCase(), toSave.getCreatedBy());
            params.put(ClsfNodePO.FieldColumns.DESCRIPTION.name().toLowerCase(), toSave.getDescription());
            params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(),
                    toSave.getNodeId() != null ? toSave.getNodeId() : IdUtils.v1String());
            params.put(
                    ClsfNodePO.FieldColumns.PARENT_NODE_ID.name().toLowerCase(),
                    StringUtils.isNotEmpty(toSave.getParentId()) ? toSave.getParentId() : null
            );
            params.put(ClsfNodePO.FieldColumns.UPDATED_AT.name().toLowerCase(), toSave.getUpdatedAt());
            params.put(ClsfNodePO.FieldColumns.UPDATED_BY.name().toLowerCase(), toSave.getUpdatedBy());
            params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);
            params.put(ClsfNodePO.FieldColumns.CUSTOM_PROPS.name().toLowerCase(), toSave.getCustomProperties());
            toInsert[i] = params;
        }
        namedJdbcTemplate.batchUpdate(INSERT_CLSF_NODE, toInsert);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#insertNodeAttrs(java.util.List,
     * java.lang.String)
     */
    private void insertNodeAttrs(List<ClsfNodeAttrPO> nodeAttrs, String nodeId, String clsfName) {

        if (CollectionUtils.isEmpty(nodeAttrs)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object>[] toInsert = new Map[nodeAttrs.size()];
        for (int i = 0; i < toInsert.length; i++) {

            ClsfNodeAttrPO toSave = nodeAttrs.get(i);
            Map<String, Object> params = new HashMap<>();
            params.put(ClsfNodeAttrPO.FieldColumns.ATTR_NAME.name().toLowerCase(), toSave.getAttrName());
            params.put(ClsfNodeAttrPO.FieldColumns.CREATED_AT.name().toLowerCase(),
                    toSave.getCreatedAt() == null ? new Date() : toSave.getCreatedAt());
            params.put(ClsfNodeAttrPO.FieldColumns.CREATED_BY.name().toLowerCase(),
                    toSave.getCreatedBy() == null ? SecurityUtils.getCurrentUserName() : toSave.getCreatedBy());
            params.put(ClsfNodeAttrPO.FieldColumns.DATA_TYPE.name().toLowerCase(), toSave.getDataType());
            params.put(ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_TYPE.name().toLowerCase(), toSave.getLookupEntityType());
            params.put(ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_DATA_TYPE.name().toLowerCase(), toSave.getLookupEntityCodeAttributeType());
            params.put(ClsfNodeAttrPO.FieldColumns.DESCRIPTION.name().toLowerCase(), toSave.getDescription());
            params.put(ClsfNodeAttrPO.FieldColumns.DISPLAY_NAME.name().toLowerCase(), toSave.getDisplayName());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_HIDDEN.name().toLowerCase(), toSave.isHidden());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_NULLABLE.name().toLowerCase(), toSave.isNullable());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_READ_ONLY.name().toLowerCase(), toSave.isReadOnly());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_SEARCHABLE.name().toLowerCase(), toSave.isSearchable());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_UNIQUE.name().toLowerCase(), toSave.isUnique());
            final String attrType = ClsfDao.ATTR_TYPE_MAPPING.get(toSave.getClass());
            params.put(ClsfNodeAttrPO.FieldColumns.ATTR_TYPE.name().toLowerCase(), attrType);
            params.put(ClsfNodeAttrPO.FieldColumns.ORDER.name().toLowerCase(), toSave.getOrder());
            params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), nodeId);
            params.put(ClsfNodePO.FieldColumns.CUSTOM_PROPS.name().toLowerCase(), toSave.getCustomProperties());
            params.put("clsf_name", clsfName);
            toInsert[i] = params;
            toInsert[i] = ATTR_OBJECT_PARAMS_MAPPING.get(attrType).apply(params, toSave);
            toInsert[i].putIfAbsent(ClsfNodeSimpleAttrPO.FieldColumns.ENUM_DATA_TYPE.name().toLowerCase(), null);
        }

        namedJdbcTemplate.batchUpdate(insertClsfNodeAttrs, toInsert);
    }

    @Override
    public void insertNodeAttrs(final List<Pair<ClsfNodeDTO, ClsfNodeAttrPO>> attrs, final String clsfName) {
        if (attrs == null || attrs.size() == 0) {
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object>[] toInsert = new Map[attrs.size()];
        for (int i = 0; i < toInsert.length; i++) {
            ClsfNodeAttrPO toSave = attrs.get(i).getRight();
            Map<String, Object> params = new HashMap<>();
            params.put(ClsfNodeAttrPO.FieldColumns.ATTR_NAME.name().toLowerCase(), toSave.getAttrName());
            params.put(ClsfNodeAttrPO.FieldColumns.CREATED_AT.name().toLowerCase(),
                    toSave.getCreatedAt() == null ? new Date() : toSave.getCreatedAt());
            params.put(ClsfNodeAttrPO.FieldColumns.CREATED_BY.name().toLowerCase(),
                    toSave.getCreatedBy() == null ? SecurityUtils.getCurrentUserName() : toSave.getCreatedBy());
            params.put(ClsfNodeAttrPO.FieldColumns.DATA_TYPE.name().toLowerCase(), toSave.getDataType());
            params.put(ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_TYPE.name().toLowerCase(), toSave.getLookupEntityType());
            params.put(ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_DATA_TYPE.name().toLowerCase(), toSave.getLookupEntityCodeAttributeType());
            params.put(ClsfNodeAttrPO.FieldColumns.DESCRIPTION.name().toLowerCase(), toSave.getDescription());
            params.put(ClsfNodeAttrPO.FieldColumns.DISPLAY_NAME.name().toLowerCase(), toSave.getDisplayName());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_HIDDEN.name().toLowerCase(), toSave.isHidden());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_NULLABLE.name().toLowerCase(), toSave.isNullable());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_READ_ONLY.name().toLowerCase(), toSave.isReadOnly());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_SEARCHABLE.name().toLowerCase(), toSave.isSearchable());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_UNIQUE.name().toLowerCase(), toSave.isUnique());
            params.put(ClsfNodeSimpleAttrPO.FieldColumns.ENUM_DATA_TYPE.name().toLowerCase(),
                    toSave instanceof ClsfNodeSimpleAttrPO ? ((ClsfNodeSimpleAttrPO) toSave).getEnumDataType() : null);
            final String attrType = ClsfDao.ATTR_TYPE_MAPPING.get(toSave.getClass());
            params.put(ClsfNodeAttrPO.FieldColumns.ATTR_TYPE.name().toLowerCase(), attrType);
            params.put(ClsfNodeAttrPO.FieldColumns.ORDER.name().toLowerCase(), toSave.getOrder());
            params.put(ClsfNodeAttrPO.FieldColumns.CUSTOM_PROPS.name().toLowerCase(), toSave.getCustomProperties());

            params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), attrs.get(i).getLeft().getNodeId());
            params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);

            toInsert[i] = ATTR_OBJECT_PARAMS_MAPPING.get(attrType).apply(params, toSave);
        }
        namedJdbcTemplate.batchUpdate(insertClsfNodeAttrsForDifirentNodes, toInsert);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.ClsfDao#getClassifierByName(java.lang.String)
     */
    @Override
    public ClsfPO getClassifierByName(String clsfName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), clsfName);
        List<ClsfPO> result = namedJdbcTemplate.query(SELECT_CLSF_BY_NAME, paramMap, CLSF_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getAllClassifierAttrs(java.lang.
     * String)
     */
    @Override
    public List<ClsfNodeAttrPO> getAllClassifierAttrs(String classifierName) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), classifierName);
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), "root");
        params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), classifierName);
        return namedJdbcTemplate.query(SELECT_ALL_CLASSIFIER_NODE_ATTRS, params, CLSF_NODE_ATTR_ROW_MAPPER);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getNodeByCode(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ClsfNodePO getNodeByCode(String clsfName, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);
        params.put(ClsfNodePO.FieldColumns.CODE.name().toLowerCase(), code);
        List<ClsfNodePO> result = namedJdbcTemplate.query(SELECT_CLSF_NODE_BY_CODE, params, CLSF_NODE_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.ClsfDao#getNodeByPath(java.lang.String, java.lang.String)
     */
    @Override
    public ClsfNodePO getNodeByPath(String clsfName, String path) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);
        params.put(ClsfNodePO.FieldColumns.PATH.name().toLowerCase(), path);
        List<ClsfNodePO> result = namedJdbcTemplate.query(selectNodeIdByPath, params, CLSF_NODE_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#findOnlyNodeAttrs(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<ClsfNodeAttrPO> findOnlyNodeAttrs(final String clsfName, int nodeNumericId) {
        return jdbcTemplate.query(
                selectOwnAttributesOnlySQL,
                CLSF_NODE_ATTR_ROW_MAPPER,
                nodeNumericId,
                clsfName
        );
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.ClsfDao#isClsfNodeCodeExists(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isClsfNodeCodeExists(String clsfName, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CODE.name(), code);
        params.put(ClsfPO.FieldColumns.NAME.name(), clsfName);
        return namedJdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM clsf_node cn where cn.clsf_name = :NAME and cn.code = :CODE)",
                params,
                Boolean.class
        );
    }

    @Override
    public void removeAllNodesByClassifierName(final String clsfName) {
        namedJdbcTemplate.update(
                deleteNodesByClsfId,
                Collections.singletonMap("clsfName", clsfName)
        );
    }

    @Override
    public void removeOriginsLinksToClassifierNotExistsNodes(ClsfPO classifier) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("clsfName", classifier.getName());
        namedJdbcTemplate.update(
                deleteOriginsClassifiersWhereNodesNotExists,
                parameters
        );
    }

    @Override
    public void removeEtalonLinksToClassifierNotExistsNodes(ClsfPO classifier) {
        namedJdbcTemplate.update(
                deleteEtalonsClassifiersWhereNoClassifiers,
                Collections.singletonMap("clsfName", classifier.getName())
        );
    }

    @Override
    public ClsfNodePO findNodeByCodeAndNameAndParentId(
            final String clsfName,
            final String code,
            final String name,
            final String parentId,
            final String nodeId
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_NAME.name().toLowerCase(), clsfName);
        params.put(ClsfNodePO.FieldColumns.CODE.name().toLowerCase(), code);
        params.put(ClsfNodePO.FieldColumns.NAME.name().toLowerCase(), name);
        params.put(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name().toLowerCase(), parentId);
        if (StringUtils.isNotBlank(nodeId)) {
            params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), nodeId);
        }
        final String sql = StringUtils.isBlank(nodeId) ?
                selectClsfNodeByCodeAndNameAndParentId :
                selectClsfNodeByCodeAndNameAndParentIdExceptNode;
        List<ClsfNodePO> result = namedJdbcTemplate.query(sql, params, CLSF_NODE_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    @Override
    public List<ClsfPO> findAllClassifiers() {
        return namedJdbcTemplate.query(findAllClassifiers, CLSF_ROW_MAPPER);
    }

    @Override
    public void removeCodeAttrsValues(String lookupEntityName, String value) {
        namedJdbcTemplate.update(
                removeCodeAttrsValues,
                Maps.of("lookupEntityName", lookupEntityName, "value", value)
        );
    }

    @Override
    public List<ClsfNodePO> findNodesWithLookupAttributes(final String lookupEntityName) {
        return namedJdbcTemplate.query(
                selectNodesWithLookupAttributes,
                Collections.singletonMap("lookupEntityType", lookupEntityName),
                CLSF_NODE_AND_ATTR_ROW_MAPPER
        );
    }

    @Override
    public boolean containsCodeAttrsValue(String lookupEntityName, String value) {
        return namedJdbcTemplate.queryForObject(
                containsCodeAttrsValue,
                Maps.of("lookupEntityName", lookupEntityName, "value", value),
                Boolean.class
        );
    }

    @Override
    public void removeCodeAttrsWithLookupsLinks(Collection<String> lookupEntitiesIds) {
        namedJdbcTemplate.update(
                removeCodeAttrsWithLookupsLinks,
                Collections.singletonMap("lookups", lookupEntitiesIds)
        );
    }

    @Override
    public List<ClsfNodeAttrPO> fetchAttrsForCheck(final String nodeId, final List<String> attrsForCheck) {
        return namedJdbcTemplate.query(
                selectAttrsForOverrideCheck,
                Maps.of("nodeId", nodeId, "attrs", attrsForCheck),
                (rs, rowNum) -> {
                    final String attrType = rs.getString(ClsfNodeAttrPO.FieldColumns.ATTR_TYPE.name());
                    if (StringUtils.isEmpty(attrType)) {
                        throw new RuntimeException("No attr type for " + rs.getString("attr_name"));
                    }
                    final ClsfNodeAttrPO attr = ClsfDao.ATTR_OBJECT_SUPPLIER_MAPPING.get(attrType).get();
                    attr.setAttrName(rs.getString(ClsfNodeAttrPO.FieldColumns.ATTR_NAME.name()));
                    attr.setCreatedAt(rs.getDate(ClsfNodeAttrPO.FieldColumns.CREATED_AT.name()));
                    attr.setCreatedBy(rs.getString(ClsfNodeAttrPO.FieldColumns.CREATED_BY.name()));
                    attr.setDataType(rs.getString(ClsfNodeAttrPO.FieldColumns.DATA_TYPE.name()));
                    attr.setDescription(rs.getString(ClsfNodeAttrPO.FieldColumns.DESCRIPTION.name()));
                    attr.setDisplayName(rs.getString(ClsfNodeAttrPO.FieldColumns.DISPLAY_NAME.name()));
                    attr.setHidden(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_HIDDEN.name()));
                    attr.setId(rs.getInt(ClsfNodeAttrPO.FieldColumns.ID.name()));
                    attr.setNodeId(rs.getInt(ClsfNodeAttrPO.FieldColumns.CLSF_NODE_ID.name()));
                    attr.setNullable(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_NULLABLE.name()));
                    attr.setReadOnly(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_READ_ONLY.name()));
                    attr.setSearchable(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_SEARCHABLE.name()));
                    attr.setUnique(rs.getBoolean(ClsfNodeAttrPO.FieldColumns.IS_UNIQUE.name()));
                    attr.setUpdatedAt(rs.getDate(ClsfNodeAttrPO.FieldColumns.UPDATED_AT.name()));
                    attr.setUpdatedBy(rs.getString(ClsfNodeAttrPO.FieldColumns.UPDATED_BY.name()));
                    attr.setLookupEntityType(rs.getString(ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_TYPE.name()));
                    attr.setLookupEntityCodeAttributeType(rs.getString(ClsfNodeAttrPO.FieldColumns.LOOKUP_ENTITY_DATA_TYPE.name()));
                    attr.setOrder(rs.getInt(ClsfNodeAttrPO.FieldColumns.ORDER.name()));
                    attr.setCustomProperties(rs.getString(ClsfNodeAttrPO.FieldColumns.CUSTOM_PROPS.name()));
                    return attr;
                }
        );
    }
}

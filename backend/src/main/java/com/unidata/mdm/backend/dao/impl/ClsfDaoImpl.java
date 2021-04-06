package com.unidata.mdm.backend.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.util.collections.Maps;
import com.unidata.mdm.backend.util.collections.Transformations;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.dao.rm.ClsfNodeAndAttrRowMapper;
import com.unidata.mdm.backend.dao.rm.ClsfNodeAttrRowMapper;
import com.unidata.mdm.backend.dao.rm.ClsfNodeRowMapper;
import com.unidata.mdm.backend.dao.rm.ClsfRowMapper;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;



/**
 * The Class ClsfDaoImpl.
 */
@Repository
public class ClsfDaoImpl extends AbstractDaoImpl implements ClsfDao {

    /** The clsf row mapper. */
    private static final ClsfRowMapper CLSF_ROW_MAPPER = new ClsfRowMapper();

    /** The clsf node row mapper. */
    private static final ClsfNodeRowMapper CLSF_NODE_ROW_MAPPER = new ClsfNodeRowMapper();

    /** The clsf node attr row mapper. */
    private static final ClsfNodeAttrRowMapper CLSF_NODE_ATTR_ROW_MAPPER = new ClsfNodeAttrRowMapper();

    /** The clsf node and attr row mapper. */
    private static final ClsfNodeAndAttrRowMapper CLSF_NODE_AND_ATTR_ROW_MAPPER = new ClsfNodeAndAttrRowMapper();

    /** The insert clsf. */
    private String INSERT_CLSF;

    /** The insert clsf node. */
    private String INSERT_CLSF_NODE;

    /** The get all clsf nodes. */
    private String SELECT_ALL_CLSF_NODES;

    /** The get all clsf. */
    private String SELECT_ALL_CLSF;

    /** The update clsf. */
    private String UPDATE_CLSF_BY_NAME;

    /** The update clsf node by node id. */
    private String UPDATE_CLSF_NODE_BY_NODE_ID;

    /** The select clsf db id. */
    private String SELECT_CLSF_DB_ID;

    /** The select clsf node db id. */
    private String SELECT_CLSF_NODE_DB_ID;

    /** The delete clsf by name. */
    private String DELETE_CLSF_BY_NAME;

    /** The delete clsf node by node id. */
    private String DELETE_CLSF_NODE_BY_NODE_ID;

    /** The select clsf by name. */
    private String SELECT_CLSF_BY_NAME;

    /** The select child node ids. */
    private String SELECT_CHILD_NODE_IDS;

    /** The select parent node ids. */
    private String SELECT_PARENT_NODE_IDS;

    /** The select node attrs. */
    private String SELECT_NODE_ATTRS;

    /** The select all classifier node attrs. */
    private String SELECT_ALL_CLASSIFIER_NODE_ATTRS;

    /** The select clsf node by node id. */
    private String SELECT_CLSF_NODE_BY_NODE_ID;

    private final String selectClsfRootNode;

    /** The select clsf node by parent id. */
    private String selectClsfNodeByParentId;

    private String selectClsfNodeWithChildCountAndHasAttrsByParentId;

    /** The Constant SELECT_CLSF_NODE_BY_CODE. */
    private String SELECT_CLSF_NODE_BY_CODE;

    /** The select node attrs by node db id. */
    private String SELECT_NODE_ATTRS_BY_NODE_DB_ID;

    /** The insert clsf node attrs. */
    private final String insertClsfNodeAttrs;

    /** The insert clsf node attrs. */
    private final String insertClsfNodeAttrsForDifirentNodes;

    /** The delete clsf node attrs. */
    private final String deleteClsfNodeAttrs;

    /** The select node id by path. */
    private final String selectNodeIdByPath;

    private final String deleteNodesByClsfId;

    private final String deleteOriginsClassifiersWhereNodesNotExists;

    private final String deleteEtalonsClassifiersWhereNoClassifiers;

    private final String selectClsfNodeByCodeAndNameAndParentId;

    private final String findAllClassifiers;

    private final String selectAttributesInClassifierByName;

    private final String selectAttributesOnlyForNode;

    /**
     * Instantiates a new clsf dao impl.
     *
     * @param dataSource
     *            the data source
     */
//    public ClsfDaoImpl(DataSource dataSource) {
//        super(dataSource);
//    }

    /**
     * Instantiates a new clsf dao impl.
     *
     * @param dataSource
     *            the data source
     * @param sql
     *            the sql
     */
    @Autowired
    public ClsfDaoImpl(DataSource dataSource, @Qualifier("classifiers-sql") final Properties sql) {
        super(dataSource);
        INSERT_CLSF = sql.getProperty("INSERT_CLSF");
        INSERT_CLSF_NODE = sql.getProperty("INSERT_CLSF_NODE");
        SELECT_ALL_CLSF_NODES = sql.getProperty("SELECT_ALL_CLSF_NODES");
        SELECT_ALL_CLSF = sql.getProperty("SELECT_ALL_CLSF");
        UPDATE_CLSF_BY_NAME = sql.getProperty("UPDATE_CLSF_BY_NAME");
        UPDATE_CLSF_NODE_BY_NODE_ID = sql.getProperty("UPDATE_CLSF_NODE_BY_NODE_ID");
        SELECT_CLSF_DB_ID = sql.getProperty("SELECT_CLSF_DB_ID");
        SELECT_CLSF_NODE_DB_ID = sql.getProperty("SELECT_CLSF_NODE_DB_ID");
        DELETE_CLSF_BY_NAME = sql.getProperty("DELETE_CLSF_BY_NAME");
        DELETE_CLSF_NODE_BY_NODE_ID = sql.getProperty("DELETE_CLSF_NODE_BY_NODE_ID");
        SELECT_CLSF_BY_NAME = sql.getProperty("SELECT_CLSF_BY_NAME");
        SELECT_CHILD_NODE_IDS = sql.getProperty("SELECT_CHILD_NODE_IDS");
        SELECT_PARENT_NODE_IDS = sql.getProperty("SELECT_PARENT_NODE_IDS");
        SELECT_NODE_ATTRS = sql.getProperty("SELECT_NODE_ATTRS");
        SELECT_ALL_CLASSIFIER_NODE_ATTRS = sql.getProperty("SELECT_ALL_CLASSIFIER_NODE_ATTRS");
        SELECT_CLSF_NODE_BY_NODE_ID = sql.getProperty("SELECT_CLSF_NODE_BY_NODE_ID");
        selectClsfRootNode = sql.getProperty("SELECT_CLSF_ROOT_NODE");
        selectClsfNodeByParentId = sql.getProperty("SELECT_CLSF_NODE_BY_PARENT_ID");
        selectClsfNodeWithChildCountAndHasAttrsByParentId =
                sql.getProperty("SELECT_CLSF_NODE_WITH_CHILD_COUNT_AND_HAS_ATTRS_BY_PARENT_ID");
        SELECT_CHILD_NODE_IDS = sql.getProperty("SELECT_CHILD_NODE_IDS");
        SELECT_CLSF_NODE_BY_CODE = sql.getProperty("SELECT_CLSF_NODE_BY_CODE");
        SELECT_NODE_ATTRS_BY_NODE_DB_ID = sql.getProperty("SELECT_NODE_ATTRS_BY_NODE_DB_ID");
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
        findAllClassifiers = sql.getProperty("SELECT_ALL_CLASSIFIERS");
        selectAttributesInClassifierByName = sql.getProperty("SELECT_ATTRIBUTES_IN_CLASSIFIER_BY_NAME");
        selectAttributesOnlyForNode = sql.getProperty("SELECT_ONLY_NODE_ATTRS");
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

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#isClsfExists(java.lang.String)
     */
    @Override
    public boolean isClsfExists(String clsfName) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), clsfName);
        List<?> result = namedJdbcTemplate.queryForList(SELECT_CLSF_DB_ID, params);
        return result != null && result.size() != 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#create(java.lang.String,
     * com.unidata.mdm.backend.service.classifier.po.ClsfNodePO)
     */
    @Override
    public void create(String clsfName, ClsfNodePO toSave) {
        if (toSave == null) {
            return;
        }
        int clsfId = resolveClsfDBId(clsfName);
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
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
        namedJdbcTemplate.update(INSERT_CLSF_NODE, params);
        removeNodeAttr(toSave.getNodeId(), clsfName);
        insertNodeAttrs(toSave.getNodeAttrs(), toSave.getNodeId(), clsfName);

    }

    /**
     * Select classifier id by name.
     *
     * @param clsfName
     *            classifier name.
     * @return database id.
     */
    private int resolveClsfDBId(String clsfName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), clsfName);
        int result = namedJdbcTemplate.queryForObject(SELECT_CLSF_DB_ID, paramMap, Integer.class);
        return result;
    }

    /**
     * Resolve clsf node DB id.
     *
     * @param clsfNodeId            the clsf node id
     * @param clsfName the clsf name
     * @return the int
     */
    private int resolveClsfNodeDBId(String clsfNodeId, String clsfName) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), clsfNodeId);
        paramMap.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), resolveClsfDBId(clsfName));
        int result = namedJdbcTemplate.queryForObject(SELECT_CLSF_NODE_DB_ID, paramMap, Integer.class);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.ClsfDao#isClsfNodeExists(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean isClsfNodeExists(String clsfName, String nodeId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name(), nodeId);
        params.put(ClsfPO.FieldColumns.NAME.name(), clsfName);
        return namedJdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM clsf_node cn inner join clsf cl on cn.clsf_id=cl.id where cl.name=:NAME and cn.node_id=:NODE_ID)", params,
                Boolean.class);
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
        int clsfId = resolveClsfDBId(toUpdate.getName());
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfPO.FieldColumns.CODE_PATTERN.name().toLowerCase(), toUpdate.getCodePattern());
        params.put(ClsfPO.FieldColumns.CREATED_AT.name().toLowerCase(), toUpdate.getCreatedAt());
        params.put(ClsfPO.FieldColumns.CREATED_BY.name().toLowerCase(), toUpdate.getCreatedBy());
        params.put(ClsfPO.FieldColumns.DESCRIPTION.name().toLowerCase(), toUpdate.getDescription());
        params.put(ClsfPO.FieldColumns.DISPLAY_NAME.name().toLowerCase(), toUpdate.getDisplayName());
        params.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), toUpdate.getName());
        params.put(ClsfPO.FieldColumns.UPDATED_AT.name().toLowerCase(), toUpdate.getUpdatedAt());
        params.put(ClsfPO.FieldColumns.UPDATED_BY.name().toLowerCase(), toUpdate.getUpdatedBy());
        params.put(ClsfPO.FieldColumns.ID.name().toLowerCase(), clsfId);
        namedJdbcTemplate.update(UPDATE_CLSF_BY_NAME, params);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#update(com.unidata.mdm.backend.
     * service.classifier.po.ClsfNodePO)
     */
    @Override
    public void update(ClsfNodePO toUpdate, String clsfName) {
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
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), resolveClsfDBId(clsfName));
        namedJdbcTemplate.update(UPDATE_CLSF_NODE_BY_NODE_ID, params);
        removeNodeAttr(toUpdate.getNodeId(), clsfName);
        insertNodeAttrs(toUpdate.getNodeAttrs(), toUpdate.getNodeId(), clsfName);

    }

    /**
     * Removes the node attr.
     *
     * @param nodeId            the node id
     * @param clsfName the clsf name
     */
    private void removeNodeAttr(String nodeId, String clsfName) {
        int dbNodeId = resolveClsfNodeDBId(nodeId, clsfName);
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodeAttrPO.FieldColumns.CLSF_NODE_ID.name().toLowerCase(), dbNodeId);
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), resolveClsfDBId(clsfName));
        namedJdbcTemplate.update(deleteClsfNodeAttrs, params);

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

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#remove(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void remove(String clsfName, String nodeId) {
        int clsfId = resolveClsfDBId(clsfName);
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), nodeId);
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
        namedJdbcTemplate.update(DELETE_CLSF_NODE_BY_NODE_ID, params);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.ClsfDao#getNodesByParentId(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<ClsfNodePO> getNodesByParentId(String clsfName, String parentId) {
        int clsfId = resolveClsfDBId(clsfName);
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
        params.put(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name().toLowerCase(), parentId);
        List<ClsfNodePO> result = namedJdbcTemplate.query(selectClsfNodeByParentId, params, CLSF_NODE_ROW_MAPPER);
        return result;
    }

    @Override
    public List<Triple<ClsfNodePO, Integer, Boolean>> findNodesByParentIdWithChildCountAndHasAttrs(
            final String clsfName,
            final String parentId
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), clsfName);
        params.put(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name().toLowerCase(), parentId);
        return namedJdbcTemplate.query(selectClsfNodeWithChildCountAndHasAttrsByParentId, params, (rs, rowNum) -> {
            final ClsfNodePO clsfNodePO = CLSF_NODE_ROW_MAPPER.mapRow(rs, rowNum);
            return Triple.of(
                    clsfNodePO,
                    rs.getInt("count"),
                    rs.getBoolean("hasAttrs")
            );
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getNodeById(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ClsfNodePO getNodeById(String clsfName, String nodeId) {
        int clsfId = resolveClsfDBId(clsfName);
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), nodeId);
        List<ClsfNodePO> result = namedJdbcTemplate.query(SELECT_CLSF_NODE_BY_NODE_ID, params, CLSF_NODE_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    @Override
    public ClsfNodePO getRootNode(String clsfName) {
        int clsfId = resolveClsfDBId(clsfName);
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
        List<ClsfNodePO> result = namedJdbcTemplate.query(selectClsfRootNode, params, CLSF_NODE_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#create(java.util.List)
     */
    @Override
    public void create(final String clsfName, final List<ClsfNodePO> nodes) {
        create(resolveClsfDBId(clsfName), nodes);
    }

    @Override
    public void create(final int clsfId, final List<ClsfNodePO> nodes) {
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
            params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
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
    @Override
    public void insertNodeAttrs(List<ClsfNodeAttrPO> nodeAttrs, String nodeId, String clsfName) {
        if (nodeAttrs == null || nodeAttrs.size() == 0) {
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
            params.put(ClsfNodeAttrPO.FieldColumns.DEFAULT_VALUE.name().toLowerCase(), toSave.getDefaultValue());
            params.put(ClsfNodeAttrPO.FieldColumns.DESCRIPTION.name().toLowerCase(), toSave.getDescription());
            params.put(ClsfNodeAttrPO.FieldColumns.DISPLAY_NAME.name().toLowerCase(), toSave.getDisplayName());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_HIDDEN.name().toLowerCase(), toSave.isHidden());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_NULLABLE.name().toLowerCase(), toSave.isNullable());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_READ_ONLY.name().toLowerCase(), toSave.isReadOnly());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_SEARCHABLE.name().toLowerCase(), toSave.isSearchable());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_UNIQUE.name().toLowerCase(), toSave.isUnique());
            params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), nodeId);
            params.put(ClsfPO.FieldColumns.NAME.name().toLowerCase(), clsfName);
            toInsert[i] = params;
        }
        namedJdbcTemplate.batchUpdate(insertClsfNodeAttrs, toInsert);

    }

    @Override
    public void insertNodeAttrs(final List<Pair<ClsfNodeDTO,ClsfNodeAttrPO>> attrs, final int clsfId) {
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
            params.put(ClsfNodeAttrPO.FieldColumns.DEFAULT_VALUE.name().toLowerCase(), toSave.getDefaultValue());
            params.put(ClsfNodeAttrPO.FieldColumns.DESCRIPTION.name().toLowerCase(), toSave.getDescription());
            params.put(ClsfNodeAttrPO.FieldColumns.DISPLAY_NAME.name().toLowerCase(), toSave.getDisplayName());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_HIDDEN.name().toLowerCase(), toSave.isHidden());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_NULLABLE.name().toLowerCase(), toSave.isNullable());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_READ_ONLY.name().toLowerCase(), toSave.isReadOnly());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_SEARCHABLE.name().toLowerCase(), toSave.isSearchable());
            params.put(ClsfNodeAttrPO.FieldColumns.IS_UNIQUE.name().toLowerCase(), toSave.isUnique());
            params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), attrs.get(i).getLeft().getNodeId());
            params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
            toInsert[i] = params;
        }
        namedJdbcTemplate.batchUpdate(insertClsfNodeAttrsForDifirentNodes, toInsert);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getAllNodeIds(java.lang.String)
     */
    @Override
    public List<String> getAllNodeIds(String classifierName) {
        return null;
        // not needed, remove
        // return namedJdbcTemplate.queryForList(sql, paramMap, String.class);
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
     * @see com.unidata.mdm.backend.dao.ClsfDao#getNodesToRoot(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<String> getNodesToRoot(String ownNodeId, String classifierName) {
        int clsfId = resolveClsfDBId(classifierName);
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), ownNodeId);

        return namedJdbcTemplate.queryForList(SELECT_PARENT_NODE_IDS, params, String.class);
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
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), resolveClsfDBId(classifierName));
        List<ClsfNodeAttrPO> result = namedJdbcTemplate.query(SELECT_ALL_CLASSIFIER_NODE_ATTRS, params,
                CLSF_NODE_ATTR_ROW_MAPPER);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getNodeByCode(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ClsfNodePO getNodeByCode(String clsfName, String code) {
        int clsfId = resolveClsfDBId(clsfName);
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfId);
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
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), clsfName);
        params.put(ClsfNodePO.FieldColumns.PATH.name().toLowerCase(), path);
        List<ClsfNodePO> result = namedJdbcTemplate.query(selectNodeIdByPath, params, CLSF_NODE_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getNodeAttrs(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<ClsfNodeAttrPO> getNodeAttrs(String classifierName, String ownNodeId) {
        return findClsfNodeAttrs(classifierName, ownNodeId, SELECT_NODE_ATTRS);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#getOnlyNodeAttrs(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<ClsfNodeAttrPO> getOnlyNodeAttrs(String classifierName, String ownNodeId) {
        return findClsfNodeAttrs(classifierName, ownNodeId, selectAttributesOnlyForNode);
    }

    private List<ClsfNodeAttrPO> findClsfNodeAttrs(String classifierName, String ownNodeId, String query) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.NODE_ID.name().toLowerCase(), ownNodeId);
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), resolveClsfDBId(classifierName));
        return namedJdbcTemplate.query(query, params, CLSF_NODE_ATTR_ROW_MAPPER);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#countChilds(java.lang.String)
     */
    @Override
    public int countChilds(String nodeId, String clsfName) {
        Map<String, Object> params = new HashMap<>();
        params.put("node_id", nodeId);
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), resolveClsfDBId(clsfName));
        return namedJdbcTemplate.queryForObject("select count(*) from clsf_node where parent_node_id=:node_id  and clsf_id=:clsf_id", params,
                Integer.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.ClsfDao#isOwnAttrs(java.lang.String)
     */
    @Override
    public boolean isOwnAttrs(String nodeId, String clsfName) {
        Map<String, Object> params = new HashMap<>();
        params.put("node_id", nodeId);
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), resolveClsfDBId(clsfName));
        return namedJdbcTemplate.queryForObject(
                "select count(*)from clsf_node_attr where clsf_node_id=(select id from clsf_node where node_id=:node_id and clsf_id=:clsf_id)",
                params, Integer.class) != 0;

    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.ClsfDao#isClsfNodeCodeExists(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isClsfNodeCodeExists(String clsfName, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CODE.name(), code);
        params.put(ClsfPO.FieldColumns.NAME.name(), clsfName);
        return namedJdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM clsf_node cn inner join clsf cl on cn.clsf_id=cl.id where cl.name=:NAME and cn.code=:CODE)", params,
                Boolean.class);
    }

    @Override
    public void removeAllNodesByClassifierId(final int classifierId) {
        namedJdbcTemplate.update(
                deleteNodesByClsfId,
                Collections.singletonMap("clsfId", classifierId)
        );
    }

    @Override
    public void removeOriginsLinksToClassifierNotExistsNodes(ClsfPO classifier) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("clsfName", classifier.getName());
        parameters.put("clsfId", classifier.getId());
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
    public ClsfNodePO findNodeByCodeAndNameAndParentId(String clsfName, String code, String name, String parentId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ClsfNodePO.FieldColumns.CLSF_ID.name().toLowerCase(), resolveClsfDBId(clsfName));
        params.put(ClsfNodePO.FieldColumns.CODE.name().toLowerCase(), code);
        params.put(ClsfNodePO.FieldColumns.NAME.name().toLowerCase(), name);
        params.put(ClsfNodePO.FieldColumns.PARENT_NODE_ID.name().toLowerCase(), parentId);
        List<ClsfNodePO> result = namedJdbcTemplate.query(selectClsfNodeByCodeAndNameAndParentId, params, CLSF_NODE_ROW_MAPPER);
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    @Override
    public List<ClsfPO> findAllClassifiers() {
        return namedJdbcTemplate.query(findAllClassifiers, CLSF_ROW_MAPPER);
    }

    @Override
    public Map<String, List<String>> findNodesWithPresentAttributesInClassifier(
            final String clsfName,
            final String nodeId,
            final List<String> names
    ) {
        if (CollectionUtils.isEmpty(names)) {
            return Collections.emptyMap();
        }
        final List<Map<String, Object>> rows = namedJdbcTemplate.queryForList(
                selectAttributesInClassifierByName,
                Maps.of("attrs_names", names, "clsfName", clsfName, "nodeId", nodeId)
        );
        return Transformations.fold(rows, new HashMap<String, List<String>>(), (result, row) -> {
            final String classifierNodeName = (String) row.get("classifierNodeName");
            result.computeIfAbsent(classifierNodeName, (key) -> new ArrayList<>());
            result.get(classifierNodeName).add((String) row.get("attr_name"));
            return result;
        });
    }
}

package com.unidata.mdm.backend.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.dao.SecurityLabelDao;
import com.unidata.mdm.backend.service.security.po.LabelAttributeValuePO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.dao.RoleDao;
import com.unidata.mdm.backend.dao.rm.LabelAttributeRowMapper;
import com.unidata.mdm.backend.dao.rm.LabelRowMapper;
import com.unidata.mdm.backend.dao.rm.ResourceRowMapper;
import com.unidata.mdm.backend.dao.rm.RightRowMapper;
import com.unidata.mdm.backend.dao.rm.RolePropertyRowMapper;
import com.unidata.mdm.backend.dao.rm.RolePropertyValueRowMapper;
import com.unidata.mdm.backend.dao.rm.RoleRowMapper;
import com.unidata.mdm.backend.service.security.po.LabelAttributePO;
import com.unidata.mdm.backend.service.security.po.LabelPO;
import com.unidata.mdm.backend.service.security.po.ResourcePO;
import com.unidata.mdm.backend.service.security.po.ResourceRightPO;
import com.unidata.mdm.backend.service.security.po.RightPO;
import com.unidata.mdm.backend.service.security.po.RolePO;
import com.unidata.mdm.backend.service.security.po.RolePropertyPO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO.FieldColumns;

/**
 * The Class RoleDAO.
 */
@Repository
public class RoleDaoImpl extends AbstractDaoImpl implements RoleDao {

    private static final String CONNECTION_TABLE = "s_role_s_label_attribute_value";

    /**
     * Combined roles query.
     */
    private final String LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_USER_LOGIN_SQL;
    /**
     * TODO start to use.
     */
    private final String LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_ROLE_NAME_SQL;
    private final String LOAD_ALL_ROLE_PROPERTIES;
    private final String LOAD_ROLE_PROPERTY_BY_NAME;
    private final String LOAD_ROLE_PROPERTY_BY_DISPLAY_NAME;
    private final String INSERT_ROLE_PROPERTY;
    private final String UPDATE_ROLE_PROPERTY_BY_ID;
    private final String DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_PROPERTY_ID;
    private final String DELETE_ROLE_PROPERTY_BY_ID;
    private final String INSERT_ROLE_PROPERTY_VALUE;
    private final String UPDATE_ROLE_PROPERTY_VALUE_BY_ID;
    private final String DELETE_ROLE_PROPERTY_VALUES_BY_IDS;
    private final String DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_ID;
    private final String LOAD_ROLE_PROPERTY_VALUES_BY_ROLE_IDS;
    private final String INSERT_RESOURCE_SQL;
    private final String DROP_ALL_NON_SYSTEM_RESOURCES_SQL;
    private final String DELETE_RESOURCES_BY_CATEGORY_SQL;
    private final String LOAD_ALL_RESOURCES_SQL;
    private final String INSERT_ROLE_RESOURCE_RIGHT_LINK_SQL;
    private final String UPDATE_ROLE_RESOURCE_RIGHT_LINK_SQL;
    private final String DELETE_BY_ROLE_RESOURCE_RIGHT_LINK_SQL;

    @Autowired
    private DaoHelper daoHelper;

    /** The right row mapper. */
    private RightRowMapper rightRowMapper = new RightRowMapper();

    /** The role property row mapper. */
    private RolePropertyRowMapper rolePropertyRowMapper = new RolePropertyRowMapper();

    /** The role property value row mapper. */
    private RolePropertyValueRowMapper rolePropertyValueRowMapper = new RolePropertyValueRowMapper();

    private final SecurityLabelDao securityLabelDao;

    /**
     * Instantiates a new role dao.
     *
     * @param dataSource
     *            the data source
     */
    @Autowired
    public RoleDaoImpl(final DataSource dataSource, final @Qualifier("security-sql") Properties sql) {
        super(dataSource);
        LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_USER_LOGIN_SQL = sql.getProperty("LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_USER_LOGIN_SQL");
        LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_ROLE_NAME_SQL = sql.getProperty("LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_ROLE_NAME_SQL");
        LOAD_ALL_ROLE_PROPERTIES = sql.getProperty("LOAD_ALL_ROLE_PROPERTIES");
        LOAD_ROLE_PROPERTY_BY_NAME = sql.getProperty("LOAD_ROLE_PROPERTY_BY_NAME");
        LOAD_ROLE_PROPERTY_BY_DISPLAY_NAME = sql.getProperty("LOAD_ROLE_PROPERTY_BY_DISPLAY_NAME");
        INSERT_ROLE_PROPERTY = sql.getProperty("INSERT_ROLE_PROPERTY");
        UPDATE_ROLE_PROPERTY_BY_ID = sql.getProperty("UPDATE_ROLE_PROPERTY_BY_ID");
        DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_PROPERTY_ID = sql.getProperty("DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_PROPERTY_ID");
        DELETE_ROLE_PROPERTY_BY_ID = sql.getProperty("DELETE_ROLE_PROPERTY_BY_ID");
        INSERT_ROLE_PROPERTY_VALUE = sql.getProperty("INSERT_ROLE_PROPERTY_VALUE");
        UPDATE_ROLE_PROPERTY_VALUE_BY_ID = sql.getProperty("UPDATE_ROLE_PROPERTY_VALUE_BY_ID");
        DELETE_ROLE_PROPERTY_VALUES_BY_IDS = sql.getProperty("DELETE_ROLE_PROPERTY_VALUES_BY_IDS");
        DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_ID = sql.getProperty("DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_ID");
        LOAD_ROLE_PROPERTY_VALUES_BY_ROLE_IDS = sql.getProperty("LOAD_ROLE_PROPERTY_VALUES_BY_ROLE_IDS");
        INSERT_RESOURCE_SQL = sql.getProperty("INSERT_RESOURCE_SQL");
        DROP_ALL_NON_SYSTEM_RESOURCES_SQL = sql.getProperty("DROP_ALL_NON_SYSTEM_RESOURCES_SQL");
        DELETE_RESOURCES_BY_CATEGORY_SQL = sql.getProperty("DELETE_RESOURCES_BY_CATEGORY_SQL");
        LOAD_ALL_RESOURCES_SQL = sql.getProperty("LOAD_ALL_RESOURCES_SQL");
        INSERT_ROLE_RESOURCE_RIGHT_LINK_SQL = sql.getProperty("INSERT_ROLE_RESOURCE_RIGHT_LINK_SQL");
        UPDATE_ROLE_RESOURCE_RIGHT_LINK_SQL = sql.getProperty("UPDATE_ROLE_RESOURCE_RIGHT_LINK_SQL");
        DELETE_BY_ROLE_RESOURCE_RIGHT_LINK_SQL = sql.getProperty("DELETE_BY_ROLE_RESOURCE_RIGHT_LINK_SQL");

        securityLabelDao = new SecurityLabelDaoImpl(CONNECTION_TABLE, dataSource, sql);
    }

    /**
     * Creates the.
     *
     * @param role
     *            the role
     * @return the role po
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#create(com.unidata.mdm.backend.service.security.po.RolePO)
     */
    @Override
    @Transactional
    public RolePO create(RolePO role) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(RolePO.Fields.UPDATED_AT, role.getUpdatedAt());
        paramMap.put(RolePO.Fields.UPDATED_BY, role.getUpdatedBy());
        paramMap.put(RolePO.Fields.CREATED_AT, role.getCreatedAt());
        paramMap.put(RolePO.Fields.CREATED_BY, role.getCreatedBy());
        paramMap.put(RolePO.Fields.NAME, role.getName());
        paramMap.put(RolePO.Fields.R_TYPE, role.getRType());
        paramMap.put(RolePO.Fields.DESCRIPTION, role.getDescription());
        paramMap.put(RolePO.Fields.DISPLAY_NAME, role.getDisplayName());
        namedJdbcTemplate.update(RolePO.Queries.INSERT_NEW, paramMap);
        role.setId(findByName(role.getName()).getId());
        return role;
    }

    /**
     * Find by name.
     *
     * @param name
     *            the name
     * @return the role po
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#findByName(java.lang.String)
     */
    @Override
    @Transactional
    public RolePO findByName(String name) {

        MeasurementPoint.start();
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(RolePO.Fields.NAME, name);
            List<RolePO> result = namedJdbcTemplate.query(RolePO.Queries.SELECT_BY_NAME, paramMap,
                    RoleRowMapper.DEFAULT_ROLE_ROW_MAPPER);

            if (CollectionUtils.isEmpty(result)) {
                return null;
            }

            RolePO role = result.get(0);
            paramMap.put(RightPO.Fields.S_ROLE_ID, role.getId());
            String sql = "select sre.id, sre.name, sre.r_type, sre.category, sre.display_name, srr.id as link_id, sr.* from s_right sr inner join s_right_s_resource srr on sr.id=srr.s_right_id inner join s_resource sre on srr.s_resource_id=sre.id where srr.s_role_id=?";
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, role.getId());
            while (rs.next()) {

                RightPO right = new RightPO();
                right.setId(rs.getInt(7));
                right.setName(rs.getString(8));
                right.setDescription(rs.getString(9));
                right.setCreatedAt(rs.getTimestamp(10));
                right.setUpdatedAt(rs.getTimestamp(11));
                right.setCreatedBy(rs.getString(12));
                right.setUpdatedBy(rs.getString(13));

                ResourcePO resource = new ResourcePO();
                resource.setId(rs.getInt(1));
                resource.setName(rs.getString(2));
                resource.setRType(rs.getString(3));
                resource.setCategory(rs.getString(4));
                resource.setDisplayName(rs.getString(5));

                ResourceRightPO resourceRightPO = new ResourceRightPO();
                resourceRightPO.setId(rs.getInt(6));
                resourceRightPO.setRight(right);
                resourceRightPO.setResource(resource);
                resourceRightPO.setRole(role);
                role.getConnectedResourceRights().add(resourceRightPO);
            }
            role.setLabelPOs(findSecurityLabelsByRoleName(role.getName()));
            role.setLabelAttributeValues(securityLabelDao.findLabelsAttributesValuesForObject(role.getId()));
            role.setProperties(loadRolePropertyValuesByRoleId(role.getId()));
            return role;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Find right by name.
     *
     * @param name
     *            the name
     * @return the right po
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#findRightByName(java.lang.String)
     */
    @Override
    @Transactional
    public RightPO findRightByName(String name) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(RightPO.Fields.NAME, name);
        List<RightPO> result = namedJdbcTemplate.query(RightPO.Queries.SELECT_BY_NAME, paramMap, rightRowMapper);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * Find resource by name.
     *
     * @param name
     *            the name
     * @return the resource po
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#findResourceByName(java.lang.String)
     */
    @Override
    @Transactional
    public ResourcePO findResourceByName(String name) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(ResourcePO.Fields.NAME, name);
        List<ResourcePO> result = namedJdbcTemplate.query(ResourcePO.Queries.SELECT_BY_NAME, paramMap,
                ResourceRowMapper.DEFAULT_ROW_MAPPER);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * Update.
     *
     * @param name
     *            the name
     * @param role
     *            the role
     * @return the role po
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#update(java.lang.String, com.unidata.mdm.backend.service.security.po.RolePO)
     */
    @Override
    @Transactional
    public RolePO update(String name, RolePO role) {
        Map<String, Object> params = new HashMap<>();
        params.put(RolePO.Fields.UPDATED_AT, role.getUpdatedAt());
        params.put(RolePO.Fields.UPDATED_BY, role.getUpdatedBy());
        params.put(RolePO.Fields.CREATED_AT, role.getCreatedAt());
        params.put(RolePO.Fields.CREATED_BY, role.getCreatedBy());
        params.put(RolePO.Fields.NAME, role.getName());
        params.put(RolePO.Fields.R_TYPE, role.getRType());
        params.put(RolePO.Fields.DESCRIPTION, role.getDescription());
        params.put(RolePO.Fields.DISPLAY_NAME, role.getDisplayName());
        namedJdbcTemplate.update(RolePO.Queries.UPDATE_BY_NAME, params);
        return role;
    }

    /**
     * Update.
     * @param roleName
     *            the role name
     * @param newRole
     *            the new role
     * @param securityLabels
     *            the label names
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#update(java.lang.String, com.unidata.mdm.backend.service.security.po.RolePO, java.util.List)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void update(String roleName, RolePO newRole, List<SecurityLabel> securityLabels) {

        // 1. Role general
        update(roleName, newRole);

        // 2. Labels
        RolePO updated = findByName(newRole.getName());

        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(RolePO.Fields.S_ROLE_ID, updated.getId());
        namedJdbcTemplate.update(RolePO.Queries.CLEAN_LABELS, paramMap);

        if (CollectionUtils.isNotEmpty(securityLabels)) {
            final List<Map<String, Object>> params = new ArrayList<>();
            securityLabels.stream().map(SecurityLabel::getName).forEach(labelName -> {
                Map<String, Object> toAttach = new HashMap<>();
                toAttach.put(RolePO.Fields.S_ROLE_ID, updated.getId());
                toAttach.put(RolePO.Fields.NAME, labelName);
                params.add(toAttach);
            });
            namedJdbcTemplate.batchUpdate(RolePO.Queries.ATTACH_LABELS, params.toArray(new Map[params.size()]));
        }

        securityLabelDao.saveLabelsForObject(updated.getId(), securityLabels);
        securityLabelDao.cleanUsersLabels(roleName);

        // 3. Single permissions connect
        List<ResourceRightPO> resourcesToCreate = CollectionUtils.isEmpty(newRole.getConnectedResourceRights())
                ? Collections.emptyList()
                : newRole.getConnectedResourceRights().stream().filter(po -> po.getId() == null).collect(Collectors.toList());
        List<ResourceRightPO> resourcesToUpdate = CollectionUtils.isEmpty(newRole.getConnectedResourceRights())
                ? Collections.emptyList()
                : newRole.getConnectedResourceRights().stream().filter(po -> po.getId() != null).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(resourcesToCreate)) {
            jdbcTemplate.batchUpdate(INSERT_ROLE_RESOURCE_RIGHT_LINK_SQL, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ResourceRightPO po = resourcesToCreate.get(i);
                    ps.setInt(1, updated.getId());
                    ps.setInt(2, po.getResource().getId());
                    ps.setInt(3, po.getRight().getId());
                    ps.setString(4, po.getCreatedBy());
                }

                @Override
                public int getBatchSize() {
                    return resourcesToCreate.size();
                }
            });
        }

        if (!CollectionUtils.isEmpty(resourcesToUpdate)) {
            jdbcTemplate.batchUpdate(UPDATE_ROLE_RESOURCE_RIGHT_LINK_SQL, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ResourceRightPO po = resourcesToUpdate.get(i);
                    ps.setString(1, po.getUpdatedBy());
                    ps.setInt(2, po.getId());
                }

                @Override
                public int getBatchSize() {
                    return resourcesToUpdate.size();
                }
            });
        }

        // 4. Single permissions disconnect
        if (!CollectionUtils.isEmpty(newRole.getDisconnectedResourceRights())) {
            jdbcTemplate.batchUpdate(DELETE_BY_ROLE_RESOURCE_RIGHT_LINK_SQL, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ResourceRightPO po = newRole.getDisconnectedResourceRights().get(i);
                    ps.setInt(1, po.getRole().getId());
                    ps.setInt(2, po.getResource().getId());
                    ps.setInt(3, po.getRight().getId());
                }

                @Override
                public int getBatchSize() {
                    return newRole.getDisconnectedResourceRights().size();
                }
            });
        }
    }

    /**
     * Delete.
     *
     * @param name
     *            the name
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#delete(java.lang.String)
     */
    @Override
    @Transactional
    public void delete(String name) {
        Map<String, Object> params = new HashMap<>();
        RolePO toDelete = findByName(name);
        params.put(RolePO.Fields.ID, toDelete.getId());
        params.put(RolePO.Fields.S_ROLE_ID, toDelete.getId());
        params.put(RolePO.Fields.S_ROLES_ID, toDelete.getId());
        namedJdbcTemplate.update(RolePO.Queries.CLEAN_PROPERTIES, params);
        namedJdbcTemplate.update(RolePO.Queries.CLEAN_RESOURCES, params);
        namedJdbcTemplate.update(RolePO.Queries.CLEAN_USERS, params);
        namedJdbcTemplate.update(RolePO.Queries.CLEAN_LABELS, params);
        namedJdbcTemplate.update(RolePO.Queries.DELETE_BY_ID, params);
    }

    /**
     * Gets the all.
     *
     * @return the all
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#getAll()
     */
    @Override
    @Transactional
    public List<RolePO> getAll() {
        MeasurementPoint.start();
        try {
            List<RolePO> roles = namedJdbcTemplate.query(RolePO.Queries.SELECT_ALL,
                    RoleRowMapper.DEFAULT_ROLE_ROW_MAPPER);

            Map<Integer, List<RolePropertyValuePO>> properties
                = loadRolePropertyValuesByRoleIds(roles.stream()
                        .map(RolePO::getId)
                        .collect(Collectors.toCollection(ArrayList::new)));

            for (RolePO rolePO : roles) {
                rolePO.setLabelPOs(findSecurityLabelsByRoleName(rolePO.getName()));
                rolePO.setLabelAttributeValues(securityLabelDao.findLabelsAttributesValuesForObject(rolePO.getId()));
                rolePO.setProperties(properties.get(rolePO.getId()));
            }

            return roles;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets combined roles, rights and resources by user login name.
     * @param login the user login
     * @return list of roles.
     */
    @Override
    public List<RolePO> findRolesByUserLogin(String login) {

        Map<Integer, RolePO> roles = new HashMap<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(LOAD_ROLES_RESOURCES_AND_RIGHTS_BY_USER_LOGIN_SQL, login, login);

        while (rs.next()) {

            Integer roleId = rs.getInt("role_id");
            RolePO current = roles.get(roleId);
            if (current == null) {
                current = new RolePO();
                current.setId(roleId);
                current.setName(rs.getString("role_name"));
                current.setRType(rs.getString("role_type"));
                current.setDisplayName(rs.getString("role_display_name"));
                current.setDescription(rs.getString("role_description"));

                roles.put(roleId, current);
            }
			int rightId = rs.getInt("right_id");
			RightPO right = new RightPO();
			if (rightId != 0) {
				right.setId(rightId);
				right.setName(rs.getString("right_name"));
				right.setDescription(rs.getString("right_description"));
			}
			int resourceId = rs.getInt("resource_id");
			ResourcePO resource = new ResourcePO();
			if (resourceId != 0) {
				resource.setId(rs.getInt("resource_id"));
				resource.setName(rs.getString("resource_name"));
				resource.setRType(rs.getString("resource_type"));
				resource.setCategory(rs.getString("resource_category"));
				resource.setDisplayName(rs.getString("resource_display_name"));
			}
			int resourceRightId = rs.getInt("resource_right_id");
			if (resourceRightId != 0) {
				ResourceRightPO resourceRight = new ResourceRightPO();
				resourceRight.setId(resourceRightId);
				resourceRight.setRight(right);
				resourceRight.setResource(resource);
				resourceRight.setRole(current);

				if (current.getConnectedResourceRights() == null) {
					current.setConnectedResourceRights(new ArrayList<>());
				}

				current.getConnectedResourceRights().add(resourceRight);
			}
		}

        Map<Integer, List<RolePropertyValuePO>> properties
            = loadRolePropertyValuesByRoleIds(roles.keySet());

        properties.forEach((key, value) -> {

            RolePO r = roles.get(key);
            if (Objects.nonNull(r)) {
                r.setProperties(value);
            }
        });

        Map<Integer, List<LabelAttributeValuePO>> securityLabels = loadRoleSecurityLabelsByRoleIds(roles.keySet());
        securityLabels.forEach((key, value) -> {
            RolePO r = roles.get(key);
            if (r != null) {
                r.setLabelAttributeValues(value);
            }
        });

        return new ArrayList<>(roles.values());
    }

    private Map<Integer, List<LabelAttributeValuePO>> loadRoleSecurityLabelsByRoleIds(Set<Integer> ids) {
        return ids.stream()
                .map(id -> Pair.of(id, securityLabelDao.findLabelsAttributesValuesForObject(id)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private List<LabelPO> toLabelsPOs(List<LabelAttributeValuePO> labelsAttributesValuesForObject) {
        final Map<Integer, LabelPO> labelsPOs = new HashMap<>();
        labelsAttributesValuesForObject.stream()
                .map(lav -> lav.getLabelAttribute().getLabel())
                .forEach(l -> labelsPOs.putIfAbsent(l.getId(), l));
        return new ArrayList<>(labelsPOs.values());
    }

    /**
     * Gets the all secured res.
     *
     * @return the all secured res
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#getAllSecuredRes()
     */
    @Override
    @Transactional
    public List<ResourcePO> getAllSecurityResources() {
        return jdbcTemplate.query(LOAD_ALL_RESOURCES_SQL, ResourceRowMapper.DEFAULT_ROW_MAPPER);
    }

    /**
     * Gets the all security labels.
     *
     * @return the all security labels
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#getAllSecurityLabels()
     */
    @Override
    @Transactional
    public List<LabelPO> getAllSecurityLabels() {
        List<LabelPO> result = namedJdbcTemplate.query(LabelPO.Queries.SELECT_ALL,
                LabelRowMapper.DEFAULT_LABEL_ROW_MAPPER);
        attachAttributes(result);
        return result;
    }

    /**
     * Find security label by name.
     *
     * @param name
     *            the name
     * @return the label po
     */
    @Override
    @Transactional
    public LabelPO findSecurityLabelByName(String name) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(LabelPO.Fields.NAME, name);
        List<LabelPO> result = namedJdbcTemplate.query(LabelPO.Queries.SELECT_BY_NAME, paramMap,
                LabelRowMapper.DEFAULT_LABEL_ROW_MAPPER);
        attachAttributes(result);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }

        return result.get(0);
    }

    /*
     * TODO: merge this part with queries from above and below.
     */
    private void attachAttributes(List<LabelPO> labels) {

        if(labels == null){
            return;
        }

        for (LabelPO labelPO : labels) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(LabelAttributePO.Fields.S_LABEL_ID, labelPO.getId());
            List<LabelAttributePO> result = namedJdbcTemplate.query(LabelAttributePO.Queries.SELECT_BY_LABEL_ID,
                    paramMap, LabelAttributeRowMapper.DEFAULT_ROW_MAPPER);
            labelPO.setLabelAttributePO(result);
        }
    }

    @Override
    @Transactional
    public List<LabelPO> findSecurityLabelsByRoleName(String roleName) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(LabelPO.Fields.ROLE_NAME, roleName);
        List<LabelPO> result = namedJdbcTemplate.query(LabelPO.Queries.SELECT_BY_ROLE_NAME, paramMap,
                LabelRowMapper.DEFAULT_LABEL_ROW_MAPPER);

        //attachAttributes(result);
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }

        return result;
    }

    /**
     * Delete security label by name.
     *
     * @param name
     *            the name
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#deleteSecurityLabelByName(java.lang.String)
     */
    @Override
    @Transactional
    public void deleteSecurityLabelByName(String name) {
        Map<String, Object> params = new HashMap<>();
        LabelPO toDelete = findSecurityLabelByName(name);
        params.put(LabelPO.Fields.ID, toDelete.getId());
        params.put(LabelPO.Fields.S_LABEL_ID, toDelete.getId());
        namedJdbcTemplate.update(LabelPO.Queries.CLEAN_ROLE_LABELS, params);
        namedJdbcTemplate.update(LabelPO.Queries.CLEAN_LABEL_ATTRIBUTE_VALUES, params);
        namedJdbcTemplate.update(LabelPO.Queries.CLEAN_LABEL_ATTRIBUTES, params);
        namedJdbcTemplate.update(LabelPO.Queries.DELETE_BY_ID, params);

    }

    /**
     * Creates the security label.
     *
     * @param label
     *            the label
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#createSecurityLabel(com.unidata.mdm.backend.service.security.po.LabelPO)
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional
    public void createSecurityLabel(LabelPO label) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(LabelPO.Fields.UPDATED_AT, label.getUpdatedAt());
        paramMap.put(LabelPO.Fields.UPDATED_BY, label.getUpdatedBy());
        paramMap.put(LabelPO.Fields.CREATED_AT, label.getCreatedAt());
        paramMap.put(LabelPO.Fields.CREATED_BY, label.getCreatedBy());
        paramMap.put(LabelPO.Fields.NAME, label.getName());
        paramMap.put(LabelPO.Fields.DESCRIPTION, label.getDescription());
        paramMap.put(LabelPO.Fields.DISPLAY_NAME, label.getDisplayName());
        namedJdbcTemplate.update(LabelPO.Queries.INSERT_NEW, paramMap);
        label.setId(findSecurityLabelByName(label.getName()).getId());
        List<Map<String, Object>> las = new ArrayList<>();
        for (LabelAttributePO la : label.getLabelAttribute()) {
            paramMap = new HashMap<>();
            paramMap.put(LabelAttributePO.Fields.UPDATED_AT, la.getUpdatedAt());
            paramMap.put(LabelAttributePO.Fields.UPDATED_BY, la.getUpdatedBy());
            paramMap.put(LabelAttributePO.Fields.CREATED_AT, la.getCreatedAt());
            paramMap.put(LabelAttributePO.Fields.CREATED_BY, la.getCreatedBy());
            paramMap.put(LabelAttributePO.Fields.NAME, la.getName());
            paramMap.put(LabelAttributePO.Fields.DESCRIPTION, la.getDescription());
            paramMap.put(LabelAttributePO.Fields.S_LABEL_ID, label.getId());
            paramMap.put(LabelAttributePO.Fields.VALUE, la.getPath());
            las.add(paramMap);
        }
        namedJdbcTemplate.batchUpdate(LabelAttributePO.Queries.INSERT_NEW,  las.toArray(new Map[las.size()]));
    }

    /**
     * Update security label by name.
     *
     * @param name
     *            the name
     * @param label
     *            the label
     */
    @SuppressWarnings({ "unchecked"})
    @Override
    @Transactional
    public void updateSecurityLabelByName(String name, LabelPO label) {
        LabelPO oldLabel = findSecurityLabelByName(label.getName());
        label.setId(oldLabel.getId());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(LabelAttributePO.Fields.S_LABEL_ID, oldLabel.getId());
        List<LabelAttributePO> oldAttrs = namedJdbcTemplate.query(LabelAttributePO.Queries.SELECT_BY_LABEL_ID,
                paramMap, LabelAttributeRowMapper.DEFAULT_ROW_MAPPER);
        Map<Integer, LabelAttributePO> las = new HashMap<>();
        for (LabelAttributePO oa : oldAttrs) {
            las.put(oa.getId(), oa);
        }
        List<LabelAttributePO> toCreate = new ArrayList<>();
        List<LabelAttributePO> toDelete = new ArrayList<>();
        List<LabelAttributePO> toUpdate = new ArrayList<>();
        for (LabelAttributePO la : label.getLabelAttribute()) {
            if (la.getId()<0) {
                toCreate.add(la);
            } else {
                toUpdate.add(la);
                las.remove(la.getId());
            }
        }
        toDelete.addAll(las.values());
        if (!CollectionUtils.isEmpty(toCreate)) {
            List<Map<String, Object>> ls = new ArrayList<>();
            for (LabelAttributePO la : toCreate) {
                paramMap = new HashMap<>();
                paramMap.put(LabelAttributePO.Fields.UPDATED_AT, la.getUpdatedAt());
                paramMap.put(LabelAttributePO.Fields.UPDATED_BY, la.getUpdatedBy());
                paramMap.put(LabelAttributePO.Fields.CREATED_AT, la.getCreatedAt());
                paramMap.put(LabelAttributePO.Fields.CREATED_BY, la.getCreatedBy());
                paramMap.put(LabelAttributePO.Fields.NAME, la.getName());
                paramMap.put(LabelAttributePO.Fields.DESCRIPTION, la.getDescription());
                paramMap.put(LabelAttributePO.Fields.S_LABEL_ID, label.getId());
                paramMap.put(LabelAttributePO.Fields.VALUE, la.getPath());
                ls.add(paramMap);
            }
            namedJdbcTemplate.batchUpdate(LabelAttributePO.Queries.INSERT_NEW, ls.toArray(new Map[ls.size()]));
        }
        if (!CollectionUtils.isEmpty(toUpdate)) {
            List<Map<String, Object>> ls = new ArrayList<>();
            for (LabelAttributePO la : toUpdate) {
                paramMap = new HashMap<>();
                paramMap.put(LabelAttributePO.Fields.ID, la.getId());
                paramMap.put(LabelAttributePO.Fields.UPDATED_AT, la.getUpdatedAt());
                paramMap.put(LabelAttributePO.Fields.UPDATED_BY, la.getUpdatedBy());
                paramMap.put(LabelAttributePO.Fields.CREATED_AT, la.getCreatedAt());
                paramMap.put(LabelAttributePO.Fields.CREATED_BY, la.getCreatedBy());
                paramMap.put(LabelAttributePO.Fields.NAME, la.getName());
                paramMap.put(LabelAttributePO.Fields.DESCRIPTION, la.getDescription());
                paramMap.put(LabelAttributePO.Fields.S_LABEL_ID, label.getId());
                paramMap.put(LabelAttributePO.Fields.VALUE, la.getPath());
                ls.add(paramMap);
            }
            namedJdbcTemplate.batchUpdate(LabelAttributePO.Queries.UPDATE_BY_ID, ls.toArray(new Map[ls.size()]));
        }
        if (!CollectionUtils.isEmpty(toDelete)) {
            List<Map<String, Object>> ls = new ArrayList<>();
            for (LabelAttributePO la :toDelete) {
                paramMap = new HashMap<>();
                paramMap.put(LabelAttributePO.Fields.NAME, la.getName());
                paramMap.put(LabelAttributePO.Fields.ID, la.getId());
                ls.add(paramMap);
            }
            namedJdbcTemplate.batchUpdate("delete from s_label_attribute_value where s_label_attribute_id = :ID", ls.toArray(new Map[ls.size()]));
            namedJdbcTemplate.batchUpdate(LabelAttributePO.Queries.DELETE_BY_ID,  ls.toArray(new Map[ls.size()]));

        }

        paramMap = new HashMap<>();
        paramMap.put(LabelPO.Fields.UPDATED_AT, label.getUpdatedAt());
        paramMap.put(LabelPO.Fields.UPDATED_BY, label.getUpdatedBy());
        paramMap.put(LabelPO.Fields.CREATED_AT, label.getCreatedAt());
        paramMap.put(LabelPO.Fields.CREATED_BY, label.getCreatedBy());
        paramMap.put(LabelPO.Fields.NAME, label.getName());
        paramMap.put(LabelPO.Fields.DESCRIPTION, label.getDescription());
        paramMap.put(LabelPO.Fields.DISPLAY_NAME, label.getDisplayName());
        namedJdbcTemplate.update(LabelPO.Queries.UPDATE_BY_NAME, paramMap);

    }

    /**
     * Adds the label attribute.
     *
     * @param toAdd
     *            the to add
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#addLabelAttribute(com.unidata.mdm.backend.service.security.po.LabelAttributePO)
     */
    @Override
    @Transactional
    public void addLabelAttribute(LabelAttributePO toAdd) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(LabelAttributePO.Fields.UPDATED_AT, toAdd.getUpdatedAt());
        paramMap.put(LabelAttributePO.Fields.UPDATED_BY, toAdd.getUpdatedBy());
        paramMap.put(LabelAttributePO.Fields.CREATED_AT, toAdd.getCreatedAt());
        paramMap.put(LabelAttributePO.Fields.CREATED_BY, toAdd.getCreatedBy());
        paramMap.put(LabelAttributePO.Fields.NAME, toAdd.getName());
        paramMap.put(LabelAttributePO.Fields.DESCRIPTION, toAdd.getDescription());
        paramMap.put(LabelAttributePO.Fields.VALUE, toAdd.getPath());
        paramMap.put(LabelAttributePO.Fields.S_LABEL_ID, toAdd.getLabel().getId());
        namedJdbcTemplate.update(LabelAttributePO.Queries.UPDATE_BY_NAME, paramMap);

    }

    /**
     * Checks if is user in role.
     *
     * @param userName
     *            the user name
     * @param roleName
     *            the role name
     * @return true, if is user in role
     */
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.impl.RoleDao#isUserInRole(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public boolean isUserInRole(String userName, String roleName, String source) {
        return jdbcTemplate.queryForObject(
                "select count(id) from s_user_s_role" + " where s_users_id="
                        + "(select id from s_user where login=? and source=?) "
                        + "and s_roles_id=" + "(select id from s_role where name=?)",
                Long.class, userName, source, roleName) > 0;

    }

    /**
     * Creates the resources.
     *
     * @param resourcePOs
     *            the resource p os
     */
    @Override
    @Transactional
    public void createResources(List<ResourcePO> resourcePOs) {
        for (ResourcePO toAdd : resourcePOs) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(ResourcePO.Fields.UPDATED_AT, toAdd.getUpdatedAt());
            paramMap.put(ResourcePO.Fields.UPDATED_BY, toAdd.getUpdatedBy());
            paramMap.put(ResourcePO.Fields.CREATED_AT, toAdd.getCreatedAt());
            paramMap.put(ResourcePO.Fields.CREATED_BY, toAdd.getCreatedBy());
            paramMap.put(ResourcePO.Fields.NAME, toAdd.getName());
            paramMap.put(ResourcePO.Fields.DISPLAY_NAME, toAdd.getDisplayName());
            paramMap.put(ResourcePO.Fields.R_TYPE, toAdd.getRType());
            List<Map<String, Object>> resultOfSelect = namedJdbcTemplate.queryForList(ResourcePO.Queries.SELECT_BY_NAME, paramMap);

            if (CollectionUtils.isEmpty(resultOfSelect)) {
                jdbcTemplate.update(INSERT_RESOURCE_SQL,
                        toAdd.getName(),
                        toAdd.getDisplayName(),
                        toAdd.getRType(),
                        toAdd.getCategory(),
                        toAdd.getCreatedBy(),
                        toAdd.getParentName());
            } else {
                paramMap.put(ResourcePO.Fields.CREATED_AT, resultOfSelect.get(0).get(ResourcePO.Fields.CREATED_AT));
                paramMap.put(ResourcePO.Fields.CREATED_BY, resultOfSelect.get(0).get(ResourcePO.Fields.CREATED_BY));
                paramMap.put(ResourcePO.Fields.NAME, resultOfSelect.get(0).get(ResourcePO.Fields.NAME));
                paramMap.put(ResourcePO.Fields.R_TYPE, resultOfSelect.get(0).get(ResourcePO.Fields.R_TYPE));
                paramMap.put(ResourcePO.Fields.CATEGORY, resultOfSelect.get(0).get(ResourcePO.Fields.CATEGORY));
                namedJdbcTemplate.update(ResourcePO.Queries.UPDATE_BY_NAME, paramMap);
            }
        }
    }
    /**
     * Delete resource by name.
     * @param resourceName resource name.
     */
    @Override
    @Transactional
    public void deleteResource(String resourceName) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", resourceName);
        namedJdbcTemplate.update(
                "delete from s_right_s_resource where s_resource_id in(select id from s_resource where name = :name )",
                params);
        namedJdbcTemplate.update("delete from s_resource where name = :name", params);
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select id, s_label_id from s_label_attribute where value like ?",
                resourceName + ".%");
        int labelId = 0;
        while (rs.next()) {
            int labelAttrId = rs.getInt(1);
            labelId = rs.getInt(2);
            jdbcTemplate.update("delete from s_label_attribute_value where s_label_attribute_id = ?", labelAttrId);
            jdbcTemplate.update("delete from s_label_attribute where id = ?", labelAttrId);

        }
        jdbcTemplate.update("delete from s_role_s_label where s_label_id = ?", labelId);
        jdbcTemplate.update("delete from s_label where id = ?", labelId);

    }
    /**
     * Drop all resources.
     */
    @Override
    public void dropResources(SecuredResourceCategory... categories) {

        boolean hasMetaModel = false;
        if (ArrayUtils.isEmpty(categories)) {
            jdbcTemplate.update(DROP_ALL_NON_SYSTEM_RESOURCES_SQL);
            hasMetaModel = true;
        } else {
            for (SecuredResourceCategory category : categories) {
                jdbcTemplate.update(DELETE_RESOURCES_BY_CATEGORY_SQL, category.name());
                hasMetaModel = hasMetaModel ? hasMetaModel : category == SecuredResourceCategory.META_MODEL;
            }
        }

        if (hasMetaModel) {
            jdbcTemplate.update("delete from s_label_attribute_value");
            jdbcTemplate.update("delete from s_label_attribute");
            jdbcTemplate.update("delete from s_role_s_label");
            jdbcTemplate.update("delete from s_label");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<RolePropertyPO> loadAllProperties() {
        return namedJdbcTemplate.query(LOAD_ALL_ROLE_PROPERTIES, rolePropertyRowMapper);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public RolePropertyPO loadPropertyByName(String name) {
        List<RolePropertyPO> list = namedJdbcTemplate.query(LOAD_ROLE_PROPERTY_BY_NAME,
            Collections.singletonMap("name", name), rolePropertyRowMapper);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public RolePropertyPO loadPropertyByDisplayName(String displayName) {
        final List<RolePropertyPO> list = namedJdbcTemplate.query(LOAD_ROLE_PROPERTY_BY_DISPLAY_NAME,
                Collections.singletonMap("display_name", displayName), rolePropertyRowMapper);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void saveProperty(RolePropertyPO property) {
        if (property.getId() == null) {
            // Insert property
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

            sqlParameterSource.addValue("name", property.getName());
            sqlParameterSource.addValue("display_name", property.getDisplayName());
            sqlParameterSource.addValue("created_at", property.getCreatedAt());
            sqlParameterSource.addValue("updated_at", property.getUpdatedAt());
            sqlParameterSource.addValue("created_by", property.getCreatedBy());
            sqlParameterSource.addValue("updated_by", property.getUpdatedBy());

            KeyHolder keyHolder = new GeneratedKeyHolder();

            namedJdbcTemplate.update(INSERT_ROLE_PROPERTY, sqlParameterSource, keyHolder, new String[] {"id"});

            property.setId(keyHolder.getKey().longValue());

        } else {
            // Update property
            MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();

            sqlParameterSource.addValue("name", property.getName());
            sqlParameterSource.addValue("display_name", property.getDisplayName());
            sqlParameterSource.addValue("updated_at", property.getUpdatedAt());
            sqlParameterSource.addValue("updated_by", property.getUpdatedBy());
            sqlParameterSource.addValue("id", property.getId());

            namedJdbcTemplate.update(UPDATE_ROLE_PROPERTY_BY_ID, sqlParameterSource);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteProperty(long id) {
        // Delete all values first.
        namedJdbcTemplate.update(DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_PROPERTY_ID,
            Collections.singletonMap("rolePropertyId", id));

        // Delete property.
        namedJdbcTemplate.update(DELETE_ROLE_PROPERTY_BY_ID,
            Collections.singletonMap("rolePropertyId", id));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void saveRolePropertyValues(Collection<RolePropertyValuePO> propertyValues) {

        if (CollectionUtils.isEmpty(propertyValues)) {
            return;
        }

        List<RolePropertyValuePO> insertValues = new ArrayList<>();
        List<RolePropertyValuePO> updateValues = new ArrayList<>();

        for (RolePropertyValuePO propertyValue : propertyValues) {
            if (propertyValue.getId() == null) {
                insertValues.add(propertyValue);
            } else {
                updateValues.add(propertyValue);
            }
        }

        if (!CollectionUtils.isEmpty(insertValues)) {
            insertRolePropertyValues(insertValues);
        }

        if (!CollectionUtils.isEmpty(updateValues)) {
            Map<String, Object>[] map = createRolePropertyValueParams(updateValues);

            namedJdbcTemplate.batchUpdate(UPDATE_ROLE_PROPERTY_VALUE_BY_ID, map);
        }
    }

    /**
     *
     * @param propertyValues
     */
    private void insertRolePropertyValues(List<RolePropertyValuePO> propertyValues) {
        if (CollectionUtils.isEmpty(propertyValues)) {
            return;
        }

        long[] ids = daoHelper.createIds(propertyValues.size(), "s_role_property_value_id_seq");

        for (int i = 0; i < propertyValues.size(); i++) {
           RolePropertyValuePO propertyValue = propertyValues.get(i);

            propertyValue.setId(ids[i]);
        }

        Map<String, Object>[] map = createRolePropertyValueParams(propertyValues);

        namedJdbcTemplate.batchUpdate(INSERT_ROLE_PROPERTY_VALUE, map);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteRolePropertyValuesByIds(Collection<Long> ids) {
        long listId = daoHelper.insertLongsToTemp(ids);

        namedJdbcTemplate.update(DELETE_ROLE_PROPERTY_VALUES_BY_IDS,
            Collections.singletonMap("listId", listId));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteRolePropertyValuesByRoleId(long roleId) {
        namedJdbcTemplate.update(DELETE_ROLE_PROPERTY_VALUES_BY_ROLE_ID,
            Collections.singletonMap("roleId", roleId));
    }

    @Override
    public List<RolePropertyValuePO> loadRolePropertyValuesByRoleId(Integer roleId) {
        return loadRolePropertyValuesByRoleIds(Collections.singleton(roleId)).get(roleId);
    }
    /**
     * {@inheritDoc }
     */
    @Override
    public Map<Integer, List<RolePropertyValuePO>> loadRolePropertyValuesByRoleIds(Collection<Integer> roleIds) {

        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyMap();
        }

        return namedJdbcTemplate.query(LOAD_ROLE_PROPERTY_VALUES_BY_ROLE_IDS,
            Collections.singletonMap("listId", roleIds),
            rs -> {

                Map<Integer, List<RolePropertyValuePO>> result = new HashMap<>();
                while (rs.next()) {
                    Integer roleId = rs.getInt(FieldColumns.ROLE_ID.name());

                    List<RolePropertyValuePO> propValues = result.get(roleId);
                    if (propValues == null) {
                        propValues = new ArrayList<>();
                        result.put(roleId, propValues);
                    }

                    RolePropertyValuePO propValue = rolePropertyValueRowMapper.mapRow(rs, 0);
                    propValues.add(propValue);
                }

                return result;
            }
        );
    }

    /**
     *
     * @param propertyValues
     * @return
     */
    private Map<String, Object>[] createRolePropertyValueParams(List<RolePropertyValuePO> propertyValues) {

        Map<String, Object>[] result = new Map[propertyValues.size()];
        for (int i = 0; i < propertyValues.size(); i++) {
            RolePropertyValuePO propertyValue = propertyValues.get(i);

            Map<String, Object> params = new HashMap<>();

            params.put("id", propertyValue.getId());
            params.put("role_id", propertyValue.getRoleId());

            if (propertyValue.getProperty() != null) {
                params.put("property_id", propertyValue.getProperty().getId());
            }

            params.put("value", propertyValue.getValue());
            params.put("created_at", propertyValue.getCreatedAt());
            params.put("updated_at", propertyValue.getUpdatedAt());
            params.put("created_by", propertyValue.getCreatedBy());
            params.put("updated_by", propertyValue.getUpdatedBy());

            result[i] = params;
        }

        return result;
    }
}

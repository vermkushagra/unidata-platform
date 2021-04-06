package com.unidata.mdm.backend.dao.impl;

import static java.util.Collections.singletonMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.dao.MatchingGroupDao;
import com.unidata.mdm.backend.po.matching.MatchingGroupPO;

@Repository
public class MatchingGroupDaoImpl extends AbstractDaoImpl implements MatchingGroupDao {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingGroupDaoImpl.class);
    /**
     * Insert group.
     */
    private final String insertGroupSQL;
    /**
     * Update group.
     */
    private final String updateGroupSQL;
    /**
     * Delete group.
     */
    private final String deleteGroupSQL;
    /**
     * Selects group by id.
     */
    private final String selectGroupByIdSQL;
    /**
     * Selects group by entity name and group name.
     */
    private final String selectGroupByEntityNameAndGroupNameSQL;
    /**
     * Selects all groups.
     */
    private final String selectAllGroupsSQL;
    /**
     * Selects groups by entity name.
     */
    private final String selectGroupsByEntityNameSQL;
    /**
     * Selects ids by entity name.
     */
    private final String selectGroupIdsByEntityNameSQL;
    /**
     * Deletes rules to groups assignments.
     */
    private final String deleteRulesToGroupsAssignmentsSQL;
    /**
     * Inserts rules to groups assignments.
     */
    private final String insertRulesToGroupsAssignmentsSQL;
    /**
     * Group extractor.
     */
    private static final ResultSetExtractor<List<MatchingGroupPO>> GROUP_EXTRACTOR = rs -> {

        Map<Integer, MatchingGroupPO> groups = new HashMap<>();
        while (rs.next()) {

            Integer groupId = rs.getInt(MatchingGroupPO.FIELD_ID);
            MatchingGroupPO po = groups.get(groupId);
            if (po == null) {

                po = new MatchingGroupPO();
                po.setRuleIds(new ArrayList<>());
                po.setId(groupId);
                po.setDescription(rs.getString(MatchingGroupPO.FIELD_DESCRIPTION));
                po.setAutoMerge(rs.getBoolean(MatchingGroupPO.FIELD_AUTO_MERGE));
                po.setEntityName(rs.getString(MatchingGroupPO.FIELD_ENTITY_NAME));
                po.setName(rs.getString(MatchingGroupPO.FIELD_NAME));
                po.setActive(rs.getBoolean(MatchingGroupPO.FIELD_ACTIVE));

                groups.put(groupId, po);
            }

            int ruleId = rs.getInt(MatchingGroupPO.FIELD_RULE_ID);
            if (!rs.wasNull()) {
                po.getRuleIds().add(ruleId);
            }
        }

        return groups.values().stream().collect(Collectors.toList());
    };

    /**
     * Constructor.
     *
     * @param dataSource
     */
    @Autowired
    public MatchingGroupDaoImpl(DataSource dataSource, @Qualifier("matching-sql") Properties sql) {
        super(dataSource);
        insertGroupSQL = sql.getProperty("insertGroupSQL");
        updateGroupSQL = sql.getProperty("updateGroupSQL");
        deleteGroupSQL = sql.getProperty("deleteGroupSQL");
        selectGroupByIdSQL = sql.getProperty("selectGroupByIdSQL");
        selectGroupByEntityNameAndGroupNameSQL = sql.getProperty("selectGroupByEntityNameAndGroupNameSQL");
        selectAllGroupsSQL = sql.getProperty("selectAllGroupsSQL");
        selectGroupsByEntityNameSQL = sql.getProperty("selectGroupsByEntityNameSQL");
        selectGroupIdsByEntityNameSQL = sql.getProperty("selectGroupIdsByEntityNameSQL");
        deleteRulesToGroupsAssignmentsSQL = sql.getProperty("deleteRulesToGroupsAssignmentsSQL");
        insertRulesToGroupsAssignmentsSQL = sql.getProperty("insertRulesToGroupsAssignmentsSQL");
    }

    @Nonnull
    @Override
    public MatchingGroupPO save(@Nonnull MatchingGroupPO matchingGroup) {

        String name = matchingGroup.getName();
        String entityName = matchingGroup.getEntityName();
        try {

            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue(MatchingGroupPO.FIELD_NAME, name);
            mapSqlParameterSource.addValue(MatchingGroupPO.FIELD_DESCRIPTION, matchingGroup.getDescription());
            mapSqlParameterSource.addValue(MatchingGroupPO.FIELD_AUTO_MERGE, matchingGroup.isAutoMerge());
            mapSqlParameterSource.addValue(MatchingGroupPO.FIELD_ENTITY_NAME, entityName);
            mapSqlParameterSource.addValue(MatchingGroupPO.FIELD_ACTIVE, matchingGroup.isActive());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedJdbcTemplate.update(insertGroupSQL, mapSqlParameterSource, keyHolder, new String[] { MatchingGroupPO.FIELD_ID });
            matchingGroup.setId(keyHolder.getKey().intValue());
            Integer groupId = matchingGroup.getId();
            batchUpdateGroupRules(groupId, matchingGroup.getRuleIds());

            return matchingGroup;
        } catch (DuplicateKeyException dke) {
            LOGGER.warn("Duplicate key exception caught.", dke);
            throw new BusinessException("Name was duplicated:" + name, ExceptionId.EX_MATCHING_GROUP_NAME_WAS_DUPLICATE,
                    name, entityName);
        }
    }

    @Nonnull
    @Override
    public MatchingGroupPO update(@Nonnull MatchingGroupPO matchingGroup) {

        Map<String, Object> paramMap = new HashMap<>();
        Integer groupId = matchingGroup.getId();
        paramMap.put(MatchingGroupPO.FIELD_NAME, matchingGroup.getName());
        paramMap.put(MatchingGroupPO.FIELD_DESCRIPTION, matchingGroup.getDescription());
        paramMap.put(MatchingGroupPO.FIELD_ENTITY_NAME, matchingGroup.getEntityName());
        paramMap.put(MatchingGroupPO.FIELD_ACTIVE, matchingGroup.isActive());
        paramMap.put(MatchingGroupPO.FIELD_AUTO_MERGE, matchingGroup.isAutoMerge());
        paramMap.put(MatchingGroupPO.FIELD_ID, groupId);

        namedJdbcTemplate.update(updateGroupSQL, paramMap);
        namedJdbcTemplate.update(deleteRulesToGroupsAssignmentsSQL, singletonMap(MatchingGroupPO.FIELD_GROUP_ID, groupId));

        batchUpdateGroupRules(groupId, matchingGroup.getRuleIds());
        return matchingGroup;
    }

    private void batchUpdateGroupRules(Integer groupId, Collection<Integer> ruleIds) {
        if(CollectionUtils.isEmpty(ruleIds)){
            return;
        }

        SqlParameterSource[] batch = new SqlParameterSource[ruleIds.size()];
        int i = 0;
        for (Integer ruleId : ruleIds) {
            batch[i] = new MapSqlParameterSource()
                .addValue(MatchingGroupPO.FIELD_GROUP_ID, groupId)
                .addValue(MatchingGroupPO.FIELD_RULE_ID, ruleId)
                .addValue(MatchingGroupPO.FIELD_ORDER_NUMBER, i);
            i++;
        }

        namedJdbcTemplate.batchUpdate(insertRulesToGroupsAssignmentsSQL, batch);
    }

    @Override
    public void delete(@Nonnull Integer id) {
        namedJdbcTemplate.update(deleteGroupSQL, singletonMap(MatchingGroupPO.FIELD_ID, id));
    }

    @Nonnull
    @Override
    public Collection<MatchingGroupPO> getByEntityName(@Nonnull String entityName) {
        return namedJdbcTemplate.query(selectGroupsByEntityNameSQL, singletonMap(MatchingGroupPO.FIELD_ENTITY_NAME, entityName), GROUP_EXTRACTOR);
    }

    @Nullable
    @Override
    public MatchingGroupPO getById(@Nonnull Integer id) {
        Collection<MatchingGroupPO> matchingGroupPO = namedJdbcTemplate.query(selectGroupByIdSQL, singletonMap(MatchingGroupPO.FIELD_ID, id), GROUP_EXTRACTOR);
        return matchingGroupPO.size() == 1 ? matchingGroupPO.iterator().next() : null;
    }

    @Nullable
    @Override
    public MatchingGroupPO getByEntityNameAndGroupName(@Nonnull String entityName, @Nonnull String groupName) {

        Map<String, Object> map = new HashMap<>();
        map.put(MatchingGroupPO.FIELD_ENTITY_NAME, entityName);
        map.put(MatchingGroupPO.FIELD_NAME, groupName);
        List<MatchingGroupPO> matchingGroupPO = namedJdbcTemplate.query(selectGroupByEntityNameAndGroupNameSQL, map, GROUP_EXTRACTOR);
        return CollectionUtils.isEmpty(matchingGroupPO) ? null : matchingGroupPO.get(0);
    }

    @Nonnull
    @Override
    public Collection<MatchingGroupPO> getAll() {
        return namedJdbcTemplate.query(selectAllGroupsSQL, Collections.emptyMap(), GROUP_EXTRACTOR);
    }

    @Nonnull
    @Override
    public Collection<Integer> getGroupIds(@Nonnull String entityName) {
        return namedJdbcTemplate.queryForList(selectGroupIdsByEntityNameSQL, Collections.singletonMap(MatchingGroupPO.FIELD_ENTITY_NAME, entityName), Integer.class);
    }
}

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

import static java.util.Collections.singletonMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.dao.MatchingRuleDao;
import com.unidata.mdm.backend.po.matching.MatchingAlgorithmPO;
import com.unidata.mdm.backend.po.matching.MatchingRulePO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

@Repository
public class MatchingRuleDaoImpl extends AbstractDaoImpl implements MatchingRuleDao {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingRuleDaoImpl.class);
    /**
     * Insert rule.
     */
    private final String insertRuleSQL;
    /**
     * Update rule.
     */
    private final String updateRuleSQL;
    /**
     * Select all rules.
     */
    private final String selectAllSQL;
    /**
     * Deletes a rule by id.
     */
    private final String deleteRuleByIdSQL;
    /**
     * Selects rules by entity name.
     */
    private final String selectRulesByEntityNameSQL;
    /**
     * Selects rule by id.
     */
    private final String selectRuleByIdSQL;
    /**
     * Selects rule by entity name and rule name.
     */
    private final String selectRuleByEntityNameAndRuleNameSQL;
    /**
     * Deletes algorithm assignment by rule id.
     */
    private final String deleteAlgorithmsByRuleIdSQL;
    /**
     * Inserts algorithms assignment.
     */
    private final String insertAlgorithmSQL;
    /**
     * Selects algorithm assignments by rule id.
     */
    private final String selectAlgorithmsByRuleIdSQL;
    /**
     * Rule row mapper.
     */
    private static final RowMapper<MatchingRulePO> RULE_MAPPER = (rs, rowNum) -> {

        MatchingRulePO result = new MatchingRulePO();
        result.setId(rs.getInt(MatchingRulePO.FIELD_ID));
        result.setName(rs.getString(MatchingRulePO.FIELD_NAME));
        result.setEntityName(rs.getString(MatchingRulePO.FIELD_ENTITY_NAME));
        result.setDescription(rs.getString(MatchingRulePO.FIELD_DESCRIPTION));
        result.setSettings(rs.getString(MatchingRulePO.FIELD_SETTINGS));
        result.setActive(rs.getBoolean(MatchingRulePO.FIELD_ACTIVE));
        result.setAutoMerge(rs.getBoolean(MatchingRulePO.FIELD_AUTO_MERGE));
        result.setWithPreprocessing(rs.getBoolean(MatchingRulePO.FIELD_WITH_PREPROCESSING));

        return result;
    };
    /**
     * Algorithm link row mapper.
     */
    private static final RowMapper<MatchingAlgorithmPO> ALGORITHM_MAPPER = (rs, rowNum) -> {

        MatchingAlgorithmPO matchingAlgorithmPO = new MatchingAlgorithmPO();
        matchingAlgorithmPO.setAlgorithmId(rs.getInt(MatchingAlgorithmPO.FIELD_ALGORITHM_ID));
        matchingAlgorithmPO.setRuleId(rs.getInt(MatchingAlgorithmPO.FIELD_RULE_ID));
        matchingAlgorithmPO.setData(rs.getString(MatchingAlgorithmPO.FIELD_DATA));

        return matchingAlgorithmPO;
    };

    /**
     * Constructor.
     *
     * @param dataSource
     */
    @Autowired
    public MatchingRuleDaoImpl(DataSource dataSource, @Qualifier("matching-sql") Properties sql) {

        super(dataSource);
        insertRuleSQL = sql.getProperty("insertRuleSQL");
        updateRuleSQL = sql.getProperty("updateRuleSQL");
        deleteRuleByIdSQL = sql.getProperty("deleteRuleByIdSQL");
        selectAllSQL = sql.getProperty("selectAllSQL");
        selectRulesByEntityNameSQL = sql.getProperty("selectRulesByEntityNameSQL");
        selectRuleByIdSQL = sql.getProperty("selectRuleByIdSQL");
        selectRuleByEntityNameAndRuleNameSQL = sql.getProperty("selectRuleByEntityNameAndRuleNameSQL");

        deleteAlgorithmsByRuleIdSQL = sql.getProperty("deleteAlgorithmsByRuleIdSQL");
        insertAlgorithmSQL = sql.getProperty("insertAlgorithmSQL");
        selectAlgorithmsByRuleIdSQL = sql.getProperty("selectAlgorithmsByRuleIdSQL");
    }

    @Nonnull
    @Transactional
    @Override
    public MatchingRulePO save(@Nonnull MatchingRulePO matchingRule) {

        String ruleName = matchingRule.getName();
        String entityName = matchingRule.getEntityName();
        try{
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue(MatchingRulePO.FIELD_NAME, ruleName);
            mapSqlParameterSource.addValue(MatchingRulePO.FIELD_SETTINGS, matchingRule.getSettings());
            mapSqlParameterSource.addValue(MatchingRulePO.FIELD_ENTITY_NAME, entityName);
            mapSqlParameterSource.addValue(MatchingRulePO.FIELD_ACTIVE, matchingRule.isActive());
            mapSqlParameterSource.addValue(MatchingRulePO.FIELD_AUTO_MERGE, matchingRule.isAutoMerge());
            mapSqlParameterSource.addValue(MatchingRulePO.FIELD_WITH_PREPROCESSING, matchingRule.isWithPreprocessing());
            mapSqlParameterSource.addValue(MatchingRulePO.FIELD_STORAGE_FKEY, SecurityUtils.getCurrentUserStorageId());
            mapSqlParameterSource.addValue(MatchingRulePO.FIELD_DESCRIPTION, matchingRule.getDescription());

            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedJdbcTemplate.update(insertRuleSQL, mapSqlParameterSource, keyHolder, new String[]{ MatchingRulePO.FIELD_ID });
            matchingRule.setId(keyHolder.getKey().intValue());
            insertAlgorithms(matchingRule.getMatchingAlgorithms(), matchingRule.getId());

            return matchingRule;
        } catch (DuplicateKeyException dke) {
            LOGGER.warn("Duplicate key esception caught!", dke);
            throw new BusinessException("Name was duplicated:" + ruleName,
                    ExceptionId.EX_MATCHING_RULE_NAME_WAS_DUPLICATE, ruleName, entityName);
        }
    }

    @Nonnull
    @Transactional
    @Override
    public MatchingRulePO update(@Nonnull MatchingRulePO matchingRule) {

        Map<String, Object> map = new HashMap<>();
        map.put(MatchingRulePO.FIELD_NAME, matchingRule.getName());
        map.put(MatchingRulePO.FIELD_SETTINGS, matchingRule.getSettings());
        map.put(MatchingRulePO.FIELD_ENTITY_NAME, matchingRule.getEntityName());
        map.put(MatchingRulePO.FIELD_ACTIVE, matchingRule.isActive());
        map.put(MatchingRulePO.FIELD_AUTO_MERGE, matchingRule.isAutoMerge());
        map.put(MatchingRulePO.FIELD_WITH_PREPROCESSING, matchingRule.isWithPreprocessing());
        map.put(MatchingRulePO.FIELD_STORAGE_FKEY, SecurityUtils.getCurrentUserStorageId());
        map.put(MatchingRulePO.FIELD_DESCRIPTION, matchingRule.getDescription());
        map.put(MatchingRulePO.FIELD_ID, matchingRule.getId());

        namedJdbcTemplate.update(updateRuleSQL, map);
        namedJdbcTemplate.update(deleteAlgorithmsByRuleIdSQL, singletonMap(MatchingAlgorithmPO.FIELD_RULE_ID, matchingRule.getId()));
        insertAlgorithms(matchingRule.getMatchingAlgorithms(), matchingRule.getId());
        return matchingRule;
    }

    private void insertAlgorithms(Collection<MatchingAlgorithmPO> matchingAlgorithms, Integer ruleId) {

        matchingAlgorithms.forEach(algo -> algo.setRuleId(ruleId));
        SqlParameterSource[] batch = new SqlParameterSource[matchingAlgorithms.size()];

        int i = 0;
        for (MatchingAlgorithmPO matchingAlgorithm : matchingAlgorithms) {
            batch[i] = new MapSqlParameterSource()
                    .addValue(MatchingAlgorithmPO.FIELD_ALGORITHM_ID, matchingAlgorithm.getAlgorithmId())
                    .addValue(MatchingAlgorithmPO.FIELD_RULE_ID, matchingAlgorithm.getRuleId())
                    .addValue(MatchingAlgorithmPO.FIELD_DATA, matchingAlgorithm.getData());
            i++;
        }

        namedJdbcTemplate.batchUpdate(insertAlgorithmSQL, batch);
    }

    @Override
    public void delete(@Nonnull Integer id) {
        namedJdbcTemplate.update(deleteRuleByIdSQL, singletonMap(MatchingRulePO.FIELD_ID, id));
    }

    @Nonnull
    @Override
    public Collection<MatchingRulePO> getByEntityName(@Nonnull String entityName) {
        Map<String, Object> map = new HashMap<>();
        map.put(MatchingRulePO.FIELD_ENTITY_NAME, entityName);
        map.put(MatchingRulePO.FIELD_STORAGE_FKEY, SecurityUtils.getCurrentUserStorageId());
        return namedJdbcTemplate.query(selectRulesByEntityNameSQL, map, RULE_MAPPER);
    }

    @Nullable
    @Override
    public MatchingRulePO getById(@Nonnull Integer id) {
        return namedJdbcTemplate.queryForObject(selectRuleByIdSQL, singletonMap("id", id), RULE_MAPPER);
    }

    @Nullable
    @Override
	public MatchingRulePO getByEntityNameAndRuleName(@Nonnull String entityName, @Nonnull String ruleName) {
		Map<String, Object> map = new HashMap<>();
		map.put(MatchingRulePO.FIELD_ENTITY_NAME, entityName);
		map.put(MatchingRulePO.FIELD_NAME, ruleName);
		map.put(MatchingRulePO.FIELD_STORAGE_FKEY, SecurityUtils.getCurrentUserStorageId());
		List<MatchingRulePO> result = namedJdbcTemplate.query(selectRuleByEntityNameAndRuleNameSQL, map, RULE_MAPPER);
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

    @Nonnull
    @Override
    public Collection<MatchingAlgorithmPO> getAlgorithmsByRuleId(@Nonnull Integer ruleId) {
        return namedJdbcTemplate.query(selectAlgorithmsByRuleIdSQL, singletonMap(MatchingAlgorithmPO.FIELD_RULE_ID, ruleId), ALGORITHM_MAPPER);
    }

    @Nonnull
    @Override
    public Collection<MatchingRulePO> getAll() {
        return namedJdbcTemplate.query(selectAllSQL, Collections.emptyMap(), RULE_MAPPER);
    }
}

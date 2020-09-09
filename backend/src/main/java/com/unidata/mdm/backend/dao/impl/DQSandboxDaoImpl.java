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

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.dao.DQSandboxDao;
import com.unidata.mdm.backend.po.SandboxRecordPO;
import com.unidata.mdm.backend.util.collections.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DQSandboxDaoImpl extends AbstractDaoImpl implements DQSandboxDao {

    private static final RowMapper<SandboxRecordPO> SANDBOX_RECORD_ROW_MAPPER = (rs, rowNum) -> new SandboxRecordPO(
            rs.getLong("id"),
            rs.getString("entity_name"),
            rs.getBytes("data")
    );

    private final String insertRecordQuery;
    private final String updateRecordQuery;
    private final String findByIdQuery;
    private final String deleteByIdsQuery;
    private final String deleteByEntityNameQuery;
    private final String searchRecordsQuery;
    private final String findRecordsByIdsQuery;
    private final String countRecordsByEntityNameQuery;

    @Autowired
    public DQSandboxDaoImpl(
            @Qualifier("unidataDataSource") final DataSource dataSource,
            @Qualifier("sandbox-sql") final Properties sql
    ) {
        super(dataSource);
        insertRecordQuery = sql.getProperty("INSERT_DATA_RECORD");
        updateRecordQuery = sql.getProperty("UPDATE_DATA_RECORD");
        findByIdQuery = sql.getProperty("SELECT_BY_ID");
        deleteByIdsQuery = sql.getProperty("DELETE_BY_IDS");
        deleteByEntityNameQuery = sql.getProperty("DELETE_BY_ENTITY_NAME");
        searchRecordsQuery = sql.getProperty("SELECT_BY_ENTITY_NAME");
        findRecordsByIdsQuery = sql.getProperty("SELECT_BY_RECORDS_IDS");
        countRecordsByEntityNameQuery = sql.getProperty("COUNT_IDS_BY_ENTITY_NAME");
    }

    @Override
    public long save(final SandboxRecordPO sandboxRecord) {
        if (sandboxRecord.getId() == null) {
            return namedJdbcTemplate.queryForObject(
                    insertRecordQuery,
                    Maps.of("entityName", sandboxRecord.getEntityName(), "data", sandboxRecord.getData()),
                    Long.class
            );
        }
        else {
            namedJdbcTemplate.update(
                    updateRecordQuery,
                    Maps.of("id", sandboxRecord.getId(), "entityName", sandboxRecord.getEntityName(), "data", sandboxRecord.getData())
            );
            return sandboxRecord.getId();
        }

    }

    @Override
    public SandboxRecordPO findRecordById(final long recordId) {
        return namedJdbcTemplate.queryForObject(
                findByIdQuery,
                Collections.singletonMap("id", recordId),
                SANDBOX_RECORD_ROW_MAPPER
        );
    }

    @Override
    public void deleteByIds(final List<Long> recordsIds) {
        namedJdbcTemplate.update(deleteByIdsQuery, Collections.singletonMap("ids", recordsIds));
    }

    @Override
    public void deleteByEntityName(final String entityName) {
        namedJdbcTemplate.update(deleteByEntityNameQuery, Collections.singletonMap("entityName", entityName));
    }

    @Override
    public List<SandboxRecordPO> find(final String entity, final int page, final int count) {
        return namedJdbcTemplate.query(
                searchRecordsQuery,
                Maps.of("entityName", entity, "limit", count, "offset", page * count),
                SANDBOX_RECORD_ROW_MAPPER
        );
    }

    @Override
    public List<SandboxRecordPO> findByRecordsIds(List<Long> recordsIds) {
        return namedJdbcTemplate.query(
                findRecordsByIdsQuery,
                Collections.singletonMap("recordsIds", recordsIds),
                SANDBOX_RECORD_ROW_MAPPER
        );
    }

    @Override
    public long count(final String entityName) {
        return namedJdbcTemplate.queryForObject(
                countRecordsByEntityNameQuery,
                Collections.singletonMap("entityName", entityName),
                Long.class
        );
    }
}

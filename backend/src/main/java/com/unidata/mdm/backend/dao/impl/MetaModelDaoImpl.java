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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetadataException;
import com.unidata.mdm.backend.dao.MetaModelDao;
import com.unidata.mdm.backend.dao.rm.MetaModelRowMapper;
import com.unidata.mdm.backend.dao.rm.MetaStorageRowMapper;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplateImpl;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplateImpl;
import com.unidata.mdm.backend.po.MetaModelPO;
import com.unidata.mdm.backend.po.MetaStoragePO;
import com.unidata.mdm.backend.service.model.ModelType;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
@Repository
public class MetaModelDaoImpl implements MetaModelDao {
    /**
     * JDBC template.
     */
    protected UnidataJdbcTemplate jdbcTemplate;
    /**
     * Named parameters template.
     */
    protected UnidataNamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * Constructor.
     *
     * @param dataSource
     */
    @Autowired
    public MetaModelDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new UnidataJdbcTemplateImpl(dataSource);
        this.namedJdbcTemplate = new UnidataNamedParameterJdbcTemplateImpl(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MetaStoragePO> findStorageRecords() {
        String sql = String.format("select %s, %s, %s, %s, %s, %s from %s", MetaStoragePO.FIELD_ID,
                MetaStoragePO.FIELD_NAME, MetaStoragePO.FIELD_CREATE_DATE, MetaStoragePO.FIELD_CREATED_BY,
                MetaStoragePO.FIELD_UPDATE_DATE, MetaStoragePO.FIELD_UPDATED_BY, MetaStoragePO.TABLE_NAME);
        return jdbcTemplate.query(sql, new MetaStorageRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModelPO findRecordByTypeAndId(String storageId, ModelType type, String id) {
        String sql = String.format(
                "select %s, %s, %s, %s, %s, %s, %s, %s, %s from %s where %1$s = ? and %2$s = ? and %3$s = ?",
                MetaModelPO.FIELD_ID, MetaModelPO.FIELD_STORAGE_ID, MetaModelPO.FIELD_TYPE, MetaModelPO.FIELD_VERSION,
                MetaModelPO.FIELD_DATA, MetaModelPO.FIELD_CREATE_DATE, MetaModelPO.FIELD_UPDATE_DATE,
                MetaModelPO.FIELD_CREATED_BY, MetaModelPO.FIELD_UPDATED_BY, MetaModelPO.TABLE_NAME);
        return jdbcTemplate.queryForObject(sql, new MetaModelRowMapper(), id, storageId, type.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MetaModelPO> findRecordsByType(String storageId, ModelType type) {
        String sql = String.format("select %s, %s, %s, %s, %s, %s, %s, %s, %s from %s where %2$s = ? and %3$s = ?",
                MetaModelPO.FIELD_ID, MetaModelPO.FIELD_STORAGE_ID, MetaModelPO.FIELD_TYPE, MetaModelPO.FIELD_VERSION,
                MetaModelPO.FIELD_DATA, MetaModelPO.FIELD_CREATE_DATE, MetaModelPO.FIELD_UPDATE_DATE,
                MetaModelPO.FIELD_CREATED_BY, MetaModelPO.FIELD_UPDATED_BY, MetaModelPO.TABLE_NAME);
        return jdbcTemplate.query(sql, new MetaModelRowMapper(), storageId, type.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upsertRecord(String storageId, MetaModelPO record) {
        boolean isNew = record.getVersion() == 1;
        if (isNew) {
            String sql = String.format("insert into %1$s (%2$s, %3$s, %4$s, %5$s, %6$s, %7$s, %8$s) "
                    + "values (:%2$s, :%3$s, :%4$s, :%5$s, :%6$s, :%7$s, :%8$s)", MetaModelPO.TABLE_NAME,
                    MetaModelPO.FIELD_ID, MetaModelPO.FIELD_STORAGE_ID, MetaModelPO.FIELD_TYPE,
                    MetaModelPO.FIELD_VERSION, MetaModelPO.FIELD_DATA, MetaModelPO.FIELD_CREATE_DATE,
                    MetaModelPO.FIELD_CREATED_BY);

            Map<String, Object> params = new HashMap<>();

            params.put(MetaModelPO.FIELD_ID, record.getId());
            params.put(MetaModelPO.FIELD_STORAGE_ID, record.getStorageId());
            params.put(MetaModelPO.FIELD_TYPE, record.getType().name());
            params.put(MetaModelPO.FIELD_VERSION, record.getVersion());
            params.put(MetaModelPO.FIELD_DATA, record.getData());
            params.put(MetaModelPO.FIELD_CREATE_DATE, record.getCreateDate());
            params.put(MetaModelPO.FIELD_CREATED_BY, record.getCreatedBy());

            int rowCount = namedJdbcTemplate.update(sql, params);
            if (rowCount == 0) {
                throw new MetadataException("Cannot insert meta data record.", ExceptionId.EX_META_INSERT_FAILED, sql);
            }         
        } else {
            String sql = String.format("update %1$s set %2$s = :%2$s, %3$s = :%3$s, %4$s = :%4$s, %5$s = :%5$s "
                    + "where %6$s = :%6$s and %7$s = :%7$s and %8$s = %8$s and %2$s + 1 <= :%2$s",
                    MetaModelPO.TABLE_NAME, MetaModelPO.FIELD_VERSION, MetaModelPO.FIELD_DATA,
                    MetaModelPO.FIELD_UPDATE_DATE, MetaModelPO.FIELD_UPDATED_BY, MetaModelPO.FIELD_ID,
                    MetaModelPO.FIELD_STORAGE_ID, MetaModelPO.FIELD_TYPE);

            Map<String, Object> params = new HashMap<>();

            params.put(MetaModelPO.FIELD_ID, record.getId());
            params.put(MetaModelPO.FIELD_STORAGE_ID, record.getStorageId());
            params.put(MetaModelPO.FIELD_TYPE, record.getType().name());
            params.put(MetaModelPO.FIELD_VERSION, record.getVersion());
            params.put(MetaModelPO.FIELD_DATA, record.getData());
            params.put(MetaModelPO.FIELD_UPDATE_DATE, record.getUpdateDate());
            params.put(MetaModelPO.FIELD_UPDATED_BY, record.getUpdatedBy());

            int rowCount = namedJdbcTemplate.update(sql, params);
            if (rowCount == 0) {
                throw new MetadataException("Meta data update failed. Key doesn't exist or version is too old.",
                        ExceptionId.EX_META_UPDATE_FAILED, sql);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upsertRecords(String storageId, List<MetaModelPO> records) {
        for (MetaModelPO po : records) {
            upsertRecord(storageId, po);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRecord(String storageId, ModelType type, String id) {
        deleteRecords(storageId, type, Collections.singletonList(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRecords(String storageId, ModelType type, List<String> ids) {
        if(ids.isEmpty()) return;
        String sql = String.format("delete from %s where %s = :%2$s and %s = :%3$s and %s in (:%4$s)",
                MetaModelPO.TABLE_NAME, MetaModelPO.FIELD_STORAGE_ID, MetaModelPO.FIELD_TYPE, MetaModelPO.FIELD_ID);

        Map<String, Object> params = new HashMap<>();

        params.put(MetaModelPO.FIELD_ID, ids);
        params.put(MetaModelPO.FIELD_STORAGE_ID, storageId);
        params.put(MetaModelPO.FIELD_TYPE, type.name());

        int rowCount = namedJdbcTemplate.update(sql, params);
        //No reason to throw exception in this case
//        if (rowCount == 0) {
//            throw new MetadataException("Meta data delete failed. Object not found.",
//                    ExceptionId.EX_META_DELETE_FAILED, sql);
//        }
   
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteModel(String storageId) {
        String sql = String.format("delete from %s where %s = ?", MetaModelPO.TABLE_NAME, MetaModelPO.FIELD_STORAGE_ID);

        return jdbcTemplate.update(sql, storageId) > 0;
    }


}

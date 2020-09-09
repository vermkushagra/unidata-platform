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

/**
 *
 */
package com.unidata.mdm.backend.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.dao.rm.EtalonDraftStateRowMapper;
import com.unidata.mdm.backend.dao.rm.EtalonRecordRowMapper;
import com.unidata.mdm.backend.dao.rm.OriginRecordRowMapper;
import com.unidata.mdm.backend.dao.rm.RecordKeysRowMapper;
import com.unidata.mdm.backend.dao.util.VendorUtils;
import com.unidata.mdm.backend.dao.util.VendorUtils.CopyDataOutputStream;
import com.unidata.mdm.backend.dao.util.pg.VendorDataType;
import com.unidata.mdm.backend.po.DuplicatePO;
import com.unidata.mdm.backend.po.EtalonDraftStatePO;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.EtalonTransitionPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;
import com.unidata.mdm.backend.po.RecordKeysPO;
import com.unidata.mdm.backend.service.data.merge.TransitionType;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;


/**
 * @author Mikhail Mikhailov
 * Data records DAO.
 */
@Repository
public class DataRecordsDaoImpl extends AbstractDaoImpl implements DataRecordsDao {

    /**
     * Logger.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordsDaoImpl.class);
    /**
     * Insert draft.
     */
    private final String insertEtalonStateDraftSQL;
    /**
     * Cleanup state drafts.
     */
    private final String cleanupEtalonStateDraftsSQL;
    /**
     * Load current state.
     */
    private final String loadLastEtalonStateDraftByEtalonIdSQL;
    /**
     * Load origins by etalon id SQL.
     */
    private final String loadOriginsByEtalonIdSQL;
    /**
     * Load keys by etalon id and source system name.
     */
    private final String loadKeysByEtalonIdAndSourceSystemNameSQL;
    /**
     * Loads keys by etalon id, source system name and ext. id for enrichment records.
     */
    private final String loadKeysByEtalonIdExternalIdAndSourceSystemNameSQL;
    /**
     * Loads keys by external id.
     */
    private final String loadKeysByExternalIdSQL;
    /**
     * Loads keys by etalon id
     */
    private final String loadKeysByEtalonIdSQL;
    /**
     * Loads keys by GSN.
     */
    private final String loadKeysByGSNSQL;
    /**
     * Loads keys by origin id.
     */
    private final String loadKeysByOriginIdSQL;
    /**
     * Mass identify template for etalons ids.
     */
    private final String loadKeysByEtalonIdsTemplateSQL;
    /**
     * Mass identify template for origin ids.
     */
    private final String loadKeysByOriginIdsTemplateSQL;
    /**
     * Mass identify template for GSNs.
     */
    private final String loadKeysByGSNsTemplateSQL;
    /**
     * Mass identify template for external ids.
     */
    private final String loadKeysByExternalIdsTemplateSQL;
    /**
     * Insert etalon SQL.
     */
    private final String insertEtalonSQL;

    /**
     * Load etalon record SQL.
     */
    private final String loadEtalonSQL;
    /**
     * Insert origin SQL.
     */
    private final String insertOriginSQL;
    /**
     * Update etalon SQL.
     */
    private final String updateEtalonSQL;
    /**
     * Update origin SQL. Only status may be actually updated after create.
     */
    private final String updateOriginSQL;
    /**
     * Select origin record SQL.
     */
    private final String selectOriginByIdSQL;
    /**
     * Select by origin id (external id + source system name).
     */
    private final String selectOriginByExternalIdSQL;
    /**
     * Change origin status by ID SQL.
     */
    private final String changeOriginStatusSQL;
    /**
     * Change etalon status by ID SQL.
     */
    private final String changeEtalonStatusSQL;
    /**
     * Change etalon approval state SQL.
     */
    private final String changeEtalonApprovalSQL;
    /**
     * Change etalon status by ID SQL.
     */
    private final String changeOriginsStatusByEtalonIdSQL;
    /**
     * Change origin status by ID SQL.
     */
    //private final String changeOriginsStatusSQL;
    /**
     * Change etalon status by ID SQL.
     */
    private final String changeEtalonsStatusSQL;
    /**
     * Change etalon status by ID SQL.
     */
    private final String changeOriginsStatusAndOwnershipByEtalonIdsSQL;
    /**
     * Change etalon status by ID SQL.
     */
    private final String changeOriginsOwnershipByEtalonIdsSQL;
    /**
     * Change vistory status.
     */
    private final String changeVistoryStatusByEtalonIdsSQL;
	/**
	 * Delete character data by origin id.
	 */
	private final String deleteCdataByOriginIdSQL;
	/**
     * Delete binary data by origin id.
     */
	private final String deleteBdataByOriginIdSQL;
	/**
     * Delete character data by etalon id.
     */
	private final String deleteCdataByEtalonIdSQL;
	/**
     * Delete binary data by etalon id.
     */
	private final String deleteBdataByEtalonIdSQL;
	/**
     * Delete vistory records by origin id.
     */
	private final String deleteVistoryByOriginIdSQL;
	/**
     * Delete origin by id.
     */
	private final String deleteOriginByIdSQL;
	/**
     * Delete etalon by id.
     */
	private final String deleteEtalonByIdSQL;

    /**
     * Delete character data by origin ids.
     */
    private final String deleteCdataByOriginIdsSQL;
    /**
     * Delete binary data by origin ids.
     */
    private final String deleteBdataByOriginIdsSQL;
    /**
     * Delete character data by etalon ids.
     */
    private final String deleteCdataByEtalonIdsSQL;
    /**
     * Delete binary data by etalon ids.
     */
    private final String deleteBdataByEtalonIdsSQL;
    /**
     * Delete vistory records by origin ids.
     */
    private final String deleteVistoryByOriginIdsSQL;
    /**
     * Delete origin by ids.
     */
    private final String deleteOriginByIdsSQL;
    /**
     * Delete etalon by ids.
     */
    private final String deleteEtalonByIdsSQL;
	/**
	 * Merge transition duplicates log support.
	 */
	private final String mergeDuplicatesTransitionSQL;
	/**
     * Insert transition record.
     */
    private final String insertEtalonTransitionSQL;
    /**
	 * Obtain transition lock.
	 */
	private final String obtainTransitionLockSQL;
	/**
     * Inserts an initial origin transition.
     */
    private final String originAttachTransitionSQL;
    /**
     * Origins merge.
     */
    private final String insertOriginsMergeTransitionSQL;

    private final String findAllOriginsForEtlaonsQuery;

    /**
     * External utility support.
     */
    @Autowired
    public DataRecordsDaoImpl(
            @Qualifier("unidataDataSource") final DataSource dataSource,
            @Qualifier("records-sql") final Properties sql
    ) {
        super(dataSource);
        insertEtalonSQL = sql.getProperty("insertEtalonSQL");
        insertEtalonStateDraftSQL = sql.getProperty("insertEtalonStateDraftSQL");
        cleanupEtalonStateDraftsSQL = sql.getProperty("cleanupEtalonStateDraftsSQL");
        loadLastEtalonStateDraftByEtalonIdSQL = sql.getProperty("loadLastEtalonStateDraftByEtalonIdSQL");
        loadOriginsByEtalonIdSQL = sql.getProperty("loadOriginsByEtalonIdSQL");
        loadKeysByExternalIdSQL = sql.getProperty("loadKeysByExternalIdSQL");
        loadKeysByOriginIdSQL = sql.getProperty("loadKeysByOriginIdSQL");
        loadEtalonSQL = sql.getProperty("loadEtalonSQL");
        loadKeysByEtalonIdAndSourceSystemNameSQL = sql.getProperty("loadKeysByEtalonIdAndSourceSystemNameSQL");
        loadKeysByEtalonIdExternalIdAndSourceSystemNameSQL = sql.getProperty("loadKeysByEtalonIdExternalIdAndSourceSystemNameSQL");
        insertOriginSQL = sql.getProperty("insertOriginSQL");
        updateEtalonSQL = sql.getProperty("updateEtalonSQL");
        updateOriginSQL = sql.getProperty("updateOriginSQL");
        selectOriginByIdSQL = sql.getProperty("selectOriginByIdSQL");
        selectOriginByExternalIdSQL = sql.getProperty("selectOriginByExternalIdSQL");
        changeOriginStatusSQL = sql.getProperty("changeOriginStatusSQL");
        changeEtalonStatusSQL = sql.getProperty("changeEtalonStatusSQL");
        changeEtalonApprovalSQL = sql.getProperty("changeEtalonApprovalSQL");
        changeOriginsStatusByEtalonIdSQL = sql.getProperty("changeOriginsStatusByEtalonIdSQL");
        //changeOriginsStatusSQL = sql.getProperty("changeOriginsStatusSQL");
        changeEtalonsStatusSQL = sql.getProperty("changeEtalonsStatusSQL");
        changeOriginsStatusAndOwnershipByEtalonIdsSQL = sql.getProperty("changeOriginsStatusAndOwnershipByEtalonIdsSQL");
        changeOriginsOwnershipByEtalonIdsSQL = sql.getProperty("changeOriginsOwnershipByEtalonIdsSQL");
        changeVistoryStatusByEtalonIdsSQL = sql.getProperty("changeVistoryStatusByEtalonIdsSQL");
        deleteCdataByOriginIdSQL = sql.getProperty("deleteCdataByOriginIdSQL");
        deleteBdataByOriginIdSQL = sql.getProperty("deleteBdataByOriginIdSQL");
        deleteCdataByEtalonIdSQL = sql.getProperty("deleteCdataByEtalonIdSQL");
        deleteBdataByEtalonIdSQL = sql.getProperty("deleteBdataByEtalonIdSQL");
        deleteVistoryByOriginIdSQL = sql.getProperty("deleteVistoryByOriginIdSQL");
        deleteOriginByIdSQL = sql.getProperty("deleteOriginByIdSQL");
        deleteEtalonByIdSQL = sql.getProperty("deleteEtalonByIdSQL");
        deleteCdataByOriginIdsSQL = sql.getProperty("deleteCdataByOriginIdsSQL");
        deleteBdataByOriginIdsSQL = sql.getProperty("deleteBdataByOriginIdsSQL");
        deleteCdataByEtalonIdsSQL = sql.getProperty("deleteCdataByEtalonIdsSQL");
        deleteBdataByEtalonIdsSQL = sql.getProperty("deleteBdataByEtalonIdsSQL");
        deleteVistoryByOriginIdsSQL = sql.getProperty("deleteVistoryByOriginIdsSQL");
        deleteOriginByIdsSQL = sql.getProperty("deleteOriginByIdsSQL");
        deleteEtalonByIdsSQL = sql.getProperty("deleteEtalonByIdsSQL");
        insertEtalonTransitionSQL = sql.getProperty("insertEtalonTransitionSQL");
        originAttachTransitionSQL = sql.getProperty("originAttachTransitionSQL");
        insertOriginsMergeTransitionSQL = sql.getProperty("insertOriginsMergeTransitionSQL");
        mergeDuplicatesTransitionSQL = sql.getProperty("mergeDuplicatesTransitionSQL");
        obtainTransitionLockSQL = sql.getProperty("obtainTransitionLockSQL");
        loadKeysByEtalonIdSQL = sql.getProperty("loadKeysByEtalonIdSQL");
        loadKeysByGSNSQL = sql.getProperty("loadKeysByGSNSQL");
        findAllOriginsForEtlaonsQuery = sql.getProperty("findAllOriginsForEtlaonsQuery");
        loadKeysByEtalonIdsTemplateSQL = StringUtils.trim(sql.getProperty("loadKeysByEtalonIdsTemplateSQL"));
        loadKeysByOriginIdsTemplateSQL = StringUtils.trim(sql.getProperty("loadKeysByOriginIdsTemplateSQL"));
        loadKeysByGSNsTemplateSQL = StringUtils.trim(sql.getProperty("loadKeysByGSNsTemplateSQL"));
        loadKeysByExternalIdsTemplateSQL = StringUtils.trim(sql.getProperty("loadKeysByExternalIdsTemplateSQL"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecordPO loadEtalonRecord(String id, boolean softDeleted, boolean merged) {
        MeasurementPoint.start();
        try {

            List<RecordStatus> states = new ArrayList<>(1 + (softDeleted ? 0 : 1) + (merged ? 0 : 1));
            states.add(RecordStatus.ACTIVE);

            if (softDeleted) {
                states.add(RecordStatus.INACTIVE);
            }

            if (merged) {
                states.add(RecordStatus.MERGED);
            }

            String param = states.stream()
                    .map(RecordStatus::name)
                    .collect(Collectors.joining(","));

            return jdbcTemplate.query(loadEtalonSQL,
                    EtalonRecordRowMapper.DEFAULT_ETALON_FIRST_RESULT_EXTRACTOR,
                    id, param);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecordPO findOriginRecordById(String id) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(selectOriginByIdSQL,
                    OriginRecordRowMapper.DEFAULT_ORIGIN_FIRST_RESULT_EXTRACTOR,
                    id);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecordPO findOriginRecordByExternalId(String externalId, String sourceSystem, String entityName) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(selectOriginByExternalIdSQL,
                    OriginRecordRowMapper.DEFAULT_ORIGIN_FIRST_RESULT_EXTRACTOR,
                    sourceSystem,
                    externalId,
                    entityName);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginRecordPO> findOriginRecordsByEtalonId(String etalonId, String sourceSystem, String externalId) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadOriginsByEtalonIdSQL,
                    OriginRecordRowMapper.DEFAULT_ROW_MAPPER,
                    etalonId,
                    sourceSystem,
                    externalId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    /*
    @Override
    public OriginRecordPO upsertOriginRecord(OriginRecordPO record, boolean isNew) {
        MeasurementPoint.start();
        try {
            if (isNew) {
                // 1. Create origin record
                int rowCount = jdbcTemplate.update(insertOriginSQL,
                        record.getId(),
                        record.getEtalonId(),
                        record.getExternalId(),
                        record.getSourceSystem(),
                        record.getName(),
                        record.getVersion(),
                        record.getCreatedBy(),
                        record.getStatus() != null ? record.getStatus().name() : RecordStatus.ACTIVE.name(),
                        record.isEnrichment()
                );

                if (rowCount == 0) {
                    throw new DataProcessingException("Cannot insert origin record.",
                            ExceptionId.EX_DATA_ORIGIN_INSERT_FAILED);
                }

                // 2. Create etalon transition of type ORIGIN_ATTACH
                jdbcTemplate.queryForRowSet(obtainTransitionLockSQL, record.getEtalonId());

                EtalonTransitionPO transition
                    = DataRecordUtils.newEtalonTransitionPO(record.getEtalonId(), null, TransitionType.ORIGIN_ATTACH);

                rowCount = jdbcTemplate.update(insertEtalonTransitionSQL,
                        transition.getId(),
                        transition.getEtalonId(),
                        transition.getOperationId(),
                        transition.getType().name(),
                        transition.getEtalonId(),
                        transition.getCreatedBy());

                if (rowCount == 0) {
                    throw new DataProcessingException("Cannot insert etalon transition record on origin insert.",
                            ExceptionId.EX_DATA_ORIGIN_INSERT_ETALON_TRANSITION_INSERT_FAILED, record);
                }

                // 3. Create (copy and join) new origin state
                rowCount = jdbcTemplate.update(originAttachTransitionSQL,
                        record.getEtalonId(),
                        record.getId());

                if (rowCount == 0) {
                    throw new DataProcessingException("Cannot insert origin transition record.",
                            ExceptionId.EX_DATA_ORIGIN_TRANSITION_ATTACH_FAILED);
                }
            } else {
                // Update status
                int rowCount = jdbcTemplate.update(updateOriginSQL,
                        record.getVersion(),
                        record.getUpdateDate(),
                        record.getUpdatedBy(),
                        record.getStatus() != null ? record.getStatus().name() : RecordStatus.ACTIVE.name(),
                        record.getId() //, record.getVersion()
                );

                if (rowCount == 0) {
                    throw new DataProcessingException("Key doesn't exist or version is too old.",
                            ExceptionId.EX_DATA_ORIGIN_UPDATE_FAILED);
                }
            }

            return record;
        } finally {
            MeasurementPoint.stop();
        }
    }
    */
    /**
     * {@inheritDoc}
     */
    @Override
    public void upsertOriginRecords(List<OriginRecordPO> records, boolean areNew) {
        MeasurementPoint.start();
        try {
            if (areNew) {

                int[] result = upsertOriginRecordsDb(records);

                if (result.length != records.size()) {
                    final String message = "Failed to batch-insert origin records.";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_ORIGIN_BATCH_INSERT_FAILED);
                }
            } else {

                int[] result = jdbcTemplate.batchUpdate(updateOriginSQL, new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, records.get(i).getVersion());
                        ps.setTimestamp(2, VendorUtils.coalesce(records.get(i).getUpdateDate()));
                        ps.setString(3, records.get(i).getUpdatedBy());
                        ps.setString(4, records.get(i).getStatus() != null ? records.get(i).getStatus().name() : RecordStatus.ACTIVE.name());
                        ps.setString(5, records.get(i).getEtalonId());
                        ps.setString(6, records.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return records.size();
                    }
                });

                if (result.length != records.size()) {
                    final String message = "Failed to batch-update origin records.";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_ORIGIN_BATCH_UPDATE_FAILED);
                }
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    private int[] upsertOriginRecordsDb(List<OriginRecordPO> records) {
        MeasurementPoint.start();
        try {
        return jdbcTemplate.batchUpdate(insertOriginSQL, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, records.get(i).getId());
                    ps.setString(2, records.get(i).getEtalonId());
                    ps.setString(3, records.get(i).getExternalId());
                    ps.setString(4, records.get(i).getSourceSystem());
                    ps.setString(5, records.get(i).getName());
                    ps.setInt(6, records.get(i).getVersion());
                    ps.setTimestamp(7, new Timestamp(records.get(i).getCreateDate().getTime()));
                    ps.setString(8, records.get(i).getCreatedBy());
                    ps.setString(9, records.get(i).getStatus() != null ? records.get(i).getStatus().name() : RecordStatus.ACTIVE.name());
                    ps.setBoolean(10, records.get(i).isEnrichment());
                }

                @Override
                public int getBatchSize() {
                    return records.size();
                }
            }
        );
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upsertEtalonRecords(List<EtalonRecordPO> records, boolean isNew) {

        MeasurementPoint.start();
        try {

            if (isNew) {

                // 1. Insert etalon record
                int[] result = upsertEtalonRecordsDb(records);

                if (result.length != records.size()) {
                    throw new DataProcessingException("Cannot insert etalon records. Records inserted {}, input {}.",
                            ExceptionId.EX_DATA_ETALON_INSERT_FAILED, result.length, records.size());
                }

                // 2. Insert pending state draft if needed
                List<EtalonRecordPO> pendings = records.stream()
                        .filter(r -> r.getApproval() == ApprovalState.PENDING)
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(pendings)) {

                    final String currentUser = SecurityUtils.getCurrentUserName();
                    jdbcTemplate.batchUpdate(insertEtalonStateDraftSQL, new BatchPreparedStatementSetter() {

                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            EtalonRecordPO pending = pendings.get(i);

                            ps.setString(1, pending.getId());
                            ps.setString(2, pending.getId());
                            ps.setString(3, pending.getStatus() == null ? RecordStatus.ACTIVE.name() : pending.getStatus().name());
                            ps.setString(4, pending.getCreatedBy() == null ? currentUser : pending.getCreatedBy());
                        }

                        @Override
                        public int getBatchSize() {
                            return pendings.size();
                        }
                    });
                }
            } else {

                List<EtalonRecordPO> pendings = records.stream()
                        .filter(r -> r.getApproval() == ApprovalState.PENDING)
                        .collect(Collectors.toList());

                List<EtalonRecordPO> updates = records.stream()
                        .filter(r -> r.getApproval() != ApprovalState.PENDING)
                        .collect(Collectors.toList());

                for (EtalonRecordPO pending : pendings) {

                    if (!changeEtalonApproval(pending.getId(), pending.getApproval())) {
                        final String message = "Cannot change approval state for a record {}.";
                        LOGGER.warn(message, pending.getId());
                        throw new DataProcessingException(message,
                                ExceptionId.EX_DATA_ETALON_APPROVAL_STATE_UPDATE_FAILED, pending.getId());
                    }

                    putEtalonStateDraft(pending.getId(), pending.getStatus(), pending.getCreatedBy());
                }

                int[] result = jdbcTemplate.batchUpdate(updateEtalonSQL, new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        EtalonRecordPO record = updates.get(i);

                        ps.setTimestamp(1, VendorUtils.coalesce(record.getUpdateDate()));
                        ps.setString(2, record.getUpdatedBy());
                        ps.setString(3, record.getStatus() != null ? record.getStatus().name() : RecordStatus.ACTIVE.name());
                        ps.setString(4, record.getApproval() != null ? record.getApproval().name() : ApprovalState.APPROVED.name());
                        ps.setString(5, record.getOperationId());
                        ps.setString(6, record.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return updates.size();
                    }
                });

                if (result.length != updates.size()) {
                    throw new DataProcessingException("Key doesn't exist or version is too old.",
                            ExceptionId.EX_DATA_ETALON_UPDATE_FAILED);
                }
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    private int[] upsertEtalonRecordsDb(List<EtalonRecordPO> records) {
        MeasurementPoint.start();
        try {
        return jdbcTemplate.batchUpdate(insertEtalonSQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                EtalonRecordPO record = records.get(i);

                ps.setString(1, record.getName());
                ps.setInt(2, record.getVersion());
                ps.setTimestamp(3, new Timestamp(record.getCreateDate().getTime()));
                ps.setString(4, record.getCreatedBy());
                ps.setString(5, record.getId());
                ps.setString(6, record.getApproval() == null ? ApprovalState.APPROVED.name() : record.getApproval().name());
                ps.setString(7, record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name());
                ps.setString(8, record.getOperationId());
            }

            @Override
            public int getBatchSize() {
                return records.size();
            }
        });
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bulkInsertEtalonRecords(List<EtalonRecordPO> records, String targetTable) {

        MeasurementPoint.start();
        try {

            final String prolog = new StringBuilder().append("copy ")
                    .append(targetTable)
                    .append(" (id, version, name, create_date, created_by, status, approval) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                VendorDataType.UUID,
                VendorDataType.INT4,
                VendorDataType.CHAR,
                VendorDataType.TIMESTAMP,
                VendorDataType.CHAR,
                VendorDataType.CHAR,
                VendorDataType.CHAR
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, prolog)) {

                for (EtalonRecordPO record : records) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = record.getVersion();
                    params[2] = record.getName();
                    params[3] = record.getCreateDate();
                    params[4] = record.getCreatedBy();
                    params[5] = record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name();
                    params[6] = record.getApproval() == null ? ApprovalState.APPROVED.name() : record.getApproval().name();

                    VendorUtils.bulkAppend(stream, types, params);
                }

                VendorUtils.bulkFinish(stream);
            } catch (SQLException e) {
                LOGGER.error("SQL exception caught!", e);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bulkInsertOriginRecords(List<OriginRecordPO> records, String targetTable) {
        MeasurementPoint.start();
        try {

            final String prolog = new StringBuilder()
                    .append("copy ")
                    .append(targetTable)
                    .append(" (id, version, name, source_system, external_id, etalon_id, create_date, created_by, status, is_enrichment) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                VendorDataType.UUID,
                VendorDataType.INT4,
                VendorDataType.CHAR,
                VendorDataType.CHAR,
                VendorDataType.CHAR,
                VendorDataType.UUID,
                VendorDataType.TIMESTAMP,
                VendorDataType.CHAR,
                VendorDataType.CHAR,
                VendorDataType.BOOLEAN
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, prolog)) {

                for (OriginRecordPO record : records) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = record.getVersion();
                    params[2] = record.getName();
                    params[3] = record.getSourceSystem();
                    params[4] = record.getExternalId();
                    params[5] = UUID.fromString(record.getEtalonId());
                    params[6] = record.getCreateDate();
                    params[7] = record.getCreatedBy();
                    params[8] = record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name();
                    params[9] = record.isEnrichment();

                    VendorUtils.bulkAppend(stream, types, params);
                }

                VendorUtils.bulkFinish(stream);
            } catch (SQLException e) {
                LOGGER.error("SQL exception caught!", e);
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bulkUpdateEtalonRecords(List<EtalonRecordPO> records, String targetTable) {

        MeasurementPoint.start();
        try {

            final String prolog = new StringBuilder().append("copy ")
                    .append(targetTable)
                    .append(" (id, update_date, updated_by, status) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                VendorDataType.UUID,
                VendorDataType.TIMESTAMP,
                VendorDataType.CHAR,
                VendorDataType.CHAR
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, prolog)) {

                for (EtalonRecordPO record : records) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = record.getUpdateDate();
                    params[2] = record.getUpdatedBy();
                    params[3] = record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name();

                    VendorUtils.bulkAppend(stream, types, params);
                }

                VendorUtils.bulkFinish(stream);
            } catch (SQLException e) {
                LOGGER.error("SQL exception caught!", e);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bulkUpdateOriginRecords(List<OriginRecordPO> records, String targetTable) {

        MeasurementPoint.start();
        try {

            final String prolog = new StringBuilder()
                    .append("copy ")
                    .append(targetTable)
                    .append(" (id, update_date, updated_by, status) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                VendorDataType.UUID,
                VendorDataType.TIMESTAMP,
                VendorDataType.CHAR,
                VendorDataType.CHAR
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, prolog)) {

                for (OriginRecordPO record : records) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = record.getUpdateDate();
                    params[2] = record.getUpdatedBy();
                    params[3] = record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name();

                    VendorUtils.bulkAppend(stream, types, params);
                }

                VendorUtils.bulkFinish(stream);
            } catch (SQLException e) {
                LOGGER.error("SQL exception caught!", e);
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    /*
    @Override
    public EtalonRecordPO upsertEtalonRecord(EtalonRecordPO record, boolean isNew) {

        MeasurementPoint.start();
        try {

            if (isNew) {

                // 1. Insert etalon record
                int rowCount = jdbcTemplate.update(insertEtalonSQL,
                        record.getName(),
                        record.getVersion(),
                        record.getCreateDate(),
                        record.getCreatedBy(),
                        record.getId(),
                        record.getApproval() == null ? ApprovalState.APPROVED : record.getApproval().name(),
                        record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name());

                if (rowCount == 0) {
                    throw new DataProcessingException("Cannot insert etalon record.",
                            ExceptionId.EX_DATA_ETALON_INSERT_FAILED, record);
                }

                // 2. Insert pending state draft if needed
                if (ApprovalState.PENDING == record.getApproval()) {
                    putEtalonStateDraft(record.getId(), record.getStatus(), record.getCreatedBy());
                }

                // 3. Insert etalon transition record.
                EtalonTransitionPO transition
                    = DataRecordUtils.newEtalonTransitionPO(record.getId(), null, TransitionType.CREATE);

                rowCount = jdbcTemplate.update(insertEtalonTransitionSQL,
                        transition.getId(),
                        transition.getEtalonId(),
                        transition.getOperationId(),
                        transition.getType().name(),
                        transition.getEtalonId(),
                        transition.getCreatedBy());

                if (rowCount == 0) {
                    throw new DataProcessingException("Cannot insert initial etalon transition record.",
                            ExceptionId.EX_DATA_INITIAL_ETALON_TRANSITION_INSERT_FAILED, record);
                }
            } else {

                if (ApprovalState.PENDING == record.getApproval()) {

                    if (!changeEtalonApproval(record.getId(), record.getApproval())) {
                        final String message = "Cannot change approval state for a record {}.";
                        LOGGER.warn(message, record.getId());
                        throw new DataProcessingException(message,
                                ExceptionId.EX_DATA_ETALON_APPROVAL_STATE_UPDATE_FAILED, record.getId());
                    }

                    putEtalonStateDraft(record.getId(), record.getStatus(), record.getCreatedBy());

                } else {

                    int rowCount = jdbcTemplate.update(updateEtalonSQL,
                        record.getUpdateDate(),
                        record.getUpdatedBy(),
                        record.getStatus() != null ? record.getStatus().name() : RecordStatus.ACTIVE.name(),
                        record.getApproval() != null ? record.getApproval().name() : ApprovalState.APPROVED.name(),
                        record.getId()
                    );

                    if (rowCount == 0) {
                        throw new DataProcessingException("Key doesn't exist or version is too old.",
                                ExceptionId.EX_DATA_ETALON_UPDATE_FAILED, record);
                    }
                }
            }

            return record;
        } finally {
            MeasurementPoint.stop();
        }
    }
    */
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cleanupEtalonStateDrafts(String etalonId) {

        boolean success = false;
        MeasurementPoint.start();
        try {
            success = jdbcTemplate.update(cleanupEtalonStateDraftsSQL, etalonId) > 0;
        } finally {
            MeasurementPoint.stop();
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean putEtalonStateDraft(String etalonId, RecordStatus status, String createdBy) {

        boolean success = false;
        MeasurementPoint.start();
        try {
            success = jdbcTemplate.update(insertEtalonStateDraftSQL,
                        etalonId,
                        etalonId,
                        status == null ? RecordStatus.ACTIVE.name() : status.name(),
                        createdBy == null ? SecurityUtils.getCurrentUserName() : createdBy) > 0;
        } finally {
            MeasurementPoint.stop();
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonDraftStatePO loadLastEtalonStateDraft(String etalonId) {

        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadLastEtalonStateDraftByEtalonIdSQL,
                    EtalonDraftStateRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    etalonId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteEtalonRecord(String etalonId, String operationId, ApprovalState state, Date deleteDate, boolean cascade) {

        boolean success;
        if (ApprovalState.PENDING == state) {
            changeEtalonApproval(etalonId, state);
            success = putEtalonStateDraft(etalonId, RecordStatus.INACTIVE, SecurityUtils.getCurrentUserName());
        } else {

            success = jdbcTemplate.update(changeEtalonStatusSQL,
                    RecordStatus.INACTIVE.name(),
                    state == null ? ApprovalState.APPROVED.name() : state.name(),
                    deleteDate,
                    SecurityUtils.getCurrentUserName(),
                    operationId,
                    etalonId,
                    RecordStatus.INACTIVE.name()) > 0;

            if (cascade && success) {
                jdbcTemplate.update(changeOriginsStatusByEtalonIdSQL,
                        RecordStatus.INACTIVE.name(),
                        SecurityUtils.getCurrentUserName(),
                        etalonId,
                        RecordStatus.INACTIVE.name());
            }
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteOriginRecord(String originId, Date deleteDate) {

        return jdbcTemplate.update(changeOriginStatusSQL,
                RecordStatus.INACTIVE.name(),
                deleteDate,
                SecurityUtils.getCurrentUserName(),
                originId,
                RecordStatus.INACTIVE.name()) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wipeOriginRecord(String originId) {

        jdbcTemplate.update(deleteCdataByOriginIdSQL, originId);
        jdbcTemplate.update(deleteBdataByOriginIdSQL, originId);
        jdbcTemplate.update(deleteVistoryByOriginIdSQL, originId);
        jdbcTemplate.update(deleteOriginByIdSQL, originId);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wipeEtalonRecord(String etalonId) {

        jdbcTemplate.update(deleteCdataByEtalonIdSQL, etalonId);
        jdbcTemplate.update(deleteBdataByEtalonIdSQL, etalonId);
        jdbcTemplate.update(deleteEtalonByIdSQL, etalonId);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wipeOriginRecords(List<String> originIds) {
        Map<String, Object> params = new HashMap<>();
        params.put(OriginsVistoryRecordPO.FIELD_ORIGIN_ID, originIds);

        final SqlParameterSource paramSource = new MapSqlParameterSource(params) {
            @Override
            public Object getValue(String paramName) {

                Object val = super.getValue(paramName);
                if (OriginsVistoryRecordPO.FIELD_ORIGIN_ID.equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream().filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }

                return val;
            }
        };

        namedJdbcTemplate.update(deleteCdataByOriginIdsSQL, paramSource);
        namedJdbcTemplate.update(deleteBdataByOriginIdsSQL, paramSource);
        namedJdbcTemplate.update(deleteVistoryByOriginIdsSQL, paramSource);
        namedJdbcTemplate.update(deleteOriginByIdsSQL, paramSource);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wipeEtalonRecords(List<String> etalonIds) {

        Map<String, Object> params = new HashMap<>();
        params.put(OriginRecordPO.FIELD_ETALON_ID, etalonIds);

        final SqlParameterSource paramSource = new MapSqlParameterSource(params) {
            @Override
            public Object getValue(String paramName) {

                Object val = super.getValue(paramName);
                if (OriginRecordPO.FIELD_ETALON_ID.equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream().filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }

                return val;
            }

        };

        namedJdbcTemplate.update(deleteCdataByEtalonIdsSQL, paramSource);
        namedJdbcTemplate.update(deleteBdataByEtalonIdsSQL, paramSource);
        namedJdbcTemplate.update(deleteEtalonByIdsSQL, paramSource);

        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean restoreEtalonRecord(String etalonId, String operationId, Date restoreDate) {

        boolean success = jdbcTemplate.update(changeEtalonStatusSQL,
                RecordStatus.ACTIVE.name(),
                ApprovalState.APPROVED.name(),
                restoreDate,
                SecurityUtils.getCurrentUserName(),
                operationId,
                etalonId,
                RecordStatus.ACTIVE.name()) > 0;

        if (success) {
            success = jdbcTemplate.update(changeOriginsStatusByEtalonIdSQL,
                    RecordStatus.ACTIVE.name(),
                    SecurityUtils.getCurrentUserName(),
                    etalonId,
                    RecordStatus.ACTIVE.name()) > 0;
        }

        return success;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mergeRecords(String masterId, List<String> duplicateIds, String operationId, boolean isManual) {

        // 1. Update golden records
        Map<String, Object> params = new HashMap<>();
        params.put(OriginRecordPO.FIELD_STATUS, RecordStatus.MERGED.name());
        params.put(OriginRecordPO.FIELD_ID, duplicateIds);
        params.put(OriginRecordPO.FIELD_ETALON_ID, masterId);
        params.put(EtalonRecordPO.FIELD_OPERATION_ID, operationId);
        params.put(OriginRecordPO.FIELD_UPDATED_BY, SecurityUtils.getCurrentUserName());
        params.put(DuplicatePO.FIELD_IS_AUTO, !isManual);

        final SqlParameterSource paramSource = new MapSqlParameterSource(params) {
            @Override
            public Object getValue(String paramName) {

                Object val = super.getValue(paramName);
                if (OriginRecordPO.FIELD_ETALON_ID.equals(paramName)) {
                    return val == null ? null : UUID.fromString(val.toString());
                } else if (OriginRecordPO.FIELD_ID.equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream().filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }

                return val;
            }

        };

        int rowCount = namedJdbcTemplate.update(changeEtalonsStatusSQL, paramSource);

        if (rowCount != duplicateIds.size()) {
            return false;
        }

        if (isManual) {
            // 2. Update origins_vistory
            //namedJdbcTemplate.update(changeVistoryStatusByEtalonIdsSQL, paramSource);

            // 3. Update origins
            //namedJdbcTemplate.update(changeOriginsStatusAndOwnershipByEtalonIdsSQL, paramSource);
            namedJdbcTemplate.update(changeOriginsOwnershipByEtalonIdsSQL, paramSource);
        } else {
            // 3 Update origins
            namedJdbcTemplate.update(changeOriginsOwnershipByEtalonIdsSQL, paramSource);
        }

        // 4. Insert etalon transition mark
        jdbcTemplate.queryForRowSet(obtainTransitionLockSQL, masterId);

        EtalonTransitionPO transition
            = DataRecordUtils.newEtalonTransitionPO(masterId, null, TransitionType.ETALON_MERGE);

        rowCount = jdbcTemplate.update(insertEtalonTransitionSQL,
                transition.getId(),
                transition.getEtalonId(),
                transition.getOperationId(),
                transition.getType().name(),
                transition.getEtalonId(),
                transition.getCreatedBy());

        if (rowCount == 0) {
            throw new DataProcessingException("Cannot insert etalon transition record etalon(s) merge.",
                    ExceptionId.EX_DATA_ETALON_MERGE_ETALON_TRANSITION_INSERT_FAILED, transition);
        }

        // 5. Insert merge state
        rowCount = namedJdbcTemplate.update(mergeDuplicatesTransitionSQL, paramSource);

        if (rowCount == 0) {
            throw new DataProcessingException("Cannot insert duplicates state transition on merge.",
                    ExceptionId.EX_DATA_DUPLICATES_STATE_ETALON_TRANSITION_INSERT_FAILED, params);
        }

        // 6. Copy origins.
        rowCount = jdbcTemplate.update(insertOriginsMergeTransitionSQL, masterId);

        if (rowCount == 0) {
            throw new DataProcessingException("Cannot insert origins transition on merge.",
                    ExceptionId.EX_DATA_ORIGIN_TRANSITION_MERGE_FAILED, params);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeEtalonApproval(String etalonId, ApprovalState approval) {

        boolean success = false;
        MeasurementPoint.start();
        try {

            int rowCount = jdbcTemplate.update(changeEtalonApprovalSQL,
                SecurityUtils.getCurrentUserName(),
                approval != null ? approval.name() : ApprovalState.APPROVED.name(),
                etalonId,
                approval != null ? approval.name() : ApprovalState.APPROVED.name()
            );

            success = rowCount > 0;

        } finally {
            MeasurementPoint.stop();
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordKeysPO> loadRecordKeysByEtalonId(String id, String sourceSystem) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByEtalonIdAndSourceSystemNameSQL,
                    RecordKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    sourceSystem, id);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordKeysPO> loadRecordKeysByEtalonId(String id) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByEtalonIdSQL,
                    RecordKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    id,
                    id);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordKeysPO> loadRecordKeysByGSN(long gsn) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByGSNSQL,
                    RecordKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    gsn);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordKeysPO> loadRecordKeysByEtalonId(String id, String sourceSystem, String externalId, boolean isEnrichment) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByEtalonIdExternalIdAndSourceSystemNameSQL,
                    RecordKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    sourceSystem, externalId, isEnrichment, id);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordKeysPO> loadRecordKeysByOriginId(String id) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByOriginIdSQL,
                    RecordKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    id);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordKeysPO> loadRecordKeysByExternalId(String externalId, String sourceSystem, String name) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByExternalIdSQL,
                    RecordKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    sourceSystem, externalId, name);
        } finally {
            MeasurementPoint.stop();
        }
    }

    private Map<String, String> prepareEtalonsMap(Map<String, Object> params, StringBuilder sqlBuffer, List<Object> values) {

        if (CollectionUtils.isNotEmpty(values)) {

            Map<String, String> etalons = new HashMap<>(values.size());
            params.put(OriginRecordPO.FIELD_ETALON_ID,
                    values.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(v -> { etalons.put(v, StringUtils.EMPTY); return v; })
                        .map(UUID::fromString)
                        .collect(Collectors.toList()));

            if (sqlBuffer.length() > 0) {
                sqlBuffer.append(StringUtils.LF)
                         .append("union ")
                         .append(StringUtils.LF);
            }

            sqlBuffer.append(loadKeysByEtalonIdsTemplateSQL);
            return etalons;
        }

        return Collections.emptyMap();
    }

    private Map<String, String> prepareOriginsMap(Map<String, Object> params, StringBuilder sqlBuffer, List<Object> values) {

        if (CollectionUtils.isNotEmpty(values)) {

            Map<String, String> origins = new HashMap<>(values.size());
            params.put(OriginsVistoryRecordPO.FIELD_ORIGIN_ID,
                    values.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(v -> { origins.put(v, StringUtils.EMPTY); return v; })
                        .map(UUID::fromString)
                        .collect(Collectors.toList()));

            if (sqlBuffer.length() > 0) {
                sqlBuffer.append(StringUtils.LF)
                         .append("union ")
                         .append(StringUtils.LF);
            }

            sqlBuffer.append(loadKeysByOriginIdsTemplateSQL);
            return origins;
        }

        return Collections.emptyMap();
    }

    private Map<Long, String> prepareGsnMap(Map<String, Object> params, StringBuilder sqlBuffer, List<Object> values) {

        if (CollectionUtils.isNotEmpty(values)) {

            Map<Long, String> gsns = new HashMap<>(values.size());
            params.put(OriginsVistoryRecordPO.FIELD_GSN,
                    values.stream()
                        .filter(Objects::nonNull)
                        .map(v -> (Long) v)
                        .map(v -> { gsns.put(v, StringUtils.EMPTY); return v; })
                        .collect(Collectors.toList()));

            if (sqlBuffer.length() > 0) {
                sqlBuffer.append(StringUtils.LF)
                         .append("union ")
                         .append(StringUtils.LF);
            }

            sqlBuffer.append(loadKeysByGSNsTemplateSQL);
            return gsns;
        }

        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private Map<Triple<String, String, String>, String>
        prepareExternalIdMap(StringBuilder sqlBuffer, List<Object> values) {

        if (CollectionUtils.isNotEmpty(values)) {

            Map<Triple<String, String, String>, String> externalIds = new HashMap<>(values.size());
            final StringBuilder localSql = new StringBuilder();
            for (Object val : values) {

                Triple<String, String, String> triple = (Triple<String, String, String>) val;
                if (localSql.length() > 0) {
                    localSql
                        .append(StringUtils.LF)
                        .append("union select '");
                } else {
                    localSql
                        .append("select '");
                }

                localSql
                    .append(triple.getLeft())
                    .append("'::text as source_system, '").append(triple.getMiddle())
                    .append("'::text as external_id, '").append(triple.getRight())
                    .append("'::text as name ");

                externalIds.put(triple, StringUtils.EMPTY);
            }

            String replacement = StringUtils.replace(loadKeysByExternalIdsTemplateSQL, ":external_id", localSql.toString());
            if (sqlBuffer.length() > 0) {
                sqlBuffer.append(StringUtils.LF)
                         .append("union ")
                         .append(StringUtils.LF);
            }

            sqlBuffer.append(replacement);
            return externalIds;
        }

        return Collections.emptyMap();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Object, List<RecordKeysPO>> loadRecordKeys(Map<IdSetType, List<Object>> ids) {

        MeasurementPoint.start();
        try {

            if (MapUtils.isEmpty(ids)) {
                return Collections.emptyMap();
            }

            Map<String, Object> params = new HashMap<>(3);
            StringBuilder sqlBuffer = new StringBuilder();

            final Map<String, String> etalons = prepareEtalonsMap(params, sqlBuffer, ids.get(IdSetType.ETALON_ID));
            final Map<String, String> origins = prepareOriginsMap(params, sqlBuffer, ids.get(IdSetType.ORIGIN_ID));
            final Map<Long, String> gsns = prepareGsnMap(params, sqlBuffer, ids.get(IdSetType.GSN));
            final Map<Triple<String, String, String>, String> externalIds = prepareExternalIdMap(sqlBuffer, ids.get(IdSetType.EXTERNAL_ID));
            final SqlParameterSource paramSource = new MapSqlParameterSource(params) {
                @Override
                public Object getValue(String paramName) {

                    Object val = super.getValue(paramName);
                    if (OriginRecordPO.FIELD_ETALON_ID.equals(paramName) && val instanceof Collection) {
                        return ((Collection<?>) val).stream().filter(Objects::nonNull)
                                .map(Object::toString)
                                .map(UUID::fromString)
                                .collect(Collectors.toList());
                    }

                    return val;
                }

            };
            return namedJdbcTemplate.query(sqlBuffer.toString(),  paramSource, rs -> {

                Map<String, List<RecordKeysPO>> interim = new HashMap<>(ids.size());
                while (rs.next()) {

                    RecordKeysPO row = RecordKeysRowMapper.DEFAULT_ROW_MAPPER.mapRow(rs, 0);
                    interim.computeIfAbsent(row.getEtalonId(), id -> new ArrayList<>()).add(row);

                    // Etalon id found.
                    String mark = etalons.get(row.getEtalonId());
                    if (Objects.nonNull(mark) && mark == StringUtils.EMPTY) {

                        // Hack - HashMap will not modify old key value
                        etalons.put(row.getEtalonId(), row.getEtalonId());
                        continue;
                    }

                    // GSN found
                    mark = gsns.get(row.getEtalonGsn());
                    if (Objects.nonNull(mark) && mark == StringUtils.EMPTY) {

                        // Hack - HashMap will not modify old key value
                        gsns.put(row.getEtalonGsn(), row.getEtalonId());
                        continue;
                    }

                    // Origin found
                    mark = origins.get(row.getOriginId());
                    if (Objects.nonNull(mark) && mark == StringUtils.EMPTY) {

                        // Hack - HashMap will not modify old key value
                        origins.put(row.getOriginId(), row.getEtalonId());
                        continue;
                    }

                    // Ext id found
                    Triple<String, String, String> id
                        = new ImmutableTriple<>(row.getOriginSourceSystem(), row.getOriginExternalId(), row.getOriginName());
                    mark = externalIds.get(id);
                    if (Objects.nonNull(mark) && mark == StringUtils.EMPTY) {

                        // Hack - HashMap will not modify old key value
                        externalIds.put(id, row.getEtalonId());
                        continue;
                    }
                }

                Map<Object, List<RecordKeysPO>> result
                    = new IdentityHashMap<>(etalons.size() + origins.size() + externalIds.size() + gsns.size());

                result.putAll(etalons.entrySet().stream()
                    .filter(entry -> entry.getValue() != StringUtils.EMPTY)
                    .collect(Collectors.toMap(Entry::getKey, entry -> interim.get(entry.getValue()))));

                result.putAll(origins.entrySet().stream()
                        .filter(entry -> entry.getValue() != StringUtils.EMPTY)
                        .collect(Collectors.toMap(Entry::getKey, entry -> interim.get(entry.getValue()))));

                result.putAll(gsns.entrySet().stream()
                        .filter(entry -> entry.getValue() != StringUtils.EMPTY)
                        .collect(Collectors.toMap(Entry::getKey, entry -> interim.get(entry.getValue()))));

                result.putAll(externalIds.entrySet().stream()
                        .filter(entry -> entry.getValue() != StringUtils.EMPTY)
                        .collect(Collectors.toMap(Entry::getKey, entry -> interim.get(entry.getValue()))));

                return result;
            });

        } finally {
            MeasurementPoint.stop();
        }
    }

    @Override
    public Map<String, List<OriginRecordPO>> findAllActiveOriginsForEtlaons(List<String> etalonsIds) {

        if (CollectionUtils.isEmpty(etalonsIds)) {
            return Collections.emptyMap();
        }

        final Map<String, Object> params = new HashMap<>();
        params.put(OriginRecordPO.FIELD_ETALON_ID, etalonsIds);

        final SqlParameterSource paramSource = new MapSqlParameterSource(params) {
            @Override
            public Object getValue(String paramName) {

                Object val = super.getValue(paramName);
                if (OriginRecordPO.FIELD_ETALON_ID.equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream().filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }

                return val;
            }

        };

        return namedJdbcTemplate.query(
                findAllOriginsForEtlaonsQuery,
                paramSource,
                rs -> {

                    final Map<String, List<OriginRecordPO>> result = new HashMap<>();
                    while (rs.next()) {
                        final String etalonId = rs.getString(OriginRecordPO.FIELD_ETALON_ID);
                        if (!result.containsKey(etalonId)) {
                            result.put(etalonId, new ArrayList<>());
                        }
                        result.get(etalonId).add(OriginRecordRowMapper.DEFAULT_ROW_MAPPER.mapRow(rs, 0));
                    }

                    return result;
                }
        );
    }
}

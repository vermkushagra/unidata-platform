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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.configuration.DumpTargetFormat;
import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.dao.ClassifiersDAO;
import com.unidata.mdm.backend.dao.rm.ClassifierKeysRowMapper;
import com.unidata.mdm.backend.dao.rm.ClassifierVistoryRowMapper;
import com.unidata.mdm.backend.dao.rm.EtalonClassifierDraftStateRowMapper;
import com.unidata.mdm.backend.dao.rm.EtalonClassifierRowMapper;
import com.unidata.mdm.backend.dao.rm.OriginClassifierRowMapper;
import com.unidata.mdm.backend.dao.util.VendorUtils;
import com.unidata.mdm.backend.dao.util.VendorUtils.CopyDataOutputStream;
import com.unidata.mdm.backend.dao.util.pg.VendorDataType;
import com.unidata.mdm.backend.po.ClassifierKeysPO;
import com.unidata.mdm.backend.po.EtalonClassifierDraftStatePO;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.po.OriginClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.DumpUtils;

/**
 * @author Mikhail Mikhailov
 *         Classifiers data DAO implementation.
 */
@Repository
public class ClassifiersDAOImpl extends AbstractDaoImpl implements ClassifiersDAO {
    /**
     * Current platform configuration.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
    /**
     * Lock acquisition.
     */
    private final String acquireOriginClassifierLockSQL;
    /**
     * Puts JAXB version.
     */
    private final String putClassifierVersionJaxbSQL;
    /**
     * Puts protostuff version.
     */
    private final String putClassifierVersionProtostuffSQL;
    /**
     * Inserts origin.
     */
    private final String insertOriginClassifierSQL;
    /**
     * Updates origin.
     */
    private final String updateOriginClassifierSQL;
    /**
     * Inserts etalon.
     */
    private final String insertEtalonClassifierSQL;
    /**
     * Updates etalon.
     */
    private final String updateEtalonClassifierSQL;
    /**
     * Loads etalon by id.
     */
    private final String loadEtalonClassifierByIdSQL;
    /**
     * Loads etalon records by record etalon id.
     */
    private final String loadEtalonClassifiersByFromNameAndStatusSQL;
    /**
     * Loads versions.
     */
    private final String loadClassifierVersionsByEtalonIdAndDateJaxbSQL;
    /**
     * Loads versions.
     */
    private final String loadClassifierVersionsByEtalonIdAndDateProtostuffSQL;
    /**
     * Loads versions by operation id.
     */
    private final String loadClassifierVersionsByEtalonIdOperationIdAndDateJaxbSQL;
    /**
     * Loads versions by operation id.
     */
    private final String loadClassifierVersionsByEtalonIdOperationIdAndDateProtostuffSQL;
    /**
     * Loads keys.
     */
    private final String loadKeysByClassiferOriginIdSQL;
    /**
     * Loads keys.
     */
    private final String loadKeysByClassifierEtalonIdSQL;
    /**
     * Loads keys.
     */
    private final String loadKeysByRecordEtalonIdSQL;
    /**
     * Loads keys.
     */
    private final String loadKeysByRecordOriginIdSQL;
    /**
     * Loads origin.
     */
    private final String loadOriginClassifiersByIdAndSourceSystemSQL;
    /**
     * Loads origin.
     */
    private final String loadOriginClassifierByIdSQL;
    /**
     * Delete vistory records.
     */
    private final String deleteVistoryByOriginIdSQL;
    /**
     * Wipe origin.
     */
    private final String deleteOriginByIdSQL;
    /**
     * Wipe etalon.
     */
    private final String deleteEtalonByIdSQL;
    /**
     * Check usage.
     */
    private final String checkUsageByRecordEtalonIdsSQL;
    /**
     * Remap classifier
     */
    private final String remapEtalonClassifier;
    /**
     * Mark merged.
     */
    private final String markEtalonClassifiersMergedByEtalonRecordIdSQL;
    /**
     * Change state.
     */
    private final String changeEtalonApprovalSQL;
    private final String changeVersionsApprovalSQL;
    private final String insertEtalonClassifierStateDraft;
    private final String cleanupEtalonClassifierStateDrafts;
    private final String loadLastEtalonClassifierStateDraftByEtalonIdSQL;
    private final String loadActiveInactiveClassifierVersionsInfoSQL;
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifiersDAOImpl.class);

    /**
     * Constructor.
     *
     * @param dataSource
     */
    @Autowired
    public ClassifiersDAOImpl(DataSource dataSource, @Qualifier("classifier-data-sql") Properties sql) {
        super(dataSource);
        acquireOriginClassifierLockSQL = sql.getProperty("acquireOriginClassifierLockSQL");
        putClassifierVersionJaxbSQL = sql.getProperty("putClassifierVersionJaxbSQL");
        putClassifierVersionProtostuffSQL = sql.getProperty("putClassifierVersionProtostuffSQL");
        insertOriginClassifierSQL = sql.getProperty("insertOriginClassifierSQL");
        updateOriginClassifierSQL = sql.getProperty("updateOriginClassifierSQL");
        insertEtalonClassifierSQL = sql.getProperty("insertEtalonClassifierSQL");
        updateEtalonClassifierSQL = sql.getProperty("updateEtalonClassifierSQL");
        loadEtalonClassifierByIdSQL = sql.getProperty("loadEtalonClassifierByIdSQL");
        loadEtalonClassifiersByFromNameAndStatusSQL = sql.getProperty("loadEtalonClassifiersByFromNameAndStatusSQL");
        loadClassifierVersionsByEtalonIdAndDateJaxbSQL = sql.getProperty("loadClassifierVersionsByEtalonIdAndDateJaxbSQL");
        loadClassifierVersionsByEtalonIdAndDateProtostuffSQL = sql.getProperty("loadClassifierVersionsByEtalonIdAndDateProtostuffSQL");
        loadClassifierVersionsByEtalonIdOperationIdAndDateJaxbSQL = sql.getProperty("loadClassifierVersionsByEtalonIdOperationIdAndDateJaxbSQL");
        loadClassifierVersionsByEtalonIdOperationIdAndDateProtostuffSQL = sql.getProperty("loadClassifierVersionsByEtalonIdOperationIdAndDateProtostuffSQL");
        loadKeysByClassiferOriginIdSQL = sql.getProperty("loadKeysByClassiferOriginIdSQL");
        loadKeysByClassifierEtalonIdSQL = sql.getProperty("loadKeysByClassifierEtalonIdSQL");
        loadKeysByRecordEtalonIdSQL = sql.getProperty("loadKeysByRecordEtalonIdSQL");
        loadKeysByRecordOriginIdSQL = sql.getProperty("loadKeysByRecordOriginIdSQL");
        loadOriginClassifiersByIdAndSourceSystemSQL = sql.getProperty("loadOriginClassifiersByIdAndSourceSystemSQL");
        loadOriginClassifierByIdSQL = sql.getProperty("loadOriginClassifierByIdSQL");
        deleteVistoryByOriginIdSQL = sql.getProperty("deleteVistoryByOriginIdSQL");
        deleteOriginByIdSQL = sql.getProperty("deleteOriginByIdSQL");
        deleteEtalonByIdSQL = sql.getProperty("deleteEtalonByIdSQL");
        markEtalonClassifiersMergedByEtalonRecordIdSQL = sql.getProperty("markEtalonClassifiersMergedByEtalonRecordIdSQL");
        checkUsageByRecordEtalonIdsSQL = sql.getProperty("checkUsageByRecordEtalonIdsSQL");
        remapEtalonClassifier = sql.getProperty("remapEtalonClassifier");
        changeEtalonApprovalSQL = sql.getProperty("changeEtalonApprovalSQL");
        changeVersionsApprovalSQL = sql.getProperty("changeVersionsApprovalSQL");
        insertEtalonClassifierStateDraft = sql.getProperty("insertEtalonClassifierStateDraft");
        cleanupEtalonClassifierStateDrafts = sql.getProperty("cleanupEtalonClassifierStateDrafts");
        loadLastEtalonClassifierStateDraftByEtalonIdSQL = sql.getProperty("loadLastEtalonClassifierStateDraftByEtalonIdSQL");
        loadActiveInactiveClassifierVersionsInfoSQL = sql.getProperty("loadActiveInactiveClassifierVersionsInfoSQL");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeysPO loadClassifierKeysByClassifierOriginId(String originId) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByClassiferOriginIdSQL,
                    ClassifierKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    originId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeysPO loadClassifierKeysByClassifierEtalonId(String sourceSystem, String etalonId) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByClassifierEtalonIdSQL,
                    ClassifierKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    sourceSystem, etalonId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClassifierKeysPO> loadPotentialClassifierKeysByRecordEtalonIdAndClassifierName(String sourceSystem, String etalonId,
                                                                                               String classifierName) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByRecordEtalonIdSQL,
                    ClassifierKeysRowMapper.DEFAULT_ROW_MAPPER,
                    sourceSystem, etalonId, classifierName);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClassifierKeysPO> loadPotentialClassifierKeysByRecordOriginIdAndClassifierName(String originId, String classifierName) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByRecordOriginIdSQL,
                    ClassifierKeysRowMapper.DEFAULT_ROW_MAPPER,
                    originId, classifierName);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryClassifierPO> loadClassifierVersions(String classifierEtalonId, Date asOf,
                                                                   boolean includeDraftVersions) {
        MeasurementPoint.start();
        try {

            String statement;
            RowMapper<OriginsVistoryClassifierPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadClassifierVersionsByEtalonIdAndDateJaxbSQL;
                rm = ClassifierVistoryRowMapper.DEFAULT_CLASSIFIER_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadClassifierVersionsByEtalonIdAndDateProtostuffSQL;
                rm = ClassifierVistoryRowMapper.DEFAULT_CLASSIFIER_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String user = SecurityUtils.getCurrentUserName();
            Timestamp point = asOf == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(asOf.getTime());
            return jdbcTemplate.query(statement, rm,
                    classifierEtalonId,
                    point,
                    includeDraftVersions ? "true" : "false",
                    user,
                    includeDraftVersions,
                    user);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryClassifierPO> loadClassifierVersions(String classifierEtalonId, Date asOf, String operationId,
                                                                   boolean includeDraftVersions) {
        MeasurementPoint.start();
        try {

            String statement;
            RowMapper<OriginsVistoryClassifierPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadClassifierVersionsByEtalonIdOperationIdAndDateJaxbSQL;
                rm = ClassifierVistoryRowMapper.DEFAULT_CLASSIFIER_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadClassifierVersionsByEtalonIdOperationIdAndDateProtostuffSQL;
                rm = ClassifierVistoryRowMapper.DEFAULT_CLASSIFIER_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String user = SecurityUtils.getCurrentUserName();
            Timestamp point = asOf == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(asOf.getTime());
            return jdbcTemplate.query(statement, rm,
                    classifierEtalonId,
                    point,
                    operationId,
                    includeDraftVersions ? "true" : "false",
                    user,
                    includeDraftVersions,
                    user);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean upsertOriginClassifier(OriginClassifierPO origin, boolean isNew) {
        MeasurementPoint.start();
        try {
            int count;
            if (isNew) {

                count = jdbcTemplate.update(
                        insertOriginClassifierSQL,
                        origin.getId(),
                        origin.getEtalonId(),
                        origin.getName(),
                        origin.getNodeId(),
                        origin.getOriginIdRecord(),
                        0, // Version
                        origin.getSourceSystem(),
                        origin.getCreatedBy(),
                        origin.getStatus() == null ? RecordStatus.ACTIVE.name() : origin.getStatus().name());
            } else {

                count = jdbcTemplate.update(
                        updateOriginClassifierSQL,
                        // 0,
                        origin.getUpdateDate() == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(origin.getUpdateDate().getTime()),
                        origin.getUpdatedBy(),
                        origin.getStatus() == null ? RecordStatus.ACTIVE.name() : origin.getStatus().name(),
                        origin.getId());
            }

            return count > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upsertOriginClassifiers(List<OriginClassifierPO> origins, boolean isNew) {

        MeasurementPoint.start();
        try {

            if (isNew) {
                jdbcTemplate.batchUpdate(insertOriginClassifierSQL, origins, origins.size(), (ps, origin) -> {
                    ps.setString(1, origin.getId());
                    ps.setString(2, origin.getEtalonId());
                    ps.setString(3, origin.getName());
                    ps.setString(4, origin.getNodeId());
                    ps.setString(5, origin.getOriginIdRecord());
                    ps.setInt(6, 0);
                    ps.setString(7, origin.getSourceSystem());
                    ps.setString(8, origin.getCreatedBy());
                    ps.setString(9, origin.getStatus() == null ? RecordStatus.ACTIVE.name() : origin.getStatus().name());
                });
            } else {
                jdbcTemplate.batchUpdate(updateOriginClassifierSQL, origins, origins.size(), (ps, origin) -> {
                    ps.setTimestamp(1, VendorUtils.coalesce(origin.getUpdateDate()));
                    ps.setString(2, origin.getUpdatedBy());
                    ps.setString(3, origin.getStatus() == null ? RecordStatus.ACTIVE.name() : origin.getStatus().name());
                    ps.setString(4, origin.getId());
                });
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean upsertEtalonClassifier(EtalonClassifierPO etalon, boolean isNew) {

        MeasurementPoint.start();
        try {
            int count;
            if (isNew) {

                count = jdbcTemplate.update(
                        insertEtalonClassifierSQL,
                        etalon.getId(),
                        etalon.getName(),
                        etalon.getEtalonIdRecord(),
                        etalon.getCreatedBy(),
                        etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name(),
                        etalon.getApproval() == null ? ApprovalState.APPROVED.name() : etalon.getApproval().name(),
                        0,
                        etalon.getOperationId());

                if (ApprovalState.PENDING == etalon.getApproval()) {
                    jdbcTemplate.update(insertEtalonClassifierStateDraft,
                            etalon.getId(),
                            etalon.getId(),
                            etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name(),
                            etalon.getCreatedBy());
                }

            } else {

                if (ApprovalState.PENDING == etalon.getApproval()) {

                    count = jdbcTemplate.update(insertEtalonClassifierStateDraft,
                            etalon.getId(),
                            etalon.getId(),
                            etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name(),
                            etalon.getCreatedBy());

                    changeEtalonApprovalState(etalon.getId(), etalon.getApproval());

                } else {

                    count = jdbcTemplate.update(
                            updateEtalonClassifierSQL,
                            // 0,
                            etalon.getUpdateDate() == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(etalon.getUpdateDate().getTime()),
                            etalon.getUpdatedBy(),
                            etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name(),
                            etalon.getApproval() == null ? ApprovalState.APPROVED.name() : etalon.getApproval().name(),
                            etalon.getOperationId(),
                            etalon.getId());
                }
            }

            return count > 0;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upsertEtalonClassifiers(List<EtalonClassifierPO> etalons, boolean isNew) {

        MeasurementPoint.start();
        try {

            if (isNew) {

                jdbcTemplate.batchUpdate(insertEtalonClassifierSQL, etalons, etalons.size(), (ps, etalon) -> {

                    ps.setString(1, etalon.getId());
                    ps.setString(2, etalon.getName());
                    ps.setString(3, etalon.getEtalonIdRecord());
                    ps.setString(4, etalon.getCreatedBy());
                    ps.setString(5, etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name());
                    ps.setString(6, etalon.getApproval() == null ? ApprovalState.APPROVED.name() : etalon.getApproval().name());
                    ps.setInt(7, 0);
                    ps.setString(8, etalon.getOperationId());

                });

                List<EtalonClassifierPO> pendings = etalons.stream()
                        .filter(e -> e.getApproval() == ApprovalState.PENDING)
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(pendings)) {

                    jdbcTemplate.batchUpdate(insertEtalonClassifierStateDraft, pendings, pendings.size(),
                        (ps, pending) -> {

                            ps.setString(1, pending.getId());
                            ps.setString(2, pending.getId());
                            ps.setString(3, pending.getStatus() == null ? RecordStatus.ACTIVE.name() : pending.getStatus().name());
                            ps.setString(4, pending.getCreatedBy());

                        });
                }

            } else {

                List<EtalonClassifierPO> pendings = etalons.stream()
                        .filter(e -> e.getApproval() == ApprovalState.PENDING)
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(pendings)) {

                    jdbcTemplate.batchUpdate(insertEtalonClassifierStateDraft, pendings, pendings.size(),
                        (ps, pending) -> {

                            ps.setString(1, pending.getId());
                            ps.setString(2, pending.getId());
                            ps.setString(3, pending.getStatus() == null ? RecordStatus.ACTIVE.name() : pending.getStatus().name());
                            ps.setString(4, pending.getCreatedBy());

                        });
                }

                List<EtalonClassifierPO> straight = etalons.stream()
                        .filter(e -> e.getApproval() != ApprovalState.PENDING)
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(straight)) {

                    jdbcTemplate.batchUpdate(updateEtalonClassifierSQL, etalons, etalons.size(), (ps, etalon) -> {

                        ps.setTimestamp(1, VendorUtils.coalesce(etalon.getUpdateDate()));
                        ps.setString(2, etalon.getUpdatedBy());
                        ps.setString(3, etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name());
                        ps.setString(4, etalon.getApproval() == null ? ApprovalState.APPROVED.name() : etalon.getApproval().name());
                        ps.setString(5, etalon.getOperationId());
                        ps.setString(6, etalon.getId());

                    });
                }
            }
        } finally {
            MeasurementPoint.stop();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean putVersion(OriginsVistoryClassifierPO version) {

        MeasurementPoint.start();
        try {

            // Acquire tn lock
            jdbcTemplate.query(acquireOriginClassifierLockSQL, rs -> true, version.getOriginId());

            String statement = platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB
                    ? putClassifierVersionJaxbSQL
                    : putClassifierVersionProtostuffSQL;

            Timestamp from = version.getValidFrom() == null ? null : new Timestamp(version.getValidFrom().getTime());
            Timestamp to = version.getValidTo() == null ? null : new Timestamp(version.getValidTo().getTime());
            String status = version.getStatus() == null ? RecordStatus.ACTIVE.name() : version.getStatus().name();
            String state = version.getApproval() == null ? ApprovalState.APPROVED.name() : version.getApproval().name();

            jdbcTemplate.update(statement, ps -> {

                ps.setString(1, version.getId());
                ps.setString(2, version.getOriginId());
                ps.setString(3, version.getOperationId());
                ps.setString(4, version.getOriginId()); // revision
                ps.setTimestamp(5, from);
                ps.setTimestamp(6, to);

                if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                    ps.setString(7, DumpUtils.dumpOriginClassifierToJaxb(version.getData()));
                } else if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.PROTOSTUFF) {
                    ps.setBytes(7, DumpUtils.dumpToProtostuff(version.getData()));
                }

                ps.setString(8, version.getCreatedBy());
                ps.setString(9, status);
                ps.setString(10, state);
                ps.setInt(11, version.getMajor());
                ps.setInt(12, version.getMinor());
            });

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putVersions(List<OriginsVistoryClassifierPO> versions) {

        String statement = platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB
                ? putClassifierVersionJaxbSQL
                : putClassifierVersionProtostuffSQL;

        jdbcTemplate.batchUpdate(statement, versions, versions.size(), (ps, version) -> {

            Timestamp from = version.getValidFrom() == null ? null : new Timestamp(version.getValidFrom().getTime());
            Timestamp to = version.getValidTo() == null ? null : new Timestamp(version.getValidTo().getTime());
            String status = version.getStatus() == null ? RecordStatus.ACTIVE.name() : version.getStatus().name();
            String state = version.getApproval() == null ? ApprovalState.APPROVED.name() : version.getApproval().name();

            ps.setString(1, version.getId());
            ps.setString(2, version.getOriginId());
            ps.setString(3, version.getOperationId());
            ps.setString(4, version.getOriginId()); // revision
            ps.setTimestamp(5, from);
            ps.setTimestamp(6, to);

            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                ps.setString(7, DumpUtils.dumpOriginClassifierToJaxb(version.getData()));
            } else if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.PROTOSTUFF) {
                ps.setBytes(7, DumpUtils.dumpToProtostuff(version.getData()));
            }

            ps.setString(8, version.getCreatedBy());
            ps.setString(9, status);
            ps.setString(10, state);
            ps.setInt(11, version.getMajor());
            ps.setInt(12, version.getMinor());

        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bulkInsertEtalonRecords(List<EtalonClassifierPO> records, String targetTable) {

        MeasurementPoint.start();
        try {

            final String prolog = new StringBuilder().append("copy ")
                    .append(targetTable)
                    .append(" (id, name, etalon_id_record, version, create_date, created_by, status, approval) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                    VendorDataType.UUID,
                    VendorDataType.CHAR,
                    VendorDataType.UUID,
                    VendorDataType.INT4,
                    VendorDataType.TIMESTAMP,
                    VendorDataType.CHAR,
                    VendorDataType.CHAR,
                    VendorDataType.CHAR
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, prolog)) {

                for (EtalonClassifierPO record : records) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = record.getName();
                    params[2] = UUID.fromString(record.getEtalonIdRecord());
                    params[3] = 0;
                    params[4] = record.getCreateDate();
                    params[5] = record.getCreatedBy();
                    params[6] = record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name();
                    params[7] = record.getApproval() == null ? ApprovalState.APPROVED.name() : record.getApproval().name();

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
    public void bulkInsertOriginRecords(List<OriginClassifierPO> records, String targetTable) {

        MeasurementPoint.start();
        try {

            final String prolog = new StringBuilder().append("copy ")
                    .append(targetTable)
                    .append(" (id, etalon_id, name, node_id, origin_id_record, version, source_system, create_date, created_by, status) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                    VendorDataType.UUID,
                    VendorDataType.UUID,
                    VendorDataType.CHAR,
                    VendorDataType.CHAR,
                    VendorDataType.UUID,
                    VendorDataType.INT4,
                    VendorDataType.CHAR,
                    VendorDataType.TIMESTAMP,
                    VendorDataType.CHAR,
                    VendorDataType.CHAR
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, prolog)) {

                for (OriginClassifierPO record : records) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = UUID.fromString(record.getEtalonId());
                    params[2] = record.getName();
                    params[3] = record.getNodeId();
                    params[4] = UUID.fromString(record.getOriginIdRecord());
                    params[5] = 0;
                    params[6] = record.getSourceSystem();
                    params[7] = record.getCreateDate();
                    params[8] = record.getCreatedBy();
                    params[9] = record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name();

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
    public void bulkUpdateEtalonRecords(List<EtalonClassifierPO> records, String targetTable) {

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

                for (EtalonClassifierPO record : records) {

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
    public void bulkUpdateOriginRecords(List<OriginClassifierPO> records, String targetTable) {

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

                for (OriginClassifierPO record : records) {

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
    public void bulkInsertVersions(List<OriginsVistoryClassifierPO> versions, String target) {

        MeasurementPoint.start();
        try {

            final String statement = new StringBuilder()
                    .append("copy ")
                    .append(target)
                    .append(" (id, origin_id, revision, valid_from, valid_to,")
                    .append(platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB ? " data_a," : " data_b,")
                    .append(" create_date, created_by, status, approval, operation_id, major, minor) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                    VendorDataType.UUID,
                    VendorDataType.UUID,
                    VendorDataType.INT4,
                    VendorDataType.TIMESTAMP,
                    VendorDataType.TIMESTAMP,
                    platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB ? VendorDataType.TEXT : VendorDataType.BYTEA,
                    VendorDataType.TIMESTAMP,
                    VendorDataType.CHAR,
                    VendorDataType.CHAR,
                    VendorDataType.CHAR,
                    VendorDataType.CHAR,
                    VendorDataType.INT4,
                    VendorDataType.INT4
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, statement)) {

                for (OriginsVistoryClassifierPO record : versions) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = UUID.fromString(record.getOriginId());
                    params[2] = record.getRevision();
                    params[3] = record.getValidFrom();
                    params[4] = record.getValidTo();

                    if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                        params[5] = DumpUtils.dumpOriginRecordToJaxb(record.getData());
                    } else if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.PROTOSTUFF) {
                        params[5] = DumpUtils.dumpToProtostuff(record.getData());
                    }

                    params[6] = record.getCreateDate();
                    params[7] = record.getCreatedBy();
                    params[8] = record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name();
                    params[9] = record.getApproval() == null ? ApprovalState.APPROVED.name() : record.getApproval().name();
                    params[10] = record.getOperationId();
                    params[11] = record.getMajor();
                    params[12] = record.getMinor();

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
    public List<EtalonClassifierPO> loadClassifierEtalons(String etalonId, String classifierName, List<RecordStatus> statuses) {
        MeasurementPoint.start();
        try {

            SqlParameterValue ev = new SqlParameterValue(Types.OTHER, CollectionUtils.isEmpty(statuses)
                    ? Collections.singletonList(RecordStatus.ACTIVE.name())
                    : statuses.stream().map(Enum::name).collect(Collectors.toList()));

            Map<String, Object> params = new HashMap<>();
            params.put(EtalonClassifierPO.FIELD_ETALON_ID_RECORD, etalonId);
            params.put(EtalonClassifierPO.FIELD_NAME, classifierName);
            params.put(EtalonClassifierPO.FIELD_STATUS, ev);

            return namedJdbcTemplate.query(
                    loadEtalonClassifiersByFromNameAndStatusSQL,
                    params,
                    EtalonClassifierRowMapper.DEFAULT_ETALON_CLASSIFIER_ROW_MAPPER);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginClassifierPO> loadClassifierOriginsByEtalonId(String classifierEtalonId) {
        return jdbcTemplate.query(
                loadOriginClassifiersByIdAndSourceSystemSQL,
                OriginClassifierRowMapper.DEFAULT_ORIGIN_CLASSIFIER_ROW_MAPPER,
                classifierEtalonId,
                null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginClassifierPO loadClassifierOriginById(String classifierOriginId, List<RecordStatus> statuses) {

        String statesString = CollectionUtils.isEmpty(statuses)
                ? RecordStatus.ACTIVE.name()
                : statuses.stream().map(RecordStatus::name).collect(Collectors.joining(","));

        return jdbcTemplate.query(loadOriginClassifierByIdSQL,
                OriginClassifierRowMapper.DEFAULT_ORIGIN_CLASSIFIER_FIRST_RESULT_EXTRACTOR,
                classifierOriginId,
                statesString
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonClassifierPO loadClassifierEtalonById(String classifierEtalonId) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(
                    loadEtalonClassifierByIdSQL,
                    EtalonClassifierRowMapper.DEFAULT_ETALON_CLASSIFIER_FIRST_RESULT_EXTRACTOR,
                    classifierEtalonId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wipeClassifierOrigin(String originId) {
        jdbcTemplate.update(deleteVistoryByOriginIdSQL, originId);
        jdbcTemplate.update(deleteOriginByIdSQL, originId);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wipeClassifierEtalon(String etalonId) {
        jdbcTemplate.update(deleteEtalonByIdSQL, etalonId);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deactivateClassifierEtalon(String classifierEtalonId, ApprovalState approvalState) {
        MeasurementPoint.start();
        try {
            EtalonClassifierPO epo = loadClassifierEtalonById(classifierEtalonId);
            if (epo == null) {
                return false;
            }

            Date ts = new Date(System.currentTimeMillis());
            String user = SecurityUtils.getCurrentUserName();
            List<OriginClassifierPO> ors = loadClassifierOriginsByEtalonId(classifierEtalonId);
            for (OriginClassifierPO opo : ors) {

                opo.setStatus(RecordStatus.INACTIVE);
                opo.setUpdateDate(ts);
                opo.setUpdatedBy(user);

                upsertOriginClassifier(opo, false);
            }

            epo.setStatus(RecordStatus.INACTIVE);
            epo.setApproval(approvalState);
            epo.setUpdateDate(ts);
            epo.setUpdatedBy(user);

            return upsertEtalonClassifier(epo, false);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deactivateClassifierOrigin(String classifierOriginId) {
        MeasurementPoint.start();
        try {
            OriginClassifierPO po = loadClassifierOriginById(classifierOriginId, null);
            if (po == null) {
                return false;
            }

            Date ts = new Date(System.currentTimeMillis());
            String user = SecurityUtils.getCurrentUserName();

            po.setStatus(RecordStatus.INACTIVE);
            po.setUpdateDate(ts);
            po.setUpdatedBy(user);

            return upsertOriginClassifier(po, false);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int markEtalonClassifiersMerged(List<String> duplicateIds, String classifierName, String operationId) {
        MeasurementPoint.start();
        try {
            String user = SecurityUtils.getCurrentUserName();
            Timestamp point = new Timestamp(System.currentTimeMillis());

            Map<String, Object> params = new HashMap<>();
            params.put(EtalonClassifierPO.FIELD_STATUS, RecordStatus.MERGED.name());
            params.put(EtalonClassifierPO.FIELD_UPDATE_DATE, point);
            params.put(EtalonClassifierPO.FIELD_UPDATED_BY, user);
            params.put(EtalonClassifierPO.FIELD_OPERATION_ID, operationId);
            params.put(EtalonClassifierPO.FIELD_ETALON_ID_RECORD, duplicateIds);
            params.put(EtalonClassifierPO.FIELD_NAME, classifierName);

            final SqlParameterSource paramSource = new MapSqlParameterSource(params) {
                @Override
                public Object getValue(String paramName) {

                    Object val = super.getValue(paramName);
                    if (EtalonClassifierPO.FIELD_ETALON_ID_RECORD.equals(paramName) && val instanceof Collection) {
                        return ((Collection<?>) val).stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .map(UUID::fromString)
                                .collect(Collectors.toList());
                    }

                    return val;
                }

            };

            return namedJdbcTemplate.update(markEtalonClassifiersMergedByEtalonRecordIdSQL, paramSource);
        } finally {
            MeasurementPoint.stop();
        }
    }

    @Override
    public int remapEtalonClassifier(String fromEtalonRecord, String toEtalonRecord,
                                     String classifierName, String operationId) {

        String user = SecurityUtils.getCurrentUserName();
        Timestamp point = new Timestamp(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("from_etalon_record", fromEtalonRecord);
        params.put("to_etalon_record", toEtalonRecord);
        params.put(EtalonClassifierPO.FIELD_UPDATE_DATE, point);
        params.put(EtalonClassifierPO.FIELD_UPDATED_BY, user);
        params.put(EtalonClassifierPO.FIELD_OPERATION_ID, operationId);
        params.put(EtalonClassifierPO.FIELD_NAME, classifierName);

        return namedJdbcTemplate.update(remapEtalonClassifier, new MapSqlParameterSource(params) {
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if ("from_etalon_record".equals(paramName) || "to_etalon_record".equals(paramName)) {
                    return UUID.fromString(val.toString());
                }
                return val;
            }
        });
    }

    @Override
    public Map<String, List<String>> checkUsageByRecordEtalonIdsSQL(List<String> etalonIds) {
        Map<String, Object> params = new HashMap<>();
        params.put(EtalonClassifierPO.FIELD_ETALON_ID_RECORD, etalonIds);

        final SqlParameterSource paramSource = new MapSqlParameterSource(params) {
            @Override
            public Object getValue(String paramName) {

                Object val = super.getValue(paramName);
                if (EtalonClassifierPO.FIELD_ETALON_ID_RECORD.equals(paramName) && val instanceof Collection) {
                    return ((Collection<?>) val).stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }

                return val;
            }

        };

        return namedJdbcTemplate.query(checkUsageByRecordEtalonIdsSQL, paramSource, rs -> {
            Map<String, List<String>> map = new HashMap<>();
            while (rs.next()) {
                String classifierName = rs.getString(EtalonClassifierPO.FIELD_NAME);

                if (StringUtils.isEmpty(classifierName)) {
                    continue;
                }
                List<String> ids = map.computeIfAbsent(classifierName, k -> new ArrayList<>());
                ids.add(rs.getString(EtalonClassifierPO.FIELD_ETALON_ID_RECORD));
            }
            return map;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeVersionsApprovalState(String relationEtalonId, ApprovalState to) {
        MeasurementPoint.start();
        try {
            jdbcTemplate.update(changeVersionsApprovalSQL,
                    to == null ? ApprovalState.APPROVED.name() : to.name(),
                    relationEtalonId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeEtalonApprovalState(String etalonId, ApprovalState state) {
        MeasurementPoint.start();
        try {
            jdbcTemplate.update(changeEtalonApprovalSQL,
                    SecurityUtils.getCurrentUserName(),
                    state.name(),
                    etalonId,
                    state.name());
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cleanupEtalonStateDrafts(String etalonId) {

        boolean success = false;
        MeasurementPoint.start();
        try {
            success = jdbcTemplate.update(cleanupEtalonClassifierStateDrafts, etalonId) > 0;
        } finally {
            MeasurementPoint.stop();
        }

        return success;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonClassifierDraftStatePO loadLastEtalonStateDraft(String etalonId) {

        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadLastEtalonClassifierStateDraftByEtalonIdSQL,
                    EtalonClassifierDraftStateRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    etalonId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Triple<String, RecordStatus, ApprovalState>>
        loadActiveInactiveClassifierVersionsInfo(String etalonId) {

        MeasurementPoint.start();
        try {

            return jdbcTemplate.query(loadActiveInactiveClassifierVersionsInfoSQL, (rs, num) ->
                    new ImmutableTriple<>(
                            rs.getString(OriginsVistoryClassifierPO.FIELD_ORIGIN_ID),
                            RecordStatus.fromValue(rs.getString(OriginsVistoryClassifierPO.FIELD_STATUS)),
                            ApprovalState.fromValue(rs.getString(OriginsVistoryClassifierPO.FIELD_APPROVAL)))
                ,
                etalonId,
                new Date(System.currentTimeMillis()));

        } finally {
            MeasurementPoint.stop();
        }
    }


}

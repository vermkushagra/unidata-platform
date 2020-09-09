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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.common.configuration.DumpTargetFormat;
import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.VistoryOperationType;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.dao.rm.OriginVistoryRowMapper;
import com.unidata.mdm.backend.dao.rm.TimeIntervalRowMapper;
import com.unidata.mdm.backend.dao.util.VendorUtils;
import com.unidata.mdm.backend.dao.util.VendorUtils.CopyDataOutputStream;
import com.unidata.mdm.backend.dao.util.pg.VendorDataType;
import com.unidata.mdm.backend.po.OriginKeyPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.DumpUtils;

/**
 * @author Mikhail Mikhailov
 * Origins vistory (versions + history) DAO class.
 */
@Repository
public class OriginsVistoryDaoImpl extends AbstractDaoImpl implements OriginsVistoryDao {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OriginsVistoryDaoImpl.class);
    /**
     * Current platform configuration.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
    /**
     * Acquire origin transaction lock (method boundary).
     */
    private final String acquireOriginLockSQL;
    /**
     * Put version SQL string.
     */
    private final String putVersionJaxbSQL;

    /**
     * Put version SQL string.
     */
    private final String putVersionProtostuffSQL;

    /**
     * Load version by origin id SQL string.
     */
    private final String loadLastApprovedActiveVersionJaxbSQL;
    /**
     * Load version by origin id SQL string.
     */
    private final String loadLastApprovedActiveVersionProtostuffSQL;
    /**
     * Load version by origin id SQL string.
     */
    private final String loadVersionJaxbSQL;
    /**
     * Load version by origin id SQL string.
     */
    private final String loadVersionProtostuffSQL;

    /**
     * Load recent versions for a date SQL string.
     */
    private final String loadPendingVersionsByOriginIdSQL;
    /**
     * Loads pending versions by etalon id.
     */
    private final String loadPendingVersionsByEtalonIdSQL;
    /**
     * Load recent versions for a date SQL string.
     */
    private final String loadVersionsJaxbSQL;
    /**
     * Load recent versions for a date SQL string.
     */
    private final String loadVersionsProtostuffSQL;
    /**
     * Load recent versions for a date SQL string.
     */
    private final String loadVersionsForLudJaxbSQL;
    /**
     * Load recent versions for a date SQL string.
     */
    private final String loadVersionsForLudProtostuffSQL;
    /**
     * Load versions which have updates after given date.
     */
    private final String loadVersionsForUpdatedAfterJaxbSQL;
    /**
     * Load versions which have updates after given date.
     */
    private final String loadVersionsForUpdatedAfterProtostuffSQL;
    /**
     * Loads versions for etalon and operation id.
     */
    private final String loadVersionsForOperationIdJaxbSQL;
    /**
     * Loads versions for etalon and operation id.
     */
    private final String loadVersionsForOperationIdProtostuffSQL;
    /**
     * Load recent versions for a date SQL string.
     */
    private final String loadUnfilteredVersionsStateSQL;

    /**
     * Load recent versions for a date SQL string.
     */
    private final String loadUnfilteredVersionsStateByOriginSQL;

    /**
     * Load complete origin history.
     */
    private final String loadOriginHistoryJaxbSQL;

    /**
     * Load complete origin history.
     */
    private final String loadOriginHistoryProtostuffSQL;
    /**
     * Load complete origin history.
     */
    private final String loadHistoryJaxbSQL;

    /**
     * Load complete origin history.
     */
    private final String loadHistoryProtostuffSQL;

    /**
     * Load time line SQL.
     */
    private final String loadTimelineSQL;

    /**
     * Loads multiple timelines for multiple etalons.
     */
    private final String loadTimelinesSQL;

    /**
     * Loads etalon boundary.
     */
    private final String loadEtalonBoundarySQL;

    /**
     * Decline all pending versions of a record.
     */
    private final String updatePendingVersionsSQL;
    /**
     * Decline all pending versions of a record.
     */
    private final String updateVersionsStatusSQL;

    /**
     * External utility support.
     */
    @Autowired
    public OriginsVistoryDaoImpl(DataSource dataSource, @Qualifier("vistory-sql") Properties sql) {
        super(dataSource);
        acquireOriginLockSQL = sql.getProperty("acquireOriginLockSQL");
        putVersionJaxbSQL = sql.getProperty("putVersionJaxbSQL");
        putVersionProtostuffSQL = sql.getProperty("putVersionProtostuffSQL");
        loadLastApprovedActiveVersionJaxbSQL = sql.getProperty("loadLastApprovedActiveVersionJaxbSQL");
        loadLastApprovedActiveVersionProtostuffSQL = sql.getProperty("loadLastApprovedActiveVersionProtostuffSQL");
        loadVersionJaxbSQL = sql.getProperty("loadVersionJaxbSQL");
        loadVersionProtostuffSQL = sql.getProperty("loadVersionProtostuffSQL");
        loadPendingVersionsByOriginIdSQL = sql.getProperty("loadPendingVersionsByOriginIdSQL");
        loadPendingVersionsByEtalonIdSQL = sql.getProperty("loadPendingVersionsByEtalonIdSQL");
        loadVersionsJaxbSQL = sql.getProperty("loadVersionsJaxbSQL");
        loadVersionsProtostuffSQL = sql.getProperty("loadVersionsProtostuffSQL");
        loadVersionsForLudJaxbSQL = sql.getProperty("loadVersionsForLudJaxbSQL");
        loadVersionsForLudProtostuffSQL = sql.getProperty("loadVersionsForLudProtostuffSQL");
        loadVersionsForUpdatedAfterJaxbSQL = sql.getProperty("loadVersionsForUpdatedAfterJaxbSQL");
        loadVersionsForUpdatedAfterProtostuffSQL = sql.getProperty("loadVersionsForUpdatedAfterProtostuffSQL");
        loadVersionsForOperationIdJaxbSQL = sql.getProperty("loadVersionsForOperationIdJaxbSQL");
        loadVersionsForOperationIdProtostuffSQL = sql.getProperty("loadVersionsForOperationIdProtostuffSQL");
        loadUnfilteredVersionsStateSQL = sql.getProperty("loadUnfilteredVersionsStateSQL");
        loadUnfilteredVersionsStateByOriginSQL = sql.getProperty("loadUnfilteredVersionsStateByOriginSQL");
        loadOriginHistoryJaxbSQL = sql.getProperty("loadOriginHistoryJaxbSQL");
        loadOriginHistoryProtostuffSQL = sql.getProperty("loadOriginHistoryProtostuffSQL");
        loadHistoryJaxbSQL = sql.getProperty("loadHistoryJaxbSQL");
        loadHistoryProtostuffSQL = sql.getProperty("loadHistoryProtostuffSQL");
        loadTimelineSQL = sql.getProperty("loadTimelineSQL");
        loadTimelinesSQL = sql.getProperty("loadTimelinesSQL");
        loadEtalonBoundarySQL = sql.getProperty("loadEtalonBoundarySQL");
        updatePendingVersionsSQL = sql.getProperty("updatePendingVersionsSQL");
        updateVersionsStatusSQL = sql.getProperty("updateVersionsStatusSQL");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginsVistoryRecordPO loadVersion(String originId, Date date, boolean unpublishedView) {

        MeasurementPoint.start();
        try {

            final String statement;
            final RowMapper<OriginsVistoryRecordPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadVersionJaxbSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadVersionProtostuffSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String drafts = unpublishedView ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            String user = SecurityUtils.getCurrentUserName();
            Timestamp asOf = VendorUtils.coalesce(date);
            return jdbcTemplate.query(statement, rs -> rs != null && rs.next() ? rm.mapRow(rs, rs.getRow()) : null,
                    originId, asOf, drafts, user);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadVersions(
    		String etalonId, Date date, boolean isApproverView, String userName) {

        MeasurementPoint.start();
        try {

            final String statement;
            final RowMapper<OriginsVistoryRecordPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadVersionsJaxbSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadVersionsProtostuffSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String drafts = isApproverView ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            Timestamp asOf = VendorUtils.coalesce(date);

            return jdbcTemplate.query(statement, rm, etalonId, asOf, drafts, userName);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadVersionsByLastUpdateDate(
    		String etalonId, Date date, Date lud, boolean isApproverView, String userName) {

        MeasurementPoint.start();
        try {
            final String statement;
            final RowMapper<OriginsVistoryRecordPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadVersionsForLudJaxbSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadVersionsForLudProtostuffSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            Timestamp asOf = VendorUtils.coalesce(date);
            Timestamp lastUpdate = new Timestamp(lud.getTime());
            String drafts = isApproverView ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            return jdbcTemplate.query(statement, rm, etalonId, asOf, lastUpdate, drafts, userName);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadVersionsByUpdatesAfter(String etalonId, Date date, Date updatesAfter, boolean isApproverView, String userName) {

        MeasurementPoint.start();
        try {
            final String statement;
            final RowMapper<OriginsVistoryRecordPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadVersionsForUpdatedAfterJaxbSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadVersionsForUpdatedAfterProtostuffSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String drafts = isApproverView ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            Timestamp asOf = VendorUtils.coalesce(date);
            Timestamp updatedAfter = new Timestamp(updatesAfter.getTime());
            return jdbcTemplate.query(statement, rm, etalonId, asOf, drafts, userName, updatedAfter);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadVersionsByOperationId(
            String etalonId, Date date, String operationId, boolean isApproverView, String userName) {

        MeasurementPoint.start();
        try {

            final String statement;
            final RowMapper<OriginsVistoryRecordPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadVersionsForOperationIdJaxbSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadVersionsForOperationIdProtostuffSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String drafts = isApproverView ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            String user = SecurityUtils.getCurrentUserName();
            Timestamp asOf = VendorUtils.coalesce(date);

            return jdbcTemplate.query(statement, rm, etalonId, asOf, operationId, drafts, user);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadVersionsUnfilterdByEtalonId(String etalonId, Date point) {

        Timestamp asOf = VendorUtils.coalesce(point);
        return jdbcTemplate.query(loadUnfilteredVersionsStateSQL,
                OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_NO_DATA_ROW_MAPPER,
                etalonId,
                asOf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadVersionsUnfilterdByOriginId(String originId, Date date) {

        Timestamp asOf = VendorUtils.coalesce(date);
        return jdbcTemplate.query(loadUnfilteredVersionsStateByOriginSQL,
                OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_NO_DATA_ROW_MAPPER,
                originId,
                asOf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadPendingVersionsByOriginId(String originId, Date point) {

        Timestamp ts = VendorUtils.coalesce(point);
        return jdbcTemplate.query(loadPendingVersionsByOriginIdSQL,
                OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_NO_DATA_ROW_MAPPER,
                originId,
                ts,
                ts,
                originId,
                ts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadPendingVersionsByEtalonId(String etalonId, Date point) {

        Timestamp ts = VendorUtils.coalesce(point);
        return jdbcTemplate.query(loadPendingVersionsByEtalonIdSQL,
                OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_NO_DATA_ROW_MAPPER,
                etalonId,
                ts,
                ts,
                ts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRecordPO> loadOriginHistory(String originId) {

        final String statement;
        final RowMapper<OriginsVistoryRecordPO> rm;
        if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
            statement = loadOriginHistoryJaxbSQL;
            rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER;
        } else {
            statement = loadOriginHistoryProtostuffSQL;
            rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER;
        }

        return jdbcTemplate.query(statement, rm, originId);
    }

    /**
     * @see com.unidata.mdm.backend.dao.OriginsVistoryDao#loadHistory(java.lang.String)
     */
    @Override
    public Map<OriginKeyPO, List<OriginsVistoryRecordPO>> loadHistory(String etalonId) {

        final String statement;
        final RowMapper<OriginsVistoryRecordPO> rm;
        if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
            statement = loadHistoryJaxbSQL;
            rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER;
        } else {
            statement = loadHistoryProtostuffSQL;
            rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER;
        }

        final Map<OriginKeyPO, List<OriginsVistoryRecordPO>> result = new HashMap<>();
        jdbcTemplate.query(statement, (rs, rowNum) -> {

                OriginsVistoryRecordPO po = rm.mapRow(rs, rowNum);

                String id = rs.getString(OriginsVistoryRecordPO.FIELD_ORIGIN_ID);
                String sourceSystem = rs.getString(OriginRecordPO.FIELD_SOURCE_SYSTEM);
                String externalId = rs.getString(OriginRecordPO.FIELD_EXTERNAL_ID);

                OriginKeyPO key = new OriginKeyPO(id, sourceSystem, externalId);
                if (!result.containsKey(key)) {
                    result.put(key, new ArrayList<>());
                }
                result.get(key).add(po);

                return po;
            },
            etalonId);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeIntervalPO> loadContributingRecordsTimeline(String etalonId, String entityName, Boolean isApproverView) {

        String user = SecurityUtils.getCurrentUserName();
        return jdbcTemplate.query(loadTimelineSQL,
                TimeIntervalRowMapper.DEFAULT_TIME_INTERVAL_ROW_MAPPER,
                entityName,
                etalonId,
                user,
                isApproverView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Map<String, List<TimeIntervalPO>>> loadContributingRecordsTimelines(List<String> etalonIds, Boolean isApproverView) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_name", SecurityUtils.getCurrentUserName());
        params.put("is_approver", isApproverView);
        params.put("ids", etalonIds);

        return namedJdbcTemplate.query(loadTimelinesSQL, new MapSqlParameterSource(params) {
                @SuppressWarnings("unchecked")
                @Override
                public Object getValue(String paramName) {
                    Object val = super.getValue(paramName);
                    if ("ids".equals(paramName)) {
                        return ((Collection<String>) val).stream()
                                .filter(Objects::nonNull)
                                .map(UUID::fromString)
                                .collect(Collectors.toList());
                    }
                    return val;
                }
            },
            TimeIntervalRowMapper.DEFAULT_COMPLETE_RECORDS_EXTRACTOR);
    }

    // loadTimelinesSQL
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalPO loadEtalonBoundary(String etalonId, Date point, Boolean isApproverView) {

        String user = SecurityUtils.getCurrentUserName();
        Timestamp ts = VendorUtils.coalesce(point);
        return jdbcTemplate.query(loadEtalonBoundarySQL,
                rs -> rs != null && rs.next() ? TimeIntervalRowMapper.BOUNDARY_TIME_INTERVAL_ROW_MAPPER.mapRow(rs, 0) : null,
                etalonId,
                ts,
                user,
                isApproverView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean putVersion(OriginsVistoryRecordPO version) {

        MeasurementPoint.start();
        try {

            // Acquire session lock
            jdbcTemplate.query(acquireOriginLockSQL, rs -> true, version.getOriginId());

            String statement = platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB
                    ? putVersionJaxbSQL
                    : putVersionProtostuffSQL;

            Timestamp ts = version.getCreateDate() != null ? new Timestamp(version.getCreateDate().getTime()) : null;
            Timestamp from = version.getValidFrom() != null ? new Timestamp(version.getValidFrom().getTime()) : null;
            Timestamp to = version.getValidTo() != null ? new Timestamp(version.getValidTo().getTime()) : null;
            String status = version.getStatus() == null ? RecordStatus.ACTIVE.name() : version.getStatus().name();
            String approval = version.getApproval() == null ? ApprovalState.APPROVED.name() : version.getApproval().name();
            String shift = version.getShift() == null ? DataShift.PRISTINE.name() : version.getShift().name();
            String operationType = version.getOperationType() == null ? VistoryOperationType.DIRECT.name() : version.getOperationType().name();

            int result = jdbcTemplate.update(statement, ps -> {

                ps.setString(1, version.getId());
                ps.setString(2, version.getOriginId());
                ps.setString(3, version.getOperationId());
                ps.setString(4, version.getOriginId());
                ps.setTimestamp(5, from);
                ps.setTimestamp(6, to);

                if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                    ps.setString(7, DumpUtils.dumpOriginRecordToJaxb(version.getData()));
                } else if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.PROTOSTUFF) {
                    ps.setBytes(7, DumpUtils.dumpToProtostuff(version.getData()));
                }

                ps.setString(8, version.getCreatedBy());
                ps.setTimestamp(9, ts);
                ps.setString(10, status);
                ps.setString(11, approval);
                ps.setString(12, shift);
                ps.setInt(13, version.getMajor());
                ps.setInt(14, version.getMinor());
                ps.setString(15, operationType);
            });

            return result == 1;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putVersions(final List<OriginsVistoryRecordPO> versions) {

        MeasurementPoint.start();
        try {

            String statement = platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB
                    ? putVersionJaxbSQL
                    : putVersionProtostuffSQL;

            int[][] updates = jdbcTemplate.batchUpdate(statement, versions, versions.size(), (ps, po) -> {

                Timestamp ts = po.getCreateDate() != null ? new Timestamp(po.getCreateDate().getTime()) : null;
                Timestamp from = po.getValidFrom() != null ? new Timestamp(po.getValidFrom().getTime()) : null;
                Timestamp to = po.getValidTo() != null ? new Timestamp(po.getValidTo().getTime()) : null;
                String status = po.getStatus() == null ? RecordStatus.ACTIVE.name() : po.getStatus().name();
                String approval = po.getApproval() == null ? ApprovalState.APPROVED.name() : po.getApproval().name();
                String shift = po.getShift() == null ? DataShift.PRISTINE.name() : po.getShift().name();
                String operationType = po.getOperationType() == null ? VistoryOperationType.DIRECT.name() : po.getOperationType().name();

                ps.setString(1, po.getId());
                ps.setString(2, po.getOriginId());
                ps.setString(3, po.getOperationId());
                ps.setString(4, po.getOriginId());
                ps.setTimestamp(5, from);
                ps.setTimestamp(6, to);

                if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                    ps.setString(7, DumpUtils.dumpOriginRecordToJaxb(po.getData()));
                } else if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.PROTOSTUFF) {
                    ps.setBytes(7, DumpUtils.dumpToProtostuff(po.getData()));
                }

                ps.setString(8, po.getCreatedBy());
                ps.setTimestamp(9, ts);
                ps.setString(10, status);
                ps.setString(11, approval);
                ps.setString(12, shift);
                ps.setInt(13, po.getMajor());
                ps.setInt(14, po.getMinor());
                ps.setString(15, operationType);
            });

            if (updates.length == 0 || updates[0].length  != versions.size()) {
                throw new DataProcessingException("Batch insert vistory records [{}] failed.",
                        ExceptionId.EX_DATA_INSERT_VISTORY_BATCH_FAILED,
                        versions.size());
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bulkInsertVersions(List<OriginsVistoryRecordPO> versions, String target) {

        MeasurementPoint.start();
        try {

            final String statement = new StringBuilder()
                    .append("copy ")
                    .append(target)
                    .append(" (id, origin_id, revision, valid_from, valid_to,")
                    .append(platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB ? " data_a," : " data_b,")
                    .append(" create_date, created_by, status, approval, shift, operation_id, major, minor, operation_type) from stdin binary")
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
                VendorDataType.CHAR,
                VendorDataType.INT4,
                VendorDataType.INT4,
                VendorDataType.CHAR
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, statement)) {

                for (OriginsVistoryRecordPO record : versions) {

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
                    params[10] = record.getShift() == null ? DataShift.PRISTINE.name() : record.getShift().name();
                    params[11] = record.getOperationId();
                    params[12] = record.getMajor();
                    params[13] = record.getMinor();
                    params[14] = record.getOperationType() == null ? VistoryOperationType.DIRECT.name() : record.getOperationType().name();

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
    public boolean updateApprovalState(String recordEtalonId, ApprovalState to) {
        MeasurementPoint.start();
        try {
            jdbcTemplate.update(updatePendingVersionsSQL,
                    to == null ? ApprovalState.APPROVED.name() : to.name(),
                    recordEtalonId);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateVistoryStatus(List<String> ids, RecordStatus status) {
        MeasurementPoint.start();
        try {

            Map<String, Object> params = new HashMap<>(2);
            params.put("ids", ids.stream().map(UUID::fromString).collect(Collectors.toList()));
            params.put(OriginsVistoryRecordPO.FIELD_STATUS, status.name());

            namedJdbcTemplate.update(updateVersionsStatusSQL, params);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginsVistoryRecordPO loadLastActiveApprovedVersion(String originId, Date date) {
        MeasurementPoint.start();
        try {

            final String statement;
            final RowMapper<OriginsVistoryRecordPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadLastApprovedActiveVersionJaxbSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadLastApprovedActiveVersionProtostuffSQL;
                rm = OriginVistoryRowMapper.DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            Timestamp asOf = VendorUtils.coalesce(date);
            return jdbcTemplate.query(statement, rs -> rs != null && rs.next() ? rm.mapRow(rs, rs.getRow()) : null, originId, asOf);

        } finally {
            MeasurementPoint.stop();
        }
    }
}

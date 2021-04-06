package com.unidata.mdm.backend.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import com.unidata.mdm.backend.dao.rm.EtalonClassifierRowMapper;
import com.unidata.mdm.backend.dao.rm.OriginClassifierRowMapper;
import com.unidata.mdm.backend.dao.util.VendorUtils;
import com.unidata.mdm.backend.dao.util.VendorUtils.CopyDataOutputStream;
import com.unidata.mdm.backend.dao.util.pg.VendorDataType;
import com.unidata.mdm.backend.po.ClassifierKeysPO;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.po.OriginClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.DumpUtils;

/**
 * @author Mikhail Mikhailov
 * Classifiers data DAO implementation.
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
     * Mark merged.
     */
    private final String markEtalonClassifiersMergedByEtalonRecordIdSQL;
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifiersDAOImpl.class);

    /**
     * Constructor.
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
    public ClassifierKeysPO loadClassifierKeysByRecordEtalonIdAndClassifierName(String sourceSystem, String etalonId,
            String classifierName) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByRecordEtalonIdSQL,
                    ClassifierKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    sourceSystem, etalonId, classifierName);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeysPO loadClassifierKeysByRecordOriginIdAndClassifierName(String originId, String classifierName) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByRecordOriginIdSQL,
                    ClassifierKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
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
            return jdbcTemplate.query(statement, rm, classifierEtalonId, point, includeDraftVersions ? "true" : "false", user);

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
            return jdbcTemplate.query(statement, rm, classifierEtalonId, point, operationId, includeDraftVersions ? "true" : "false", user);

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
                // No approval state management so far
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
            } else {
                // No approval state management so far
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
            } else {
                jdbcTemplate.batchUpdate(updateEtalonClassifierSQL, etalons, etalons.size(), (ps, etalon) -> {
                    ps.setTimestamp(1, VendorUtils.coalesce(etalon.getUpdateDate()));
                    ps.setString(2, etalon.getUpdatedBy());
                    ps.setString(3, etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name());
                    ps.setString(4, etalon.getApproval() == null ? ApprovalState.APPROVED.name() : etalon.getApproval().name());
                    ps.setString(5, etalon.getOperationId());
                    ps.setString(6, etalon.getId());
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
    public List<EtalonClassifierPO> loadClassifierEtalons(String etalonId, String classifierName, RecordStatus status) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(
                    loadEtalonClassifiersByFromNameAndStatusSQL,
                    EtalonClassifierRowMapper.DEFAULT_ETALON_CLASSIFIER_ROW_MAPPER,
                    etalonId,
                    classifierName,
                    Objects.isNull(status) ? RecordStatus.ACTIVE.name() : status.name());
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
    public int markEtalonClassifiersMerged(List<String> duplicateIds, String operationId) {
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
}

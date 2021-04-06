package com.unidata.mdm.backend.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
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

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.configuration.DumpTargetFormat;
import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.dto.RelationDigestDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.dao.RelationsDao;
import com.unidata.mdm.backend.dao.rm.EtalonRelationDraftStateRowMapper;
import com.unidata.mdm.backend.dao.rm.EtalonRelationRowMapper;
import com.unidata.mdm.backend.dao.rm.OriginRelationRowMapper;
import com.unidata.mdm.backend.dao.rm.RelationKeysRowMapper;
import com.unidata.mdm.backend.dao.rm.RelationVistoryRowMapper;
import com.unidata.mdm.backend.dao.rm.TimeIntervalRowMapper;
import com.unidata.mdm.backend.dao.util.VendorUtils;
import com.unidata.mdm.backend.dao.util.VendorUtils.CopyDataOutputStream;
import com.unidata.mdm.backend.dao.util.pg.VendorDataType;
import com.unidata.mdm.backend.po.EtalonRelationDraftStatePO;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.OriginRelationPO;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.po.RelationKeysPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.DumpUtils;



/**
 * @author Mikhail Mikhailov
 * Relations DAO implementation.
 */
@Repository
public class RelationsDaoImpl extends AbstractDaoImpl implements RelationsDao {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationsDaoImpl.class);
    /**
     * Current platform configuration.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
    /**
     * Timeline of a single rel by id.
     */
    private final String loadRelationsTimelineByRelationEtalonIdSQL;
    /**
     * Timeline of a single rel by id and date.
     */
    private final String loadRelationsTimelineByRelationEtalonIdAndAsOfSQL;
    /**
     * Timeline of a single rel by id and date.
     */
    private final String loadRelationsTimelineByRelationEtalonIdAndFromBoundarySQL;
    /**
     * Load complete relations time line by from id.
     */
    private final String loadCompleteRelationsTimelineByFromEtalonIdSQL;
    /**
     * Load complete relations time line by to id.
     */
    private final String loadCompleteRelationsTimelineByToEtalonIdSQL;
    /**
     * Load relations time line SQL.
     */
    private final String loadRelationsTimelineByFromEtalonIdSQL;
    /**
     * Loads relations time line filtering relation etalons by boundary.
     */
    private final String loadRelationsTimelineByFromEtalonIdAndAsOfSQL;
    /**
     * Loads relations time line filtering relation etalons by boundary.
     */
    private final String loadRelationsTimelineByFromEtalonIdAndFromBoundarySQL;
    /**
     * Loads relation etalon boundary.
     */
    private final String loadRelationsBoundaryByFromEtalonIdSQL;
    /**
     * Loads relation etalon boundary by date and id.
     */
    private final String loadRelationsBoundaryByEtalonIdSQL;
    /**
     * Acquire session lock.
     */
    private final String acquireOriginRelationLockSQL;
    /**
     * Put new origins relation. Contains simple guard against identical version put.
     */
    private final String putRelationVersionJaxbSQL;
    /**
     * Put new origins relation. Contains simple guard against identical version put.
     */
    private final String putRelationVersionProtostuffSQL;
    /**
     * Insert new origins relation.
     */
    private final String insertOriginRelationSQL;
    /**
     * Insert new origins relation.
     */
    private final String insertEtalonRelationSQL;
    /**
     * State draft.
     */
    private final String insertEtalonRelationStateDraft;
    /**
     * Cleanup etalon state drafts.
     */
    private final String cleanupEtalonRelationStateDrafts;
    /**
     * Load state draft.
     */
    private final String loadLastEtalonRelationStateDraftByEtalonIdSQL;
    /**
     * Loads etalon relations.
     */
    private final String loadEtalonRelationsByFromNameAndStatusSQL;
    /**
     * Loads etalon relations.
     */
    private final String loadEtalonRelationsByToNameAndStatusSQL;
    /**
     * Load all 'incoming' relations for given etalon id.
     */
    private final String loadEtalonRelationsByToSQL;
    /**
     * Loads etalon relation.
     */
    private final String loadEtalonRelationsByIdSQL;
    /**
     * Loads etalon relations.
     */
    private final String loadEtalonRelationByFromToNameAndStatusSQL;
    /**
     * Loads etalon relations.
     */
    private final String loadOriginRelationByFromToNameAndStatusSQL;
    /**
     * Loads origin relation by id.
     */
    private final String loadOriginRelationByIdSQL;
    /**
     * Loads etalon relations.
     */
    private final String loadOriginRelationByRelationEtalonIdAndSourceSystemSQL;
    /**
     * Loads origin relations, which are not in an (possibly upserted) set.
     */
    private final String loadMissingOriginRelationByFromNameFromIdsActiveIdsAndStatusSQL;
    /**
     * Loads origins vistory.
     */
    private final String loadRelationVersionsByEtalonIdAndDateJaxbSQL;
    /**
     * Loads origins vistory.
     */
    private final String loadRelationVersionsByEtalonIdAndDateProtostuffSQL;
    /**
     * Loads versions by date and op. id
     */
    private final String loadRelationVersionsByEtalonIdOperationIdAndDateJaxbSQL;
    /**
     * Loads versions by date and op. id
     */
    private final String loadRelationVersionsByEtalonIdOperationIdAndDateProtostuffSQL;
    /**
     * Loads pending versions by etalon id.
     */
    private final String loadPendingVersionsByEtalonIdJaxbSQL;
    /**
     * Loads pending versions by etalon id.
     */
    private final String loadPendingVersionsByEtalonIdProtostuffSQL;
    /**
     * Loads versions by name, dates and from etalon id.
     */
    private final String calcTotalCountByNameDateAndFromEtalonIdSQL;
    /**
     * Loads versions by name, dates and from etalon id.
     */
    private final String calcTotalCountByNameDateAndToEtalonIdSQL;
    /**
     * Count all 'incoming' relations for given etalon id.
     */
    private final String calcTotalCountByFromEtalonIdSQL;
    /**
     * Count all 'incoming' relations for given etalon ids.
     */
    private final String checkUsageByFromEtalonIdsSQL;
    /**
     * Count all 'outgoing' relations for given etalon id.
     */
    private final String calcTotalCountByToEtalonIdSQL;
    /**
     * Count all 'outgoing' relations for given relation name
     */
    private final String calcTotalCountByRelName;
    /**
     * Check exist relations data  for given relation name
     */
    private final String checkExistByRelName;
    /**
     * Loads versions by name, dates and from etalon id.
     */
    private final String loadToEtalonIdsByNameDateAndFromEtalonIdSQL;
    /**
     * Loads versions by name, dates and from etalon id.
     */
    private final String loadFromEtalonIdsByNameDateAndToEtalonIdSQL;
    /**
     * Update origin SQL. Only status may be actually updated after create.
     */
    private final String updateOriginRelationSQL;
    /**
     * Update etalon SQL. Only status may be actually updated after create.
     */
    private final String updateEtalonRelationSQL;
    /**
     * Change approval state SQL.
     */
    private final String changeEtalonApprovalSQL;
    /**
     * Mark etalon relations by MERGED by from side.
     */
    private final String markEtalonRelationsMergedByFromSide;
    /**
     * Mark etalon relations 'MERGED' by to side.
     */
    private final String remapToEtalonRelations;
    /**
     * Mark etalon relations 'MERGED' by to side.
     */
    private final String remapFromEtalonRelations;
    /**
     * Load keys by relation origin id.
     */
    private final String loadKeysByRelationOriginIdSQL;
    /**
     * Load keys be relation etalon id.
     */
    private final String loadKeysByRelationEtalonIdSQL;
    /**
     * Load keys by records origin ids.
     */
    private final String loadKeysByOriginIdsSQL;
    /**
     * Load keys be records etalon ids.
     */
    private final String loadKeysByEtalonIdsSQL;
    /**
     * Wipe vistory records.
     */
    private final String deleteVistoryByOriginId;
    /**
     * Wipe origin records.
     */
    private final String deleteOriginById;
    /**
     * Wipe etalon records.
     */
    private final String deleteEtalonById;
    /**
     * Resets approval state on vistory records.
     */
    private final String updatePendingVersionsSQL;

    /**
     * Deactivate rels by name
     */
    private final String deactivateRelationByNameSQL;
    /**
     * External utility support.
     */
    @Autowired
    public RelationsDaoImpl(DataSource dataSource, @Qualifier("relations-sql") Properties sql) {
        super(dataSource);
        loadCompleteRelationsTimelineByFromEtalonIdSQL = sql.getProperty("loadCompleteRelationsTimelineByFromEtalonIdSQL");
        loadCompleteRelationsTimelineByToEtalonIdSQL = sql.getProperty("loadCompleteRelationsTimelineByToEtalonIdSQL");
        acquireOriginRelationLockSQL = sql.getProperty("acquireOriginRelationLockSQL");
        putRelationVersionJaxbSQL = sql.getProperty("putRelationVersionJaxbSQL");
        putRelationVersionProtostuffSQL = sql.getProperty("putRelationVersionProtostuffSQL");
        loadRelationsTimelineByRelationEtalonIdSQL = sql.getProperty("loadRelationsTimelineByRelationEtalonIdSQL");
        loadRelationsTimelineByRelationEtalonIdAndAsOfSQL = sql.getProperty("loadRelationsTimelineByRelationEtalonIdAndAsOfSQL");
        loadRelationsTimelineByRelationEtalonIdAndFromBoundarySQL = sql.getProperty("loadRelationsTimelineByRelationEtalonIdAndFromBoundarySQL");
        loadRelationsTimelineByFromEtalonIdSQL = sql.getProperty("loadRelationsTimelineByFromEtalonIdSQL");
        loadRelationsTimelineByFromEtalonIdAndAsOfSQL = sql.getProperty("loadRelationsTimelineByFromEtalonIdAndAsOfSQL");
        loadRelationsTimelineByFromEtalonIdAndFromBoundarySQL = sql.getProperty("loadRelationsTimelineByFromEtalonIdAndFromBoundarySQL");
        loadRelationsBoundaryByFromEtalonIdSQL = sql.getProperty("loadRelationsBoundaryByFromEtalonIdSQL");
        loadRelationsBoundaryByEtalonIdSQL = sql.getProperty("loadRelationsBoundaryByEtalonIdSQL");
        insertOriginRelationSQL = sql.getProperty("insertOriginRelationSQL");
        insertEtalonRelationSQL = sql.getProperty("insertEtalonRelationSQL");
        insertEtalonRelationStateDraft = sql.getProperty("insertEtalonRelationStateDraft");
        cleanupEtalonRelationStateDrafts = sql.getProperty("cleanupEtalonRelationStateDrafts");
        loadLastEtalonRelationStateDraftByEtalonIdSQL = sql.getProperty("loadLastEtalonRelationStateDraftByEtalonIdSQL");
        loadEtalonRelationsByFromNameAndStatusSQL = sql.getProperty("loadEtalonRelationsByFromNameAndStatusSQL");
        loadEtalonRelationsByToNameAndStatusSQL = sql.getProperty("loadEtalonRelationsByToNameAndStatusSQL");
        loadEtalonRelationsByToSQL = sql.getProperty("loadEtalonRelationsByToSQL");
        loadEtalonRelationsByIdSQL = sql.getProperty("loadEtalonRelationsByIdSQL");
        loadEtalonRelationByFromToNameAndStatusSQL = sql.getProperty("loadEtalonRelationByFromToNameAndStatusSQL");
        loadOriginRelationByFromToNameAndStatusSQL = sql.getProperty("loadOriginRelationByFromToNameAndStatusSQL");
        loadOriginRelationByIdSQL = sql.getProperty("loadOriginRelationByIdSQL");
        loadOriginRelationByRelationEtalonIdAndSourceSystemSQL = sql.getProperty("loadOriginRelationByRelationEtalonIdAndSourceSystemSQL");
        loadMissingOriginRelationByFromNameFromIdsActiveIdsAndStatusSQL = sql.getProperty("loadMissingOriginRelationByFromNameFromIdsActiveIdsAndStatusSQL");
        loadRelationVersionsByEtalonIdAndDateJaxbSQL = sql.getProperty("loadRelationVersionsByEtalonIdAndDateJaxbSQL");
        loadRelationVersionsByEtalonIdAndDateProtostuffSQL = sql.getProperty("loadRelationVersionsByEtalonIdAndDateProtostuffSQL");
        loadRelationVersionsByEtalonIdOperationIdAndDateJaxbSQL = sql.getProperty("loadRelationVersionsByEtalonIdOperationIdAndDateJaxbSQL");
        loadRelationVersionsByEtalonIdOperationIdAndDateProtostuffSQL = sql.getProperty("loadRelationVersionsByEtalonIdOperationIdAndDateProtostuffSQL");
        loadPendingVersionsByEtalonIdJaxbSQL = sql.getProperty("loadPendingVersionsByEtalonIdJaxbSQL");
        loadPendingVersionsByEtalonIdProtostuffSQL= sql.getProperty("loadPendingVersionsByEtalonIdProtostuffSQL");
        calcTotalCountByNameDateAndFromEtalonIdSQL = sql.getProperty("calcTotalCountByNameDateAndFromEtalonIdSQL");
        calcTotalCountByNameDateAndToEtalonIdSQL = sql.getProperty("calcTotalCountByNameDateAndToEtalonIdSQL");
        calcTotalCountByFromEtalonIdSQL = sql.getProperty("calcTotalCountByFromEtalonIdSQL");
        checkUsageByFromEtalonIdsSQL = sql.getProperty("checkUsageByFromEtalonIdsSQL");
        calcTotalCountByToEtalonIdSQL = sql.getProperty("calcTotalCountByToEtalonIdSQL");
        loadToEtalonIdsByNameDateAndFromEtalonIdSQL = sql.getProperty("loadToEtalonIdsByNameDateAndFromEtalonIdSQL");
        loadFromEtalonIdsByNameDateAndToEtalonIdSQL = sql.getProperty("loadFromEtalonIdsByNameDateAndToEtalonIdSQL");
        updateOriginRelationSQL = sql.getProperty("updateOriginRelationSQL");
        updateEtalonRelationSQL = sql.getProperty("updateEtalonRelationSQL");
        changeEtalonApprovalSQL = sql.getProperty("changeEtalonApprovalSQL");
        markEtalonRelationsMergedByFromSide = sql.getProperty("markEtalonRelationsMergedByFromSide");
        remapToEtalonRelations = sql.getProperty("remapToEtalonRelations");
        remapFromEtalonRelations = sql.getProperty("remapFromEtalonRelations");
        loadKeysByRelationOriginIdSQL = sql.getProperty("loadKeysByRelationOriginIdSQL");
        loadKeysByRelationEtalonIdSQL = sql.getProperty("loadKeysByRelationEtalonIdSQL");
        loadKeysByOriginIdsSQL = sql.getProperty("loadKeysByOriginIdsSQL");
        loadKeysByEtalonIdsSQL = sql.getProperty("loadKeysByEtalonIdsSQL");
        calcTotalCountByRelName = sql.getProperty("calcTotalCountByRelName");
        deleteVistoryByOriginId = sql.getProperty("deleteVistoryByOriginId");
        deleteOriginById = sql.getProperty("deleteOriginById");
        deleteEtalonById = sql.getProperty("deleteEtalonById");
        updatePendingVersionsSQL = sql.getProperty("updatePendingVersionsSQL");
        deactivateRelationByNameSQL = sql.getProperty("deactivateRelationByNameSQL");
        checkExistByRelName = sql.getProperty("checkExistByRelName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<OriginsVistoryRelationsPO>> loadRelationsVersions(
            String etalonId, String relationName, Date asOf, List<RecordStatus> statuses, boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String statement;
            RowMapper<OriginsVistoryRelationsPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadRelationVersionsByEtalonIdAndDateJaxbSQL;
                rm = RelationVistoryRowMapper.DEFAULT_RELATIONS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadRelationVersionsByEtalonIdAndDateProtostuffSQL;
                rm = RelationVistoryRowMapper.DEFAULT_RELATIONS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String loadDrafts = includeDraftVersions ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            String user = SecurityUtils.getCurrentUserName();
            Timestamp point = asOf == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(asOf.getTime());
            Map<String, List<OriginsVistoryRelationsPO>> result = new HashMap<>();
            List<EtalonRelationPO> relationEtalons = loadEtalonRelations(etalonId, relationName, statuses, RelationSide.FROM);

            for (int i = 0; relationEtalons != null && i < relationEtalons.size(); i++) {
                EtalonRelationPO relationEtalon = relationEtalons.get(i);
                List<OriginsVistoryRelationsPO> versions = jdbcTemplate.query(statement, rm, relationEtalon.getId(), point, loadDrafts, user);
                if (versions != null) {
                    result.put(relationEtalon.getId(), versions);
                }
            }

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRelationsPO> loadRelationVersions(String relationEtalonId, Date asOf, boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String statement;
            RowMapper<OriginsVistoryRelationsPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadRelationVersionsByEtalonIdAndDateJaxbSQL;
                rm = RelationVistoryRowMapper.DEFAULT_RELATIONS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadRelationVersionsByEtalonIdAndDateProtostuffSQL;
                rm = RelationVistoryRowMapper.DEFAULT_RELATIONS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String loadDrafts = includeDraftVersions ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            String user = SecurityUtils.getCurrentUserName();
            Timestamp point = asOf == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(asOf.getTime());
            return jdbcTemplate.query(statement, rm, relationEtalonId, point, loadDrafts, user);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginsVistoryRelationsPO> loadRelationVersions(String relationEtalonId, Date asOf, String operationId, boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String statement;
            RowMapper<OriginsVistoryRelationsPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadRelationVersionsByEtalonIdOperationIdAndDateJaxbSQL;
                rm = RelationVistoryRowMapper.DEFAULT_RELATIONS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadRelationVersionsByEtalonIdOperationIdAndDateProtostuffSQL;
                rm = RelationVistoryRowMapper.DEFAULT_RELATIONS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            String loadDrafts = includeDraftVersions ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            String user = SecurityUtils.getCurrentUserName();
            Timestamp point = asOf == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(asOf.getTime());
            return jdbcTemplate.query(statement, rm, relationEtalonId, point, loadDrafts, operationId, user);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationDigestDTO loadDigestDestinationEtalonIds(String etalonId, String relationName,
            RelationSide viewSide, Map<String, Integer> sourceSystems, Date asOf, int count, int from) {

        List<String> result = null;
        long totalCount;
        if (viewSide == RelationSide.TO) {
            totalCount = jdbcTemplate.queryForObject(calcTotalCountByNameDateAndFromEtalonIdSQL,
                    Long.class, etalonId, relationName /*, namesArray, weightsArray, point*/);

            if (totalCount > 0) {
                result = jdbcTemplate.queryForList(
                        loadToEtalonIdsByNameDateAndFromEtalonIdSQL,
                        String.class,
                        etalonId, relationName, /*namesArray, weightsArray, point,*/ from, count);
            }
        } else {
            totalCount = jdbcTemplate.queryForObject(calcTotalCountByNameDateAndToEtalonIdSQL,
                    Long.class, etalonId, relationName /*, namesArray, weightsArray, point*/);

            if (totalCount > 0) {
                result = jdbcTemplate.queryForList(
                        loadFromEtalonIdsByNameDateAndToEtalonIdSQL,
                        String.class,
                        etalonId, relationName, /*namesArray, weightsArray, point,*/ from, count);
            }
        }

        return new RelationDigestDTO(result, totalCount);
    }

    /**
     * Load origins which are different to supplied origins diff.
     * @param originIdFrom
     * @param originIdsDiff
     * @param relationName
     * @param status
     * @return
     */
    @Override
    public List<OriginRelationPO> loadOriginsRealtionsDiffByStatus(
            String originIdFrom, List<String> originIdsDiff, String relationName, RecordStatus status) {

        MeasurementPoint.start();
        try {

            Map<String, Object> params = new HashMap<>();
            params.put(OriginRelationPO.FIELD_NAME, relationName);
            params.put(OriginRelationPO.FIELD_ORIGIN_ID_FROM, originIdFrom);
            params.put(OriginRelationPO.FIELD_STATUS, Collections.singletonList(status == null ? RecordStatus.ACTIVE : status));
            params.put(OriginRelationPO.FIELD_ID, originIdsDiff);

            return namedJdbcTemplate.query(loadMissingOriginRelationByFromNameFromIdsActiveIdsAndStatusSQL,
                    params, OriginRelationRowMapper.DEFAULT_ORIGIN_RELATION_ROW_MAPPER);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean upsertOriginRelation(OriginRelationPO origin, boolean isNew) {

        MeasurementPoint.start();
        try {
            int count;
            if (isNew) {

                count = jdbcTemplate.update(
                        insertOriginRelationSQL,
                        origin.getId(),
                        origin.getEtalonId(),
                        origin.getName(),
                        origin.getOriginIdFrom(),
                        origin.getOriginIdTo(),
                        0, // Version
                        origin.getSourceSystem(),
                        origin.getCreatedBy(),
                        origin.getStatus() == null ? RecordStatus.ACTIVE.name() : origin.getStatus().name());
            } else {

                count = jdbcTemplate.update(
                        updateOriginRelationSQL,
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
    public void upsertOriginRelations(List<OriginRelationPO> origins, boolean isNew) {

        MeasurementPoint.start();
        try {

            int[][] result;
            if (isNew) {

                result = jdbcTemplate.batchUpdate(insertOriginRelationSQL, origins, origins.size(),
                    (ps, origin) -> {

                        ps.setString(1, origin.getId());
                        ps.setString(2, origin.getEtalonId());
                        ps.setString(3, origin.getName());
                        ps.setString(4, origin.getOriginIdFrom());
                        ps.setString(5, origin.getOriginIdTo());
                        ps.setInt(6, 0); // Version
                        ps.setString(7, origin.getSourceSystem());
                        ps.setString(8, origin.getCreatedBy());
                        ps.setString(9, origin.getStatus() == null ? RecordStatus.ACTIVE.name() : origin.getStatus().name());

                    });
            } else {

                result = jdbcTemplate.batchUpdate(updateOriginRelationSQL, origins, origins.size(),
                    (ps, origin) -> {

                        ps.setTimestamp(1, origin.getUpdateDate() == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(origin.getUpdateDate().getTime()));
                        ps.setString(2, origin.getUpdatedBy());
                        ps.setString(3, origin.getStatus() == null ? RecordStatus.ACTIVE.name() : origin.getStatus().name());
                        ps.setString(4, origin.getId());

                    });
            }

            if (result.length == 0 || result[0].length != origins.size()) {
                final String message = "Relation origin record upsert failed. Stopping.";
                LOGGER.warn(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATIONS_BATCH_UPSERT_ORIGIN_FAILED);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean upsertEtalonRelation(EtalonRelationPO etalon, boolean isNew) {

        MeasurementPoint.start();
        try {
            int count;
            if (isNew) {

                count = jdbcTemplate.update(
                        insertEtalonRelationSQL,
                        etalon.getId(),
                        etalon.getName(),
                        etalon.getEtalonIdFrom(),
                        etalon.getEtalonIdTo(),
                        etalon.getCreatedBy(),
                        etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name(),
                        etalon.getApproval() == null ? ApprovalState.APPROVED.name() : etalon.getApproval().name(),
                        0,
                        etalon.getOperationId());

                if (ApprovalState.PENDING == etalon.getApproval()) {
                    jdbcTemplate.update(insertEtalonRelationStateDraft,
                            etalon.getId(),
                            etalon.getId(),
                            etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name(),
                            etalon.getCreatedBy());
                }
            } else {

                if (ApprovalState.PENDING == etalon.getApproval()) {

                    changeEtalonApproval(etalon.getId(), etalon.getApproval());

                    count = jdbcTemplate.update(insertEtalonRelationStateDraft,
                            etalon.getId(),
                            etalon.getId(),
                            etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name(),
                            etalon.getCreatedBy());
                } else {

                    count = jdbcTemplate.update(
                            updateEtalonRelationSQL,
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
    public void upsertEtalonRelations(List<EtalonRelationPO> etalons, boolean isNew) {

        MeasurementPoint.start();
        try {

            int count = 0;
            if (isNew) {

                int[][] result = jdbcTemplate.batchUpdate(insertEtalonRelationSQL, etalons, etalons.size(),
                    (ps, etalon) -> {

                        ps.setString(1, etalon.getId());
                        ps.setString(2, etalon.getName());
                        ps.setString(3, etalon.getEtalonIdFrom());
                        ps.setString(4, etalon.getEtalonIdTo());
                        ps.setString(5, etalon.getCreatedBy());
                        ps.setString(6, etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name());
                        ps.setString(7, etalon.getApproval() == null ? ApprovalState.APPROVED.name() : etalon.getApproval().name());
                        ps.setInt(8, 0);
                        ps.setString(9, etalon.getOperationId());

                    });

                count = result.length > 0 ? result[0].length : 0;

                List<EtalonRelationPO> pendings = etalons.stream()
                        .filter(e -> e.getApproval() == ApprovalState.PENDING)
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(pendings)) {

                    jdbcTemplate.batchUpdate(insertEtalonRelationStateDraft, pendings, pendings.size(),
                        (ps, pending) -> {

                            ps.setString(1, pending.getId());
                            ps.setString(2, pending.getId());
                            ps.setString(3, pending.getStatus() == null ? RecordStatus.ACTIVE.name() : pending.getStatus().name());
                            ps.setString(4, pending.getCreatedBy());

                        });
                }

            } else {

                List<EtalonRelationPO> pendings = etalons.stream()
                        .filter(e -> e.getApproval() == ApprovalState.PENDING)
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(pendings)) {

                    int[][] result = jdbcTemplate.batchUpdate(insertEtalonRelationStateDraft, pendings, pendings.size(),
                        (ps, pending) -> {

                            ps.setString(1, pending.getId());
                            ps.setString(2, pending.getId());
                            ps.setString(3, pending.getStatus() == null ? RecordStatus.ACTIVE.name() : pending.getStatus().name());
                            ps.setString(4, pending.getCreatedBy());

                        });

                    count += result.length > 0 ? result[0].length : 0;
                }

                List<EtalonRelationPO> straight = etalons.stream()
                        .filter(e -> e.getApproval() != ApprovalState.PENDING)
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(straight)) {

                    int[][] result = jdbcTemplate.batchUpdate(updateEtalonRelationSQL, straight, straight.size(),
                        (ps, etalon) -> {

                            ps.setTimestamp(1, etalon.getUpdateDate() == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(etalon.getUpdateDate().getTime()));
                            ps.setString(2, etalon.getUpdatedBy());
                            ps.setString(3, etalon.getStatus() == null ? RecordStatus.ACTIVE.name() : etalon.getStatus().name());
                            ps.setString(4, etalon.getApproval() == null ? ApprovalState.APPROVED.name() : etalon.getApproval().name());
                            ps.setString(5, etalon.getOperationId());
                            ps.setString(6, etalon.getId());

                        });

                    count += result.length > 0 ? result[0].length : 0;
                }
            }

            if (count != etalons.size()) {
                final String message = "Relation etalon record upsert failed. Stopping";
                LOGGER.warn(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATIONS_BATCH_UPSERT_ETALON_FAILED);
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeEtalonApproval(String etalonId, ApprovalState approval) {

        boolean success = false;
        MeasurementPoint.start();
        try {

            success = jdbcTemplate.update(changeEtalonApprovalSQL,
                SecurityUtils.getCurrentUserName(),
                approval != null ? approval.name() : ApprovalState.APPROVED.name(),
                etalonId,
                approval != null ? approval.name() : ApprovalState.APPROVED.name()
            ) > 0;

        } finally {
            MeasurementPoint.stop();
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cleanupEtalonStateDrafts(String etalonId) {

        boolean success = false;
        MeasurementPoint.start();
        try {
            success = jdbcTemplate.update(cleanupEtalonRelationStateDrafts, etalonId) > 0;
        } finally {
            MeasurementPoint.stop();
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRelationDraftStatePO loadLastEtalonStateDraft(String etalonId) {

        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadLastEtalonRelationStateDraftByEtalonIdSQL,
                    EtalonRelationDraftStateRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    etalonId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bulkInsertEtalonRecords(List<EtalonRelationPO> records, String targetTable) {

        MeasurementPoint.start();
        try {

            final String prolog = new StringBuilder().append("copy ")
                    .append(targetTable)
                    .append(" (id, version, name, create_date, created_by, status, approval, etalon_id_from, etalon_id_to) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                VendorDataType.UUID,
                VendorDataType.INT4,
                VendorDataType.CHAR,
                VendorDataType.TIMESTAMP,
                VendorDataType.CHAR,
                VendorDataType.CHAR,
                VendorDataType.CHAR,
                VendorDataType.UUID,
                VendorDataType.UUID
            };

            final Object[] params = new Object[types.length];

            try (Connection connection = getBareConnection();
                 CopyDataOutputStream stream = VendorUtils.bulkStart(connection, prolog)) {

                for (EtalonRelationPO record : records) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = 1;
                    params[2] = record.getName();
                    params[3] = record.getCreateDate();
                    params[4] = record.getCreatedBy();
                    params[5] = record.getStatus() == null ? RecordStatus.ACTIVE.name() : record.getStatus().name();
                    params[6] = record.getApproval() == null ? ApprovalState.APPROVED.name() : record.getApproval().name();
                    params[7] = UUID.fromString(record.getEtalonIdFrom());
                    params[8] = UUID.fromString(record.getEtalonIdTo());

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
    public void bulkInsertOriginRecords(List<OriginRelationPO> records, String targetTable) {
        MeasurementPoint.start();
        try {

            final String prolog = new StringBuilder()
                    .append("copy ")
                    .append(targetTable)
                    .append(" (id, etalon_id, name, origin_id_from, origin_id_to, version, source_system, create_date, created_by, status) from stdin binary")
                    .toString();

            final VendorDataType[] types = {
                VendorDataType.UUID,
                VendorDataType.UUID,
                VendorDataType.CHAR,
                VendorDataType.UUID,
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

                for (OriginRelationPO record : records) {

                    params[0] = UUID.fromString(record.getId());
                    params[1] = UUID.fromString(record.getEtalonId());
                    params[2] = record.getName();
                    params[3] = UUID.fromString(record.getOriginIdFrom());
                    params[4] = UUID.fromString(record.getOriginIdTo());
                    params[5] = 1;
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
    public void bulkUpdateEtalonRecords(List<EtalonRelationPO> records, String targetTable) {

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

                for (EtalonRelationPO record : records) {

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
    public void bulkUpdateOriginRecords(List<OriginRelationPO> records, String targetTable) {

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

                for (OriginRelationPO record : records) {

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
    public void bulkInsertVersions(List<OriginsVistoryRelationsPO> versions, String target) {

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

                for (OriginsVistoryRelationsPO record : versions) {

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

    @Override
    public void putVersion(OriginsVistoryRelationsPO version) {

        MeasurementPoint.start();
        try {

            // Acquire tn lock
            jdbcTemplate.query(acquireOriginRelationLockSQL, rs -> true, version.getOriginId());

            String statement = platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB
                ? putRelationVersionJaxbSQL
                : putRelationVersionProtostuffSQL;

            Timestamp from = version.getValidFrom() == null ? null : new Timestamp(version.getValidFrom().getTime());
            Timestamp to = version.getValidTo() == null ? null : new Timestamp(version.getValidTo().getTime());
            String status = version.getStatus() == null ? RecordStatus.ACTIVE.name() : version.getStatus().name();
            String approval = version.getApproval() == null ? ApprovalState.APPROVED.name() : version.getApproval().name();

            jdbcTemplate.update(statement, ps -> {

                ps.setString(1, version.getId());
                ps.setString(2, version.getOriginId());
                ps.setString(3, version.getOperationId());
                ps.setString(4, version.getOriginId()); // Revision
                ps.setTimestamp(5, from);
                ps.setTimestamp(6, to);

                if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                    ps.setString(7, DumpUtils.dumpOriginRelationToJaxb(version.getData()));
                } else if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.PROTOSTUFF) {
                    ps.setBytes(7, DumpUtils.dumpToProtostuff(version.getData()));
                }

                ps.setString(8, version.getCreatedBy());
                ps.setString(9, status);
                ps.setString(10, approval);
                ps.setInt(11, version.getMajor());
                ps.setInt(12, version.getMinor());
                ps.setString(13, version.getShift() == null ? DataShift.PRISTINE.name() : version.getShift().name());
            });

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putVersions(List<OriginsVistoryRelationsPO> versions) {

        MeasurementPoint.start();
        try {

            String statement = platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB
                ? putRelationVersionJaxbSQL
                : putRelationVersionProtostuffSQL;

            int[][] result = jdbcTemplate.batchUpdate(statement, versions, versions.size(), (ps, version) -> {

                    Timestamp from = version.getValidFrom() == null ? null : new Timestamp(version.getValidFrom().getTime());
                    Timestamp to = version.getValidTo() == null ? null : new Timestamp(version.getValidTo().getTime());
                    String status = version.getStatus() == null ? RecordStatus.ACTIVE.name() : version.getStatus().name();
                    String approval = version.getApproval() == null ? ApprovalState.APPROVED.name() : version.getApproval().name();

                    ps.setString(1, version.getId());
                    ps.setString(2, version.getOriginId());
                    ps.setString(3, version.getOperationId());
                    ps.setString(4, version.getOriginId()); // Revision
                    ps.setTimestamp(5, from);
                    ps.setTimestamp(6, to);

                    if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                        ps.setString(7, DumpUtils.dumpOriginRelationToJaxb(version.getData()));
                    } else if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.PROTOSTUFF) {
                        ps.setBytes(7, DumpUtils.dumpToProtostuff(version.getData()));
                    }

                    ps.setString(8, version.getCreatedBy());
                    ps.setString(9, status);
                    ps.setString(10, approval);
                    ps.setInt(11, version.getMajor());
                    ps.setInt(12, version.getMinor());
                    ps.setString(13, version.getShift() == null ? DataShift.PRISTINE.name() : version.getShift().name());

            });

            if (result.length == 0 || result[0].length != versions.size()) {
                final String message = "Cannot insert relation version record(s).";
                LOGGER.warn(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_VERSION_FAILED);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeIntervalPO> loadContributingRelationTimeline(String relationEtalonId, boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String user = SecurityUtils.getCurrentUserName();
            return jdbcTemplate.query(loadRelationsTimelineByRelationEtalonIdSQL,
                    TimeIntervalRowMapper.RELATIONS_DEFAULT_TIME_INTERVAL_ROW_MAPPER,
                    relationEtalonId,
                    user,
                    includeDraftVersions,
                    relationEtalonId,
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
    public List<TimeIntervalPO> loadContributingRelationTimeline(String relationEtalonId, Date asOf, boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String user = SecurityUtils.getCurrentUserName();
            Timestamp ts = VendorUtils.coalesce(asOf);
            return jdbcTemplate.query(loadRelationsTimelineByRelationEtalonIdAndAsOfSQL,
                    TimeIntervalRowMapper.RELATIONS_DEFAULT_TIME_INTERVAL_ROW_MAPPER,
                    relationEtalonId,
                    user,
                    includeDraftVersions,
                    relationEtalonId,
                    includeDraftVersions,
                    user,
                    ts);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TimeIntervalPO> loadContributingRelationTimeline(String relationEtalonId, Date from, Date to,
            boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String user = SecurityUtils.getCurrentUserName();
            Timestamp tsFrom = VendorUtils.coalesceFrom(from);
            Timestamp tsTo = VendorUtils.coalesceTo(to);
            return jdbcTemplate.query(loadRelationsTimelineByRelationEtalonIdAndFromBoundarySQL,
                    TimeIntervalRowMapper.RELATIONS_DEFAULT_TIME_INTERVAL_ROW_MAPPER,
                    relationEtalonId,
                    user,
                    includeDraftVersions,
                    relationEtalonId,
                    includeDraftVersions,
                    user,
                    tsFrom,
                    tsTo,
                    tsFrom,
                    tsTo,
                    tsFrom,
                    tsTo);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Map<String, List<TimeIntervalPO>>> loadCompleteContributingRelationsTimelineByFromSide(String etalonId,
            boolean includeDraftVersions) {

        String user = SecurityUtils.getCurrentUserName();
        return jdbcTemplate.query(loadCompleteRelationsTimelineByFromEtalonIdSQL,
                TimeIntervalRowMapper.DEFAULT_COMPLETE_RELATIONS_EXTRACTOR,
                user,
                includeDraftVersions,
                etalonId,
                includeDraftVersions,
                user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Map<String, List<TimeIntervalPO>>> loadCompleteContributingRelationsTimelineByToSide(String etalonId,
            boolean includeDraftVersions) {

        String user = SecurityUtils.getCurrentUserName();
        return jdbcTemplate.query(loadCompleteRelationsTimelineByToEtalonIdSQL,
                TimeIntervalRowMapper.DEFAULT_COMPLETE_RELATIONS_EXTRACTOR,
                user,
                includeDraftVersions,
                etalonId,
                includeDraftVersions,
                user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<TimeIntervalPO>> loadContributingRelationsTimeline(String etalonId, String name, boolean includeDraftVersions) {
        MeasurementPoint.start();
        try {

            String user = SecurityUtils.getCurrentUserName();
            return jdbcTemplate.query(loadRelationsTimelineByFromEtalonIdSQL,
                    TimeIntervalRowMapper.DEFAULT_RELATIONS_BY_FROM_ETALON_ID_EXTRACTOR,
                    user,
                    includeDraftVersions,
                    etalonId,
                    name,
                    includeDraftVersions,
                    user,
                    name);

        } finally {
            MeasurementPoint.stop();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<TimeIntervalPO>> loadContributingRelationsTimeline(String etalonId, String name, Date asOf,
            boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String user = SecurityUtils.getCurrentUserName();
            Timestamp ts = VendorUtils.coalesce(asOf);
            return jdbcTemplate.query(loadRelationsTimelineByFromEtalonIdAndAsOfSQL,
                    TimeIntervalRowMapper.DEFAULT_RELATIONS_BY_FROM_ETALON_ID_EXTRACTOR,
                    user,
                    includeDraftVersions,
                    etalonId,
                    name,
                    includeDraftVersions,
                    user,
                    name,
                    ts);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<TimeIntervalPO>> loadContributingRelationsTimeline(String etalonId, String name, Date from, Date to,
            boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String user = SecurityUtils.getCurrentUserName();
            Timestamp tsFrom = VendorUtils.coalesceFrom(from);
            Timestamp tsTo = VendorUtils.coalesceTo(to);
            return jdbcTemplate.query(loadRelationsTimelineByFromEtalonIdAndFromBoundarySQL,
                    TimeIntervalRowMapper.DEFAULT_RELATIONS_BY_FROM_ETALON_ID_EXTRACTOR,
                    user,
                    includeDraftVersions,
                    etalonId,
                    name,
                    includeDraftVersions,
                    user,
                    name,
                    tsFrom,
                    tsTo,
                    tsFrom,
                    tsTo,
                    tsFrom,
                    tsTo);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalPO loadRelationEtalonBoundary(String etalonId, Date point, boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String user = SecurityUtils.getCurrentUserName();
            Timestamp ts = VendorUtils.coalesce(point);
            return jdbcTemplate.query(loadRelationsBoundaryByEtalonIdSQL,
                    TimeIntervalRowMapper.DEFAULT_RELATIONS_BOUNDARY_EXTRACTOR,
                    ts,
                    user,
                    includeDraftVersions,
                    etalonId);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalPO loadRelationsEtalonBoundary(String etalonId, String name, Date point, boolean includeDraftVersions) {

        MeasurementPoint.start();
        try {

            String user = SecurityUtils.getCurrentUserName();
            Timestamp ts = VendorUtils.coalesce(point);
            return jdbcTemplate.query(loadRelationsBoundaryByFromEtalonIdSQL,
                    TimeIntervalRowMapper.DEFAULT_RELATIONS_BOUNDARY_EXTRACTOR,
                    etalonId,
                    name,
                    etalonId,
                    name,
                    name,
                    ts,
                    user,
                    includeDraftVersions,
                    etalonId,
                    name);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EtalonRelationPO> loadEtalonRelations(String etalonId, String relationName, List<RecordStatus> statuses, RelationSide side) {
        MeasurementPoint.start();
        try {

            Map<String, Object> params = new HashMap<>();
            params.put(EtalonRelationPO.FIELD_NAME, relationName);
            params.put(EtalonRelationPO.FIELD_STATUS,
                    statuses == null
                        ? Collections.singletonList(RecordStatus.ACTIVE.name())
                        : statuses.stream().map(RecordStatus::name).collect(Collectors.toList()));

            if (RelationSide.FROM == side) {

                params.put(EtalonRelationPO.FIELD_ETALON_ID_FROM, etalonId);
                return namedJdbcTemplate.query(
                        loadEtalonRelationsByFromNameAndStatusSQL,
                        params,
                        EtalonRelationRowMapper.DEFAULT_ETALON_RELATION_ROW_MAPPER);

            } else if (RelationSide.TO == side) {

                params.put(EtalonRelationPO.FIELD_ETALON_ID_TO, etalonId);
                return namedJdbcTemplate.query(
                        loadEtalonRelationsByToNameAndStatusSQL,
                        params,
                        EtalonRelationRowMapper.DEFAULT_ETALON_RELATION_ROW_MAPPER);
            }

            return Collections.emptyList();
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRelationPO loadEtalonRelation(String relationEtalonId) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(
                    loadEtalonRelationsByIdSQL,
                    EtalonRelationRowMapper.DEFAULT_ETALON_RELATION_FIRST_RESULT_EXTRACTOR,
                    relationEtalonId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deactivateRelationByOriginId(String relationOriginId) {
        MeasurementPoint.start();
        try {
            OriginRelationPO po = loadOriginRelation(relationOriginId, null);
            if (po == null) {
                return false;
            }

            Date ts = new Date(System.currentTimeMillis());
            String user = SecurityUtils.getCurrentUserName();

            po.setStatus(RecordStatus.INACTIVE);
            po.setUpdateDate(ts);
            po.setUpdatedBy(user);

            upsertOriginRelation(po, false);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deactivateRelationByEtalonId(String relationEtalonId, ApprovalState approvalState) {

        MeasurementPoint.start();
        try {
            EtalonRelationPO epo = loadEtalonRelation(relationEtalonId);
            if (epo == null) {
                return false;
            }

            Date ts = new Date(System.currentTimeMillis());
            String user = SecurityUtils.getCurrentUserName();
            List<OriginRelationPO> ors = loadOriginRelationsByEtalonId(epo.getId());
            for (OriginRelationPO opo : ors) {

                opo.setStatus(RecordStatus.INACTIVE);
                opo.setUpdateDate(ts);
                opo.setUpdatedBy(user);

                upsertOriginRelation(opo, false);
            }

            epo.setStatus(RecordStatus.INACTIVE);
            epo.setApproval(approvalState);
            epo.setUpdateDate(ts);
            epo.setUpdatedBy(user);

            upsertEtalonRelation(epo, false);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateRelationsByName(String relationName) {
        jdbcTemplate.update(deactivateRelationByNameSQL, new Date(), RecordStatus.INACTIVE.name(), relationName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRelationPO loadEtalonRelation(String keyFrom, String keyTo, String name, List<RecordStatus> statuses) {
        return jdbcTemplate.query(
                loadEtalonRelationByFromToNameAndStatusSQL,
                EtalonRelationRowMapper.DEFAULT_ETALON_RELATION_FIRST_RESULT_EXTRACTOR,
                keyFrom, keyTo, name,
                enumToString(statuses == null ? Collections.singletonList(RecordStatus.ACTIVE) : statuses));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRelationPO loadOriginRelation(String keyFrom, String keyTo, String name, List<RecordStatus> statuses) {
        return jdbcTemplate.query(
                loadOriginRelationByFromToNameAndStatusSQL,
                OriginRelationRowMapper.DEFAULT_ORIGIN_RELATION_FIRST_RESULT_EXTRACTOR,
                keyFrom, keyTo, name,
                enumToString(statuses == null ? Collections.singletonList(RecordStatus.ACTIVE) : statuses));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRelationPO loadOriginRelation(String relationOriginId, List<RecordStatus> statuses) {
        return jdbcTemplate.query(loadOriginRelationByIdSQL,
                OriginRelationRowMapper.DEFAULT_ORIGIN_RELATION_FIRST_RESULT_EXTRACTOR,
                relationOriginId,
                enumToString(statuses == null ? Collections.singletonList(RecordStatus.ACTIVE) : statuses));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginRelationPO> loadOriginRelationsByEtalonId(String relationEtalonId) {
        return jdbcTemplate.query(
                loadOriginRelationByRelationEtalonIdAndSourceSystemSQL,
                OriginRelationRowMapper.DEFAULT_ORIGIN_RELATION_ROW_MAPPER,
                relationEtalonId,
                null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRelationPO loadOriginRelationByEtalonIdAndSourceSystem(String relationEtalonId, @Nonnull String sourceSystem) {
        return jdbcTemplate.query(
                loadOriginRelationByRelationEtalonIdAndSourceSystemSQL,
                OriginRelationRowMapper.DEFAULT_ORIGIN_RELATION_FIRST_RESULT_EXTRACTOR,
                relationEtalonId,
                sourceSystem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EtalonRelationPO> loadCurrentRelationsToEtalon(String etalonId, RecordStatus status) {
        return jdbcTemplate.query(loadEtalonRelationsByToSQL,
                EtalonRelationRowMapper.DEFAULT_ETALON_RELATION_ROW_MAPPER, etalonId, status.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countCurrentRelationsToEtalon(String etalonId, RecordStatus status) {
        return jdbcTemplate.queryForObject(calcTotalCountByFromEtalonIdSQL, Integer.class, etalonId, status.name(), status.name());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> checkUsageByFromEtalonIds(List<String> etalonIds, String relationName) {
        Map<String, Object> params = new HashMap<>();
        params.put(EtalonRelationPO.FIELD_NAME, relationName);
        params.put(EtalonRelationPO.FIELD_ETALON_ID_FROM, etalonIds);

        return namedJdbcTemplate.query(checkUsageByFromEtalonIdsSQL, new MapSqlParameterSource(params) {
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if (EtalonRelationPO.FIELD_ETALON_ID_FROM.equals(paramName)) {
                    return ((Collection<String>) val).stream()
                            .filter(Objects::nonNull)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }
                return val;
            }
        }, (ResultSetExtractor<List>) rs -> {
            List<String> res = new ArrayList<>();
            while(rs.next()){
                res.add(rs.getString(EtalonRelationPO.FIELD_ETALON_ID_FROM));
            }
            return res;
        });

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int countCurrentRelationsFromEtalon(String etalonId, RecordStatus status) {
        return jdbcTemplate.queryForObject(calcTotalCountByToEtalonIdSQL, Integer.class, etalonId, status.name(), status.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countRelationByName(String relName) {
        return jdbcTemplate.queryForObject(calcTotalCountByRelName, Long.class, relName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkExistDataRelationByName(String relName) {
        return jdbcTemplate.queryForObject(checkExistByRelName, Boolean.class, relName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int markFromEtalonRelationsMerged(List<String> ids, List<String> skipRelNames, String operationId) {

        String user = SecurityUtils.getCurrentUserName();
        Timestamp point = new Timestamp(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put(EtalonRelationPO.FIELD_STATUS, RecordStatus.MERGED.name());
        params.put(EtalonRelationPO.FIELD_UPDATE_DATE, point);
        params.put(EtalonRelationPO.FIELD_UPDATED_BY, user);
        params.put(EtalonRelationPO.FIELD_OPERATION_ID, operationId);
        params.put(EtalonRelationPO.FIELD_ETALON_ID_FROM, ids);
        params.put(EtalonRelationPO.FIELD_NAME,
            CollectionUtils.isEmpty(skipRelNames)
                ? Collections.singletonList("")
                : skipRelNames);

        return namedJdbcTemplate.update(markEtalonRelationsMergedByFromSide, new MapSqlParameterSource(params) {
            @SuppressWarnings("unchecked")
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if (EtalonRelationPO.FIELD_ETALON_ID_FROM.equals(paramName)) {
                    return ((Collection<String>) val).stream()
                            .filter(Objects::nonNull)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }
                return val;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int remapToEtalonRelations(List<String> fromEtalonIds, String toEtalonId, String operationId) {

        String user = SecurityUtils.getCurrentUserName();
        Timestamp point = new Timestamp(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("etalon_id_to_new", toEtalonId);
        params.put(EtalonRelationPO.FIELD_UPDATE_DATE, point);
        params.put(EtalonRelationPO.FIELD_UPDATED_BY, user);
        params.put(EtalonRelationPO.FIELD_ETALON_ID_TO, fromEtalonIds);
        params.put(EtalonRelationPO.FIELD_OPERATION_ID, operationId);

        return namedJdbcTemplate.update(remapToEtalonRelations, new MapSqlParameterSource(params) {
            @SuppressWarnings("unchecked")
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if ("etalon_id_to_new".equals(paramName)) {
                    return UUID.fromString(val.toString());
                } else if (EtalonRelationPO.FIELD_ETALON_ID_TO.equals(paramName)) {
                    return ((Collection<String>) val).stream()
                            .filter(Objects::nonNull)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }
                return val;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int remapFromEtalonRelations(List<String> fromEtalonIds, String toEtalonId, List<String> m2mNames, String operationId) {

        String user = SecurityUtils.getCurrentUserName();
        Timestamp point = new Timestamp(System.currentTimeMillis());

        Map<String, Object> params = new HashMap<>();
        params.put("etalon_id_from_new", toEtalonId);
        params.put(EtalonRelationPO.FIELD_UPDATE_DATE, point);
        params.put(EtalonRelationPO.FIELD_UPDATED_BY, user);
        params.put(EtalonRelationPO.FIELD_ETALON_ID_FROM, fromEtalonIds);
        params.put(EtalonRelationPO.FIELD_OPERATION_ID, operationId);
        params.put(EtalonRelationPO.FIELD_NAME, CollectionUtils.isEmpty(m2mNames) ? Collections.singletonList("") : m2mNames);

        return namedJdbcTemplate.update(remapFromEtalonRelations, new MapSqlParameterSource(params) {
            @SuppressWarnings("unchecked")
            @Override
            public Object getValue(String paramName) {
                Object val = super.getValue(paramName);
                if ("etalon_id_from_new".equals(paramName)) {
                    return UUID.fromString(val.toString());
                } else if (EtalonRelationPO.FIELD_ETALON_ID_FROM.equals(paramName)) {
                    return ((Collection<String>) val).stream()
                            .filter(Objects::nonNull)
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                }
                return val;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationKeysPO loadRelationKeysByRelationOriginId(String originId) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByRelationOriginIdSQL,
                    RelationKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    originId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationKeysPO loadRelationKeysByRelationEtalonId(String sourceSystem, String etalonId) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByRelationEtalonIdSQL,
                    RelationKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    sourceSystem, etalonId);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationKeysPO loadRelationKeysBySidesOriginIds(String originIdFrom, String originIdTo, String name) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByOriginIdsSQL,
                    RelationKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    originIdFrom, originIdTo, name);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationKeysPO loadRelationKeysBySidesEtalonIds(String sourceSystem, String etalonIdFrom,
            String etalonIdTo, String name) {
        MeasurementPoint.start();
        try {
            return jdbcTemplate.query(loadKeysByEtalonIdsSQL,
                    RelationKeysRowMapper.DEFAULT_FIRST_RESULT_EXTRACTOR,
                    sourceSystem, etalonIdFrom, etalonIdTo, name);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * @see com.unidata.mdm.backend.dao.RelationsDao#loadPendingVersionsByEtalonId(java.lang.String, java.util.Date)
     */
    @Override
    public List<OriginsVistoryRelationsPO> loadPendingVersionsByEtalonId(String etalonId, Date asOf) {
        MeasurementPoint.start();
        try {

            String statement;
            RowMapper<OriginsVistoryRelationsPO> rm;
            if (platformConfiguration.getDumpTargetFormat() == DumpTargetFormat.JAXB) {
                statement = loadPendingVersionsByEtalonIdJaxbSQL;
                rm = RelationVistoryRowMapper.DEFAULT_RELATIONS_VISTORY_JAXB_ROW_MAPPER;
            } else {
                statement = loadPendingVersionsByEtalonIdProtostuffSQL;
                rm = RelationVistoryRowMapper.DEFAULT_RELATIONS_VISTORY_PROTOSTUFF_ROW_MAPPER;
            }

            Timestamp point = new Timestamp(asOf == null ? System.currentTimeMillis() : asOf.getTime());
            return jdbcTemplate.query(statement, rm, etalonId, point, point, point);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateApprovalState(String relationEtalonId, ApprovalState to) {
        MeasurementPoint.start();
        try {
            jdbcTemplate.update(updatePendingVersionsSQL,
                    to == null ? ApprovalState.APPROVED.name() : to.name(),
                    relationEtalonId);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wipeOriginRecord(String originId) {
        jdbcTemplate.update(deleteVistoryByOriginId, originId);
        jdbcTemplate.update(deleteOriginById, originId);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean wipeEtalonRecord(String etalonId) {
        jdbcTemplate.update(deleteEtalonById, etalonId);
        return true;
    }
}

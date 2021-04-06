package com.unidata.mdm.backend.service.data.relations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.context.AbstractRelationToRequestContext;
import com.unidata.mdm.backend.common.context.AbstractRelationsFromRequestContext;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext.GetRelationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.RelationIdentityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteRelationDTO;
import com.unidata.mdm.backend.common.dto.RelationStateDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.OriginRelationInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.dao.RelationsDao;
import com.unidata.mdm.backend.po.ContributorPO;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.OriginRelationPO;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.po.RelationKeysPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.data.batch.RelationBatchSet;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowService;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Contains functionality, common to all types of relations.
 */
@Component
public class CommonRelationsComponent {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRelationsComponent.class);
    /**
     * Common functionality.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;
    /**
     * Relations vistory DAO.
     */
    @Autowired
    private RelationsDao relationsDao;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Work flow service.
     */
    @Autowired(required = false)
    private WorkflowService workflowService;
    /**
     * Etalon composer.
     */
    @Autowired
    private EtalonComposer etalonComposer;
    /**
     * Constructor.
     */
    public CommonRelationsComponent() {
        super();
    }

    /**
     * Fills the keys instance.
     * @param po po object
     * @return keys
     */
    private RelationKeys fillKeys(RelationKeysPO po) {

        if (po != null) {

            EtalonKey etalonKeyFrom = EtalonKey.builder().id(po.getEtalonIdFrom()).build();
            OriginKey originKeyFrom = null;
            if (!StringUtils.isBlank(po.getOriginIdFrom())) {
                originKeyFrom = OriginKey.builder()
                    .entityName(po.getOriginFromName())
                    .externalId(po.getOriginFromExternalId())
                    .sourceSystem(po.getOriginFromSourceSystem())
                    .id(po.getOriginIdFrom())
                    .build();
            }

            EtalonKey etalonKeyTo = EtalonKey.builder().id(po.getEtalonIdTo()).build();
            OriginKey originKeyTo = null;
            if (!StringUtils.isBlank(po.getOriginIdTo())) {
                originKeyTo = OriginKey.builder()
                    .entityName(po.getOriginToName())
                    .externalId(po.getOriginToExternalId())
                    .sourceSystem(po.getOriginToSourceSystem())
                    .id(po.getOriginIdTo())
                    .build();
            }

            return RelationKeys.builder()
                    .from(RecordKeys.builder()
                            .etalonKey(etalonKeyFrom)
                            .originKey(originKeyFrom)
                            .entityName(po.getEtalonFromName())
                            .etalonStatus(po.getEtalonFromStatus())
                            .originStatus(po.getOriginFromStatus())
                            .etalonState(po.getEtalonFromState())
                            .build())
                    .to(RecordKeys.builder()
                            .etalonKey(etalonKeyTo)
                            .originKey(originKeyTo)
                            .entityName(po.getEtalonToName())
                            .etalonStatus(po.getEtalonToStatus())
                            .originStatus( po.getOriginToStatus())
                            .etalonState(po.getEtalonToState())
                            .build())
                    .relationName(po.getEtalonName())
                    .etalonId(po.getEtalonId())
                    .etalonStatus(po.getEtalonStatus())
                    .etalonState(po.getEtalonState())
                    .originId(po.getOriginId())
                    .originRevision(po.getOriginRevision())
                    .originStatus(po.getOriginStatus())
                    .originSourceSystem(po.getOriginSourceSystem())
                    .build();
        }

        return null;
    }

    /**
     * Identifies side, using cached keys.
     * @param ctx the context
     * @return keys
     */
    public RecordKeys identifySide(RecordIdentityContext ctx) {

        MeasurementPoint.start();
        try {

            RecordKeys side = commonComponent.identify(ctx);
            if (side != null) {

                if (side.getEtalonKey() != null && side.getOriginKey() == null) {
                    OriginKey newKey = commonComponent.createSystemOriginRecord(
                            side.getEtalonKey().getId(),
                            side.getEntityName());
                    side = RecordKeys.builder(side)
                            .originKey(newKey)
                            .originStatus(RecordStatus.ACTIVE)
                            .build();
                }

                ((CommonRequestContext) ctx).putToStorage(ctx.keysId(), side);
            }

            return side;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Identify by relation keys.
     * @param ctx the context
     * @return keys
     */
    public RelationKeys identify(RelationIdentityContext ctx) {
        MeasurementPoint.start();
        try {

            RelationKeysPO po = null;
            if (ctx.isRelationOriginKey()) {
                po = relationsDao.loadRelationKeysByRelationOriginId(ctx.getRelationOriginKey());
            } else if (ctx.isRelationEtalonKey()) {
                po = relationsDao.loadRelationKeysByRelationEtalonId(
                        metaModelService.getAdminSourceSystem().getName(), ctx.getRelationEtalonKey());
            }

            RelationKeys keys = fillKeys(po);
            if (Objects.nonNull(keys)) {
                ((CommonRequestContext) ctx).putToStorage(ctx.relationKeysId(), keys);
            }

            return keys;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Identify relation by sides keys
     * @param name relation name
     * @param from the from side
     * @param to the to side
     * @return relation keys
     */
    public RelationKeys identify(String name, RecordKeys from, RecordKeys to) {
        MeasurementPoint.start();
        try {

            if (from == null || to == null) {
                return null;
            }

            RelationKeysPO po = null;
            if ((from.getOriginKey() != null && from.getOriginKey().getId() != null)
             && (to.getOriginKey() != null && to.getOriginKey().getId() != null)) {
                po = relationsDao.loadRelationKeysBySidesOriginIds(
                        from.getOriginKey().getId(), to.getOriginKey().getId(), name);
            }

            if (po == null && from.getEtalonKey() != null && to.getEtalonKey() != null) {
                po = relationsDao.loadRelationKeysBySidesEtalonIds(
                        metaModelService.getAdminSourceSystem().getName(),
                        from.getEtalonKey().getId(), to.getEtalonKey().getId(), name);
            }

            return fillKeys(po);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets the from key from the context, if supplied.
     * @param ctx the context
     * @return keys or null
     */
    public RecordKeys ensureAndGetFromRecordKeys(AbstractRelationsFromRequestContext<?> ctx) {

        // Try to resolve from side
        RecordKeys from = ctx.keys();
        if (from == null) {
            from = identifySide(ctx);
        }

        return from;
    }

    /**
     * Gets relation keys resolving also the to side.
     * @param relationName name of the relation
     * @param ctx context
     * @return keys or null
     */
    public RelationKeys ensureAndGetRelationKeys(String relationName, AbstractRelationToRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        if (keys == null) {

            // 1. Try relation identity first
            if (ctx.isValidRelationKey()) {
                keys = identify(ctx);
            }

            // 2. Try sides secondly
            if (keys == null) {

                // 2.1 From key must already be resolved, if defined. Just check for presence
                RecordKeys from = ctx.getFromStorage(StorageId.RELATIONS_FROM_KEY);
                RecordKeys to = ctx.getFromStorage(StorageId.RELATIONS_TO_KEY);

                if (from == null) {
                    return null;
                }

                if (to == null) {

                    to = identifySide(ctx);
                    if (to == null) {
                        return null;
                    }

                    ctx.putToStorage(StorageId.RELATIONS_TO_KEY, to);
                }

                // 2.2. Skip pointless keys resolution upon initial load.
                // May quite have an impact on millions of records
                boolean initialLoad = ctx instanceof UpsertRelationRequestContext
                        ? ((UpsertRelationRequestContext) ctx).isInitialLoad()
                        : false;

                if (!initialLoad) {
                    keys = identify(relationName, from, to);
                }
            }

            if (keys != null) {
                ctx.putToStorage(ctx.relationKeysId(), keys);
            }
        }

        return keys;
    }

    /**
     * Ensures keys existance.
     * @param relationName name of the relation
     * @param ctx current context
     */
    private void ensureRelationUpsertContextBefore(String relationName, UpsertRelationRequestContext ctx) {

        // Must be already resolved
        RelationKeys relationKeys = ctx.relationKeys();

        // Both keys must be already resolved. Check for presence
        RecordKeys from = ctx.getFromStorage(StorageId.RELATIONS_FROM_KEY);
        RecordKeys to = ctx.getFromStorage(StorageId.RELATIONS_TO_KEY);
        UpsertAction action = ctx.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION);

        // 2. Possibly a new object
        if (relationKeys == null) {

            // 2.1 Fail upsert. Incomplete identity.
            if (from == null) {
                final String message
                    = "Cannot identify relation's from side record by given origin id [{}], external id [{}, {}, {}], etalon id [{}]. Stopping.";
                LOGGER.warn(message,
                        ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
                throw new BusinessException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_FROM_NOT_FOUND,
                        ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
            }

            // 2.2 Fail upsert. Incomplete identity.
            if (to == null) {
                final String message
                    = "Cannot identify relation's to side record by given origin id [{}], external id [{}, {}, {}], etalon id [{}]. Stopping.";
                LOGGER.warn(message,
                        ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
                throw new BusinessException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_TO_NOT_FOUND,
                        ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
            }

            // 2.3 Check sides status
            if (from.getEtalonStatus() != RecordStatus.ACTIVE
             || to.getEtalonStatus() != RecordStatus.ACTIVE) {
                final String message = "Left or right side of the relation is in inactive state. Stopping.";
                LOGGER.warn(message);
                throw new BusinessException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_SIDES_INACTIVE);
            }

            // 2.4 New relation etalon
            EtalonRelationPO etalon
                = DataRecordUtils.newEtalonRelationPO(ctx, relationName, from.getEtalonKey().getId(),
                    to.getEtalonKey().getId(), RecordStatus.ACTIVE);

            if (ctx.isBatchUpsert()) {
                RelationBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                batchSet.setEtalonRelationInsertPO(etalon);
            } else {
                if (!relationsDao.upsertEtalonRelation(etalon, true)) {
                    final String message = "Relation etalon record upsert failed. Stopping";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_ETALON_FAILED);
                }
            }

            relationKeys = RelationKeys.builder()
                    .from(from)
                    .to(to)
                    .relationName(relationName)
                    .etalonId(etalon.getId())
                    .etalonStatus(etalon.getStatus())
                    .etalonState(etalon.getApproval())
                    .build();
        }

        // 3. new relation origin, if needed
        if (relationKeys.getOriginId() == null) {

            // Support for old versions. Sysorigin may not be present for from side
            if (relationKeys.getFrom().getOriginKey() == null) {
                OriginKey fromOriginKey
                    = commonComponent
                        .createSystemOriginRecord(relationKeys.getFrom().getEtalonKey().getId(), relationKeys.getFrom().getEntityName());
                from = RecordKeys.builder(relationKeys.getFrom())
                        .originKey(fromOriginKey)
                        .originStatus(RecordStatus.ACTIVE)
                        .build();
                ctx.putToStorage(StorageId.RELATIONS_FROM_KEY, from);
            }

            // Support for old versions. Sysorigin may not be present for to side
            if (relationKeys.getTo().getOriginKey() == null) {
                OriginKey toOriginKey
                    = commonComponent
                        .createSystemOriginRecord(relationKeys.getTo().getEtalonKey().getId(), relationKeys.getTo().getEntityName());
                to = RecordKeys.builder(relationKeys.getTo())
                        .originKey(toOriginKey)
                        .originStatus(RecordStatus.ACTIVE)
                        .build();
                ctx.putToStorage(StorageId.RELATIONS_TO_KEY, to);
            }

            OriginRelationPO system = null;
            OriginRelationPO origin
                = DataRecordUtils.newOriginsRelationPO(ctx, relationKeys.getEtalonId(),
                        relationName, from.getOriginKey().getId(),
                        to.getOriginKey().getId(),
                        from.getOriginKey().getSourceSystem(), RecordStatus.ACTIVE);

            String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
            if (action == UpsertAction.INSERT && !adminSourceSystem.equals(origin.getSourceSystem())) {

                OriginKey fromSystemKey = from.getKeyBySourceSystem(adminSourceSystem);
                OriginKey toSystemKey = to.getKeyBySourceSystem(adminSourceSystem);

                if (fromSystemKey != null && toSystemKey != null) {
                    system
                        = DataRecordUtils.newOriginsRelationPO(ctx, relationKeys.getEtalonId(),
                                relationName, fromSystemKey.getId(),
                                toSystemKey.getId(),
                                adminSourceSystem, RecordStatus.ACTIVE);
                } else {
                    LOGGER.warn("Cannot create system origin relation! Either 'from' or 'to' system key is missing.");
                }
            }

            if (ctx.isBatchUpsert()) {
                RelationBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                batchSet.getOriginRelationInsertPOs().add(origin);
                if (Objects.nonNull(system)) {
                    batchSet.getOriginRelationInsertPOs().add(system);
                }
            } else {
                if (!relationsDao.upsertOriginRelation(origin, true)
                 || (system != null && !relationsDao.upsertOriginRelation(system, true))) {
                    final String message = "Relation origin record upsert failed. Stopping.";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_ORIGIN_FAILED);
                }
            }

            // New origin record, Batch will increment revisions using its own procedure.
            // For all the other puposes 1 should be used.
            relationKeys = RelationKeys.builder(relationKeys)
                    .originId(origin.getId())
                    .originRevision(ctx.isBatchUpsert() ? 0 : 1)
                    .originStatus(origin.getStatus())
                    .originSourceSystem(origin.getSourceSystem())
                    .build();
        }

        ctx.putToStorage(ctx.relationKeysId(), relationKeys);
    }

    /**
     * Possibly reset pending state and keys.
     * @param keys keys
     * @param ctx the context
     * @return keys
     */
    public RelationKeys possiblyResetPendingState(RelationKeys keys, AbstractRelationToRequestContext ctx) {

        boolean resetEtalonKey = !keys.isPending();
        if (resetEtalonKey) {

            changeApproval(keys.getEtalonId(), ApprovalState.PENDING);

            RelationKeys newKeys = RelationKeys.builder(keys)
                    .etalonState(ApprovalState.PENDING)
                    .build();

            ctx.putToStorage(StorageId.RELATIONS_RELATION_KEY, newKeys);
            return newKeys;
        }

        return keys;
    }

    /**
     * Upserts a single relation.
     * @param ctx the context
     * @param relation definition
     */
    public void upsertRelation(UpsertRelationRequestContext ctx, RelationDef relation) {

        MeasurementPoint.start();
        try {

            // 1. Check the context, create records if needed
            ensureRelationUpsertContextBefore(relation.getName(), ctx);

            RelationKeys keys = ctx.relationKeys();

            // 3. Check etalon status, re-enable, if inactive
            if (keys.getEtalonStatus() == RecordStatus.INACTIVE) {

                EtalonRelationPO po
                    = DataRecordUtils.newEtalonRelationPO(ctx, relation.getName(),
                        keys.getFrom().getEtalonKey().getId(),
                        keys.getTo().getEtalonKey().getId(), RecordStatus.ACTIVE);

                po.setId(keys.getEtalonId());

                if (ctx.isBatchUpsert()) {
                    RelationBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                    batchSet.setEtalonRelationUpdatePO(po);
                } else {
                    relationsDao.upsertEtalonRelation(po, false);
                }
            }

            // 4. Check origin status, re-enable, if inactive
            if (keys.getOriginStatus() == RecordStatus.INACTIVE) {

                OriginRelationPO origin
                    = DataRecordUtils.newOriginsRelationPO(ctx, keys.getEtalonId(),
                            relation.getName(),
                            keys.getFrom().getOriginKey().getId(),
                            keys.getTo().getOriginKey().getId(),
                            keys.getFrom().getOriginKey().getSourceSystem(), RecordStatus.ACTIVE);

                origin.setId(keys.getOriginId());

                if (ctx.isBatchUpsert()) {
                    RelationBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                    batchSet.getOriginRelationUpdatePOs().add(origin);
                } else {
                    relationsDao.upsertOriginRelation(origin, false);
                }
            }

            // 5. Check validity period boundaries
            Date from = ctx.getValidFrom();
            Date to = ctx.getValidTo();
            OriginRelation data = relation.getRelType() == RelType.CONTAINS
                    ? null
                    : ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);

            // 6. Put new vistory record
            OriginsVistoryRelationsPO version
                = DataRecordUtils.newRelationsVistoryRecordPO(ctx, keys.getOriginId(), from, to, data,
                        RecordStatus.ACTIVE,
                        DataShift.PRISTINE);

            putVersion(ctx, version);

            // 7. Reset keys, if needed
            if (version.getApproval() == ApprovalState.PENDING) {
                possiblyResetPendingState(keys, ctx);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Load etalon relation's state.
     * @param etalonId the _REATION_ etalon ID
     * @param asOf the date
     * @param includeDraftVersions include draft versions into view or not
     * @return state or null
     */
    public RelationStateDTO loadEtalonRelationState(String etalonId, Date asOf, boolean includeDraftVersions) {

        TimeIntervalPO info = relationsDao.loadRelationEtalonBoundary(etalonId, asOf, includeDraftVersions);
        if (info == null) {
            return null;
        }

        RelationDef relDef = metaModelService.getRelationById(info.getName());
        if (relDef == null) {
            return null;
        }

        RelationStateDTO result = new RelationStateDTO();
        result.setRelationName(relDef.getName());
        result.setRelationType(relDef.getRelType());
        result.setRangeFrom(info.getFrom());
        result.setRangeTo(info.getTo());
        /*
        for (int j = 0; info.getContributors() != null && j < info.getContributors().length; j++) {
            ContributorPO copo = info.getContributors()[j];
            result.getContributors().add(
                    new ContributorDTO(copo.getOriginId(),
                            copo.getRevision(),
                            copo.getSourceSystem(),
                            copo.getStatus() == null ? null : copo.getStatus().toString(),
                            copo.getApproval() == null ? null : copo.getApproval().toString(),
                            copo.getOwner(),
                            copo.getLastUpdate(),
                            info.getName()));
        }
        */
        return result;
    }

    /**
     * Load etalon relation's state.
     * @param etalonId the _RECORD FROM_ etalon ID
     * @param relDef realtion definition
     * @param asOf the date
     * @param includeDraftVersions include draft versions into view or not
     * @return state or null
     */
    public RelationStateDTO loadEtalonRelationState(String etalonId, RelationDef relDef, Date asOf, boolean includeDraftVersions) {

        TimeIntervalPO info = relationsDao.loadRelationsEtalonBoundary(etalonId, relDef.getName(), asOf, includeDraftVersions);
        if (info == null) {
            return null;
        }

        RelationStateDTO result = new RelationStateDTO();
        result.setRelationName(relDef.getName());
        result.setRelationType(relDef.getRelType());
        result.setRangeFrom(info.getFrom());
        result.setRangeTo(info.getTo());
        /*
        for (int j = 0; info.getContributors() != null && j < info.getContributors().length; j++) {
            ContributorPO copo = info.getContributors()[j];
            result.getContributors().add(
                    new ContributorDTO(copo.getOriginId(),
                            copo.getRevision(),
                            copo.getSourceSystem(),
                            copo.getStatus() == null ? null : copo.getStatus().toString(),
                            copo.getApproval() == null ? null : copo.getApproval().toString(),
                            copo.getOwner(),
                            copo.getLastUpdate(),
                            info.getName()));
        }
        */
        return result;
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for a relation etalon ID.
     *
     * @param relationEtalonId the etalon ID of the relation
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public TimelineDTO loadRelationTimeline(String relationEtalonId, boolean includeDrafts, boolean checkPendingState) {
        boolean includeDraftVersions = isIncludeDrafts(relationEtalonId, includeDrafts, checkPendingState);
        List<TimeIntervalPO> ordered = relationsDao.loadContributingRelationTimeline(relationEtalonId, includeDraftVersions);
        return DataRecordUtils.buildTimeline(ordered, relationEtalonId, etalonComposer);
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for a relation etalon ID.
     *
     * @param relationEtalonId the etalon ID of the relation
     * @param asOf as of date
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @param name the name of the relation type
     * @return time line
     */
    public TimelineDTO loadRelationTimeline(String relationEtalonId, Date asOf, boolean includeDrafts, boolean checkPendingState) {
        boolean includeDraftVersions = isIncludeDrafts(relationEtalonId, includeDrafts, checkPendingState);
        List<TimeIntervalPO> ordered = relationsDao.loadContributingRelationTimeline(relationEtalonId, asOf, includeDraftVersions);
        return DataRecordUtils.buildTimeline(ordered, relationEtalonId, etalonComposer);
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for a relation etalon ID.
     *
     * @param relationEtalonId the etalon ID of the relation
     * @param from date from
     * @param to date to
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public TimelineDTO loadRelationTimeline(String relationEtalonId, Date from, Date to, boolean includeDrafts, boolean checkPendingState) {
        boolean includeDraftVersions = isIncludeDrafts(relationEtalonId, includeDrafts, checkPendingState);
        List<TimeIntervalPO> ordered = relationsDao.loadContributingRelationTimeline(relationEtalonId, from, to, includeDraftVersions);
        return DataRecordUtils.buildTimeline(ordered, relationEtalonId, etalonComposer);
    }

    private boolean isIncludeDrafts(String relationEtalonId, boolean includeDrafts, boolean checkPendingState) {
        boolean includeDraftVersions = includeDrafts;
        if (!includeDraftVersions && checkPendingState) {
            if (SecurityUtils.isAdminUser()) {
                includeDraftVersions = true;
            } else {

                GetRelationRequestContext ctx = new GetRelationRequestContextBuilder().relationEtalonKey(
                        relationEtalonId).build();

                RelationKeys keys = identify(ctx);
                includeDraftVersions = keys != null && keys.getFrom() != null && workflowService != null ?
                        workflowService.hasEditTasks(keys.getFrom().getEtalonKey().getId()) :
                        false;
            }
        }
        return includeDraftVersions;
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time lines for _all_ active relations
     * for an etalon ID.
     *
     * @param etalonId the etalon ID of the 'from' end
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time lines or empty map
     */
    public Map<RelationDef, List<TimelineDTO>>
        loadCompleteRelationsTimelineByFromSide(String etalonId, boolean includeDrafts, boolean checkPendingState) {
        return loadCompleteRelationsTimeline(etalonId, true, includeDrafts, checkPendingState);
    }

    /**
     * Loads (calculates) contributing relations ('from' participants) time lines for _all_ active relations
     * for an etalon ID.
     *
     * @param etalonId the etalon ID of the 'to' end
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time lines or empty map
     */
    public Map<RelationDef, List<TimelineDTO>>
        loadCompleteRelationsTimelineByToSide(String etalonId, boolean includeDrafts, boolean checkPendingState) {
        return loadCompleteRelationsTimeline(etalonId, false, includeDrafts, checkPendingState);
    }

    /**
     * Loads timeline respective to given side.
     * @param etalonId the id
     * @param byFromSide whether to load by from side (true), or to side
     * @param includeDrafts include draft versions or not
     * @param checkPendingState check pending state or not
     * @return timeline
     */
    private Map<RelationDef, List<TimelineDTO>>
        loadCompleteRelationsTimeline(String etalonId, boolean byFromSide, boolean includeDrafts, boolean checkPendingState) {

        boolean includeDraftVersions = includeDrafts;
        if (!includeDraftVersions && checkPendingState) {
            includeDraftVersions = SecurityUtils.isAdminUser() || workflowService.hasEditTasks(etalonId);
        }

        Map<String, Map<String, List<TimeIntervalPO>>> intervals = byFromSide
            ? relationsDao.loadCompleteContributingRelationsTimelineByFromSide(etalonId, includeDraftVersions)
            : relationsDao.loadCompleteContributingRelationsTimelineByToSide(etalonId, includeDraftVersions);

        if (MapUtils.isEmpty(intervals)) {
            return Collections.emptyMap();
        }

        Map<RelationDef, List<TimelineDTO>> result = new HashMap<>();
        for (Entry<String, Map<String, List<TimeIntervalPO>>> oe : intervals.entrySet()) {

            RelationDef def = metaModelService.getRelationById(oe.getKey());
            if (Objects.isNull(def)) {
                continue;
            }

            for (Entry<String, List<TimeIntervalPO>> ie : oe.getValue().entrySet()) {
                result.computeIfAbsent(def, key -> new ArrayList<>())
                      .add(DataRecordUtils.buildTimeline(ie.getValue(), ie.getKey(), etalonComposer));
            }
        }

        return result;
    }
    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for an etalon ID.
     *
     * @param etalonId the etalon ID of the 'from' end
     * @param name the name of the relation type
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public List<TimelineDTO> loadRelationsTimeline(String etalonId, String name, boolean includeDrafts, boolean checkPendingState) {

        boolean includeDraftVersions = includeDrafts;
        if (!includeDraftVersions && checkPendingState) {
            includeDraftVersions = SecurityUtils.isAdminUser() || workflowService.hasEditTasks(etalonId);
        }

        List<TimelineDTO> result = new ArrayList<>();
        Map<String, List<TimeIntervalPO>> ordered = relationsDao.loadContributingRelationsTimeline(etalonId, name, includeDraftVersions);
        for (Entry<String, List<TimeIntervalPO>> entry : ordered.entrySet()) {
            result.add(DataRecordUtils.buildTimeline(entry.getValue(), entry.getKey(), etalonComposer));
        }

        return result;
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for an etalon ID.
     *
     * @param etalonId the etalon ID of the 'from' end
     * @param name the name of the relation type
     * @param asOf as of date
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public List<TimelineDTO> loadRelationsTimeline(String etalonId, String name, Date asOf, boolean includeDrafts, boolean checkPendingState) {

        boolean includeDraftVersions = includeDrafts;
        if (!includeDraftVersions && checkPendingState) {
            includeDraftVersions = SecurityUtils.isAdminUser() || workflowService.hasEditTasks(etalonId);
        }

        List<TimelineDTO> result = new ArrayList<>();
        Map<String, List<TimeIntervalPO>> ordered = relationsDao.loadContributingRelationsTimeline(etalonId, name, asOf, includeDraftVersions);
        for (Entry<String, List<TimeIntervalPO>> entry : ordered.entrySet()) {
            result.add(DataRecordUtils.buildTimeline(entry.getValue(), entry.getKey(), etalonComposer));
        }

        return result;
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for an etalon ID.
     *
     * @param etalonId the etalon ID of the 'from' end
     * @param name the name of the relation type
     * @param from date from
     * @param to date to
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public List<TimelineDTO> loadRelationsTimeline(String etalonId, String name, Date from, Date to, boolean includeDrafts, boolean checkPendingState) {

        boolean includeDraftVersions = includeDrafts;
        if (!includeDraftVersions && checkPendingState) {
            includeDraftVersions = SecurityUtils.isAdminUser() || workflowService.hasEditTasks(etalonId);
        }

        List<TimelineDTO> result = new ArrayList<>();
        Map<String, List<TimeIntervalPO>> ordered
            = relationsDao.loadContributingRelationsTimeline(etalonId, name, from, to, includeDraftVersions);
        for (Entry<String, List<TimeIntervalPO>> entry : ordered.entrySet()) {
            result.add(DataRecordUtils.buildTimeline(entry.getValue(), entry.getKey(), etalonComposer));
        }

        return result;
    }

    /**
     * Deletes a relation.
     * @param ctx the context
     * @param relation relation definition
     * @return result
     */
    public DeleteRelationDTO deleteRelation(DeleteRelationRequestContext ctx, RelationDef relation) {

        MeasurementPoint.start();
        try {

            RelationKeys keys = ctx.relationKeys();

            boolean success = false;
            if (ctx.isWipe()) {
                List<OriginRelationPO> origins
                    = relationsDao.loadOriginRelationsByEtalonId(ctx.getRelationEtalonKey());
                for (OriginRelationPO po : origins) {
                    relationsDao.wipeOriginRecord(po.getId());
                }

                success = relationsDao.wipeEtalonRecord(ctx.getRelationEtalonKey());
            } else {

                WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);
                ApprovalState state = !ctx.isInactivatePeriod()
                        ? DataRecordUtils.calculateRecordState(ctx, assignment)
                        : DataRecordUtils.calculateVersionState(ctx, keys.getFrom(), assignment);

                // 2. Relation etalon ID. Deactivate etalon and origins
                //    or Keys present (found by from <-> to). Deactivate etalon and origins.
                if (ctx.isInactivateEtalon()) {

                    if (ctx.isBatchUpsert()) {

                        RelationBatchSet set = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                        EtalonRelationPO epo = relationsDao.loadEtalonRelation(keys.getEtalonId());
                        success = epo != null;
                        if (success) {

                            Date ts = new Date(System.currentTimeMillis());
                            String user = SecurityUtils.getCurrentUserName();
                            List<OriginRelationPO> ors = relationsDao.loadOriginRelationsByEtalonId(epo.getId());
                            for (OriginRelationPO opo : ors) {

                                opo.setStatus(RecordStatus.INACTIVE);
                                opo.setUpdateDate(ts);
                                opo.setUpdatedBy(user);

                                set.getOriginRelationUpdatePOs().add(opo);
                            }

                            epo.setStatus(RecordStatus.INACTIVE);
                            epo.setApproval(ApprovalState.APPROVED);
                            epo.setUpdateDate(ts);
                            epo.setUpdatedBy(user);

                            set.setEtalonRelationUpdatePO(epo);
                        }
                    } else {
                        success = relationsDao.deactivateRelationByEtalonId(keys.getEtalonId(), state);

                        if (state == ApprovalState.PENDING) {
                            possiblyResetPendingState(keys, ctx);
                        }
                    }

                // 3. Relation origin ID. Deactivate relation origin only
                } else if (ctx.isInactivateOrigin()) {
                    success = relationsDao.deactivateRelationByOriginId(keys.getOriginId());
                // 4. Inactivate period
                } else if (ctx.isInactivatePeriod()) {

                    OriginsVistoryRelationsPO version
                        = DataRecordUtils.createInactiveRelationsVistoryRecordPO(
                            keys.getOriginId(), ctx.getOperationId(),
                            ctx.getValidFrom(),
                            ctx.getValidTo(), state);

                    success = putVersion(ctx, version);
                    if (success && !ctx.isBatchUpsert()) {
                        // 4.1. Check for having active periods
                        boolean hasActivePeriods
                            = hasActivePeriodsFromPerspective(keys.getEtalonId());
                        if (!hasActivePeriods) {
                            success = relationsDao.deactivateRelationByEtalonId(
                                keys.getEtalonId(),
                                    DataRecordUtils.calculateRecordState(ctx, assignment));
                        }
                    }

                    if (state == ApprovalState.PENDING) {
                        possiblyResetPendingState(keys, ctx);
                    }
                }
            }

            if (success) {
                return new DeleteRelationDTO(keys, relation.getName(), RelationType.valueOf(relation.getRelType().name()));
            }

            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Puts a version to DB.
     * @param ctx the context
     * @param version the version to put
     * @return true, if successful, false otherwise
     */
    public boolean putVersion(AbstractRelationToRequestContext ctx, OriginsVistoryRelationsPO version) {

        if (Objects.isNull(version)) {
            return false;
        }

        boolean isUpsert = ctx instanceof UpsertRelationRequestContext;
        boolean isBatch = false;

        if (isUpsert) {
            isBatch = ((UpsertRelationRequestContext) ctx).isBatchUpsert();
        } else {
            isBatch = ((DeleteRelationRequestContext) ctx).isBatchUpsert();
        }

        if (isBatch) {
            RelationBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
            batchSet.getOriginsVistoryRelationsPOs().add(version);
        } else {
            relationsDao.putVersion(version);
        }

        return true;
    }
    /**
     * Checks whether time lines of a relation for a 'from' etalon id have an active period.
     * @param etalonId 'from' record etalon id
     * @return true, if there are active periods, false otherwise
     */
    public boolean hasActivePeriodsFromPerspective(String etalonId) {

        TimelineDTO timeline = loadRelationTimeline(etalonId, true, false);
        for (TimeIntervalDTO period : timeline.getIntervals()) {
            if (period.isActive()) {
                return true;
            }
        }

        return false;
    }
    /**
     * Returns true, if the time line has pending versions in one of the denoted periods.
     * TODO add DB function returning only pending periods.
     * @param etalonId the etalon id
     * @return true, if so, false otherwise
     */
    public boolean hasPendingVersions(String etalonId) {

        List<TimeIntervalPO> timeline =
                relationsDao.loadContributingRelationTimeline(etalonId, true);

        for (int i = 0; timeline != null && i < timeline.size(); i++) {
            TimeIntervalPO ti = timeline.get(i);
            for (int j = 0; ti.getContributors() != null && j < ti.getContributors().length; j++) {

                ContributorPO co = ti.getContributors()[j];
                if (co.getApproval() == ApprovalState.PENDING) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Changes etalon state.
     * @param etalonId the etalon id
     * @param state the state
     * @return true, if successful, false otherwise
     */
    @Transactional
    public boolean changeApproval(String etalonId, ApprovalState state) {
        return relationsDao.changeEtalonApproval(etalonId, state);
    }

    /**
     * Loads info section.
     * @param keys relation etalon id
     * @param asOf date
     * @param includeDraftVersions include draft versions into view or not
     * @param def relation definition
     * @return info section or null
     */
    public EtalonRelationInfoSection loadEtalonRelationInfoSection(RelationKeys keys, Date asOf, boolean includeDraftVersions, RelationDef def) {

        TimeIntervalPO info = relationsDao.loadRelationEtalonBoundary(keys.getEtalonId(), asOf, includeDraftVersions);
        if (info == null) {
            return null;
        }

        return new EtalonRelationInfoSection()
                .withCreateDate(info.getCreateDate())
                .withUpdateDate(info.getUpdateDate())
                .withCreatedBy(info.getCreatedBy())
                .withUpdatedBy(info.getUpdatedBy())
                .withStatus(keys.getEtalonStatus())
                .withApproval(keys.getEtalonState())
                .withPeriodId(info.getPeriodId())
                .withValidFrom(info.getFrom())
                .withValidTo(info.getTo())
                .withRelationEtalonKey(keys.getEtalonId())
                .withRelationName(keys.getRelationName())
                .withFromEtalonKey(keys.getFrom().getEtalonKey())
                .withFromEntityName(keys.getFrom().getEntityName())
                .withToEtalonKey(keys.getTo().getEtalonKey())
                .withToEntityName(keys.getTo().getEntityName())
                .withType(RelationType.valueOf(def.getRelType().name()));
    }

    /**
     * Fills calculated fields for a vistory record.
     * @param ovr the vistory record
     * @param def relation definition
     */
    public OriginRelationInfoSection loadOriginRelationInfoSection(OriginsVistoryRelationsPO ovr, RelationDef def) {

        MeasurementPoint.start();
        try {

            return new OriginRelationInfoSection()
                .withValidFrom(ovr.getValidFrom())
                .withValidTo(ovr.getValidTo())
                .withCreateDate(ovr.getCreateDate())
                .withUpdateDate(ovr.getUpdateDate())
                .withCreatedBy(ovr.getCreatedBy())
                .withUpdatedBy(ovr.getUpdatedBy())
                .withRevision(ovr.getRevision())
                .withStatus(ovr.getStatus())
                .withApproval(ovr.getApproval())
                .withMajor(ovr.getMajor())
                .withMinor(ovr.getMinor())
                .withRelationSourceSystem(ovr.getSourceSystem())
                .withRelationName(ovr.getName())
                .withRelationOriginKey(ovr.getOriginId())
                .withType(RelationType.valueOf(def.getRelType().name()))
                .withFromOriginKey(OriginKey.builder()
                        .entityName(ovr.getOriginFromName())
                        .externalId(ovr.getOriginFromExternalId())
                        .sourceSystem(ovr.getOriginFromSourceSystem())
                        .id(ovr.getOriginIdFrom())
                        .build())
                .withFromEntityName(def.getFromEntity())
                .withToOriginKey(
                    OriginKey.builder()
                        .id(ovr.getOriginIdTo())
                        .entityName(ovr.getOriginToName())
                        .externalId(ovr.getOriginToExternalId())
                        .sourceSystem(ovr.getOriginToSourceSystem())
                        .build())
                .withToEntityName(def.getToEntity());

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Mark all relation as inactive for relation name
     * @param relationName - relation name
     */
    public void deactivateRelationsByName(String relationName) {
        relationsDao.deactivateRelationsByName(relationName);
    }
}

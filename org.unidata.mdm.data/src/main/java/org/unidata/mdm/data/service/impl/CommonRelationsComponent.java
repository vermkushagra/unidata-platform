package org.unidata.mdm.data.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.change.ChangeSet;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.AbstractTimeInterval;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.AbstractRelationToRequestContext;
import org.unidata.mdm.data.context.AbstractRelationsFromRequestContext;
import org.unidata.mdm.data.context.GetRelationTimelineRequestContext;
import org.unidata.mdm.data.context.GetRelationsTimelineRequestContext;
import org.unidata.mdm.data.context.ReadOnlyTimelineContext;
import org.unidata.mdm.data.context.ReadWriteTimelineContext;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.context.RelationIdentityContext;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.dao.RelationsDao;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.po.data.RelationTimelinePO;
import org.unidata.mdm.data.po.keys.RecordOriginKeyPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;
import org.unidata.mdm.data.po.keys.RelationOriginKeyPO;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSet;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSet;
import org.unidata.mdm.data.type.calculables.impl.RelationRecordHolder;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.EtalonRelationInfoSection;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.OriginRelationInfoSection;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.impl.EtalonRelationImpl;
import org.unidata.mdm.data.type.data.impl.OriginRelationImpl;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.type.keys.RelationOriginKey;
import org.unidata.mdm.data.type.timeline.RelationTimeInterval;
import org.unidata.mdm.data.type.timeline.RelationTimeline;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

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
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Relations vistory DAO.
     */
    @Autowired
    private RelationsDao relationsDao;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Etalon composer.
     */
// @Modules Moved to commercial part
//    @Autowired
//    private WorkflowServiceExt workflowService;
    /**
     * The composer.
     */
    @Autowired
    private RelationComposerComponent relationComposerComponent;
    /**
     * Constructor.
     */
    public CommonRelationsComponent() {
        super();
    }

    public RelationKeys ensureKeys(RelationIdentityContext ctx) {

        RelationKeys relationKeys = ensureAndGetRelationKeys((AbstractRelationToRequestContext) ctx);
        if (relationKeys == null) {
            final String message = "Relation keys can not be resolved!";
            LOGGER.warn(message);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_RELATION_CONTEXT_NO_IDENTITY);
        }

        return relationKeys;
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
     * @param ctx context
     * @return keys or null
     */
    public RelationKeys ensureAndGetRelationKeys(AbstractRelationToRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        if (keys == null) {

            // 1. Try relation identity first
            if (ctx.isValidRelationKey()) {
                keys = identify(ctx);
            }

            // 2. Try sides secondly
            if (keys == null) {

                // 2.1 From key must already be resolved, if defined. Just check for presence
                RecordKeys from = ctx.fromKeys();
                RecordKeys to = ctx.keys();

                if (from == null) {
                    return null;
                }

                if (to == null) {

                    to = identifySide(ctx);
                    if (to == null) {
                        return null;
                    }

                    ctx.keys(to);
                }

                // 2.2. Skip pointless keys resolution upon initial load.
                // May quite have an impact on millions of records
                boolean emptyStorage = ctx instanceof UpsertRelationRequestContext && ((UpsertRelationRequestContext) ctx).isEmptyStorage();
                if (!emptyStorage) {
                    keys = identify(ctx.relationName(), from, to);
                }
            }

            if (keys != null) {
                ctx.relationKeys(keys);
            }
        }

        return keys;
    }

    @SuppressWarnings("unchecked")
    public Timeline<OriginRelation> ensureAndGetRelationTimeline(AbstractRelationToRequestContext ctx) {

        Timeline<OriginRelation> t = null;
        if (ctx instanceof ReadOnlyTimelineContext) {

            t = ((ReadOnlyTimelineContext<OriginRelation>) ctx).currentTimeline();
            if (Objects.nonNull(t)) {
                return t;
            }
        }

        if (ctx.isValidRelationKey()) {

            t = loadTimeline(GetRelationTimelineRequestContext.builder()
                    .relationEtalonKey(ctx.getRelationEtalonKey())
                    .relationLsn(ctx.getLsnAsObject())
                    .build());
        } else {

            RelationKeys keys = ensureAndGetRelationKeys(ctx);
            if (Objects.nonNull(keys)) {
                t = loadTimeline(GetRelationTimelineRequestContext.builder()
                        .relationEtalonKey(keys.getEtalonKey().getId())
                        .relationLsn(keys.getEtalonKey().getLsn())
                        .relationShard(keys.getShard())
                        .build());
            }
        }

        if (Objects.nonNull(t)) {

            if (ctx instanceof ReadOnlyTimelineContext) {
                ((ReadOnlyTimelineContext<OriginRelation>) ctx).currentTimeline(t);
            }

            if (Objects.isNull(ctx.relationKeys())) {
                ctx.relationKeys(t.getKeys());
            }
        }

        return t;
    }

    /**
     * Identify by relation keys.
     * @param ctx the context
     * @return keys
     */
    public RelationKeys identify(RelationIdentityContext ctx) {
        MeasurementPoint.start();
        try {

            RelationKeys keys = null;
            if (ctx.isRelationLsnKey()) {
                RelationKeysPO po = relationsDao.loadKeysByLSN(ctx.getShard(), ctx.getLsn());
                keys = relationComposerComponent.toRelationKeys(po, lsnKeyPredicate());
            }

            if (keys == null && ctx.isRelationEtalonKey()) {
                RelationKeysPO po = relationsDao.loadKeysByEtalonId(UUID.fromString(ctx.getRelationEtalonKey()));
                keys = relationComposerComponent.toRelationKeys(po, etalonKeyPredicate());
            }

            if (Objects.nonNull(keys)) {
                ctx.relationKeys(keys);
            }

            return keys;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Resolves keys by sides.
     * @param name relation name
     * @param from the from side
     * @param to the to side
     * @return keys or null
     */
    public RelationKeys identify(String name, AbstractRelationsFromRequestContext<?> from, AbstractRelationToRequestContext to) {
        MeasurementPoint.start();
        try {

            if (from == null || to == null) {
                return null;
            }

            RelationKeys keys = null;
            if (to.isValidRelationKey()) {
                keys = identify(to);
            }

            if (keys == null && from.isEtalonRecordKey() && to.isEtalonRecordKey()) {
                RelationKeysPO po = relationsDao.loadKeysByRecordsEtalonIds(UUID.fromString(from.getEtalonKey()), UUID.fromString(to.getEtalonKey()), name);
                keys = relationComposerComponent.toRelationKeys(po, etalonKeysPredicate());
            } else if (keys == null && (from.isLsnKey() && to.isLsnKey())) {
                RelationKeysPO po = relationsDao.loadKeysByRecordsLSNs(from.getShard(), from.getLsn(), to.getShard(), to.getLsn(), name);
                keys = relationComposerComponent.toRelationKeys(po, lsnKeysPredicate());
            } else if (keys == null && (from.isOriginExternalId() && to.isOriginExternalId())) {
                RelationKeysPO po = relationsDao.loadKeysByRecordsExternalIds(from.getExternalIdAsObject(), to.getExternalIdAsObject(), name);
                keys = relationComposerComponent.toRelationKeys(po, externalIdsPredicate(
                        from.getExternalId(), from.getSourceSystem(),
                        to.getExternalId(), to.getSourceSystem()));
            }

            if (Objects.nonNull(keys)) {
                to.relationKeys(keys);
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

            RelationKeys keys = null;
            if ((from.getEtalonKey() != null && from.getEtalonKey().getId() != null)
             && (to.getEtalonKey() != null && to.getEtalonKey().getId() != null)) {
                RelationKeysPO po = relationsDao.loadKeysByRecordsEtalonIds(UUID.fromString(from.getEtalonKey().getId()), UUID.fromString(to.getEtalonKey().getId()), name);
                keys = relationComposerComponent.toRelationKeys(po, etalonKeysPredicate());
            } else if (
                (from.getEtalonKey() != null && from.getEtalonKey().getLsn() != null)
             && (to.getEtalonKey() != null && to.getEtalonKey().getLsn() != null)) {
                RelationKeysPO po = relationsDao.loadKeysByRecordsLSNs(
                        from.getShard(), from.getEtalonKey().getLsn(),
                        to.getShard(), to.getEtalonKey().getLsn(),
                        name);
                keys = relationComposerComponent.toRelationKeys(po, lsnKeysPredicate());
            } else if (
                (from.getOriginKey() != null && StringUtils.isNoneBlank(
                     from.getOriginKey().getExternalId(),
                     from.getOriginKey().getEntityName(),
                     from.getOriginKey().getSourceSystem()))
             && (to.getOriginKey() != null) && StringUtils.isNoneBlank(
                     to.getOriginKey().getExternalId(),
                     to.getOriginKey().getEntityName(),
                     to.getOriginKey().getSourceSystem())) {
                RelationKeysPO po = relationsDao.loadKeysByRecordsExternalIds(
                        from.getOriginKey().toExternalId(), to.getOriginKey().toExternalId(), name);
                keys = relationComposerComponent.toRelationKeys(po, externalIdsPredicate(
                        from.getOriginKey().getExternalId(), from.getOriginKey().getSourceSystem(),
                        to.getOriginKey().getExternalId(), to.getOriginKey().getSourceSystem()));
            }

            return keys;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Identifies side, using cached keys.
     * @param ctx the context
     * @return keys
     */
    private RecordKeys identifySide(RecordIdentityContext ctx) {

        MeasurementPoint.start();
        try {

            RecordKeys side = commonRecordsComponent.identify(ctx);
            if (side != null) {
                ctx.keys(side);
            }

            return side;
        } finally {
            MeasurementPoint.stop();
        }
    }

    public BiPredicate<RelationKeysPO, RelationOriginKeyPO> etalonKeyPredicate() {
        return (po, okpo) -> StringUtils.equals(okpo.getSourceSystem(), metaModelService.getAdminSourceSystem().getName())
               && okpo.getInitialOwner().equals(UUID.fromString(po.getId())) && !okpo.isEnrichment();
    }

    public BiPredicate<RelationKeysPO, RelationOriginKeyPO> lsnKeyPredicate() {
        return etalonKeyPredicate();
    }

    public BiPredicate<RelationKeysPO, RelationOriginKeyPO> etalonKeysPredicate() {
        return etalonKeyPredicate();
            }

    public BiPredicate<RelationKeysPO, RelationOriginKeyPO> lsnKeysPredicate() {
        return etalonKeyPredicate();
            }

    public BiPredicate<RelationKeysPO, RelationOriginKeyPO> externalIdsPredicate(
            String fromExternalId, String fromSourceSystem,
            String toExternalId, String toSourceSystem) {
        return (po, okpo) -> {

            RecordOriginKeyPO from = Objects.isNull(po.getFromKeys())
                    ? null
                    : po.getFromKeys().findByExternalId(fromExternalId, fromSourceSystem);

            RecordOriginKeyPO to = Objects.isNull(po.getToKeys())
                    ? null
                    : po.getToKeys().findByExternalId(toExternalId, toSourceSystem);

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                return from.getId().equals(okpo.getFromKey()) && to.getId().equals(okpo.getToKey());
        }

            return false;
        };
    }

    /**
     * Loads timeline respective to given side.
     * @param ctx the context
     * @return timelines map
     */
    public Map<String, List<Timeline<OriginRelation>>> loadTimelines(GetRelationsTimelineRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Ensure keys
            String recordEtalonId = null;
            if (ctx.isEtalonRecordKey()) {
                recordEtalonId = ctx.getEtalonKey();
            } else {
                RecordKeys recordKeys = commonRecordsComponent.ensureKeys(ctx);
                recordEtalonId = recordKeys.getEtalonKey().getId();
            }

            // 2. Set fields
            boolean includeDrafts = ctx.isIncludeDrafts() || SecurityUtils.isAdminUser();
// @Modules Moved to commercial part
//            if (!includeDrafts && ctx.isTasks()) {
//                includeDrafts = SecurityUtils.isAdminUser() || workflowService.hasEditTasks(recordEtalonId);
//            }

            // 3. Load data
            List<RelationTimelinePO> tls
                = relationsDao.loadTimelines(
                        UUID.fromString(recordEtalonId), ctx.getRelationNames(), ctx.isFetchByToSide(), ctx.isFetchData(), includeDrafts);

            if (CollectionUtils.isEmpty(tls)) {
                return Collections.emptyMap();
            }

            // 4. Extract data
            Map<String, List<Timeline<OriginRelation>>> result = new HashMap<>();
            for (RelationTimelinePO po : tls) {

                if (Objects.isNull(po) || Objects.isNull(po.getKeys())) {
                    continue;
                }

                RelationKeys keys = relationComposerComponent.toRelationKeys(po.getKeys(), etalonKeyPredicate());
                Timeline<OriginRelation> timeline = relationComposerComponent.toRelationTimeline(keys, po.getVistory());

                // 4.1. Possibly reduce TL by given boundaries.
                // Maybe a separate, more efficient request will be written later on.
                if (!ctx.isReduceReferences() || ((RelationKeys) timeline.getKeys()).getRelationType() != RelationType.REFERENCES) {
                    if (Objects.nonNull(ctx.getForDatesFrame())) {
                        timeline = timeline.reduceBy(ctx.getForDatesFrame().getLeft(), ctx.getForDatesFrame().getRight());
                    } else if (Objects.nonNull(ctx.getForDate())) {
                        timeline = timeline.reduceAsOf(ctx.getForDate());
                    }
                }

                // 4.2 Calc suff, if not disabled
                RelationKeys rk = timeline.getKeys();
                if (!ctx.isSkipCalculations()) {
                    timeline.forEach(ti -> {

                        List<CalculableHolder<OriginRelation>> versions = ti.toList();

                        ti.setActive(relationComposerComponent.isActive(versions));
                        ti.setPending(relationComposerComponent.isPending(versions));

                        if (ctx.isFetchData()) {
                            ti.setCalculationResult(relationComposerComponent.toEtalon(rk, versions,
                                    ti.getValidFrom(), ti.getValidTo(), true, false));
                        }
                    });
                }

                result.computeIfAbsent(keys.getRelationName(), key -> new ArrayList<>())
                	.add(timeline);
            }

            // Possibly reduce timeline for Reference type
            if (ctx.isReduceReferences()) {

                for (Entry<String, List<Timeline<OriginRelation>>> entry : result.entrySet()) {

                    if (entry.getValue().isEmpty()
                    || ((RelationKeys) entry.getValue().get(0).getKeys()).getRelationType() != RelationType.REFERENCES) {
                        continue;
                    }

                    List<Timeline<OriginRelation>> calculated = buildVirtualTimelinesForReferences(entry.getValue());
                    calculated = calculated.stream().map(timeline -> {
                        if (Objects.nonNull(ctx.getForDatesFrame())) {
                            return timeline.reduceBy(ctx.getForDatesFrame().getLeft(), ctx.getForDatesFrame().getRight());
                        } else if (Objects.nonNull(ctx.getForDate())) {
                            return timeline.reduceAsOf(ctx.getForDate());
                        } else return timeline;
                    }).collect(Collectors.toList());

                    entry.getValue().clear();
                    entry.getValue().addAll(calculated);
                }
            }

            return result;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for an etalon ID.
     *
     * @param ctx the context
     * @return timeline
     */
    public Timeline<OriginRelation> loadTimeline(GetRelationTimelineRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RelationTimelinePO po = null;

            // 1. Ensure keys
            RelationKeys keys = ctx.relationKeys();
            boolean includeDrafts = ctx.isIncludeDrafts() || SecurityUtils.isAdminUser();
// @Modules Moved to commercial part
//            if (Objects.isNull(keys) && !includeDrafts && ctx.isTasks()) {
//                // 1.1. Load keys, because we going to use them later for WF
//                // service call.
//                keys = ensureKeys(ctx);
//            }

            // 2. Set fields
// @Modules Moved to commercial part
//            if (Objects.nonNull(keys) && !includeDrafts && ctx.isTasks()) {
//                includeDrafts = SecurityUtils.isAdminUser()
//                        || workflowService.hasEditTasks(keys.getEtalonKey().getFrom().getId());
//            }

            // 3. Load TL
            if (Objects.nonNull(keys)) {
                // Fetch with keys always, otherwise CONTAINMENTs cannot be build
                po = relationsDao.loadTimeline(UUID.fromString(keys.getEtalonKey().getId()), true, ctx.isFetchData(), includeDrafts);
            } else if (ctx.isRelationLsnKey()) {
                po = relationsDao.loadTimeline(ctx.getLsnAsObject(), true, ctx.isFetchData(), includeDrafts);
                if (Objects.nonNull(po)) {
                    keys = relationComposerComponent.toRelationKeys(po.getKeys(), lsnKeyPredicate());
                }
            } else if (ctx.isRelationEtalonKey()) {
                po = relationsDao.loadTimeline(UUID.fromString(ctx.getRelationEtalonKey()), true, ctx.isFetchData(), includeDrafts);
                if (Objects.nonNull(po)) {
                    keys = relationComposerComponent.toRelationKeys(po.getKeys(), etalonKeyPredicate());
                }
            }

            // 4. Translate to internal
            Timeline<OriginRelation> timeline = relationComposerComponent.toRelationTimeline(keys, po == null ? null : po.getVistory());

            // 4.1. Possibly reduce TL by given boundaries.
            // Maybe a separate, more efficient request will be written later on.
            if (Objects.nonNull(ctx.getForDatesFrame())) {
                timeline = timeline.reduceBy(ctx.getForDatesFrame().getLeft(), ctx.getForDatesFrame().getRight());
            } else if (Objects.nonNull(ctx.getForDate())) {
                timeline = timeline.reduceAsOf(ctx.getForDate());
            }

            // 4.2 Calc suff, if not disabled
            RelationKeys rk = timeline.getKeys();
            if (!ctx.isSkipCalculations()) {
                timeline.forEach(ti -> {

                    List<CalculableHolder<OriginRelation>> calculables = ti.toList();

                    ti.setActive(relationComposerComponent.isActive(calculables));
                    ti.setPending(relationComposerComponent.isPending(calculables));

                    if (ctx.isFetchData()) {
                        ti.setCalculationResult(relationComposerComponent.toEtalon(rk, calculables,
                                ti.getValidFrom(), ti.getValidTo(), true, false));
                    }
                });
            }

            return timeline;

        } finally {
            MeasurementPoint.stop();
        }
    }

    public Timeline<OriginRelation> loadInterval(GetRelationTimelineRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RelationTimelinePO po = null;

            // 1. Ensure keys
            RelationKeys keys = ctx.relationKeys();
            boolean includeDrafts = ctx.isIncludeDrafts() || SecurityUtils.isAdminUser();

            // 3. Load Interval
            if (Objects.nonNull(keys)) {
                // Fetch with keys always, otherwise CONTAINMENTs cannot be build
                if (Objects.nonNull(ctx.getForOperationId())) {
                    po = relationsDao.loadRelationVersions(UUID.fromString(keys.getEtalonKey().getId()), ctx.getForDate(), ctx.getForOperationId(), includeDrafts);
                } else {
                    po = relationsDao.loadRelationVersions(UUID.fromString(keys.getEtalonKey().getId()), ctx.getForDate(), includeDrafts);
                }
            } else if (ctx.isRelationLsnKey()) {

                if (Objects.nonNull(ctx.getForOperationId())) {
                    po = relationsDao.loadRelationVersions(ctx.getLsnAsObject(), ctx.getForDate(), ctx.getForOperationId(), includeDrafts);
                } else {
                    po = relationsDao.loadRelationVersions(ctx.getLsnAsObject(), ctx.getForDate(), includeDrafts);
                }

                if (Objects.nonNull(po)) {
                    keys = relationComposerComponent.toRelationKeys(po.getKeys(), lsnKeyPredicate());
                }
            } else if (ctx.isRelationEtalonKey()) {

                if (Objects.nonNull(ctx.getForOperationId())) {
                    po = relationsDao.loadRelationVersions(UUID.fromString(ctx.getRelationEtalonKey()), ctx.getForDate(), ctx.getForOperationId(), includeDrafts);
                } else {
                    po = relationsDao.loadRelationVersions(UUID.fromString(ctx.getRelationEtalonKey()), ctx.getForDate(), includeDrafts);
                }

                if (Objects.nonNull(po)) {
                    keys = relationComposerComponent.toRelationKeys(po.getKeys(), etalonKeyPredicate());
                }
            }

            // 4. Translate to internal
            Timeline<OriginRelation> timeline = relationComposerComponent.toRelationTimeline(keys, po == null ? null : po.getVistory());

            // 4.1 Reduce TL by given boundaries.
            // Maybe a separate, more efficient request will be written later on.
            timeline = timeline.reduceAsOf(ctx.getForDate());

            // 4.2 Calc suff, if not disabled
            RelationKeys rk = timeline.getKeys();
            if (!ctx.isSkipCalculations()) {
                timeline.forEach(ti -> {

                    List<CalculableHolder<OriginRelation>> calculables = ti.toList();

                    ti.setActive(relationComposerComponent.isActive(calculables));
                    ti.setPending(relationComposerComponent.isPending(calculables));

                    if (ctx.isFetchData()) {
                        ti.setCalculationResult(relationComposerComponent.toEtalon(rk, calculables,
                                ti.getValidFrom(), ti.getValidTo(), true, false));
                    }
                });
            }

            return timeline;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Checks whether time lines of a relation for a 'from' etalon id have an active period.
     * @param etalonId 'from' record etalon id
     * @return true, if there are active periods, false otherwise
     */
    public boolean hasActivePeriodsFromPerspective(RelationKeys key) {

        GetRelationTimelineRequestContext ctx = GetRelationTimelineRequestContext.builder()
                .includeDrafts(true)
                .build();

        ctx.relationKeys(key);

        Timeline<OriginRelation> timeline = loadTimeline(ctx);
        for (TimeInterval<OriginRelation> period : timeline) {
            if (relationComposerComponent.isActive(period.toList())) {
                return true;
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
        return relationsDao.changeEtalonApproval(Collections.singletonList(UUID.fromString(etalonId)), state);
    }

    /**
     * Mark all relation as inactive for relation name
     * @param relationName - relation name
     */
    public void deactivateRelationsByName(String relationName) {
        // FIXME: This will not work for partitioned/sharded DB.
        // relationsDao.deactivateRelationsByName(relationName);
        throw new NotImplementedException("Deactivate relations by name is not implemented! A job must be written for that.");
    }

    @SuppressWarnings("rawtypes")
    public List<Timeline<OriginRelation>> loadOrReuseCachedTimelines(@Nonnull RelationIdentityContext ctx) {

        if (ctx.relationType() != RelationType.REFERENCES) {
            return Collections.emptyList();
        }

        RelationKeys keys = ctx.relationKeys();

        // ReferenceRelationContext
        List<Timeline<OriginRelation>> result = null;
        if (ctx instanceof ReadWriteTimelineContext) {

            ChangeSet set = ((ReadWriteTimelineContext) ctx).changeSet();
            if (set instanceof RelationUpsertBatchSet) {
                result = ((RelationUpsertBatchSet) set)
                        .findCachedReferenceTimelines(keys.getEtalonKey().getFrom().getId(), keys.getRelationName());
            } else if (set instanceof RelationDeleteBatchSet) {
                result = ((RelationDeleteBatchSet) set)
                        .findCachedReferenceTimelines(keys.getEtalonKey().getFrom().getId(), keys.getRelationName());
            }
        }

        if (Objects.nonNull(result)) {
            return result;
        }

        // Check references for overlapping. Only one reference of a type is allowed for a period
        GetRelationsTimelineRequestContext siblings = GetRelationsTimelineRequestContext.builder()
                .fetchData(true)
                .etalonKey(keys.getEtalonKey().getFrom().getId())
                .relationNames(keys.getRelationName())
                .build();

        result = loadTimelines(siblings).get(keys.getRelationName());
        return result == null ? Collections.emptyList() : result;
    }

    public List<Timeline<OriginRelation>> buildVirtualTimelinesForReferences(List<Timeline<OriginRelation>> real) {

        // Collect
        Map<OriginRelation, Pair<EtalonRelation, RelationKeys>> links = new IdentityHashMap<>();
        List<CalculableHolder<OriginRelation>> revisions = buildVirtualReferenceRevisions(real, links);

        // Build
        Timeline<OriginRelation> virtual = new RelationTimeline(null, revisions);

        // Different to records - do compact timeline by extending neighboring periods
        Map<RelationKeys, List<TimeInterval<OriginRelation>>> compacted = new IdentityHashMap<>();
        OriginRelation last = null;
        for (TimeInterval<OriginRelation> ti : virtual) {

            List<CalculableHolder<OriginRelation>> calculables = ti.toList();
            OriginRelation current = relationComposerComponent.toBVR(calculables, true, false);
            if (Objects.isNull(current)) {
                last = null;
                continue;
            }

            Pair<EtalonRelation, RelationKeys> linked = links.get(current);
            EtalonRelation data = linked.getKey();
            RelationKeys keys = linked.getValue();

            List<TimeInterval<OriginRelation>> intervals = compacted.computeIfAbsent(linked.getValue(), k -> new ArrayList<>());
            AbstractTimeInterval<OriginRelation> i = intervals.isEmpty() ? null : (AbstractTimeInterval<OriginRelation>) intervals.get(intervals.size() - 1);

            // Leader change
            if (i == null || last != current) {
                i = new RelationTimeInterval(ti.getValidFrom(), ti.getValidTo(), calculables);
                i.setActive(current.getInfoSection().getStatus() == RecordStatus.ACTIVE);
                i.setPending(current.getInfoSection().getApproval() == ApprovalState.PENDING);
                i.setCalculationResult(new EtalonRelationImpl()
                        .withDataRecord(data)
                        .withInfoSection(new EtalonRelationInfoSection()
                                .withRelationEtalonKey(keys.getEtalonKey().getId())
                                .withRelationName(keys.getRelationName())
                                .withRelationType(keys.getRelationType())
                                .withPeriodId(PeriodIdUtils.ensureDateValue(ti.getValidTo()))
                                .withValidFrom(ti.getValidFrom())
                                .withValidTo(ti.getValidTo())
                                .withStatus(current.getInfoSection().getStatus())
                                .withApproval(current.getInfoSection().getApproval())
                                .withOperationType(current.getInfoSection().getOperationType())
                                .withCreateDate(current.getInfoSection().getCreateDate())
                                .withUpdateDate(current.getInfoSection().getUpdateDate())
                                .withCreatedBy(current.getInfoSection().getCreatedBy())
                                .withUpdatedBy(current.getInfoSection().getUpdatedBy())
                                .withFromEntityName(keys.getFromEntityName())
                                .withFromEtalonKey(keys.getEtalonKey().getFrom())
                                .withToEntityName(keys.getToEntityName())
                                .withToEtalonKey(keys.getEtalonKey().getTo())));

                intervals.add(i);
                last = current;
                continue;
            }

            // Continuation. Compact timeline by extending period.
            EtalonRelation collected = i.getCalculationResult();
            i.setValidTo(ti.getValidTo());
            collected.getInfoSection()
                .withValidTo(ti.getValidTo())
                .withPeriodId(PeriodIdUtils.ensureDateValue(ti.getValidTo()));
        }

        return compacted.entrySet().stream()
                .map(entry -> new RelationTimeline(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<CalculableHolder<OriginRelation>>
        buildVirtualReferenceRevisions(List<Timeline<OriginRelation>> real, Map<OriginRelation, Pair<EtalonRelation, RelationKeys>> links) {

        // Collection<String> orderedSourceSystems = metaModelService.getReversedSourceSystems().keySet();
        List<CalculableHolder<OriginRelation>> revisions = new ArrayList<>();
        real.forEach(tl ->
            tl.forEach(ti -> {
                Optional<Date> lud = ti.toValueList().stream().map(or -> or.getInfoSection().getUpdateDate() != null
                        ? or.getInfoSection().getUpdateDate()
                        : or.getInfoSection().getCreateDate())
                        .max(Comparator.naturalOrder());
                // TODO fix after BVR driver refactoring (i. e. take the SS and stuff from real winner of the TI)
                //Set<String> suppementarySourceSystems = ti.toValueList().stream()
                //        .map(or -> or.getInfoSection().getRelationOriginKey().getSourceSystem())
                //        .collect(Collectors.toSet());

                RelationKeys relationKeys = tl.getKeys();
                EtalonRelation etalon = ti.getCalculationResult();
                OriginRelation vo = new OriginRelationImpl()
                    .withDataRecord(etalon)
                    .withInfoSection(new OriginRelationInfoSection()
                        .withRelationName(relationKeys.getRelationName())
                        .withValidFrom(ti.getValidFrom())
                        .withValidTo(ti.getValidTo())
                        .withCreateDate(relationKeys.getCreateDate())
                        .withCreatedBy(relationKeys.getCreatedBy())
                        .withUpdateDate(lud.orElse(null))
                        .withUpdatedBy(relationKeys.getUpdatedBy())
                        .withFromEntityName(relationKeys.getFromEntityName())
                        .withToEntityName(relationKeys.getToEntityName())
                        .withRelationType(relationKeys.getRelationType())
                        .withStatus(ti.isActive() ? RecordStatus.ACTIVE : RecordStatus.INACTIVE)
                        .withApproval(ti.isPending() ? ApprovalState.PENDING : ApprovalState.APPROVED)
                        .withShift(DataShift.REVISED)
                        .withRelationOriginKey(RelationOriginKey.builder()
                                .from(RecordOriginKey.builder()
                                        .createDate(relationKeys.getCreateDate())
                                        .createdBy(relationKeys.getCreatedBy())
                                        .updateDate(lud.orElse(null))
                                        .updatedBy(relationKeys.getUpdatedBy())
                                        .enrichment(false)
                                        .id(relationKeys.getEtalonKey().getFrom().getId())
                                        .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                                        .externalId(relationKeys.getEtalonKey().getFrom().getId())
                                        .entityName(relationKeys.getFromEntityName())
                                        .status(ti.isActive() ? RecordStatus.ACTIVE : RecordStatus.INACTIVE)
                                        .build())
                                .to(RecordOriginKey.builder()
                                        .createDate(relationKeys.getCreateDate())
                                        .createdBy(relationKeys.getCreatedBy())
                                        .updateDate(lud.orElse(null))
                                        .updatedBy(relationKeys.getUpdatedBy())
                                        .enrichment(false)
                                        .id(relationKeys.getEtalonKey().getTo().getId())
                                        .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                                        .externalId(relationKeys.getEtalonKey().getTo().getId())
                                        .entityName(relationKeys.getToEntityName())
                                        .status(ti.isActive() ? RecordStatus.ACTIVE : RecordStatus.INACTIVE)
                                        .build())
                                .createDate(relationKeys.getCreateDate())
                                .createdBy(relationKeys.getCreatedBy())
                                .updateDate(lud.orElse(null))
                                .updatedBy(relationKeys.getUpdatedBy())
                                .enrichment(false)
                                .id(relationKeys.getEtalonKey().getId())
                                .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                                .status(ti.isActive() ? RecordStatus.ACTIVE : RecordStatus.INACTIVE)
                                .build()));

                links.put(vo, Pair.of(etalon, tl.getKeys()));
                revisions.add(new RelationRecordHolder(vo));
            }));

        return revisions;
    }
}

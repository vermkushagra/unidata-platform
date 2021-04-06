package com.unidata.mdm.backend.service.data.relations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext.GetRelationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.GetRelationsDigestRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext.GetRelationsRequestContextBuilder;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext.IndexRequestContextBuilder;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteRelationDTO;
import com.unidata.mdm.backend.common.dto.DeleteRelationsDTO;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.dto.GetRelationsDTO;
import com.unidata.mdm.backend.common.dto.RelationDigestDTO;
import com.unidata.mdm.backend.common.dto.RelationStateDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationsDTO;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.dao.RelationsDao;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.data.batch.BatchIterator;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.BatchSetIterationType;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.data.batch.RelationBatchSetProcessor;
import com.unidata.mdm.backend.service.data.batch.RelationDeleteBatchSetAccumulator;
import com.unidata.mdm.backend.service.data.batch.RelationUpsertBatchSetAccumulator;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov Relations service component.
 */
@Component
public class RelationsServiceComponent {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationsServiceComponent.class);
    /**
     * Relations vistory DAO.
     */
    @Autowired
    private RelationsDao relationsVistoryDao;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Common relations functionality.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Relations 'data' component (does real job).
     */
    @Autowired
    private RelationDataComponent relationDataComponent;

    /**
     * The validation component.
     */
    @Autowired
    private RelationsValidationComponent validationComponent;
    /**
     * Audit writer.
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;
    /**
     * Batch set processor.
     */
    @Autowired
    private RelationBatchSetProcessor relationBatchSetProcessor;

    /**
     * Constructor.
     */
    public RelationsServiceComponent() {
        super();
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for an etalon ID.
     *
     * @param etalonId          the etalon ID of the 'from' end
     * @param name              the name of the relation type
     * @param includeDrafts     include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public List<TimelineDTO> loadRelationsTimeline(String etalonId, String name, boolean includeDrafts, boolean checkPendingState) {
        return commonRelationsComponent.loadRelationsTimeline(etalonId, name, includeDrafts, checkPendingState);
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for an etalon ID.
     *
     * @param etalonId          the etalon ID of the 'from' end
     * @param name              the name of the relation type
     * @param asOf              as of date
     * @param includeDrafts     include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public List<TimelineDTO> loadRelationsTimeline(String etalonId, String name, Date asOf, boolean includeDrafts, boolean checkPendingState) {
        return commonRelationsComponent.loadRelationsTimeline(etalonId, name, asOf, includeDrafts, checkPendingState);
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for an etalon ID.
     *
     * @param etalonId          the etalon ID of the 'from' end
     * @param name              the name of the relation type
     * @param from              date from
     * @param to                date to
     * @param includeDrafts     include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public List<TimelineDTO> loadRelationsTimeline(String etalonId, String name, Date from, Date to, boolean includeDrafts, boolean checkPendingState) {
        return commonRelationsComponent.loadRelationsTimeline(etalonId, name, from, to, includeDrafts, checkPendingState);
    }

    /**
     * Loads all periods of all relations as list by from side.
     *
     * @param keys        record from keys
     * @param operationId possibly existing operation id
     * @return collection of relations or empty list
     */
    public List<EtalonRelation> loadActiveEtalonsRelationsByFromSideAsList(@Nonnull RecordKeys keys, String operationId) {
        return loadActiveEtalonsRelationsAsList(keys, true, operationId);
    }

    /**
     * Loads all periods of all relations as list by to side.
     *
     * @param keys        record from keys
     * @param operationId possibly existing operation id
     * @return collection of relations or empty list
     */
    public List<EtalonRelation> loadActiveEtalonsRelationsByToSideAsList(@Nonnull RecordKeys keys, String operationId) {
        return loadActiveEtalonsRelationsAsList(keys, false, operationId);
    }

    /**
     * Loads all periods of all relations as list.
     *
     * @param keys        record from keys
     * @param byFromSide  by from side (true) or by to side (false)
     * @param operationId possibly existing operation id
     * @return collection of relations or empty list
     */
    private List<EtalonRelation> loadActiveEtalonsRelationsAsList(@Nonnull RecordKeys keys, boolean byFromSide, String operationId) {

        // Load complete timeline
        Map<RelationDef, List<TimelineDTO>> timelines = byFromSide
                ? commonRelationsComponent.loadCompleteRelationsTimelineByFromSide(keys.getEtalonKey().getId(), false, false)
                : commonRelationsComponent.loadCompleteRelationsTimelineByToSide(keys.getEtalonKey().getId(), false, false);

        // Build rel collection
        return timelines.values().stream()
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .filter(timeline -> CollectionUtils.isNotEmpty(timeline.getIntervals()))
                .map(tl ->
                    tl.getIntervals().stream()
                      .filter(TimeIntervalDTO::isActive)
                      .map(interval -> {

                                    GetRelationRequestContext ctx = GetRelationRequestContext.builder()
                                            .relationEtalonKey(tl.getEtalonId())
                                            .includeDrafts(false)
                                            .forDate(interval.getValidFrom() == null
                                                    ? interval.getValidTo()
                                                    : interval.getValidFrom())
                                            .build();

                                    ctx.setOperationId(operationId);
                                    GetRelationDTO dto = getRelation(ctx);

                                    return dto != null && dto.getEtalon() != null && dto.getEtalon().getInfoSection() != null
                                            ? dto.getEtalon()
                                            : null;

                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                )
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Loads etlon relations for etalon id, relation name and date.
     *
     * @param from        the object (left side, from) etalon id
     * @param name        relation name
     * @param asOf        the date
     * @param operationId the operation id
     * @return relations
     */
    public Map<RelationStateDTO, List<GetRelationDTO>> loadActiveEtalonsRelations(
            RecordKeys from,
            String name,
            Date asOf,
            String operationId
    ) {
        return loadEtalonsRelations(
                from, name, asOf, operationId, Collections.singletonList(RecordStatus.ACTIVE), Collections.singleton(RelationSide.FROM)
        );
    }

    public Map<RelationStateDTO, List<GetRelationDTO>> loadAllEtalonsRelations(
            RecordKeys from,
            String name,
            Date asOf,
            String operationId
    ) {
        return loadEtalonsRelations(
                from,
                name,
                asOf,
                operationId,
                Arrays.asList(RecordStatus.ACTIVE, RecordStatus.INACTIVE, RecordStatus.MERGED),
                EnumSet.allOf(RelationSide.class)
        );
    }

    public List<GetRelationDTO> loadRelationsToEtalon(GetRelationsRequestContext ctx) {
        return ctx.getRelationNames().stream()
                .flatMap(name ->
                        relationsVistoryDao.loadEtalonRelations(
                                ctx.getEtalonKey(),
                                name,
                                Collections.singletonList(RecordStatus.ACTIVE),
                                RelationSide.TO
                        ).stream()
                )
                .map(r -> new GetRelationDTO(
                        RelationKeys.builder()
                                .etalonId(r.getId())
                                .relationName(r.getName())
                                .build(),
                        r.getName(),
                        null
                ))
                .collect(Collectors.toList());
    }

    private Map<RelationStateDTO, List<GetRelationDTO>> loadEtalonsRelations(
            RecordKeys recordKeys,
            String name,
            Date asOf,
            String operationId,
            List<RecordStatus> statuses,
            Set<RelationSide> sides
    ) {

        MeasurementPoint.start();
        try {

            List<EtalonRelationPO> toEtalons = new ArrayList<>();
            if (sides.contains(RelationSide.FROM)) {
                toEtalons.addAll(
                        relationsVistoryDao.loadEtalonRelations(
                                recordKeys.getEtalonKey().getId(), name, statuses, RelationSide.FROM
                        )
                );
            }
            if (sides.contains(RelationSide.TO)) {
                toEtalons.addAll(
                        relationsVistoryDao.loadEtalonRelations(
                                recordKeys.getEtalonKey().getId(), name, statuses, RelationSide.TO
                        )
                );
            }

            if (toEtalons.isEmpty()) {
                return Collections.emptyMap();
            }

            List<GetRelationRequestContext> requestList = toEtalons.stream().map(
                    po -> new GetRelationRequestContextBuilder()
                            .relationEtalonKey(po.getId())
                            .forDate(asOf)
                            .forOperationId(operationId)
                            .build())
                    .collect(Collectors.toList());

            GetRelationsRequestContext ctx = new GetRelationsRequestContextBuilder()
                    .relations(Collections.singletonMap(name, requestList))
                    .build();

            ctx.putToStorage(ctx.keysId(), recordKeys);

            GetRelationsDTO result = getRelations(ctx);
            return result.getRelations();

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets relations digest.
     *
     * @param ctx the context
     * @return result
     */
    public RelationDigestDTO loadRelatedEtalonIdsForDigest(GetRelationsDigestRequestContext ctx) {
        MeasurementPoint.start();
        try {

            RelationDef relationDef = metaModelService.getRelationById(ctx.getRelName());
            if (relationDef == null) {
                return null;
            }

            Map<String, Integer> sourceSystemsMap = metaModelService.getStraightSourceSystems();
            return relationsVistoryDao.loadDigestDestinationEtalonIds(ctx.getEtalonId(), ctx.getRelName(),
                    ctx.getDirection(), sourceSystemsMap, null, ctx.getCount(), ctx.getPage() * ctx.getCount());

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets the number of relation objects by relation name.
     *
     * @param relName the name
     * @return count
     */
    public long getTotalRelationCountByRelName(String relName) {
        return relationsVistoryDao.countRelationByName(relName);
    }

    /**
     * Check exist data relation by relation name
     *
     * @param relName the name
     * @return count
     */
    public boolean checkExistDataByRelName(String relName) {
        return relationsVistoryDao.checkExistDataRelationByName(relName);
    }

    /**
     * Gets a relation by simple request context.
     *
     * @param ctx the context
     * @return relation DTO
     */
    public GetRelationDTO getRelation(GetRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            validationComponent.before(ctx);

            RelationKeys keys = ctx.relationKeys();
            RelationDef relation = metaModelService.getRelationById(keys.getRelationName());
            GetRelationDTO result = relationDataComponent.get(ctx, relation);

            validationComponent.after(ctx);

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Gets relations by simple request context.
     *
     * @param ctxts the contexts
     * @return relations DTO
     */
    public List<GetRelationDTO> getRelations(List<GetRelationRequestContext> ctxts) {

        List<GetRelationDTO> result = new ArrayList<>(ctxts.size());
        for (GetRelationRequestContext ctx : ctxts) {
            GetRelationDTO val = getRelation(ctx);
            if (val != null) {
                result.add(val);
            }
        }
        return result;
    }


    /**
     * Gets the relations.
     *
     * @param ctx the context
     * @return relations DTO
     */
    public GetRelationsDTO getRelations(GetRelationsRequestContext ctx) {
        return getRelations(ctx, false);
    }

    public GetRelationsDTO getAllRelations(GetRelationsRequestContext ctx) {
        return getRelations(ctx, true);
    }

    private GetRelationsDTO getRelations(GetRelationsRequestContext ctx, boolean all) {
        MeasurementPoint.start();
        try {

            // 1. Ensure from key and run pre-check
            validationComponent.before(ctx);

            // 2. Get relations by relation type
            Map<RelationStateDTO, List<GetRelationDTO>> collected = new HashMap<>();
            if (!ctx.getRelations().isEmpty()) {
                for (Entry<String, List<GetRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                    RelationDef relation = metaModelService.getRelationById(entry.getKey());
                    collected.putAll(relationDataComponent.get(ctx, relation));
                }
            } else if (!ctx.getRelationNames().isEmpty()) {
                RecordKeys keys = ctx.keys();
                if (keys != null) {
                    for (String name : ctx.getRelationNames()) {
                        collected.putAll(
                                all ?
                                        loadAllEtalonsRelations(keys, name, ctx.getForDate(), ctx.getForOperationId()) :
                                        loadActiveEtalonsRelations(keys, name, ctx.getForDate(), ctx.getForOperationId())
                        );
                    }
                }
            }

            // 3. Run possible 'after' actions.
            validationComponent.after(ctx);

            return new GetRelationsDTO(collected);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Actually upserts relation.
     *
     * @param ctx the context
     * @return result DTO
     */
    @Transactional
    public UpsertRelationDTO upsertRelation(UpsertRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            validationComponent.before(ctx);

            RelationKeys keys = ctx.relationKeys();
            RelationDef relation = metaModelService.getRelationById(keys.getRelationName());
            UpsertRelationDTO result = relationDataComponent.upsert(ctx, relation);

            validationComponent.after(ctx);

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Upsert multiple updating relations call.
     *
     * @param ctxts the contexts to process
     * @return result (inserted/updated record)
     */
    @Transactional
    public List<UpsertRelationDTO> upsertRelations(List<UpsertRelationRequestContext> ctxts) {
        List<UpsertRelationDTO> result = new ArrayList<>(ctxts.size());
        for (UpsertRelationRequestContext ctx : ctxts) {
            UpsertRelationDTO val = upsertRelation(ctx);
            if (val != null) {
                result.add(val);
            }
        }
        return result;
    }

    /**
     * Upsert relations call.
     *
     * @param ctx the context
     * @return result (inserted/updated records)
     */
    @Transactional
    public UpsertRelationsDTO upsertRelations(UpsertRelationsRequestContext ctx) {
        MeasurementPoint.start();
        try {

            // 1. Ensure from key and run pre-check
            validationComponent.before(ctx);

            // 2. Upsert relations by relation type
            Map<RelationStateDTO, List<UpsertRelationDTO>> upserted = new HashMap<>();
            for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                RelationDef relation = metaModelService.getRelationById(entry.getKey());
                upserted.putAll(relationDataComponent.upsert(ctx, relation));
            }

            // 3. Run possible 'after' actions.
            validationComponent.after(ctx);

            return new UpsertRelationsDTO(upserted);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes relations in batched fashion.
     *
     * @param accumulator accumulator
     * @return list of results
     */
    public List<DeleteRelationsDTO> batchDeleteRelations(BatchSetAccumulator<DeleteRelationsRequestContext> accumulator) {

        // 1. Process
        if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ORIGINS)) {
            batchDeleteOrigins(accumulator);
        }

        if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.DELETE_ETALONS)) {
            batchDeleteEtalons(accumulator);
        }

        // 2. Collect
        return accumulator.workingCopy().stream()
                .flatMap(ctx -> ctx.getRelations().entrySet().stream())
                .filter(entry -> CollectionUtils.isNotEmpty(entry.getValue()))
                .map(entry -> {

                    List<Date> vf = new ArrayList<>(entry.getValue().size());
                    List<Date> vt = new ArrayList<>(entry.getValue().size());

                    List<DeleteRelationDTO> collected = entry.getValue().stream()
                            .filter(Objects::nonNull)
                            .map(dCtx -> {
                                RelationDef def = dCtx.getFromStorage(StorageId.RELATIONS_META_DEF);
                                vf.add(dCtx.getValidFrom());
                                vt.add(dCtx.getValidTo());
                                return new DeleteRelationDTO(dCtx.relationKeys(),
                                        def.getName(),
                                        RelationType.valueOf(def.getRelType().name()));
                            })
                            .collect(Collectors.toList());

                    RelationDef relation = metaModelService.getRelationById(entry.getKey());
                    return new DeleteRelationsDTO(Collections.singletonMap(
                            new RelationStateDTO(
                                    relation.getName(),
                                    relation.getRelType(),
                                    ValidityPeriodUtils.leastFrom(vf),
                                    ValidityPeriodUtils.mostTo(vt)),
                            collected));
                })
                .collect(Collectors.toList());
    }

    /**
     * batch upsert relations with default accumulator context
     *
     * @param ctxs contexts for update
     * @return update result
     */
    public List<UpsertRelationsDTO> batchUpsertRelations(List<UpsertRelationsRequestContext> ctxs) {
        BatchSetAccumulator<UpsertRelationsRequestContext> accumulator = getDefaultRelationUpsertAccumulator();
        accumulator.charge(ctxs);
        List<UpsertRelationsDTO> result;
        try {
            result = batchUpsertRelations(accumulator);
        } finally {
            accumulator.discharge();
        }
        return result;
    }

    /**
     * batch delete relations with default accumulator context
     *
     * @param ctxs contexts for delete
     * @return delete result
     */
    public List<DeleteRelationsDTO> batchDeleteRelations(List<DeleteRelationsRequestContext> ctxs) {
        BatchSetAccumulator<DeleteRelationsRequestContext> accumulator = getDefaultRelationDeleteAccumulator();
        accumulator.charge(ctxs);
        List<DeleteRelationsDTO> result;
        try {
            result = batchDeleteRelations(accumulator);
        } finally {
            accumulator.discharge();
        }
        return result;
    }

    /**
     * Batch upsert relations.
     *
     * @param ctxts contexts
     * @return result
     */
    @Transactional(rollbackFor = Exception.class)
    public List<UpsertRelationsDTO> batchUpsertRelations(BatchSetAccumulator<UpsertRelationsRequestContext> accumulator) {

        // 1. Process
        if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ORIGINS)) {
            batchUpsertOrigins(accumulator);
        }

        if (accumulator.getSupportedIterationTypes().contains(BatchSetIterationType.UPSERT_ETALONS)) {
            batchUpsertEtalons(accumulator);
        }

        // 2. Collect result
        List<UpsertRelationsRequestContext> workingCopy = accumulator.workingCopy();
        List<UpsertRelationsDTO> result = new ArrayList<>(workingCopy.size());
        for (UpsertRelationsRequestContext ctx : workingCopy) {

            for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                if (CollectionUtils.isEmpty(entry.getValue())) {
                    continue;
                }

                RelationDef relation = entry.getValue().iterator().next().getFromStorage(StorageId.RELATIONS_META_DEF);

                List<UpsertRelationDTO> collected = entry.getValue().stream()
                        .map(relationDataComponent::upsertContextToResult)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                result.add(new UpsertRelationsDTO(Collections.singletonMap(
                        new RelationStateDTO(
                                relation.getName(),
                                relation.getRelType(),
                                ValidityPeriodUtils.leastFrom(collected.stream()
                                        .map(UpsertRelationDTO::getValidFrom)
                                        .collect(Collectors.toList())),
                                ValidityPeriodUtils.mostTo(collected.stream()
                                        .map(UpsertRelationDTO::getValidTo)
                                        .collect(Collectors.toList()))),
                        collected)));
            }
        }

        return result;
    }

    /**
     * Process origins upsert.
     *
     * @param accumulator accumulator
     */
    private void batchDeleteOrigins(BatchSetAccumulator<DeleteRelationsRequestContext> accumulator) {

        // 1. Collect origin updates.
        for (BatchIterator<DeleteRelationsRequestContext> bi = accumulator.iterator(BatchSetIterationType.DELETE_ORIGINS); bi.hasNext(); ) {

            DeleteRelationsRequestContext ctx = bi.next();
            try {

                // 1.1. Ensure from key and run pre-check
                validationComponent.before(ctx);

                // 1.2. Upsert relations by relation type
                for (Entry<String, List<DeleteRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                    for (Iterator<DeleteRelationRequestContext> li = entry.getValue().iterator(); li.hasNext(); ) {

                        DeleteRelationRequestContext dCtx = li.next();
                        try {

                            ContextUtils.storageCopy(ctx, dCtx,
                                    StorageId.RELATIONS_FROM_KEY,
                                    StorageId.RELATIONS_FROM_RIGHTS,
                                    StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);

                            relationDataComponent.deleteOrigin(dCtx);
                            if (AuditLevel.AUDIT_SUCCESS <= dCtx.getAuditLevel()) {
                                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE_RELATION, dCtx);
                            }

                        } catch (Exception e) {
                            if (AuditLevel.AUDIT_ERRORS <= dCtx.getAuditLevel()) {
                                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE_RELATION, e, dCtx);
                            }

                            li.remove();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Batch delete relations BEFORE exception caught.", e);
                bi.remove();
            }
        }

        // 2. Run processor on accumulated
        relationBatchSetProcessor.applyOrigins(accumulator);
    }

    /**
     * Process origins upsert.
     *
     * @param accumulator accumulator
     */
    private void batchDeleteEtalons(BatchSetAccumulator<DeleteRelationsRequestContext> accumulator) {

        // 1. Collect
        for (BatchIterator<DeleteRelationsRequestContext> bi = accumulator.iterator(BatchSetIterationType.DELETE_ETALONS); bi.hasNext(); ) {

            DeleteRelationsRequestContext ctx = bi.next();
            try {

                for (Entry<String, List<DeleteRelationRequestContext>> entry : ctx.getRelations().entrySet()) {
                    for (Iterator<DeleteRelationRequestContext> li = entry.getValue().iterator(); li.hasNext(); ) {

                        DeleteRelationRequestContext dCtx = li.next();
                        try {

                            relationDataComponent.deleteEtalon(dCtx);
                            if (!dCtx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= dCtx.getAuditLevel()) {
                                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE_RELATION, dCtx);
                            }
                        } catch (Exception e) {
                            if (!dCtx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= dCtx.getAuditLevel()) {
                                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE_RELATION, e, dCtx);
                            }
                            li.remove();
                        }
                    }
                }

                validationComponent.after(ctx);
            } catch (Exception e) {
                LOGGER.warn("Batch delete classifiers AFTER exception caught.", e);
                bi.remove();
            }
        }

        // 2. Post process
        List<String> containmentIds = new ArrayList<>();
        List<String> relationIds = new ArrayList<>();

        // 3. Additional step - collect index contexts
        accumulator.workingCopy().stream()
                .flatMap(u -> u.getRelations().entrySet().stream())
                .flatMap(l -> l.getValue().stream())
                .forEach(dCtx -> {
                    RelationDef def = dCtx.getFromStorage(StorageId.RELATIONS_META_DEF);
                    if (def.getRelType() == RelType.CONTAINS) {
                        DeleteRequestContext cCtx = dCtx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT);
                        if (Objects.nonNull(cCtx)) {
                            containmentIds.add(cCtx.keys().getEtalonKey().getId());
                        }
                    }

                    relationIds.add(dCtx.relationKeys().getEtalonId());
                });

        // 3. Apply
        relationBatchSetProcessor.applyEtalons(accumulator);
    }

    /**
     * Process origins upsert.
     *
     * @param accumulator accumulator
     */
    private void batchUpsertOrigins(BatchSetAccumulator<UpsertRelationsRequestContext> accumulator) {

        // 1. Collect origin updates.
        for (BatchIterator<UpsertRelationsRequestContext> bi = accumulator.iterator(BatchSetIterationType.UPSERT_ORIGINS); bi.hasNext(); ) {

            UpsertRelationsRequestContext ctx = bi.next();
            try {

                // 1.1. Ensure from key and run pre-check
                validationComponent.before(ctx);

                // 1.2. Upsert relations by relation type
                for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                    for (Iterator<UpsertRelationRequestContext> li = entry.getValue().iterator(); li.hasNext(); ) {

                        UpsertRelationRequestContext uCtx = li.next();
                        try {

                            ContextUtils.storageCopy(ctx, uCtx,
                                    StorageId.RELATIONS_FROM_KEY,
                                    StorageId.RELATIONS_FROM_RIGHTS,
                                    StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);

                            relationDataComponent.upsertOrigin(uCtx);
                            if (AuditLevel.AUDIT_SUCCESS <= uCtx.getAuditLevel() && !uCtx.isInitialLoad()) {
                                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_UPSERT_RELATION, uCtx);
                            }

                        } catch (Exception e) {
                            if (AuditLevel.AUDIT_ERRORS <= uCtx.getAuditLevel()) {
                                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT_RELATION, e, uCtx);
                            }

                            li.remove();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Batch upsert relations BEFORE exception caught.", e);
                bi.remove();
            }
        }

        // 2. Run processor on accumulated
        relationBatchSetProcessor.applyOrigins(accumulator);
    }

    /**
     * Process origins upsert.
     *
     * @param accumulator accumulator
     */
    private void batchUpsertEtalons(BatchSetAccumulator<UpsertRelationsRequestContext> accumulator) {

        // 1. Collect etalon updates.
        for (BatchIterator<UpsertRelationsRequestContext> bi = accumulator.iterator(BatchSetIterationType.UPSERT_ETALONS); bi.hasNext(); ) {

            UpsertRelationsRequestContext ctx = bi.next();
            try {

                // 2. Upsert relations by relation type
                for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                    for (Iterator<UpsertRelationRequestContext> li = entry.getValue().iterator(); li.hasNext(); ) {

                        UpsertRelationRequestContext uCtx = li.next();
                        try {

                            relationDataComponent.upsertEtalon(uCtx);

                        } catch (Exception e) {
                            if (AuditLevel.AUDIT_ERRORS <= uCtx.getAuditLevel()) {
                                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_UPSERT_RELATION, e, uCtx);
                            }

                            li.remove();
                        }
                    }
                }

                // 3. Possible post actions
                validationComponent.after(ctx);

            } catch (Exception e) {
                LOGGER.warn("Batch upsert relations AFTER exception caught.", e);
                bi.remove();
            }
        }

        // 2. Run processor on accumulated
        relationBatchSetProcessor.applyEtalons(accumulator);
    }

    /**
     * Actually deletes a relation.
     *
     * @param ctx the context
     * @return result DTO
     */
    @Transactional
    public DeleteRelationDTO deleteRelation(DeleteRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            validationComponent.before(ctx);

            RelationKeys keys = ctx.relationKeys();
            RelationDef relation = metaModelService.getRelationById(keys.getRelationName());
            DeleteRelationDTO result = relationDataComponent.delete(ctx, relation);

            validationComponent.after(ctx);

            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Deletes relations.
     *
     * @param ctxts the contexts
     * @return result DTO
     */
    @Transactional
    public List<DeleteRelationDTO> deleteRelations(List<DeleteRelationRequestContext> ctxts) {
        List<DeleteRelationDTO> result = new ArrayList<>(ctxts.size());
        for (DeleteRelationRequestContext ctx : ctxts) {
            DeleteRelationDTO val = deleteRelation(ctx);
            if (val != null) {
                result.add(val);
            }
        }
        return result;
    }

    /**
     * Deletes relations.
     *
     * @param ctx the context
     * @return result DTO
     */
    @Transactional
    public DeleteRelationsDTO deleteRelations(DeleteRelationsRequestContext ctx) {
        MeasurementPoint.start();
        try {

            // 1. Ensure from key and run pre-check
            validationComponent.before(ctx);

            // 2. Delete relations by relation type
            Map<RelationStateDTO, List<DeleteRelationDTO>> deleted = new HashMap<>();
            for (Entry<String, List<DeleteRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                RelationDef relation = metaModelService.getRelationById(entry.getKey());
                deleted.putAll(relationDataComponent.delete(ctx, relation));
            }

            // 3. Run possible 'after' actions.
            validationComponent.after(ctx);

            return new DeleteRelationsDTO(deleted);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Changes etalon state.
     *
     * @param etalonId the etalon id
     * @param state    the state
     * @return true, if successful, false otherwise
     */
    @Transactional
    public boolean changeApproval(String etalonId, ApprovalState state) {
        return commonRelationsComponent.changeApproval(etalonId, state);
    }

    /**
     * Deactivate relation version for a time interval.
     *
     * @param ctx the context
     * @return result
     */
    @Transactional
    public boolean deactivateOriginRelation(DeleteRelationRequestContext ctx) {
        throw new RuntimeException("deactivateOriginRelation(DeleteRelationRequestContext ctx) not implemented!");
    }

    /**
     * Return number of relations to given etalon record.
     *
     * @param etalonId etalon id.
     * @param status   relation status.
     * @return Number of relations to given etalon record.
     */
    public int countCurrentRelationsToEtalon(String etalonId, RecordStatus status) {
        return relationsVistoryDao.countCurrentRelationsToEtalon(etalonId, status);
    }

    /**
     * Return number of relations from given etalon record.
     *
     * @param etalonId etalon id.
     * @param status   relation status.
     * @return Number of relations to given etalon record.
     */
    public int countCurrentRelationsFromEtalon(String etalonId, RecordStatus status) {
        return relationsVistoryDao.countCurrentRelationsFromEtalon(etalonId, status);
    }

    /**
     * Merge support.
     *
     * @param master
     * @param duplicates
     * @param operationId the external id
     */
    @Transactional
    public void mergeRelations(RecordKeys master, List<RecordKeys> duplicates, String operationId) {

        MeasurementPoint.start();
        try {

            // 0. Save the timeline for dropping index data
            List<RelationDef> fromRelationTypes = metaModelService.getRelationsByFromEntityName(master.getEntityName());
            List<RelationDef> toRelationTypes = metaModelService.getRelationsByToEntityName(master.getEntityName());
            if (CollectionUtils.isEmpty(fromRelationTypes) && CollectionUtils.isEmpty(toRelationTypes)) {
                return;
            }

            List<String> duplicateIds = duplicates.stream()
                    .map(RecordKeys::getEtalonKey)
                    .map(EtalonKey::getId)
                    .collect(Collectors.toList());

            // 1. Selected relation types, which require special handling
            List<String> m2mAndContainsRelations = fromRelationTypes.stream()
                    .filter(x -> RelType.MANY_TO_MANY == x.getRelType() || RelType.CONTAINS == x.getRelType())
                    .map(RelationDef::getName)
                    .collect(Collectors.toList());


            // 2. Remap etalon relation records, having IDs in 'to' side
            int count = relationsVistoryDao.remapToEtalonRelations(duplicateIds, master.getEtalonKey().getId(), operationId);
            LOGGER.debug("Remapped {} number of TO relations for records, merged to {} etalon id.", count,
                    master.getEtalonKey().getId());

            // 3. Remap M2M and contains from relations
            if (!m2mAndContainsRelations.isEmpty()) {
                count = relationsVistoryDao.remapFromEtalonRelations(duplicateIds, master.getEtalonKey().getId(), m2mAndContainsRelations, operationId);
                LOGGER.debug("Remapped {} number of FROM relations of type M2M/CONTAINS for records, merged to {} etalon id for types [{}].", count,
                        master.getEtalonKey().getId(), m2mAndContainsRelations);
            }
            // 4. Remap References from relations
            List<String> referenceRelationNames = fromRelationTypes.stream()
                    .filter(x -> RelType.REFERENCES == x.getRelType())
                    .map(RelationDef::getName)
                    .collect(Collectors.toList());

            List<String> allEtalonIds = new ArrayList<>(duplicateIds);
            allEtalonIds.add(master.getEtalonKey().getId());

            for (String referenceRelation : referenceRelationNames) {
                List<String> relationUsage = relationsVistoryDao.checkUsageByFromEtalonIds(allEtalonIds, referenceRelation);
                if(CollectionUtils.isNotEmpty(relationUsage) && !relationUsage.contains(master.getEtalonKey().getId())){
                    String etalinIdForLink = relationUsage.iterator().next();
                    relationsVistoryDao.remapFromEtalonRelations(
                            Collections.singletonList(etalinIdForLink),
                            master.getEtalonKey().getId(),
                            Collections.singletonList(referenceRelation),
                            operationId);
                }
            }


            // 5. Set status 'MERGED' to etalon relation records, having IDs in 'from' side for merged duplicate ids
            count = relationsVistoryDao.markFromEtalonRelationsMerged(duplicateIds, Collections.emptyList(), operationId);
            LOGGER.debug("Merged {} number of FROM relations for records, merged to {} etalon id. Skept rels of type M2M/CONTAINS [{}].", count,
                    master.getEtalonKey().getId(), m2mAndContainsRelations);


            // 6. Delete from index
            // 6.1. Delete from 'own' index
            // .form(FormFieldsGroup.createAndGroup(FormField.strictString(RelationHeaderField.FIELD_FROM_ETALON_ID, single)))
            SearchRequestContext delSearch = SearchRequestContext.builder(EntitySearchType.ETALON_RELATION, master.getEntityName())
                    .onlyQuery(true)
                    .search(SearchRequestType.TERM)
                    .searchFields(Arrays.asList(
                            RelationHeaderField.FIELD_FROM_ETALON_ID.getField(),
                            RelationHeaderField.FIELD_TO_ETALON_ID.getField()))
                    .values(duplicateIds)
                    .build();

            searchService.deleteAll(delSearch);

            // 6.2. Delete counterpart 'from' indexes
            Set<String> targetNames = fromRelationTypes.stream()
                    .map(RelationDef::getToEntity)
                    .collect(Collectors.toSet());

            for (String target : targetNames) {

                delSearch = SearchRequestContext.builder(EntitySearchType.ETALON_RELATION, target)
                        .onlyQuery(true)
                        .search(SearchRequestType.TERM)
                        .searchFields(Collections.singletonList(RelationHeaderField.FIELD_FROM_ETALON_ID.getField()))
                        .values(duplicateIds)
                        .build();

                searchService.deleteAll(delSearch);
            }

            // 6.3. Delete 'to' indexes
            targetNames = toRelationTypes.stream()
                    .map(RelationDef::getFromEntity)
                    .collect(Collectors.toSet());

            for (String target : targetNames) {

                delSearch = SearchRequestContext.builder(EntitySearchType.ETALON_RELATION, target)
                        .onlyQuery(true)
                        .search(SearchRequestType.TERM)
                        .searchFields(Collections.singletonList(RelationHeaderField.FIELD_TO_ETALON_ID.getField()))
                        .values(duplicateIds)
                        .build();

                searchService.deleteAll(delSearch);
            }

            // 6.4. Index stuff
            IndexRequestContextBuilder ircb = IndexRequestContext.builder()
                    .entity(master.getEntityName());

            // 6.5. Collect from side
            if (CollectionUtils.isNotEmpty(fromRelationTypes)) {
                ircb.relations(loadActiveEtalonsRelationsByFromSideAsList(master, operationId));
            }

            // 6.6. Collect to side
            if (CollectionUtils.isNotEmpty(toRelationTypes)) {
                ircb.relations(loadActiveEtalonsRelationsByToSideAsList(master, operationId));
            }

            searchService.index(ircb.build());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for a relation etalon ID.
     *
     * @param relationEtalonId the etalon ID of the relation
     * @return time line
     */
    public TimelineDTO loadRelationTimeline(String relationEtalonId) {
        return commonRelationsComponent.loadRelationTimeline(relationEtalonId, false, false);
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for a relation etalon ID.
     *
     * @param relationEtalonId  the etalon ID of the relation
     * @param asOf              as of date
     * @param includeDrafts     include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public TimelineDTO loadRelationTimeline(String relationEtalonId, Date asOf, boolean includeDrafts, boolean checkPendingState) {
        return commonRelationsComponent.loadRelationTimeline(relationEtalonId, asOf, includeDrafts, checkPendingState);
    }

    /**
     * Loads (calculates) contributing relations ('to' participants) time line
     * for a relation etalon ID.
     *
     * @param relationEtalonId  the etalon ID of the relation
     * @param from              date from
     * @param to                date to
     * @param includeDrafts     include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return time line
     */
    public TimelineDTO loadRelationTimeline(String relationEtalonId, Date from, Date to, boolean includeDrafts, boolean checkPendingState) {
        return commonRelationsComponent.loadRelationTimeline(relationEtalonId, from, to, includeDrafts, checkPendingState);
    }

    /**
     * Set record status to inactive for all rels for it rel name!
     *
     * @param relationName relation name
     */
    public void deactiveteRelationsByName(String relationName) {
        commonRelationsComponent.deactivateRelationsByName(relationName);
    }

    private BatchSetAccumulator<UpsertRelationsRequestContext> getDefaultRelationUpsertAccumulator() {
        RelationUpsertBatchSetAccumulator accumulator
                = new RelationUpsertBatchSetAccumulator(500, null, null, false, null);
        accumulator.setBatchSetSize(BatchSetSize.SMALL);
        accumulator.setSupportedIterationTypes(Arrays.asList(BatchSetIterationType.UPSERT_ORIGINS,
                BatchSetIterationType.UPSERT_ETALONS));
        return accumulator;
    }

    private BatchSetAccumulator<DeleteRelationsRequestContext> getDefaultRelationDeleteAccumulator() {
        RelationDeleteBatchSetAccumulator accumulator
                = new RelationDeleteBatchSetAccumulator(500, null, false, null);
        accumulator.setBatchSetSize(BatchSetSize.SMALL);
        accumulator.setSupportedIterationTypes(Arrays.asList(BatchSetIterationType.DELETE_ORIGINS,
                BatchSetIterationType.DELETE_ETALONS));
        return accumulator;
    }
}

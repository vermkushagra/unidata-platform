package com.unidata.mdm.backend.service.data.relations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext;
import com.unidata.mdm.backend.common.context.GetTasksRequestContext.GetTasksRequestContextBuilder;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteRelationDTO;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationDTO;
import com.unidata.mdm.backend.common.dto.security.ResourceSpecificRightDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTaskDTO;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.EtalonRelationImpl;
import com.unidata.mdm.backend.common.types.impl.OriginRelationImpl;
import com.unidata.mdm.backend.dao.RelationsDao;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.service.data.batch.RelationBatchSet;
import com.unidata.mdm.backend.service.data.driver.CalculableHolder;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.driver.EtalonCompositionDriverType;
import com.unidata.mdm.backend.service.data.driver.RelationHolder;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowService;
import com.unidata.mdm.conf.WorkflowProcessType;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Abstract rel component - executors holder.
 */
@Component
public class RelationDataComponent implements RelationComponent {
    /**
     * Upsert relation (single relto) listener qualifier.
     */
    private static final String UPSERT_RELATION_ACTION_LISTENER_QUALIFIER = "upsertRelationActionListener";

    /**
     * Calculate etalon relation (single relto) listener qualifier.
     */
    private static final String CALCULATE_ETALON_ACTION_LISTENER_QUALIFIER = "etalonCalculationRelationActionListener";
    /**
     * Get relation (single relto) listener qualifier name.
     */
    private static final String GET_RELATION_ACTION_LISTENER_QUALIFIER = "getRelationActionListener";
    /**
     * Delete relation (single relto) listener qualifier name.
     */
    private static final String DELETE_RELATION_ACTION_LISTENER_QUALIFIER = "deleteRelationActionListener";
    /**
     * 'Upsert' action listener.
     */
    @Autowired
    @Qualifier(value = UPSERT_RELATION_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<UpsertRelationRequestContext> upsertRelationActionListener;
    /**
     * 'Upsert' action listener.
     */
    @Autowired
    @Qualifier(value = CALCULATE_ETALON_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<UpsertRelationRequestContext> calculateEtalonRelationActionListener;
    /**
     * 'Get' action listener.
     */
    @Autowired
    @Qualifier(value = GET_RELATION_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<GetRelationRequestContext> getRelationActionListener;
    /**
     * 'Delete' action listener.
     */
    @Autowired
    @Qualifier(value = DELETE_RELATION_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<DeleteRelationRequestContext> deleteRelationActionListener;
    /**
     * WF service instance.
     */
    @Autowired(required = false)
    private WorkflowService workflowService;
    /**
     * Etalon records component.
     */
    @Autowired
    private EtalonRecordsComponent etalonRecordsComponent;
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Relations DAO.
     */
    @Autowired
    private RelationsDao relationsDao;
    /**
     * Etalon composer.
     */
    @Autowired
    private EtalonComposer etalonComposer;

    @Autowired
    private AuditEventsWriter auditEventsWriter;

    /**
     * @return the upsertRelationActionListener
     */
    public DataRecordLifecycleListener<UpsertRelationRequestContext> getUpsertRelationActionListener() {
        return upsertRelationActionListener;
    }
    /**
     * @return the getRelationActionListener
     */
    public DataRecordLifecycleListener<GetRelationRequestContext> getGetRelationActionListener() {
        return getRelationActionListener;
    }
    /**
     * @return the deleteRelationActionListener
     */
    public DataRecordLifecycleListener<DeleteRelationRequestContext> getDeleteRelationActionListener() {
        return deleteRelationActionListener;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRelationDTO upsert(UpsertRelationRequestContext ctx, RelationDef relation) {

        MeasurementPoint.start();
        try {

            // 1. Upsert
            upsertOrigin(ctx);
            upsertEtalon(ctx);

            // 2. Collect
            return upsertContextToResult(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRelationDTO delete(DeleteRelationRequestContext ctx, RelationDef relation) {

        MeasurementPoint.start();
        try {

            // 1. Delete
            deleteOrigin(ctx);
            deleteEtalon(ctx);

            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_SUCCESS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeSuccessEvent(AuditActions.DATA_DELETE_RELATION, ctx);
            }

            // 2. Collect
            return deleteContextToResult(ctx);
        } catch (Exception e) {
            if (!ctx.isSuppressAudit() && AuditLevel.AUDIT_ERRORS <= ctx.getAuditLevel()) {
                auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_DELETE_RELATION, e, ctx);
            }
            throw e;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public GetRelationDTO get(GetRelationRequestContext ctx, RelationDef relation) {

        MeasurementPoint.start();
        try {

            // 1. Run before for key resolution and security check
            getRelationActionListener.before(ctx);

            RelationKeys keys = ctx.relationKeys();

            // 2. Check tasks for being present
            List<WorkflowTaskDTO> tasks = null;
            if (ctx.isTasks()) {

                GetTasksRequestContext tCtx = new GetTasksRequestContextBuilder()
                    .assignedUser(SecurityUtils.getCurrentUserName())
                    .processKey(keys.getFrom().getEtalonKey().getId())
                    .build();

                tasks = workflowService == null ? Collections.emptyList() : workflowService.tasks(tCtx);
            }

            boolean hasEditTasks = tasks != null && tasks.stream().anyMatch(r -> r.getProcessType() == WorkflowProcessType.RECORD_EDIT);
            boolean loadDrafts = ctx.isTasks() && (hasEditTasks || SecurityUtils.isAdminUser()) || ctx.isIncludeDrafts();

            // 3. Load etalon data
            EtalonRelation etalon = loadEtalon(keys, relation, ctx.getForDate(), ctx.getForOperationId(), loadDrafts);

            GetRelationDTO dto = new GetRelationDTO(keys, relation.getName(), RelationType.fromValue(relation.getRelType().name()));
            dto.setEtalon(etalon);
            dto.setTasks(tasks);
            dto.setRights(SecurityUtils.calculateRightsForTopLevelResource(
                relation.getRelType() == RelType.CONTAINS ? keys.getTo().getEntityName() : keys.getFrom().getEntityName(),
                etalon != null ? etalon.getInfoSection().getStatus() : null,
                etalon != null ? etalon.getInfoSection().getApproval() : null,
                hasEditTasks, true));

            // 4. Run after (empty for now)
            getRelationActionListener.after(ctx);

            return dto;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Utility method. Made public due to usage by batch.
     * @param ctx the context
     * @return result
     */
    public UpsertRelationDTO upsertContextToResult(UpsertRelationRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);

        UpsertRelationDTO result = new UpsertRelationDTO(keys, relation.getName(), RelationType.valueOf(relation.getRelType().name()));
        result.setAction(ctx.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION));

        result.setRights(ctx.getFromStorage(StorageId.RELATIONS_FROM_RIGHTS)
                instanceof ResourceSpecificRightDTO ?
                ctx.getFromStorage(StorageId.RELATIONS_FROM_RIGHTS):
                new ResourceSpecificRightDTO(ctx.getFromStorage(StorageId.RELATIONS_FROM_RIGHTS)));

        result.setValidFrom(relation.getRelType() == RelType.CONTAINS
                ? ((UpsertRequestContext) ctx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT)).getValidFrom()
                : ctx.getValidFrom());
        result.setValidTo(relation.getRelType() == RelType.CONTAINS
                ? ((UpsertRequestContext) ctx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT)).getValidTo()
                : ctx.getValidTo());
        result.setEtalon(ctx.getFromStorage(StorageId.RELATIONS_ETALON_DATA));
        result.setTasks(null);

        result.setErrors(ctx.getFromStorage(StorageId.PROCESS_ERRORS));

        return result;
    }
    /**
     * Creates delete relation result upon completion.
     * @param ctx the context
     * @return result
     */
    public DeleteRelationDTO deleteContextToResult(DeleteRelationRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);

        DeleteRelationDTO result = new DeleteRelationDTO(keys,
                relation.getName(),
                RelationType.valueOf(relation.getRelType().name()));
        result.setErrors(ctx.getFromStorage(StorageId.PROCESS_ERRORS));

        return result;
    }
    /**
     * Upsert origin relation.
     * @param ctx the context to upsert
     */
    public void upsertOrigin(UpsertRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Run before
            upsertRelationActionListener.before(ctx);

            // 2. Upsert
            RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            commonRelationsComponent.upsertRelation(ctx, relation);

            // 3. Run after
            upsertRelationActionListener.after(ctx);

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Upsert etalon relation.
     * @param ctx the context to upsert
     */
    public void upsertEtalon(UpsertRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Upsert etalon
            RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            EtalonRelation etalon;
            if (relation.getRelType() == RelType.CONTAINS) {
                etalon = upsertContainmentEtalon(ctx);
            } else {
                etalon = upsertRelToEtalon(ctx);
            }

            ctx.putToStorage(StorageId.RELATIONS_ETALON_DATA, etalon);

            // 2. Run after
            calculateEtalonRelationActionListener.after(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Deletes origin data for a relation.
     * @param ctx the context
     */
    public void deleteOrigin(DeleteRelationRequestContext ctx) {
        MeasurementPoint.start();
        try {

            // 1. Run before, containment deleted here
            deleteRelationActionListener.before(ctx);

            // 2. Delete rel. origin
            RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            commonRelationsComponent.deleteRelation(ctx, relation);

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Deletes etalon data for a relation.
     * @param ctx the context
     */
    public void deleteEtalon(DeleteRelationRequestContext ctx) {
        MeasurementPoint.start();
        try {
            // NOP
            // 1. Run after
            deleteRelationActionListener.after(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does real rel to etalon calculation.
     * @param ctx the context
     * @return etalon relation
     */
    private EtalonRelation upsertRelToEtalon(UpsertRelationRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION);
        RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);

        RelationKeys keys = ctx.relationKeys();
        Date asOf = ctx.getValidFrom() != null ? ctx.getValidFrom() : ctx.getValidTo();
        EtalonRelation etalon = null;

        if (action == UpsertAction.INSERT) {
            OriginRelation originRelation = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
            etalon = new EtalonRelationImpl()
                    .withDataRecord(originRelation)
                    .withInfoSection(new EtalonRelationInfoSection()
                            .withCreateDate(ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP))
                            .withUpdateDate(ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP))
                            .withCreatedBy(SecurityUtils.getCurrentUserName())
                            .withUpdatedBy(SecurityUtils.getCurrentUserName())
                            .withStatus(keys.getEtalonStatus())
                            .withApproval(keys.getEtalonState())
                            .withPeriodId(Objects.isNull(ctx.getValidTo()) ? SearchUtils.ES_TIMELINE_PERIOD_ID_UPPER_BOUND : ctx.getValidTo().getTime())
                            .withValidFrom(ctx.getValidFrom())
                            .withValidTo(ctx.getValidTo())
                            .withRelationEtalonKey(keys.getEtalonId())
                            .withRelationName(keys.getRelationName())
                            .withFromEtalonKey(keys.getFrom().getEtalonKey())
                            .withFromEntityName(keys.getFrom().getEntityName())
                            .withToEtalonKey(keys.getTo().getEtalonKey())
                            .withToEntityName(keys.getTo().getEntityName())
                            .withType(RelationType.valueOf(relation.getRelType().name())));

        } else {
            etalon = loadEtalon(keys, relation, asOf, null, ctx.isIncludeDraftVersions());
        }

        if (etalon != null) {

            IndexRequestContext context = IndexRequestContext.builder()
                    .relations(Collections.singletonList(etalon))
                    .drop(action == UpsertAction.UPDATE)
                    .entity(etalon.getInfoSection().getFromEntityName())
                    .build();

            if (ctx.isBatchUpsert()) {
                RelationBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                batchSet.setIndexRequestContext(context);
            } else {
                searchService.index(context);
            }
        }

        return etalon;
    }
    /**
     * Does real conntainnment etalon calculation.
     * @param ctx the context
     * @return etalon relation
     */
    private EtalonRelation upsertContainmentEtalon(UpsertRelationRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION);
        RelationDef def = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);

        UpsertRequestContext uCtx = ctx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT);
        etalonRecordsComponent.upsertEtalon(uCtx);

        EtalonRecord record = uCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
        EtalonRelation relation = null;
        if (record != null) {

            Date asOf = record.getInfoSection().getValidFrom() != null
                ? record.getInfoSection().getValidFrom()
                : record.getInfoSection().getValidTo();

            relation = new EtalonRelationImpl()
                .withDataRecord(record)
                .withInfoSection(commonRelationsComponent.loadEtalonRelationInfoSection(
                        ctx.relationKeys(), asOf, record.getInfoSection().getApproval() == ApprovalState.PENDING, def));

            IndexRequestContext context = IndexRequestContext.builder()
                    .relations(Collections.singletonList(relation))
                    .drop(action == UpsertAction.UPDATE)
                    .entity(relation.getInfoSection().getFromEntityName())
                    .build();

            if (ctx.isBatchUpsert()) {
                RelationBatchSet batchSet = ctx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                batchSet.setIndexRequestContext(context);
            } else {
                searchService.index(context);
            }
        }

        return relation;
    }
    /**
     * Metarialize etalon relation record.
     * @param keys the keys
     * @param def relation definition
     * @param asOf as of date
     * @param operationId the operation id
     * @param loadDrafts see drafts or not
     * @return etalon relation
     */
    public EtalonRelation loadEtalon(RelationKeys keys, RelationDef def, Date asOf, String operationId, boolean loadDrafts) {

        if (def.getRelType() == RelType.CONTAINS) {
            return loadContainmentEtalon(keys, def, asOf, operationId, loadDrafts);
        }

        return loadRelToEtalon(keys, def, asOf, operationId, loadDrafts);
    }

    /**
     * Loads rel to etalon.
     * @param keys the keys
     * @param def the relation definition
     * @param asOf date
     * @param operationId op. id
     * @param loadDrafts include draft versions or not
     * @return etalon
     */
    private EtalonRelation loadContainmentEtalon(RelationKeys keys, RelationDef def, Date asOf, String operationId, boolean loadDrafts) {

        MeasurementPoint.start();
        try {
            EtalonRecord composed
                = etalonRecordsComponent.loadEtalonData(keys.getTo().getEtalonKey().getId(), asOf, null, null, operationId, false, loadDrafts);

            return new EtalonRelationImpl()
                    .withDataRecord(composed)
                    .withInfoSection(commonRelationsComponent.loadEtalonRelationInfoSection(keys, asOf, loadDrafts, def));

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads containment etalon.
     * @param keys the keys
     * @param def the relation definition
     * @param asOf date
     * @param operationId op. id
     * @param loadDrafts include draft versions or not
     * @return etalon
     */
    private EtalonRelation loadRelToEtalon(RelationKeys keys, RelationDef def, Date asOf, String operationId, boolean loadDrafts) {

        MeasurementPoint.start();
        try {
            List<OriginsVistoryRelationsPO> versions;
            if (Objects.nonNull(operationId)) {
                versions = relationsDao.loadRelationVersions(keys.getEtalonId(), asOf, operationId, loadDrafts);
            } else {
                versions = relationsDao.loadRelationVersions(keys.getEtalonId(), asOf, loadDrafts);
            }

            if (CollectionUtils.isEmpty(versions)) {
                return null;
            }

            List<CalculableHolder<OriginRelation>> calculables = new ArrayList<>(versions.size());
            for (OriginsVistoryRelationsPO po : versions) {

                OriginRelationImpl ori = new OriginRelationImpl()
                        .withDataRecord(po.getData())
                        .withInfoSection(commonRelationsComponent.loadOriginRelationInfoSection(po, def));

                calculables.add(new RelationHolder(ori));
            }

            DataRecord record = etalonComposer.compose(EtalonCompositionDriverType.BVR, calculables, false, false);
            if (record != null) {
                return new EtalonRelationImpl()
                        .withDataRecord(record)
                        .withInfoSection(commonRelationsComponent.loadEtalonRelationInfoSection(keys, asOf, loadDrafts, def));
            }

            return null;

        } finally {
            MeasurementPoint.stop();
        }
    }
}

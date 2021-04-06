package com.unidata.mdm.backend.service.data.relations;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.context.AbstractRelationToRequestContext;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.AbstractWorkflowProcessStarterAfterExecutor;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.conf.WorkflowProcessType;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Former top-level relations executors.
 */
@Component
public class RelationsValidationComponent extends AbstractWorkflowProcessStarterAfterExecutor {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationsValidationComponent.class);
    /**
     * Common records component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * {@link UpsertRelationsRequestContext} before.
     * @param ctx the context
     */
    public void before(UpsertRelationsRequestContext ctx) {

        if (CollectionUtils.isEmpty(ctx.getRelations())) {
            return;
        }

        MeasurementPoint.start();
        try {

            RelationDef relation = null;
            for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                final RelationDef thisRelation = metaModelService.getRelationById(entry.getKey());
                if (thisRelation == null) {
                    final String message = "Relation {} not found. Stopping.";
                    LOGGER.warn(message, entry.getKey());
                    throw new DataProcessingException(message,
                            ExceptionId.EX_DATA_RELATIONS_UPSERT_RELATION_NOT_FOUND,
                            entry.getKey());
                }

                if (thisRelation.getRelType() == RelType.REFERENCES
                 && entry.getValue().size() != 1) {
                    final String message
                        = "More then one (or zero) reference(s) supplied for the same id(s). Stopping.";
                    LOGGER.warn(message);
                    throw new BusinessException(message, ExceptionId.EX_DATA_RELATIONS_UPSERT_MORE_THEN_ONE_REFERENCE);
                }

                entry.getValue().forEach(uCtx -> uCtx.putToStorage(StorageId.RELATIONS_META_DEF, thisRelation));
                relation = thisRelation;
            }

            if (relation == null) {
                final String message = "Relation delete received invalid input. Stopping.";
                LOGGER.warn(message);
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_RELATIONS_UPSERT_INVALID_INPUT);
            }

            commonRelationsComponent.ensureAndGetFromRecordKeys(ctx);

            RecordKeys fromKeys = ctx.keys();
            String entityName = fromKeys != null ? fromKeys.getEntityName() : relation.getFromEntity();

            putResourceRightsAndWorkflowAssignments(ctx, StorageId.RELATIONS_FROM_RIGHTS, StorageId.RELATIONS_FROM_WF_ASSIGNMENTS,
                    entityName, WorkflowProcessType.RECORD_EDIT);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@link UpsertRelationsRequestContext} after.
     * @param ctx the context
     */
    public void after(UpsertRelationsRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Find changed records, which were put into pending state,
            // while parent is not in pending state
            UpsertRelationRequestContext hit = null;
            for (Entry<String, List<UpsertRelationRequestContext>> entry : ctx.getRelations().entrySet()) {

                hit = entry.getValue().stream().filter(urrc -> {
                    RelationKeys keys = urrc.getFromStorage(StorageId.RELATIONS_RELATION_KEY);
                    return keys.isPending() && !keys.getFrom().isPending();
                }).findFirst().map(urrc -> urrc).orElse(null);

                if (hit != null) {
                    break;
                }
            }

            // 2. Start WF, diff found
            if (hit != null && workflowService != null) {

                RelationKeys keys = hit.getFromStorage(StorageId.RELATIONS_RELATION_KEY);
                WorkflowAssignmentDTO assignment = hit.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);
                Date point = hit.getValidFrom() != null ? hit.getValidFrom() : hit.getValidTo();

                EtalonRecord etalon = etalonsComponent.loadEtalonData(
                    keys.getFrom().getEtalonKey().getId(), point, null, null, null, false, false);

                Date validFrom = etalon != null ? etalon.getInfoSection().getValidFrom() : hit.getValidFrom();
                Date validTo = etalon != null ? etalon.getInfoSection().getValidTo() : hit.getValidTo();

                commonRecordsComponent.changeApproval(keys.getFrom().getEtalonKey().getId(), ApprovalState.PENDING);
                commonRecordsComponent.putEtalonStateDraft(keys.getFrom().getEtalonKey().getId(), keys.getFrom().getEtalonStatus(), null);

                workflowService.start(createStartWorkflowContext(assignment, keys.getFrom(), etalon, true, false,
                        ctx.getOperationId(), validFrom, validTo));
            }

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@link GetRelationsRequestContext} after.
     * @param ctx the context
     */
    public void before(GetRelationsRequestContext ctx) {

        MeasurementPoint.start();
        try {

            Collection<String> relationNames = null;
            if (!CollectionUtils.isEmpty(ctx.getRelations())) {
                relationNames = ctx.getRelations().keySet();
            } else if (!CollectionUtils.isEmpty(ctx.getRelationNames())) {
                relationNames = ctx.getRelationNames();
            }

            if (CollectionUtils.isEmpty(relationNames)) {
                final String message = "Relation get received invalid input. Stopping.";
                LOGGER.warn(message);
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_RELATIONS_GET_INVALID_INPUT);
            }

            RelationDef relation = null;
            for (String name : relationNames) {

                final RelationDef thisRelation = metaModelService.getRelationById(name);
                if (thisRelation == null) {
                    final String message = "Relation [{}] not found. Stopping.";
                    LOGGER.warn(message, name);
                    throw new DataProcessingException(message,
                            ExceptionId.EX_DATA_RELATIONS_GET_RELATION_NOT_FOUND,
                            name);
                }

                // Set rel defs, if contexts supplied
                List<GetRelationRequestContext> relations = ctx.getRelations().get(name);
                if (!CollectionUtils.isEmpty(relations)) {
                    relations.forEach(gCtx -> gCtx.putToStorage(StorageId.RELATIONS_META_DEF, thisRelation));
                }

                relation = thisRelation;
            }

            commonRelationsComponent.ensureAndGetFromRecordKeys(ctx);

            RecordKeys fromKeys = ctx.keys();
            String entityName = fromKeys != null ? fromKeys.getEntityName() : relation.getFromEntity();

            putResourceRightsAndWorkflowAssignments(ctx, StorageId.RELATIONS_FROM_RIGHTS, StorageId.RELATIONS_FROM_WF_ASSIGNMENTS,
                    entityName, WorkflowProcessType.RECORD_EDIT);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@link GetRelationsRequestContext} after.
     * @param ctx the context
     */
    public void after(GetRelationsRequestContext ctx) {
        // NOP
    }
    /**
     * {@link DeleteRelationsRequestContext} after.
     * @param ctx the context
     */
    public void before(DeleteRelationsRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RelationDef relation = null;
            for (Entry<String, List<DeleteRelationRequestContext>> entry :
                ctx.getRelations().entrySet()) {

                final RelationDef thisRelation = metaModelService.getRelationById(entry.getKey());
                if (thisRelation == null) {
                    final String message = "Relation {} not found. Stopping.";
                    LOGGER.warn(message, entry.getKey());
                    throw new DataProcessingException(message,
                            ExceptionId.EX_DATA_RELATIONS_DELETE_RELATION_NOT_FOUND,
                            entry.getKey());
                }

                entry.getValue().forEach(dCtx -> dCtx.putToStorage(StorageId.RELATIONS_META_DEF, thisRelation));
                relation = thisRelation;
            }

            if (relation == null) {
                final String message = "Relation delete received invalid input. Stopping.";
                LOGGER.warn(message);
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_RELATIONS_DELETE_INVALID_INPUT);
            }

            commonRelationsComponent.ensureAndGetFromRecordKeys(ctx);

            RecordKeys fromKeys = ctx.keys();
            String entityName = fromKeys != null ? fromKeys.getEntityName() : relation.getFromEntity();

            putResourceRightsAndWorkflowAssignments(ctx, StorageId.RELATIONS_FROM_RIGHTS, StorageId.RELATIONS_FROM_WF_ASSIGNMENTS,
                    entityName, WorkflowProcessType.RECORD_EDIT);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@link DeleteRelationsRequestContext} after.
     * @param ctx the context
     */
    public void after(DeleteRelationsRequestContext ctx) {
        // NOP
    }
    /**
     * {@link UpsertRelationRequestContext} after.
     * @param ctx the context
     */
    public void before(UpsertRelationRequestContext ctx) {
        beforeRelationTo(ctx);
    }
    /**
     * {@link UpsertRelationRequestContext} after.
     * @param ctx the context
     */
    public void after(UpsertRelationRequestContext ctx) {
        afterRelationTo(ctx);
    }
    /**
     * {@link GetRelationRequestContext} after.
     * @param ctx the context
     */
    public void before(GetRelationRequestContext ctx) {
        beforeRelationTo(ctx);
    }
    /**
     * {@link GetRelationRequestContext} after.
     * @param ctx the context
     */
    public void after(GetRelationRequestContext ctx) {
        // NOP
    }
    /**
     * {@link DeleteRelationRequestContext} after.
     * @param ctx the context
     */
    public void before(DeleteRelationRequestContext ctx) {
        beforeRelationTo(ctx);
    }
    /**
     * {@link DeleteRelationRequestContext} after.
     * @param ctx the context
     */
    public void after(DeleteRelationRequestContext ctx) {
        afterRelationTo(ctx);
    }
    /**
     * Puts resource rights and WF assignments.
     * @param ctx the context to put to
     * @param resourceRightsId
     * @param wfAssignmentId
     * @param resourceName
     * @param processType
     */
    private void putResourceRightsAndWorkflowAssignments(CommonRequestContext ctx, StorageId resourceRightsId, StorageId wfAssignmentId,
            String resourceName, WorkflowProcessType processType) {

        ctx.putToStorage(resourceRightsId, SecurityUtils.getRightsForResourceWithDefault(resourceName));
        ctx.putToStorage(wfAssignmentId, workflowService.getAssignmentsByEntityNameAndType(resourceName, processType));
    }
    /**
     * Before relation to check
     * @param ctx the context to check
     */
    private void beforeRelationTo(AbstractRelationToRequestContext ctx) {

        MeasurementPoint.start();
        try {

            if (!ctx.isValidRelationKey()) {
                final String message = "Invalid relation to context. Keys missing.";
                LOGGER.warn(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATION_MISSING_RELTO_KEY);
            }

            RelationKeys keys = commonRelationsComponent.identify(ctx);
            if (keys ==  null) {
                final String message = "Relation not found by supplied keys.";
                LOGGER.warn(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATION_NOT_FOUND_BY_KEY);
            }

            ctx.putToStorage(ctx.relationKeysId(), keys);
            ctx.putToStorage(StorageId.RELATIONS_META_DEF, metaModelService.getRelationById(keys.getRelationName()));

            String entityName = keys.getFrom().getEntityName();

            putResourceRightsAndWorkflowAssignments(ctx, StorageId.RELATIONS_FROM_RIGHTS, StorageId.RELATIONS_FROM_WF_ASSIGNMENTS,
                    entityName, WorkflowProcessType.RECORD_EDIT);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * After relation to check
     * @param ctx the context to check
     */
    private void afterRelationTo(AbstractRelationToRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RelationKeys keys = ctx.relationKeys();
            if (keys.isPending() && !keys.getFrom().isPending() && workflowService != null) {

                WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);
                if (Objects.isNull(assignment)) {
                    return;
                }

                EtalonRecord etalon = etalonsComponent.loadEtalonData(
                    keys.getFrom().getEtalonKey().getId(), null, null, null, null, true, true);

                Date validFrom = etalon != null ? etalon.getInfoSection().getValidFrom() : null;
                Date validTo = etalon != null ? etalon.getInfoSection().getValidTo() : null;

                commonRecordsComponent.changeApproval(keys.getFrom().getEtalonKey().getId(), ApprovalState.PENDING);
                commonRecordsComponent.putEtalonStateDraft(keys.getFrom().getEtalonKey().getId(), keys.getFrom().getEtalonStatus(), null);

                workflowService.start(
                    createStartWorkflowContext(assignment, keys.getFrom(), etalon, true, false,
                        ctx.getOperationId(), validFrom, validTo));
            }
        } finally {
            MeasurementPoint.stop();
        }
    }
}

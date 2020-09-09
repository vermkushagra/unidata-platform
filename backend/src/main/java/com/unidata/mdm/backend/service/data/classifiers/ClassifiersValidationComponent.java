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

package com.unidata.mdm.backend.service.data.classifiers;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.AbstractClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext;
import com.unidata.mdm.backend.common.context.GetProcessRequestContext.GetProcessRequestContextBuilder;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowProcessDTO;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.AbstractWorkflowProcessStarterAfterExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.data.listener.RequestContextSetup;
import com.unidata.mdm.backend.service.data.relations.RelationsValidationComponent;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Pre-validation / key resolution code, analogously to {@link RelationsValidationComponent}.
 */
@Component
public class ClassifiersValidationComponent extends AbstractWorkflowProcessStarterAfterExecutor
    implements RequestContextSetup<CommonRequestContext> {

    /**
     * Get classifiers listener.
     */
    private static final String GET_CLASSIFIERS_ACTION_LISTENER_QUALIFIER = "getClassifiersActionListener";

    /**
     * Get multiple classifiers executors.
     */
    @Autowired
    @Qualifier(GET_CLASSIFIERS_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<GetClassifiersDataRequestContext> getClassifiersActionListener;

    /**
     * CRC.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;

    public void before(GetClassifiersDataRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // Nothing else so far.
            ensureAndGetRecordKeys(ctx);

            getClassifiersActionListener.before(ctx);

            RecordKeys recordKeys = ctx.keys();
            String entityName = recordKeys != null ? recordKeys.getEntityName() : ctx.getEntityName();

            putResourceRights(ctx, StorageId.COMMON_ACCESS_RIGHTS, entityName);
            putWorkflowAssignments(ctx, StorageId.COMMON_WF_ASSIGNMENTS, entityName, WorkflowProcessType.RECORD_EDIT);

        } finally {
            MeasurementPoint.stop();
        }
    }

    public void after(GetClassifiersDataRequestContext ctx) {
        getClassifiersActionListener.after(ctx);
    }

    public void before(DeleteClassifiersDataRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // Nothing else so far.
            ensureAndGetRecordKeys(ctx);

            RecordKeys recordKeys = ctx.keys();
            String entityName = recordKeys != null ? recordKeys.getEntityName() : ctx.getEntityName();

            putResourceRights(ctx, StorageId.COMMON_ACCESS_RIGHTS, entityName);
            putWorkflowAssignments(ctx, StorageId.COMMON_WF_ASSIGNMENTS, entityName, WorkflowProcessType.RECORD_EDIT);

        } finally {
            MeasurementPoint.stop();
        }
    }
    public void after(DeleteClassifiersDataRequestContext ctx) {
        MeasurementPoint.start();
        try {

            // 1. Find changed records, which were put into pending state,
            // while parent is not in pending state
            DeleteClassifierDataRequestContext hit = null;
            for (Entry<String, List<DeleteClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {

                hit = entry.getValue().stream()
                        .filter(urrc -> urrc.classifierKeys().isPending() && !urrc.classifierKeys().getRecord().isPending())
                        .findFirst().orElse(null);

                if (hit != null) {
                    break;
                }
            }

            // 2. Start WF, diff found
            if (hit != null && workflowService != null) {

                ClassifierKeys keys = hit.classifierKeys();

                // Skip if a process instance is already running for this etalon id
                GetProcessRequestContext pCtx = new GetProcessRequestContextBuilder()
                        .processKey(keys.getRecord().getEtalonKey().getId())
                        .skipVariables(true)
                        .build();

                WorkflowProcessDTO current = workflowService.process(pCtx);
                if (current != null) {
                    return;
                }

                WorkflowAssignmentDTO assignment = hit.getFromStorage(StorageId.COMMON_WF_ASSIGNMENTS);
                Date point = hit.getValidFrom() != null ? hit.getValidFrom() : hit.getValidTo();

                EtalonRecord etalon = etalonsComponent.loadEtalonData(
                    keys.getRecord().getEtalonKey().getId(), point, null, null, null, false, false);

                Date validFrom = etalon != null ? etalon.getInfoSection().getValidFrom() : hit.getValidFrom();
                Date validTo = etalon != null ? etalon.getInfoSection().getValidTo() : hit.getValidTo();

                commonRecordsComponent.changeApproval(keys.getRecord().getEtalonKey().getId(), ApprovalState.PENDING);
                commonRecordsComponent.putEtalonStateDraft(keys.getRecord().getEtalonKey().getId(), keys.getRecord().getEtalonStatus(), null);

                workflowService.start(createStartWorkflowContext(assignment, keys.getRecord(), etalon, true, false,
                        ctx.getOperationId(), validFrom, validTo));
            }

        } finally {
            MeasurementPoint.stop();
        }
    }
    public void before(UpsertClassifiersDataRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // Nothing else so far.
            ensureAndGetRecordKeys(ctx);

            RecordKeys recordKeys = ctx.keys();
            String entityName = recordKeys != null ? recordKeys.getEntityName() : ctx.getEntityName();

            putResourceRights(ctx, StorageId.COMMON_ACCESS_RIGHTS, entityName);
            putWorkflowAssignments(ctx, StorageId.COMMON_WF_ASSIGNMENTS, entityName, WorkflowProcessType.RECORD_EDIT);

        } finally {
            MeasurementPoint.stop();
        }
    }
    public void after(UpsertClassifiersDataRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Find changed records, which were put into pending state,
            // while parent is not in pending state
            UpsertClassifierDataRequestContext hit = null;
            for (Entry<String, List<UpsertClassifierDataRequestContext>> entry : ctx.getClassifiers().entrySet()) {

                hit = entry.getValue().stream()
                        .filter(urrc -> urrc.classifierKeys().isPending() && !urrc.classifierKeys().getRecord().isPending())
                        .findFirst()
                        .map(urrc -> urrc)
                        .orElse(null);

                if (hit != null) {
                    break;
                }
            }

            // 2. Start WF, diff found
            if (hit != null && workflowService != null) {

                ClassifierKeys keys = hit.classifierKeys();

                // Skip if a process instance is already running for this etalon id
                GetProcessRequestContext pCtx = new GetProcessRequestContextBuilder()
                        .processKey(keys.getRecord().getEtalonKey().getId())
                        .skipVariables(true)
                        .build();

                WorkflowProcessDTO current = workflowService.process(pCtx);
                if (current != null) {
                    return;
                }

                WorkflowAssignmentDTO assignment = hit.getFromStorage(StorageId.COMMON_WF_ASSIGNMENTS);
                Date point = hit.getValidFrom() != null ? hit.getValidFrom() : hit.getValidTo();

                EtalonRecord etalon = etalonsComponent.loadEtalonData(
                    keys.getRecord().getEtalonKey().getId(), point, null, null, null, false, false);

                Date validFrom = etalon != null ? etalon.getInfoSection().getValidFrom() : hit.getValidFrom();
                Date validTo = etalon != null ? etalon.getInfoSection().getValidTo() : hit.getValidTo();

                commonRecordsComponent.changeApproval(keys.getRecord().getEtalonKey().getId(), ApprovalState.PENDING);
                commonRecordsComponent.putEtalonStateDraft(keys.getRecord().getEtalonKey().getId(), keys.getRecord().getEtalonStatus(), null);

                workflowService.start(createStartWorkflowContext(assignment, keys.getRecord(), etalon, true, false,
                        ctx.getOperationId(), validFrom, validTo));
            }

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Before single context action.
     * @param ctx the context
     */
    public void before(AbstractClassifierDataRequestContext ctx) {
        // NOPE
    }
    /**
     * After single context action.
     * @param ctx the context
     */
    public void after(AbstractClassifierDataRequestContext ctx) {

        MeasurementPoint.start();
        try {

            if (ctx instanceof GetClassifierDataRequestContext) {
                return;
            }

            checkWorkflowModifications(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Ensures record context.
     * @param ctx the context
     * @return keys
     */
    private void ensureAndGetRecordKeys(RecordIdentityContext ctx) {
        RecordKeys keys = ctx.keys();
        if (keys == null) {
            commonRecordsComponent.identify(ctx);
        }
    }

    /**
     * After classifier to check
     * @param ctx the context to check
     */
    private void checkWorkflowModifications(AbstractClassifierDataRequestContext ctx) {

        MeasurementPoint.start();
        try {

            ClassifierKeys keys = ctx.classifierKeys();
            if (keys.isPending() && !keys.getRecord().isPending() && workflowService != null) {

                WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.COMMON_WF_ASSIGNMENTS);
                if (Objects.isNull(assignment)) {
                    return;
                }

                // Skip if a process instance is already running for this etalon id
                GetProcessRequestContext pCtx = new GetProcessRequestContextBuilder()
                        .processKey(keys.getRecord().getEtalonKey().getId())
                        .skipVariables(true)
                        .build();

                WorkflowProcessDTO current = workflowService.process(pCtx);
                if (current != null) {
                    return;
                }

                ctx.skipNotification();

                EtalonRecord etalon = etalonsComponent.loadEtalonData(
                    keys.getRecord().getEtalonKey().getId(), null, null, null, null, true, true);

                Date validFrom = etalon != null ? etalon.getInfoSection().getValidFrom() : null;
                Date validTo = etalon != null ? etalon.getInfoSection().getValidTo() : null;

                commonRecordsComponent.changeApproval(keys.getRecord().getEtalonKey().getId(), ApprovalState.PENDING);
                commonRecordsComponent.putEtalonStateDraft(keys.getRecord().getEtalonKey().getId(), keys.getRecord().getEtalonStatus(), null);

                workflowService.start(
                    createStartWorkflowContext(assignment, keys.getRecord(), etalon, true, false,
                        ctx.getOperationId(), validFrom, validTo));
            }
        } finally {
            MeasurementPoint.stop();
        }
    }
}

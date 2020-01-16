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

package org.unidata.mdm.data.service.segments.relations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.DataRecordsService;
import org.unidata.mdm.data.service.segments.ContainmentRelationSupport;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * Executor responsible for modifying relations have an alias key.
 */
@Component(RelationUpsertContainmentExecutor.SEGMENT_ID)
public class RelationUpsertContainmentExecutor extends Point<UpsertRelationRequestContext> implements ContainmentRelationSupport {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationUpsertContainmentExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_CONTAINMENT]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.upsert.containment.description";
    /**
     * Records service.
     */
    @Autowired
    private DataRecordsService dataRecordsService;
    /**
     * Constructor.
     */
    public RelationUpsertContainmentExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Collect prerequisites
            if (ctx.relationType() != RelationType.CONTAINS) {
                return;
            }

            // 2. Collect assignment state
            RelationKeys relationKeys = ctx.relationKeys();
            ApprovalState state = ApprovalState.APPROVED;

            // 3. Upsert 'TO' record.
            String targetEtalonId = null;
            String targetOriginId = null;
            String targetSourceSystem = null;
            String targetExternalId = null;
            String targetEntityName = null;

            if (relationKeys != null) {
                targetOriginId = relationKeys.getOriginKey().getTo().getId();
                targetSourceSystem = relationKeys.getOriginKey().getTo().getSourceSystem();
                targetExternalId = relationKeys.getOriginKey().getTo().getExternalId();
                targetEtalonId = relationKeys.getEtalonKey().getTo().getId();
                targetEntityName = relationKeys.getToEntityName();
            } else {
                targetOriginId = ctx.getOriginKey();
                targetSourceSystem = ctx.getSourceSystem();
                targetExternalId = ctx.getExternalId();
                targetEtalonId = ctx.getEtalonKey();
                targetEntityName = ctx.getEntityName();
            }

            // if contains 'to side' is defined, keys must be resolved (not new reference)
            if (targetEtalonId != null && relationKeys == null) {
                final String message = "Containment record upsert to '{}' failed.";
                LOGGER.warn(message, targetEntityName);
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_RELATIONS_UPSERT_CONTAINS_KEYS_INVALID, targetEntityName);
            }

            UpsertRequestContext uCtx = UpsertRequestContext.builder()
                    .record(ctx.getRelation())
                    .etalonKey(targetEtalonId)
                    .originKey(targetOriginId)
                    .sourceSystem(targetSourceSystem)
                    .externalId(targetExternalId)
                    .entityName(targetEntityName)
                    .validFrom(ctx.getValidFrom())
                    .validTo(ctx.getValidTo())
                    .approvalState(state)
                    .batchOperation(ctx.isBatchOperation())
                    .auditLevel(ctx.getAuditLevel())
                    .operationId(ctx.getOperationId())
                    .build();

            uCtx.operationType(ctx.operationType());

            try {
                dataRecordsService.upsertRecord(uCtx);
            } catch (Exception exc) {
                final String message = "Containment record upsert to '{}' failed.";
                LOGGER.warn(message, targetEntityName);
                throw new DataProcessingException(message, exc, DataExceptionIds.EX_DATA_RELATIONS_UPSERT_CONTAINS_FAILED, targetEntityName);
            }

            // 4. Gather keys and context
            RecordKeys toRecordKeys = uCtx.keys();

            ctx.keys(toRecordKeys);
            ctx.containmentContext(uCtx);
            ctx.currentTimeline(mirrorTimeline(relationKeys, uCtx.currentTimeline()));

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

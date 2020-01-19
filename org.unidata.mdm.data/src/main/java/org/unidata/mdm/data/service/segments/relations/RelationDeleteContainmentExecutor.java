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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.segments.ContainmentRelationSupport;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * Executor responsible for dropping containment.
 */
@Component(RelationDeleteContainmentExecutor.SEGMENT_ID)
public class RelationDeleteContainmentExecutor extends Point<DeleteRelationRequestContext> implements ContainmentRelationSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_DELETE_CONTAINMENT]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.delete.containment.description";
    /**
     * The ES.
     */
    @Autowired
    private ExecutionService executionService;
    /**
     * Constructor.
     */
    public RelationDeleteContainmentExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(DeleteRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RelationKeys keys = ctx.relationKeys();
            if (keys.getRelationType() != RelationType.CONTAINS) {
                return;
            }

            ApprovalState state = ApprovalState.APPROVED;
            DeleteRequestContext dCtx = DeleteRequestContext.builder()
                    .etalonKey(keys.getEtalonKey().getTo().getId())
                    .cascade(false)
                    .validFrom(ctx.getValidFrom())
                    .validTo(ctx.getValidTo())
                    .approvalState(state)
                    .workflowAction(ctx.isWorkflowAction() || state == ApprovalState.PENDING)
                    .inactivatePeriod(ctx.isInactivatePeriod())
                    .inactivateEtalon(ctx.isInactivateEtalon())
                    .inactivateOrigin(ctx.isInactivateOrigin())
                    .wipe(ctx.isWipe())
                    .operationId(ctx.getOperationId())
                    .batchOperation(ctx.isBatchOperation())
                    .suppressAudit(ctx.isSuppressAudit())
                    .auditLevel(ctx.getAuditLevel())
                    .build();

            dCtx.keys(keys.getToAsRecordKeys());
            dCtx.timestamp(ctx.timestamp());

            executionService.execute(dCtx);

            ctx.containmentContext(dCtx);
            ctx.currentTimeline(mirrorTimeline(keys, dCtx.currentTimeline()));
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.context.DeleteRelationsRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.dto.DeleteRelationDTO;
import org.unidata.mdm.data.dto.DeleteRelationsDTO;
import org.unidata.mdm.data.dto.RelationStateDTO;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRelationsComponent;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentContainer;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationDeleteConnectorExecutor.SEGMENT_ID)
public class RelationDeleteConnectorExecutor extends Connector<PipelineInput, DeleteRelationsDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATIONS_DELETE_CONNECTOR]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relations.delete.connector.description";
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationDeleteConnectorExecutor.class);
    /**
     * The ES instance.
     */
    @Autowired
    private ExecutionService executionService;
    /**
     * The MMS instance.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * CRC instance.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public RelationDeleteConnectorExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRelationsDTO connect(PipelineInput ctx) {

        InputFragmentContainer target = (InputFragmentContainer) ctx;
        DeleteRelationsRequestContext payload = target.fragment(DeleteRelationsRequestContext.FRAGMENT_ID);
        if (Objects.isNull(payload)) {
            return null;
        }

        if (ctx instanceof RecordIdentityContext) {
            payload.keys(((RecordIdentityContext) ctx).keys());
        }

        return execute(payload, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRelationsDTO connect(PipelineInput ctx, Pipeline p) {

        InputFragmentContainer target = (InputFragmentContainer) ctx;
        DeleteRelationsRequestContext payload = target.fragment(DeleteRelationsRequestContext.FRAGMENT_ID);
        if (Objects.isNull(payload)) {
            return null;
        }

        if (ctx instanceof RecordIdentityContext) {
            payload.keys(((RecordIdentityContext) ctx).keys());
        }

        return execute(payload, p);
    }

    public DeleteRelationsDTO execute(@Nonnull DeleteRelationsRequestContext ctx, @Nullable Pipeline p) {

        MeasurementPoint.start();
        try {

            // 1. First of all check side's keys
            commonRelationsComponent.ensureAndGetFromRecordKeys(ctx);

            // 2. Check input. Return on no input, what is not a crime
            Map<String, List<DeleteRelationRequestContext>> input = ctx.getRelations();
            if (MapUtils.isEmpty(input)) {
                return null;
            }

            // 3. Process stuff
            RecordKeys fromKeys = ctx.keys();

            Map<RelationStateDTO, List<DeleteRelationDTO>> result = new HashMap<>();
            for (Entry<String, List<DeleteRelationRequestContext>> entry : input.entrySet()) {

                if (CollectionUtils.isEmpty(entry.getValue())) {
                    continue;
                }

                // 3.1 Check rel's existance. Fail if not found
                final RelationDef relation = metaModelService.getRelationById(entry.getKey());
                if (relation == null) {
                    final String message = "Relation {} not found. Stopping.";
                    LOGGER.warn(message, entry.getKey());
                    throw new DataProcessingException(message,
                            DataExceptionIds.EX_DATA_RELATIONS_DELETE_RELATION_NOT_FOUND,
                            entry.getKey());
                }

                // 3.2 Set up content and run single gets
                final String resolvedName = relation.getName();
                final RelationType resolvedType = RelationType.fromValue(relation.getRelType().name());

                RelationStateDTO state = new RelationStateDTO(resolvedName, resolvedType);
                List<DeleteRelationDTO> collected = new ArrayList<>(entry.getValue().size());
                for (DeleteRelationRequestContext dCtx : entry.getValue()) {

                    String entityName = fromKeys != null ? fromKeys.getEntityName() : relation.getFromEntity();

                    dCtx.accessRight(SecurityUtils.getRightsForResourceWithDefault(entityName));
                    dCtx.relationName(resolvedName);
                    dCtx.relationType(resolvedType);
                    dCtx.fromKeys(fromKeys);

                    DeleteRelationDTO interim;
                    if (Objects.isNull(p)) {
                        interim = executionService.execute(dCtx);
                    } else {
                        interim = executionService.execute(p, dCtx);
                    }

                    if (Objects.nonNull(interim)) {
                        collected.add(interim);
                    }
                }

                result.put(state, collected);
            }

            return new DeleteRelationsDTO(result);
        } finally {
            MeasurementPoint.stop();
        }
    }
}

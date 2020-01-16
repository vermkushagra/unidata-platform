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
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRelationsRequestContext;
import org.unidata.mdm.data.dto.RelationStateDTO;
import org.unidata.mdm.data.dto.UpsertRelationDTO;
import org.unidata.mdm.data.dto.UpsertRelationsDTO;
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
@Component(RelationsUpsertConnectorExecutor.SEGMENT_ID)
public class RelationsUpsertConnectorExecutor extends Connector<PipelineInput, UpsertRelationsDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATIONS_UPSERT_CONNECTOR]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relations.upsert.connector.description";
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationsUpsertConnectorExecutor.class);
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
    public RelationsUpsertConnectorExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRelationsDTO connect(PipelineInput ctx) {

        InputFragmentContainer target = (InputFragmentContainer) ctx;
        UpsertRelationsRequestContext payload = target.fragment(UpsertRelationsRequestContext.FRAGMENT_ID);
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
    public UpsertRelationsDTO connect(PipelineInput ctx, Pipeline p) {

        InputFragmentContainer target = (InputFragmentContainer) ctx;
        UpsertRelationsRequestContext payload = target.fragment(UpsertRelationsRequestContext.FRAGMENT_ID);
        if (Objects.isNull(payload)) {
            return null;
        }

        if (ctx instanceof RecordIdentityContext) {
            payload.keys(((RecordIdentityContext) ctx).keys());
        }

        return execute(payload, p);
    }

    public UpsertRelationsDTO execute(@Nonnull UpsertRelationsRequestContext ctx, @Nullable Pipeline p) {

        MeasurementPoint.start();
        try {

            // 1. First of all check side's keys
            commonRelationsComponent.ensureAndGetFromRecordKeys(ctx);

            // 2. Check input. Return on no input, what is not a crime
            Map<String, List<UpsertRelationRequestContext>> input = ctx.getRelations();
            if (MapUtils.isEmpty(input)) {
                return null;
            }

            // 3. Process stuff
            RecordKeys fromKeys = ctx.keys();

            Map<RelationStateDTO, List<UpsertRelationDTO>> result = new HashMap<>();
            for (Entry<String, List<UpsertRelationRequestContext>> entry : input.entrySet()) {

                if (CollectionUtils.isEmpty(entry.getValue())) {
                    continue;
                }

                // 3.1 Check rel's existance. Fail if not found
                final RelationDef relation = metaModelService.getRelationById(entry.getKey());
                if (relation == null) {
                    final String message = "Relation {} not found. Stopping.";
                    LOGGER.warn(message, entry.getKey());
                    throw new DataProcessingException(message,
                            DataExceptionIds.EX_DATA_RELATIONS_UPSERT_RELATION_NOT_FOUND,
                            entry.getKey());
                }

                // 3.2 Set up content and run single gets
                final String resolvedName = relation.getName();
                final RelationType resolvedType = RelationType.fromValue(relation.getRelType().name());

                RelationStateDTO state = new RelationStateDTO(resolvedName, resolvedType);
                List<UpsertRelationDTO> collected = new ArrayList<>(entry.getValue().size());
                for (UpsertRelationRequestContext uCtx : entry.getValue()) {

                    String entityName = fromKeys != null ? fromKeys.getEntityName() : relation.getFromEntity();

                    uCtx.accessRight(SecurityUtils.getRightsForResourceWithDefault(entityName));
                    uCtx.relationName(resolvedName);
                    uCtx.relationType(resolvedType);
                    uCtx.fromKeys(fromKeys);

                    UpsertRelationDTO interim;
                    if (Objects.isNull(p)) {
                        interim = executionService.execute(uCtx);
                    } else {
                        interim = executionService.execute(p, uCtx);
                    }

                    if (Objects.nonNull(interim)) {
                        collected.add(interim);
                    }
                }

                result.put(state, collected);
            }

            return new UpsertRelationsDTO(result);
        } finally {
            MeasurementPoint.stop();
        }
    }
}

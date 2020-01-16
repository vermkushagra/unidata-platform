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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.GetRelationRequestContext;
import org.unidata.mdm.data.context.GetRelationTimelineRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRelationsComponent;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 4, 2019
 */
@Component(RelationGetStartExecutor.SEGMENT_ID)
public class RelationGetStartExecutor extends Start<GetRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_GET_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relations.get.start.description";
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationGetStartExecutor.class);
    /**
     * The CRC.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Constructor.
     */
    public RelationGetStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, GetRelationRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(GetRelationRequestContext ctx) {
        MeasurementPoint.start();
        try {
            setup(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(GetRelationRequestContext ctx) {
        setup(ctx);
        RelationKeys keys = ctx.relationKeys();
        return keys.getRelationName();
    }

    private void setup(GetRelationRequestContext ctx) {

        if (ctx.setUp()) {
            return;
        }

        RelationKeys keys = null;
        if (Objects.nonNull(ctx.relationKeys())) {
            keys = ctx.relationKeys();
        } else if (ctx.isValidRelationKey()) {

            Timeline<OriginRelation> timeline = commonRelationsComponent.loadInterval(
                    GetRelationTimelineRequestContext.builder(ctx)
                        .build());

            if (Objects.nonNull(timeline.getKeys())) {
                ctx.currentTimeline(timeline);
                keys = timeline.getKeys();
            }
        } else {
            keys = commonRelationsComponent.ensureAndGetRelationKeys(ctx);
        }

        if (Objects.isNull(keys)) {

            final String message
                = "Relation get: relation of type [{}] not found by supplied keys - relation etalon id [{}], relation origin id [{}], "
                + "etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";

            LOGGER.warn(message,
                    ctx.relationName(),
                    ctx.getRelationEtalonKey(),
                    ctx.getRelationOriginKey(),
                    ctx.getEtalonKey(),
                    ctx.getOriginKey(),
                    ctx.getExternalId(),
                    ctx.getSourceSystem(),
                    ctx.getEntityName());

            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_RELATIONS_GET_NOT_FOUND_BY_SUPPLIED_KEYS,
                    ctx.relationName(),
                    ctx.getRelationEtalonKey(),
                    ctx.getRelationOriginKey(),
                    ctx.getEtalonKey(),
                    ctx.getOriginKey(),
                    ctx.getExternalId(),
                    ctx.getSourceSystem(),
                    ctx.getEntityName());
        }

        setupFields(ctx, keys);

        ctx.setUp(true);
    }

    private void setupFields(GetRelationRequestContext ctx, RelationKeys keys) {

        // Name and type not really needed. Added just for convenience.
        ctx.relationKeys(keys);
        ctx.relationName(keys.getRelationName());
        ctx.relationType(keys.getRelationType());

        if (Objects.isNull(ctx.accessRight())) {
            ctx.accessRight(SecurityUtils.calculateRightsForTopLevelResource(
                    keys.getRelationType() == RelationType.CONTAINS ? keys.getToEntityName() : keys.getFromEntityName(),
                    keys.getEtalonKey().getStatus(),
                    keys.getEtalonKey().getState(),
                    false, true));
        }

        if (Objects.isNull(ctx.currentTimeline())) {
            ctx.currentTimeline(commonRelationsComponent.loadInterval(GetRelationTimelineRequestContext.builder(ctx)
                    .relationEtalonKey(keys.getEtalonKey().getId())
                    .build()));
        }
    }
}

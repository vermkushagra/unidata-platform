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

import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRelationsComponent;
import org.unidata.mdm.data.type.apply.RelationDeleteChangeSet;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationEtalonKey;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationDeleteStartExecutor.SEGMENT_ID)
public class RelationDeleteStartExecutor extends Start<DeleteRelationRequestContext> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationDeleteStartExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_DELETE_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.delete.start.description";
    /**
     * Common rel component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Constructor.
     */
    public RelationDeleteStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, DeleteRelationRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(DeleteRelationRequestContext ctx) {
        setup(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(DeleteRelationRequestContext ctx) {
        setup(ctx);
        RelationKeys keys = ctx.relationKeys();
        return keys.getRelationName();
    }

    /**
     * {@inheritDoc}
     */
    private void setup(DeleteRelationRequestContext ctx) {

        if (ctx.setUp()) {
            return;
        }

        // 1. Possibly setup change set
        setupChangeSet(ctx);

        // 2. Check keys
        Timeline<OriginRelation> timeline = commonRelationsComponent.ensureAndGetRelationTimeline(ctx);
        RelationKeys relationKeys = timeline != null ? timeline.getKeys() : null;

        if (Objects.isNull(relationKeys)) {

            final String message
                = "Relation delete: relation of type [{}] not found by supplied keys - relation etalon id [{}], relation origin id [{}], "
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
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_RELATIONS_DELETE_NOT_FOUND,
                    ctx.relationName(),
                    ctx.getRelationEtalonKey(),
                    ctx.getRelationOriginKey(),
                    ctx.getEtalonKey(),
                    ctx.getOriginKey(),
                    ctx.getExternalId(),
                    ctx.getSourceSystem(),
                    ctx.getEntityName());
        }

        // 3. Check for keys state - the rel may be already deleted / inactive
        if (ctx.isInactivateEtalon()) {

            if (!relationKeys.isActive()) {
                final String message = "Relation [{}], etalon id [{}] is already in inactive state.";
                LOGGER.warn(message, relationKeys.getRelationName(), relationKeys.getEtalonKey().getId());
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_RELATIONS_DELETE_ALREADY_INACTIVE,
                        relationKeys.getRelationName(), relationKeys.getEtalonKey().getId());
            }

            relationKeys = RelationKeys.builder(relationKeys)
                .etalonKey(RelationEtalonKey.builder(relationKeys.getEtalonKey())
                        .status(RecordStatus.INACTIVE)
                        .build())
                .build();
        }

        // 4. Continue
        Date ts = new Date(System.currentTimeMillis());

        ctx.currentTimeline(timeline);
        ctx.relationName(relationKeys.getRelationName());
        ctx.relationType(relationKeys.getRelationType());
        ctx.relationKeys(relationKeys);
        ctx.timestamp(ts);
        ctx.accessRight(SecurityUtils.getRightsForResourceWithDefault(
                relationKeys.getRelationType() == RelationType.CONTAINS
                    ? relationKeys.getToEntityName()
                    : relationKeys.getFromEntityName()));

        ctx.setUp(true);
    }

    private void setupChangeSet(DeleteRelationRequestContext ctx) {

        // May be already set by batch
        if (Objects.isNull(ctx.changeSet())) {
            RelationDeleteChangeSet set = new RelationDeleteChangeSet();
            set.setRelationType(ctx.relationType());
            ctx.changeSet(set);
        }
    }
}

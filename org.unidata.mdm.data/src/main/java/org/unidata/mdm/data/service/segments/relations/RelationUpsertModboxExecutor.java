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

import java.util.Collections;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.calculables.impl.RelationRecordHolder;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.OriginRelationInfoSection;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.impl.OriginRelationImpl;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.type.timeline.RelationTimeInterval;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 * Prepares upsert context.
 */
@Component(RelationUpsertModboxExecutor.SEGMENT_ID)
public class RelationUpsertModboxExecutor extends Point<UpsertRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_MODBOX]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.upsert.modbox.description";
    /**
     * Constructor.
     */
    public RelationUpsertModboxExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRelationRequestContext ctx) {

        // 1. Containments are processed by record services entirely
        if (ctx.relationType() == RelationType.CONTAINS) {
            return;
        }

        // 2. First of all create box.
        // If the boundaries should be checked and possibly fixed, this must be done prior to this segment.
        ctx.modificationBox(new RelationTimeInterval(ctx.getValidFrom(), ctx.getValidTo(), Collections.emptyList()));

        RelationKeys relationKeys = ctx.relationKeys();
        Date ts = ctx.timestamp();
        String user = SecurityUtils.getCurrentUserName();

        // 3. Push upsert
        OriginRelation origin = new OriginRelationImpl()
                .withDataRecord(ctx.getRelation() == null ? new SerializableDataRecord() : ctx.getRelation())
                .withInfoSection(new OriginRelationInfoSection()
                        .withRelationName(relationKeys.getRelationName())
                        .withRelationType(relationKeys.getRelationType())
                        .withValidFrom(ctx.getValidFrom())
                        .withValidTo(ctx.getValidTo())
                        .withFromEntityName(relationKeys.getFromEntityName())
                        .withToEntityName(relationKeys.getToEntityName())
                        .withStatus(relationKeys.getOriginKey().getStatus())
                        .withApproval(relationKeys.getEtalonKey().getState()) // <-- will be recalculated later
                        .withShift(DataShift.PRISTINE)
                        .withRelationOriginKey(relationKeys.getOriginKey())
                        .withCreateDate(ts)
                        .withUpdateDate(ts)
                        .withCreatedBy(user)
                        .withUpdatedBy(user));

        ctx.modificationBox().push(new RelationRecordHolder(origin));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

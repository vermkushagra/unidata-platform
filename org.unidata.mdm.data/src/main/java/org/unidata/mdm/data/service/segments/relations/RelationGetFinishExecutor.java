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

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.GetRelationRequestContext;
import org.unidata.mdm.data.dto.GetRelationDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationGetFinishExecutor.SEGMENT_ID)
public class RelationGetFinishExecutor extends Finish<GetRelationRequestContext, GetRelationDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_GET_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.get.finish.description";
    /**
     * Constructor.
     */
    public RelationGetFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, GetRelationDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public GetRelationDTO finish(GetRelationRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        Timeline<OriginRelation> timeline = ctx.currentTimeline();
        RelationType relationType = ctx.relationType();
        String relationName = ctx.relationName();

        GetRelationDTO dto = new GetRelationDTO(keys, relationName, relationType);
        dto.setEtalon(timeline.isEmpty() ? null : timeline.first().getCalculationResult());
        dto.setRights(SecurityUtils.calculateRightsForTopLevelResource(
                relationType == RelationType.CONTAINS ? keys.getToEntityName() : keys.getFromEntityName(),
                        keys.getEtalonKey().getStatus(),
                        keys.getEtalonKey().getState(),
                        false, true));

        return dto;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return GetRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

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
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.dto.DeleteRelationDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationDeleteFinishExecutor.SEGMENT_ID)
public class RelationDeleteFinishExecutor extends Finish<DeleteRelationRequestContext, DeleteRelationDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_DELETE_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.delete.finish.description";
    /**
     * Constructor.
     */
    public RelationDeleteFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, DeleteRelationDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRelationDTO finish(DeleteRelationRequestContext ctx) {
        RelationKeys keys = ctx.relationKeys();
        return new DeleteRelationDTO(keys, keys.getRelationName(), keys.getRelationType());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

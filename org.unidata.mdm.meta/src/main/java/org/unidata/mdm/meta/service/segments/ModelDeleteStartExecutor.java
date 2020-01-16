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

package org.unidata.mdm.meta.service.segments;

import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author maria.chistyakova
 * @since  06.12.2019
 */
@Component(ModelDeleteStartExecutor.SEGMENT_ID)
public class ModelDeleteStartExecutor extends Start<DeleteModelRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_DELETE_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.start.description";
    /**
     * Constructor.
     */
    public ModelDeleteStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, DeleteModelRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(DeleteModelRequestContext ctx) {
        // NOOP. Start does nothing here.
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(DeleteModelRequestContext ctx) {
        // No subject for this type of pipelines
        // This may be storage id in the future
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return start == this;
    }
}

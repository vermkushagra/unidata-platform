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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.PublishModelRequestContext;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.VoidPipelineOutput;

/**
 * @author maria.chistyakova
 * @since  18.12.2019
 */
@Component(ModelPublishFinishExecutor.SEGMENT_ID)
public class ModelPublishFinishExecutor extends Finish<PublishModelRequestContext, VoidPipelineOutput> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_PUBLISH_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.finish.description";
    /**
     * MMS. Cheap and dirty.
     */
    @Autowired
    private MetaDraftService metaModelService;
    /**
     * Constructor.
     */
    public ModelPublishFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, VoidPipelineOutput.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public VoidPipelineOutput finish(PublishModelRequestContext ctx) {
        metaModelService.apply();
        return VoidPipelineOutput.INSTANCE;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return PublishModelRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

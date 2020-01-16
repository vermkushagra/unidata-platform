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

package org.unidata.mdm.meta.context;

import java.io.Serializable;

import org.unidata.mdm.meta.service.segments.ModelPublishStartExecutor;
import org.unidata.mdm.system.context.AbstractCompositeRequestContext;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author maria.chistyakova
 * @since  18.12.2019
 */
public class PublishModelRequestContext
        extends AbstractCompositeRequestContext
        implements PipelineInput, Serializable {

    public static PublishModelRequestContext.PublishModelRequestContextBuilder builder() {
        return new PublishModelRequestContextBuilder();
    }
    /**
     * Constructor.
     *
     * @param b
     */
    public PublishModelRequestContext(PublishModelRequestContextBuilder b) {
        super(b);
    }

    @Override
    public String getStartTypeId() {
        return ModelPublishStartExecutor.SEGMENT_ID;
    }

    public static class PublishModelRequestContextBuilder extends AbstractCompositeRequestContextBuilder<PublishModelRequestContextBuilder> {

        @Override
        public PublishModelRequestContext build() {
            return new PublishModelRequestContext(this);
        }
    }
}

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

package org.unidata.mdm.system.util;

import java.util.Objects;

import org.unidata.mdm.system.configuration.SystemConfiguration;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Segment;

/**
 * @author Mikhail Mikhailov on Nov 25, 2019
 */
public class PipelineUtils {
    /**
     * The PS.
     */
    private static PipelineService pipelineService;
    /**
     * Disabling instantiation constructor.
     */
    private PipelineUtils() {
        super();
    }

    public static void init() {
        pipelineService = SystemConfiguration.getBean(PipelineService.class);
    }

    public static Segment findSegment(String id) {
        return pipelineService.segment(id);
    }

    public static Pipeline findPipeline(String id, String subject) {
        return Objects.isNull(subject) ? pipelineService.getById(id) : pipelineService.getByIdAndSubject(id, subject);
    }
}

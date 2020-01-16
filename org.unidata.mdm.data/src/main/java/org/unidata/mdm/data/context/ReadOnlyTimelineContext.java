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

package org.unidata.mdm.data.context;

import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author Mikhail Mikhailov
 * Read only data view - variants of GET (data, rel, clsf, etc).
 */
public interface ReadOnlyTimelineContext<T extends Calculable> extends StorageCapableContext, PipelineInput {
    /**
     * Current timeline.
     */
    StorageId SID_CURRENT_TIMELINE = new StorageId("CURRENT_TIMELINE");
    /**
     * Get TL.
     * @return timeline
     */
    default Timeline<T> currentTimeline() {
        return getFromStorage(SID_CURRENT_TIMELINE);
    }

    /**
     * Put TL.
     * @param timeline
     */
    default void currentTimeline(Timeline<T> timeline) {
        putToStorage(SID_CURRENT_TIMELINE, timeline);
    }

}

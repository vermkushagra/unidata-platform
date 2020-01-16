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

package org.unidata.mdm.data.service.segments;

import java.util.Date;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.unidata.mdm.core.type.calculables.CalculationResult;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.ReadOnlyTimelineContext;

/**
 * @author Mikhail Mikhailov
 * Common utility stuff for timeline selection operations.
 */
public interface TimelineSelectionSupport<C extends ReadOnlyTimelineContext<?>, R extends CalculationResult<?>> {

    @SuppressWarnings("unchecked")
    default R getCurrentEtalonRecord(@Nonnull final C ctx) {

        Timeline<?> timeline = ctx.currentTimeline();
        TimeInterval<?> selected = timeline.selectAsOf(new Date());
        if (Objects.nonNull(selected)) {
            return (R) selected.getCalculationResult();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    default R getFirstNonNullCalculationResult(@Nonnull C ctx) {

        Timeline<?> timeline = ctx.currentTimeline();
        if (!timeline.isEmpty()) {
            for (TimeInterval<?> interval : timeline) {
                R etalon = (R) interval.getCalculationResult();
                if (Objects.nonNull(etalon)) {
                    return etalon;
                }
            }
        }

        return null;
    }
}

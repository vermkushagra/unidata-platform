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

    default R getCurrentEtalonRecord(@Nonnull final C ctx) {

        Timeline<?> timeline = ctx.currentTimeline();
        TimeInterval<?> selected = timeline.selectAsOf(new Date());
        if (Objects.nonNull(selected)) {
            return selected.getCalculationResult();
        }

        return null;
    }

    default R getFirstNonNullCalculationResult(@Nonnull C ctx) {

        Timeline<?> timeline = ctx.currentTimeline();
        if (!timeline.isEmpty()) {
            for (TimeInterval<?> interval : timeline) {
                R etalon = interval.getCalculationResult();
                if (Objects.nonNull(etalon)) {
                    return etalon;
                }
            }
        }

        return null;
    }
}

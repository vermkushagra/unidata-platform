package org.unidata.mdm.data.context;

import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;

/**
 * @author Mikhail Mikhailov
 * Read only data view - variants of GET (data, rel, clsf, etc).
 */
public interface ReadOnlyTimelineContext<T extends Calculable> extends StorageCapableContext {
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

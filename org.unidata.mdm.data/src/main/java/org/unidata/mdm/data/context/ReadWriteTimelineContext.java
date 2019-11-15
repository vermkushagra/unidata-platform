package org.unidata.mdm.data.context;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.calculables.ModificationBox;
import org.unidata.mdm.core.type.change.ChangeSet;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.system.context.StorageId;

/**
 * @author Mikhail Mikhailov
 * Modifying stuff.
 */
public interface ReadWriteTimelineContext<T extends Calculable> extends ReadOnlyTimelineContext<T> {
    /**
     * Current timeline.
     */
    StorageId SID_NEXT_TIMELINE = new StorageId("NEXT_TIMELINE");
    /**
     * Mod box.
     */
    StorageId SID_MODIFICATION_BOX = new StorageId("MODIFICATION_BOX");
    /**
     * Collected changes.
     */
    StorageId SID_CHANGE_SET = new StorageId("CHANGE_SET");
    /**
     * Modification timestamp.
     */
    StorageId SID_MODIFICATION_TIMESTAMP = new StorageId("MODIFICATION_TIMESTAMP");
    /**
     * Locally incremented time source.
     */
    StorageId SID_LOCAL_TIMESTAMP = new StorageId("LOCAL_TIMESTAMP");
    /**
     * Get TL.
     * @return timeline
     */
    default Timeline<T> nextTimeline() {
        return getFromStorage(SID_NEXT_TIMELINE);
    }
    /**
     * Put TL.
     * @param timeline
     */
    default void nextTimeline(Timeline<T> timeline) {
        putToStorage(SID_NEXT_TIMELINE, timeline);
    }
    /**
     * Get Box.
     * @return box
     */
    default <B extends ModificationBox<T>> B modificationBox() {
        return getFromStorage(SID_MODIFICATION_BOX);
    }
    /**
     * Put Box.
     * @param box the box
     */
    default <B extends ModificationBox<T>> void modificationBox(B box) {
        putToStorage(SID_MODIFICATION_BOX, box);
    }
    /**
     * Get change set, hold by this context.
     * @return change set
     */
    default <S extends ChangeSet> S changeSet() {
        return getFromStorage(SID_CHANGE_SET);
    }
    /**
     * Put change set, hold by this context.
     */
    default <S extends ChangeSet> void changeSet(S set) {
        putToStorage(SID_CHANGE_SET, set);
    }
    /**
     * Get TS.
     * @return timestamp
     */
    default Date timestamp() {

        Date ts = getFromStorage(SID_MODIFICATION_TIMESTAMP);
        if (ts == null) {
            ts = new Date(System.currentTimeMillis());
            timestamp(ts);
        }

        return ts;
    }
    /**
     * Put TS.
     * @param ts the timestamp
     */
    default void timestamp(Date ts) {
        putToStorage(SID_MODIFICATION_TIMESTAMP, ts);
    }
    /**
     * Generates 'local timestamp', i. e. operation timestamp + 1 milli,
     * to prevent same values for different origin versions in the same context.
     * @return timestamp
     */
    default Date localTimestamp() {

        AtomicLong holder = getFromStorage(SID_LOCAL_TIMESTAMP);
        if (holder == null) {
            holder = new AtomicLong(timestamp().getTime());
            putToStorage(SID_LOCAL_TIMESTAMP, holder);
        }

        return new Date(holder.incrementAndGet());
    }
}

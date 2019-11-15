package org.unidata.mdm.data.context;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.unidata.mdm.core.type.change.ChangeSet;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.type.apply.RecordMergeChangeSet;
import org.unidata.mdm.data.type.apply.RelationMergeChangeSet;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;

/**
 * Records merge interface.
 * @author Mikhail Mikhailov on Nov 6, 2019
 */
public interface MergeCapableContext extends StorageCapableContext {
    /**
     * Duplicate record keys.
     */
    StorageId SID_MERGE_DUPLICATE_KEYS = new StorageId("MERGE_DUPLICATES_KEYS");
    /**
     * Duplicating timeline stuff here. Current timeline.
     */
    StorageId SID_MERGE_CURRENT_TIMELINE = new StorageId("MERGE_CURRENT_TIMELINE");
    /**
     * Duplicating timeline stuff here. Next timeline.
     */
    StorageId SID_MERGE_NEXT_TIMELINE = new StorageId("MERGE_NEXT_TIMELINE");
    /**
     * Last duplicate timelines.
     */
    StorageId SID_MERGE_DUPLICATE_TIMELINES = new StorageId("MERGE_DUPLICATE_TIMELINES");
    /**
     * Last duplicate timelines.
     */
    StorageId SID_MERGE_RECORD_SET = new StorageId("MERGE_RECORD_SET");
    /**
     * Last duplicate timelines.
     */
    StorageId SID_MERGE_RELATION_SET = new StorageId("MERGE_RELATION_SET");
    /**
     * Last duplicate timelines.
     */
    StorageId SID_MERGE_TIMESTAMP = new StorageId("MERGE_TIMESTAMP");
    /**
     * GET duplicates.
     * @return key list
     */
    default List<RecordKeys> duplicateKeys() {
        return getFromStorage(SID_MERGE_DUPLICATE_KEYS);
    }
    /**
     * PUT duplicates.
     * @param duplicates key list
     */
    default void duplicateKeys(List<RecordKeys> duplicates) {
        putToStorage(SID_MERGE_DUPLICATE_KEYS, duplicates);
    }
    /**
     * Get TL.
     * @return timeline
     */
    default Timeline<OriginRecord> currentTimeline() {
        return getFromStorage(SID_MERGE_CURRENT_TIMELINE);
    }
    /**
     * Put TL.
     * @param timeline
     */
    default void currentTimeline(Timeline<OriginRecord> timeline) {
        putToStorage(SID_MERGE_CURRENT_TIMELINE, timeline);
    }
    /**
     * Get TL.
     * @return timeline
     */
    default Timeline<OriginRecord> nextTimeline() {
        return getFromStorage(SID_MERGE_NEXT_TIMELINE);
    }
    /**
     * Put TL.
     * @param timeline
     */
    default void nextTimeline(Timeline<OriginRecord> timeline) {
        putToStorage(SID_MERGE_NEXT_TIMELINE, timeline);
    }
    /**
     * Get TLs.
     * @return timeline
     */
    default Map<String, Timeline<OriginRecord>> duplicateTimelines() {
        return getFromStorage(SID_MERGE_DUPLICATE_TIMELINES);
    }
    /**
     * Put TLs.
     * @param timelines timelines map
     */
    default void duplicateTimelines(Map<String, Timeline<OriginRecord>> timelines) {
        putToStorage(SID_MERGE_DUPLICATE_TIMELINES, timelines);
    }
    /**
     * Get change set, hold by this context.
     * @return change set
     */
    default <T extends RecordMergeChangeSet> T recordChangeSet() {
        return getFromStorage(SID_MERGE_RECORD_SET);
    }
    /**
     * Put change set, hold by this context.
     * @param set the change set
     */
    default <T extends RecordMergeChangeSet> void recordChangeSet(T set) {
        putToStorage(SID_MERGE_RECORD_SET, set);
    }
    /**
     * Get rel change set, hold by this context.
     * @return change set
     */
    default <T extends RelationMergeChangeSet> T relationChangeSet() {
        return getFromStorage(SID_MERGE_RELATION_SET);
    }
    /**
     * Put rel change set, hold by this context.
     * @param set the change set
     */
    default <T extends ChangeSet> void relationChangeSet(T set) {
        putToStorage(SID_MERGE_RELATION_SET, set);
    }
//    /**
//     * Get clsf change set, hold by this context.
//     * @return change set
//     */
//    public <T extends ChangeSet> T classifierChangeSet() {
//        return getFromStorage(StorageId.DATA_CLASSIFIERS_CHANGE_SET);
//    }
//    /**
//     * Put clsf change set, hold by this context.
//     * @param set the change set
//     */
//    public <T extends ChangeSet> void classifierChangeSet(T set) {
//        putToStorage(StorageId.DATA_CLASSIFIERS_CHANGE_SET, set);
//    }
    /**
     * Get TS.
     * @return timestamp
     */
    default Date timestamp() {
        return getFromStorage(SID_MERGE_TIMESTAMP);
    }
    /**
     * Put TS.
     * @param ts the timestamp
     */
    default void timestamp(Date ts) {
        putToStorage(SID_MERGE_TIMESTAMP, ts);
    }
}
